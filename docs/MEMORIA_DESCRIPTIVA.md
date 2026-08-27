# Memoria Descriptiva — EcoCiclos: Guardianes de la Materia

## 1. Visión de producto

EcoCiclos no es un libro de texto digital ni un cuestionario con piel de videojuego. Es un **simulador ecológico de resolución de puzles**: el niño no "completa el ejercicio 4", **repara un bioma** guiando correctamente los ciclos del agua, el carbono y el nitrógeno, y gestionando el flujo de residuos hacia su destino correcto.

Público objetivo: niños de **8 a 12 años**. Esto significa, en la práctica:
- Sin estética de preescolar, sin exceso de emojis, sin personajes "bebés".
- Interfaz que se siente moderna, aventurera y ligeramente desafiante — nunca condescendiente.
- Textos breves en español natural, con dificultad que crece de forma gradual (dificultad 1→2→3 dentro de cada bioma).

## 2. Los primeros 30 segundos

1. Una semilla cae y brota hasta formar el logo (splash, ~1.7 s).
2. Onboarding de máximo 4 pantallas: por qué existe la misión, quiénes son Brote y Gota, qué se puede hacer (arrastrar, enrutar, experimentar), y la promesa de privacidad (todo se guarda solo en el dispositivo).
3. El niño entra directamente al **Globo**: un mapa con tres biomas, el primero ya jugable, con Brote y Gota dándole la bienvenida y una barra de "planeta restaurado" en 0%.
4. Toca el primer bioma → Brote da un diagnóstico de 2 frases → entra al primer reto, que ya es interactivo (arrastrar el Sol al lago), no una pantalla de texto.

## 3. La acción principal de diversión

Tres mecánicas reales, nunca opción múltiple disfrazada:

- **Restaurador de Ciclos**: arrastre táctil real (no "toca para seleccionar") de un elemento de origen hasta la etapa del ciclo donde encaja. El acierto se valida por *dónde* soltó el dedo, no por texto.
- **Enrutador de Recursos**: el niño traza con el dedo, sobre un `Canvas` de Compose, una línea desde una fuente (ej. una cáscara de fruta) hasta el destino correcto (la compostera), evitando contaminar el río.
- **Laboratorio de Reacciones**: mueve un control deslizante que representa una variable real del ecosistema (temperatura, % de árboles talados, fertilizante que llega al río…) y observa, en tiempo real, un efecto calculado matemáticamente — no un texto aleatorio — sobre el estado del ecosistema.

## 4. Qué provoca curiosidad

- El bioma pasa visualmente de **gris/contaminado** a **colorido** a medida que se restauran sus ciclos: el niño ve el resultado de su acción, no solo un número.
- Cada nivel completado revela una **carta de la Eco-pedia** (organismo, elemento o fenómeno relacionado) con una explicación breve y curiosa.
- Insignias visibles pero "en silueta" (no ocultas) cuando aún no se obtienen: el niño sabe que existen y qué debe lograr para conseguirlas.

## 5. Qué hace que quiera volver mañana

- Sesiones diseñadas para 5-20 minutos: un nivel se juega y se resuelve en minutos, el progreso se guarda solo.
- Progresión real: un bioma se desbloquea solo cuando el anterior está 100% restaurado, así que siempre hay "la siguiente zona" esperando.
- Colección incompleta visible: la Eco-pedia siempre muestra cuántas cartas/insignias faltan.

## 6. Progreso visual observado

- Barra de "planeta restaurado" en el Globo (promedio real de los tres biomas).
- Barra de restauración por bioma + contador "X/5 retos".
- Tres estrellas por nivel, calculadas por número de errores, no fijas.
- Estados de módulo explícitos (bloqueado / disponible / en progreso / restaurado / dominado), señalados con icono **y** texto, nunca solo color.

## 7. Narrativa ligera

Brote (espíritu del bosque) y Gota (chispa de agua) presentan cada bioma con un diagnóstico de 1-2 frases y celebran cada nivel completado. No hay diálogos largos ni interrupciones constantes: aparecen en el diagnóstico inicial de cada bioma y en la pantalla de resultado de cada reto.

## 8. Gamificación — qué se incluyó y qué se excluyó deliberadamente

Incluido: estrellas, insignias, cartas coleccionables, progresión por biomas y niveles, desbloqueos reales.

Excluido a propósito (regla 8 de MASTER_SPEC): rankings online, presión social, compras dentro de la app, vidas que obliguen a esperar, castigos por no abrir la app. Ninguna recompensa está desconectada de una acción real del niño.

## 9. Revisión final de experiencia (autoevaluación con un niño de 10 años imaginario)

| Pregunta | Respuesta de diseño |
|---|---|
| ¿Me gustaría abrir esta app mañana? | Sí: quedan biomas por restaurar y cartas por descubrir, con sesiones cortas. |
| ¿Entiendo qué debo hacer? | Sí: cada reto trae una instrucción de una línea y Brote da contexto antes de jugar. |
| ¿Tengo algo que descubrir? | Sí: 15 cartas y 4 insignias, reveladas progresivamente. |
| ¿Me siento recompensado? | Sí: feedback inmediato (color, animación, estrellas) en cada acierto. |
| ¿Hay algo que puedo coleccionar? | Sí: Eco-pedia con cartas de elementos, plantas y animales. |
| ¿La app parece hecha para mí? | Sí: paleta e ilustraciones propias, sin estética infantil ni empresarial. |

## 10. Simplificaciones documentadas (regla de honestidad de MASTER_SPEC)

Para no reducir mecánicas complejas a versiones triviales sin decirlo, se documentan aquí las decisiones de alcance tomadas:

- **Insignias de "perfecto" y "coleccionista completo"** no se incluyeron en los datos semilla porque su lógica de desbloqueo (3 estrellas en todos los niveles de un bioma; las 15 cartas reunidas) no estaba implementada en `DesbloquearInsigniaUseCase` en esta iteración. Se prefirió lanzar con 4 insignias completamente funcionales antes que con 8 insignias, algunas de ellas decorativas. Extenderlo es straightforward: añadir los criterios al use case y las filas a `SeedData.insignias`.
- **Sonido** no se implementó en esta iteración (no hay archivos de audio locales incluidos); la arquitectura de `PreferenciasApp` ya contempla un flag `sonidoActivado` para cuando se añadan efectos.
- **Iconos de launcher para Android 7.0/7.1 (API 24-25)**: solo se proveyó el adaptive icon (`mipmap-anydpi-v26`), sin los mipmaps PNG heredados por densidad. En esos dispositivos el sistema usará un ícono por defecto hasta añadir los PNG de respaldo.
