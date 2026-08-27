# Base de Datos — EcoCiclos: Guardianes de la Materia

Room (SQLite) 100% local. Sin backend, sin Firebase, sin sincronización remota. Ver también [`database/schema.sql`](../database/schema.sql) y [`database/sample_data.sql`](../database/sample_data.sql).

## Diagrama de relaciones (textual)

```
biomas (1) ───< niveles (N)
                  │
                  ├──< elementos_nivel (N)   [puzzle Restaurador]
                  ├──< nodos_ruta (N)         [puzzle Enrutador]
                  ├──< variables_lab (N)      [puzzle Laboratorio]
                  └──(1:1 opcional)── progreso_nivel

cartas (N) ──> nivelDesbloqueoId ──> niveles (1)
cartas (1) ──< cartas_desbloqueadas (0..1)

insignias (0..1) ──> biomaId ──> biomas (1)   [NULL = insignia global]
insignias (1) ──< insignias_desbloqueadas (0..1)

perfil — fila única (id = 0)
```

## Tablas

### `biomas`
Los tres biomas jugables. `desbloqueado` es un flag persistido, aunque el estado visual real (`BLOQUEADO/DISPONIBLE/INICIADO/COMPLETADO`) se **calcula en tiempo real** en `CalcularEstadoBiomasUseCase` a partir del progreso de niveles, no se lee directamente de esta columna.

| Columna | Tipo | Notas |
|---|---|---|
| id | INTEGER PK | 1, 2, 3 |
| nombre, descripcion | TEXT | |
| tipoCiclo | TEXT | `AGUA` \| `CARBONO` \| `NITROGENO` |
| orden | INTEGER | orden de desbloqueo |
| colorHex | TEXT | color temático del bioma |
| desbloqueado | INTEGER (bool) | |

### `niveles`
15 filas semilla (5 por bioma). `FOREIGN KEY(biomaId) REFERENCES biomas(id) ON DELETE CASCADE`.

| Columna | Tipo | Notas |
|---|---|---|
| id | INTEGER PK | ids semilla: 101-105, 201-205, 301-305 |
| biomaId | INTEGER FK | |
| orden | INTEGER | orden dentro del bioma |
| titulo, instruccion | TEXT | |
| tipoReto | TEXT | `RESTAURADOR` \| `ENRUTADOR` \| `LABORATORIO` |
| dificultad | INTEGER | 1..3 |

### `elementos_nivel`, `nodos_ruta`, `variables_lab`
Contenido específico de cada mecánica; se usa una de las tres según `niveles.tipoReto`. Todas con `ON DELETE CASCADE` hacia `niveles`.

- `elementos_nivel`: pares origen→destino del Restaurador. `esOrigen=1` marca los elementos arrastrables; su `destinoCorrectoClave` apunta a la `clave` del elemento destino (`esOrigen=0`) correcto.
- `nodos_ruta`: nodos del Enrutador. `esFuente=1` son los orígenes; `tipoRecurso` (`ORGANICO`/`PLASTICO`/`VIDRIO`/`METAL`/`CONTAMINANTE`) debe coincidir entre fuente y destino elegido, además de que `destinoCorrectoClave` debe coincidir con la clave del destino.
- `variables_lab`: una fila por nivel `LABORATORIO`, con rango, valor inicial, umbral crítico y los dos mensajes educativos.

### `cartas` / `cartas_desbloqueadas`
15 cartas semilla. `nivelDesbloqueoId` referencia el nivel que la desbloquea (1:1 nivel→carta en los datos semilla actuales, aunque el esquema no lo restringe). `cartas_desbloqueadas` es la tabla de hechos: una fila = una carta que el niño realmente desbloqueó, con marca de tiempo.

### `insignias` / `insignias_desbloqueadas`
4 insignias semilla: 3 por bioma (`biomaId` no nulo) + 1 global (`biomaId IS NULL`, se otorga cuando los tres biomas llegan a 100%).

### `progreso_nivel`
Tabla de hechos central del juego: una fila por nivel jugado al menos una vez. `intentos` se incrementa en cada intento (correcto o no); `estrellas` guarda el **mejor** resultado obtenido, nunca se degrada; `completado` una vez `true` permanece `true`.

### `perfil`
Fila única (`id = 0`, upsert). Alias elegido por el niño (nunca nombre real), avatar ilustrado (1-8) y XP acumulado.

## Política de datos semilla

- La app **nunca** se entrega con "3 preguntas y 2 niveles": trae 3 biomas completos y 15 niveles jugables desde la primera instalación (`DatabaseSeeder`, invocado desde `EcoCiclosApp.onCreate()`).
- La siembra es **idempotente**: comprueba `biomaDao().contar() > 0` antes de insertar, así que reabrir la app (o que el proceso se reinicie) nunca duplica datos. Cubierto por `DatabaseSeederTest`.
- Los datos semilla viven en código (`SeedData.kt`), no en JSON/asset remoto, para que el `DatabaseSeeder` pueda ejecutarse 100% offline y sea trivial de testear.

## Privacidad de los datos almacenados

Todas las tablas contienen únicamente: progreso de juego, alias elegido libremente y avatar numérico. **Ninguna tabla almacena** nombre real, correo, teléfono, dirección ni ubicación — cumpliendo la regla de privacidad infantil de MASTER_SPEC.
