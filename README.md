# EcoCiclos: Guardianes de la Materia 🌱💧

Simulador ecológico de resolución de puzles para niños de **8 a 12 años**, construido en **Android nativo (Kotlin + Jetpack Compose)**. El niño restaura tres biomas dañados guiando correctamente los ciclos del agua, el carbono y el nitrógeno, y gestionando el flujo de recursos (reciclaje, compostaje).

100% offline. Sin cuentas, sin anuncios, sin analítica, sin conexión a internet.

## Tabla de contenidos

- [Qué es EcoCiclos](#qué-es-ecociclos)
- [Estado del proyecto y compilación](#estado-del-proyecto-y-compilación)
- [Cómo compilar](#cómo-compilar)
- [Estructura del repositorio](#estructura-del-repositorio)
- [Arquitectura](#arquitectura)
- [Documentación completa](#documentación-completa)

## Qué es EcoCiclos

- **3 biomas**: El Bosque Seco (ciclo del agua), La Ciudad Ahumada (ciclo del carbono), El Río Estancado (ciclo del nitrógeno y flujo de residuos).
- **15 niveles/retos** interactivos, mínimo 5 por bioma, con tres mecánicas educativas reales (nada de solo leer y responder):
  - **Restaurador de Ciclos** — arrastra elementos (drag & drop real) hasta la etapa correcta del ciclo.
  - **Enrutador de Recursos** — traza con el dedo, sobre un Canvas, la ruta correcta de cada recurso.
  - **Laboratorio de Reacciones** — mueve una variable real (temperatura, fertilizante, deforestación…) y observa un efecto calculado, no un texto fijo.
- **Eco-pedia**: 15 cartas coleccionables y 4 insignias, desbloqueadas por progreso real, nunca por comprar ni por esperar.
- **Brote** y **Gota**, los personajes guía, dibujados enteramente con Compose Canvas (sin imágenes externas).
- Progreso guardado en **Room/SQLite** local; perfil con alias y avatar (nunca nombre real).

## Estado del proyecto y compilación

**COMPILACIÓN NO VERIFICADA LOCALMENTE.** El código fue escrito en un entorno sin SDK de Android instalado y sin acceso de red a `maven.google.com`/`services.gradle.org`, por lo que no fue posible ejecutar `./gradlew` en ese entorno. Esto se documenta explícitamente en [`docs/BUILD_REPORT.md`](docs/BUILD_REPORT.md), siguiendo la regla de honestidad del proyecto: nunca se simulan resultados de compilación.

El workflow [`.github/workflows/android.yml`](.github/workflows/android.yml) sí compila el proyecto en un entorno real de GitHub Actions en cada `push`: ejecuta `clean`, `testDebugUnitTest`, `lintDebug`, `assembleDebug` y `assembleRelease`, y publica el APK y los reportes como artefactos descargables. Es el primer paso recomendado tras clonar este repositorio.

## Cómo compilar

### Opción recomendada: GitHub Actions
1. Sube este repositorio a GitHub (o haz push a uno existente).
2. El workflow se ejecuta automáticamente. Descarga el APK desde la pestaña **Actions → (tu ejecución) → Artifacts**.

### Localmente (requiere Android Studio o el SDK de Android instalado)
```bash
# Si gradle-wrapper.jar no está presente (ver nota abajo):
gradle wrapper --gradle-version 8.7

./gradlew clean
./gradlew testDebugUnitTest
./gradlew lintDebug
./gradlew assembleDebug
```

> **Nota sobre `gradle-wrapper.jar`**: este binario no se incluyó en el repositorio porque el entorno de generación no tenía acceso a `services.gradle.org` para descargarlo de forma verificable. `gradlew`/`gradlew.bat` están listos para usarlo en cuanto exista; genéralo una vez con `gradle wrapper --gradle-version 8.7` (con Gradle instalado) o deja que lo genere el workflow de CI.

## Estructura del repositorio

```
app/                    Código fuente Android (Kotlin/Compose)
  src/main/             Código de producción
  src/test/             76 tests unitarios (JVM, corren con testDebugUnitTest)
database/
  schema.sql            Esquema Room/SQLite documentado
  sample_data.sql       Datos semilla en formato SQL
docs/
  MEMORIA_DESCRIPTIVA.md
  MANUAL_USUARIO.md
  MANUAL_TECNICO.md
  BASE_DE_DATOS.md
  BUILD_REPORT.md
.github/workflows/android.yml   CI: test + lint + build + APK
```

## Arquitectura

MVVM + Clean Architecture, separado en `data/`, `domain/` y `ui/`, con Room + Coroutines + StateFlow. Ver [`docs/MANUAL_TECNICO.md`](docs/MANUAL_TECNICO.md) para el detalle completo.

## Documentación completa

| Documento | Contenido |
|---|---|
| [`docs/MEMORIA_DESCRIPTIVA.md`](docs/MEMORIA_DESCRIPTIVA.md) | Visión de producto, diseño pedagógico y de experiencia infantil |
| [`docs/MANUAL_USUARIO.md`](docs/MANUAL_USUARIO.md) | Cómo juega un niño de 8-12 años, pantalla por pantalla |
| [`docs/MANUAL_TECNICO.md`](docs/MANUAL_TECNICO.md) | Arquitectura, módulos, decisiones técnicas, cómo extender el proyecto |
| [`docs/BASE_DE_DATOS.md`](docs/BASE_DE_DATOS.md) | Esquema Room, relaciones, política de datos semilla |
| [`docs/BUILD_REPORT.md`](docs/BUILD_REPORT.md) | Estado real de compilación y pruebas, honestamente reportado |
