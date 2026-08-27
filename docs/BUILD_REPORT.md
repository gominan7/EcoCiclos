# Build Report — EcoCiclos: Guardianes de la Materia

> Regla 37 de MASTER_SPEC: *"Si el entorno no puede compilar: COMPILACIÓN NO VERIFICADA. Nunca simules resultados."* Este documento cumple esa regla al pie de la letra, y se actualiza con cada ejecución real de CI conocida.

## Historial de ejecuciones reales en CI

### Ejecución 1 — `testDebugUnitTest` — ❌ FALLÓ

Reportada por el usuario tras subir el repositorio a GitHub y ejecutar el workflow. Fallo real, con log real:

```
> Task :app:processDebugResources FAILED
Android resource linking failed
com.ecociclos.guardianes.app-mergeDebugResources-56:/values/values.xml:130:
error: resource attr/?? (aka com.ecociclos.guardianes.debug:attr/??) not found.
error: failed linking references.
```

**Causa raíz**: `app/src/main/res/values/strings.xml` contenía `<string name="ecopedia_bloqueada">???</string>`. En XML de recursos de Android, un valor de `<string>` que **empieza** con `?` se interpreta como una referencia a un atributo de tema (`?attr/...` o `?android:attr/...`), no como texto literal, a menos que el `?` se escape. AAPT2 tomó `??` como el nombre del atributo buscado, no lo encontró, y falló el linking de recursos.

**Corrección aplicada**: se escapó el valor como `\?\?\?` (commit posterior a esta ejecución). Además, se auditó programáticamente **todo** el árbol `res/values*/*.xml` en busca del mismo patrón (cualquier `<string>` cuyo valor empiece por `?` o `@` sin escapar) — no se encontraron más casos. El único otro uso de `@` al inicio de un valor (`@color/hoja_profunda` en `themes.xml`) es un `<item>` de estilo, donde esa sintaxis es correcta y necesaria (referencia real a un color), no un error.

**Estado del resto del build en esa ejecución**: no llegó a ejecutarse `testDebugUnitTest` en sí — el fallo ocurrió antes, en `processDebugResources`, una tarea previa de la que depende la compilación de tests. Por lo tanto, ningún test corrió realmente en esta ejecución; no hay cifras de tests que reportar todavía.

### Ejecución 2 en adelante

Pendiente. Una vez el usuario vuelva a ejecutar el workflow (o `./gradlew testDebugUnitTest` localmente) con la corrección aplicada, este documento debe actualizarse con el resultado real: número de tests ejecutados/aprobados/fallidos, o el siguiente error real si aparece uno nuevo. Nunca se debe reemplazar esta sección con una afirmación de éxito no verificada.

## Estado de la compilación en el entorno de generación original

**COMPILACIÓN NO VERIFICADA LOCALMENTE** en el entorno donde se escribió el código por primera vez: sin SDK de Android, sin Gradle instalado, y sin acceso de red a `maven.google.com`/`services.gradle.org`. Ver más abajo el detalle completo de esa limitación — sigue vigente para cualquier iteración futura de desarrollo en ese mismo tipo de entorno.

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
