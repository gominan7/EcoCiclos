# Build Report — EcoCiclos: Guardianes de la Materia

> Regla 37 de MASTER_SPEC: *"Si el entorno no puede compilar: COMPILACIÓN NO VERIFICADA. Nunca simules resultados."* Este documento cumple esa regla al pie de la letra.

## Estado real de la compilación en este entorno

**COMPILACIÓN NO VERIFICADA.**

El entorno en el que se generó este proyecto es un contenedor Linux con:
- Java 21 (OpenJDK) ✅ disponible.
- `git`, `zip` ✅ disponibles.
- **Gradle: no instalado.**
- **Android SDK: no instalado** (no existe `ANDROID_HOME` ni `/usr/lib/android-sdk`).
- **Acceso de red restringido** a una lista blanca de dominios que **no incluye** `maven.google.com`, `dl.google.com` ni `services.gradle.org` — es decir, ni el Android Gradle Plugin, ni las dependencias de AndroidX/Compose/Room, ni el propio Gradle se pueden descargar desde este entorno.

En consecuencia, **no fue posible ejecutar ninguno de los siguientes comandos** en este entorno:

```
./gradlew clean
./gradlew testDebugUnitTest
./gradlew lintDebug
./gradlew assembleDebug
```

No se generó ningún APK localmente. No existe un SHA-256 de APK que reportar porque no existe ningún APK generado en este entorno. Cualquier cifra de "tests aprobados/fallidos" que no provenga de una ejecución real de `./gradlew testDebugUnitTest` sería inventada — y por lo tanto no se reporta aquí.

## Lo que sí se hizo para compensar esta limitación

1. **Revisión manual exhaustiva** de cada archivo Kotlin: imports, nombres de paquete consistentes, tipos de retorno, nulabilidad, y coherencia entre entidades Room, mappers y modelos de dominio.
2. **76 tests unitarios** escritos (`app/src/test/...`), diseñados para correr sobre la JVM pura vía `testDebugUnitTest` sin necesitar un emulador (Room en memoria vía Robolectric para los tests de persistencia).
3. Un **workflow de GitHub Actions** (`.github/workflows/android.yml`) listo para ejecutarse en un runner real de `ubuntu-latest` con acceso completo a internet, que:
   - Genera `gradle-wrapper.jar` si no está presente en el repositorio (ver nota abajo).
   - Ejecuta `clean`, `testDebugUnitTest`, `lintDebug`, `assembleDebug` y `assembleRelease`.
   - Publica como artefactos descargables: los reportes de tests, el reporte de lint, el APK de debug, su SHA-256, y el APK de release sin firmar.

## Nota sobre `gradle-wrapper.jar`

Este binario **no se incluyó** en el repositorio. Los scripts `gradlew`/`gradlew.bat` están presentes y son funcionales, pero requieren que `gradle/wrapper/gradle-wrapper.jar` exista para arrancar. Dos formas de resolverlo:

- **Recomendada**: usar el workflow de CI incluido, que lo genera automáticamente con `gradle wrapper --gradle-version 8.7` antes de compilar.
- **Local**: si tienes Gradle instalado, ejecuta una vez `gradle wrapper --gradle-version 8.7` en la raíz del proyecto.

Se optó por esto en vez de intentar fabricar el binario a mano, porque un `.jar` generado sin poder verificar su integridad (sin acceso a `services.gradle.org` desde este entorno) sería peor que no incluirlo: el riesgo de entregar un binario corrupto o no verificable es mayor que pedir un paso adicional documentado.

## Próximos pasos sugeridos para obtener un Build Report completo y verificado

1. Hacer push de este repositorio a GitHub.
2. Revisar la pestaña **Actions**: el workflow `Android CI - EcoCiclos` se ejecuta automáticamente.
3. Si `testDebugUnitTest` o `lintDebug` fallan, revisar los artefactos `resultados-tests` y `reporte-lint` publicados, corregir, y volver a hacer push (regla 30 de MASTER_SPEC: leer el log, corregir, volver a ejecutar).
4. Una vez la ejecución sea verde, actualizar este documento con las cifras reales: número de tests aprobados/fallidos, resultado de lint, y el SHA-256 del APK generado — reemplazando esta sección, no añadiéndola sobre una afirmación de éxito no verificada.

## Riesgos conocidos a vigilar en la primera compilación real

Aunque el código fue revisado exhaustivamente a mano, estos son los puntos con mayor probabilidad de necesitar un ajuste menor en la primera compilación real (ninguno afecta a la arquitectura ni a la lógica de negocio):

- Versiones exactas de BOM de Compose / KSP / Room pueden requerir un pequeño ajuste de compatibilidad cruzada según la versión de Android Gradle Plugin que resuelva Gradle 8.7.
- El ícono de launcher solo incluye la variante adaptativa (`mipmap-anydpi-v26`); en API 24-25 el sistema mostrará un ícono por defecto hasta añadir los PNG heredados (ver `docs/MEMORIA_DESCRIPTIVA.md`, sección 10).
- Los tests de Room vía Robolectric (`data/*Test.kt`) requieren que el runner de CI descargue el jar de Robolectric para Android 34; si esa versión aún no está publicada en el momento de ejecutar, `Config(sdk = [34])` puede necesitar bajarse a `[33]` como fallback.
