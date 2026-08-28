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

### Ejecución 2 — `testDebugUnitTest` — ❌ FALLÓ (avanzó más: pasó `processDebugResources`, falló en `compileDebugKotlin`)

Reportada por el usuario. La corrección de la Ejecución 1 funcionó: `processDebugResources` pasó esta vez. El build avanzó mucho más lejos y llegó a compilar Kotlin, donde encontró errores reales de compilación:

```
> Task :app:compileDebugKotlin FAILED
e: .../ui/components/Personajes.kt:31:19 Type 'State<Float>' has no method 'getValue(...)' and thus it cannot serve as a delegate
e: .../ui/components/Personajes.kt:79:19 Type 'State<Float>' has no method 'getValue(...)' and thus it cannot serve as a delegate
e: .../ui/components/ProgresoViews.kt:27:20 Type 'State<Float>' has no method 'getValue(...)' and thus it cannot serve as a delegate
e: .../ui/components/ProgresoViews.kt:38:40 Overload resolution ambiguity (times)
e: .../ui/ecopedia/EcopediaScreen.kt:33:20 Type 'MutableState<PestanaEcopedia>' has no method 'setValue(...)'
e: .../ui/bioma/BiomaDetailScreen.kt:45:13 This material API is experimental
e: .../ui/ecopedia/EcopediaScreen.kt:37:13 This material API is experimental
e: .../ui/globe/GlobeDashboardScreen.kt:39:13 This material API is experimental
e: .../ui/perfil/PerfilScreen.kt:29:13 This material API is experimental
e: .../ui/simulator/SimuladorScreen.kt:37:13 This material API is experimental
```

**Causa raíz 1 — delegados `by` sin `getValue`/`setValue` importados.** En Kotlin, `val x by someState` y `var x by someMutableState` son azúcar sintáctica que requiere que `getValue`/`setValue` (funciones de extensión de `androidx.compose.runtime`) estén **explícitamente importadas** — no basta con que el tipo `State<T>` esté en el classpath. `Personajes.kt` y `ProgresoViews.kt` importaban `androidx.compose.runtime.Composable` de forma explícita pero no el wildcard `androidx.compose.runtime.*` ni `getValue` suelto, así que el compilador no encontraba el operador de delegación. `EcopediaScreen.kt` sí importaba `getValue` pero le faltaba `setValue` (usa `var pestana by remember { mutableStateOf(...) }`, que necesita ambos). El error de "overload resolution ambiguity" en `ProgresoViews.kt:38` era un **efecto en cascada** del mismo problema: al no resolverse el delegado, `animado` quedaba con un tipo de error, y `size.width * animado` no podía elegir un overload de `times`. No fue un bug independiente.

**Corrección aplicada**: se añadió `import androidx.compose.runtime.getValue` a `Personajes.kt` y `ProgresoViews.kt`, y `import androidx.compose.runtime.setValue` a `EcopediaScreen.kt`. Se auditó **todo** `app/src/main/java` y `app/src/test/java` programáticamente (detectando cada `val ... by ...` / `var ... by ...` y verificando que el archivo tuviera el import necesario, explícito o vía wildcard `androidx.compose.runtime.*`) — no quedó ningún caso pendiente.

**Causa raíz 2 — `TopAppBar` de Material 3 es una API experimental.** `TopAppBar` (usado dentro de `Scaffold(topBar = { ... })`) está anotado `@ExperimentalMaterial3Api`, una anotación `@RequiresOptIn`: usarla sin optar explícitamente es un **error de compilación**, no solo un warning. Afectaba a los 5 Composables de pantalla que usan `Scaffold` + `TopAppBar`: `BiomaDetailScreen`, `EcopediaScreen`, `GlobeDashboardScreen`, `PerfilScreen`, `SimuladorScreen`.

**Corrección aplicada**: se añadió `@OptIn(ExperimentalMaterial3Api::class)` sobre cada una de esas 5 funciones `@Composable`. Se revisó el resto del código en busca de otras APIs experimentales de Material 3 (`ModalBottomSheet`, `ExposedDropdownMenu`, variantes de `TopAppBar`, etc.) — no se encontró ninguna otra.

**Extra (no bloqueante)**: KSP avisó que `AppDatabase` tenía `exportSchema = true` sin `room.schemaLocation` configurado. Se cambió a `exportSchema = false` (no se necesitan migraciones exportadas para esta versión del proyecto) para eliminar el warning.

### Ejecución 3 — Prueba manual del APK en un dispositivo Android real — ❌ Encontró bugs de legibilidad reales

El usuario compiló el APK vía CI exitosamente, lo instaló en su teléfono y lo jugó de verdad. Reportó, con capturas de pantalla:

1. El onboarding (pantallas de bienvenida) parecía mostrar solo íconos, sin título ni texto de instrucciones.
2. En el Globo/Dashboard, texto poco legible ("letras blancas sobre fondo blanco").
3. En la lista de niveles de un bioma, el título del primer nivel ("El ciclo del agua roto") casi no se podía leer.
4. Dentro del puzzle Restaurador, al arrastrar los elementos a su destino, el juego decía "sigue intentando" repetidamente.
5. En general, "las letras no se observan" en varias pantallas — problema crítico para una app pensada para niños de 8-12 años.

**Causa raíz (una sola, para los puntos 1, 2, 3 y 5): el teléfono del usuario tenía el modo oscuro del sistema activado.** `EcoCiclosTheme` seguía `isSystemInDarkTheme()` y cambiaba a un esquema de color oscuro (texto claro/blanco por defecto), pero **ninguno de los componentes con fondo de color fijo** (tarjetas de bioma, tarjetas de nivel, tarjetas del Restaurador/Enrutador, el fondo del onboarding) se diseñó ni se probó contra ese escenario: siguen usando colores de fondo claros codificados directamente. El resultado: texto blanco heredado del tema oscuro, sobre tarjetas con fondo claro fijo — invisible. El modo oscuro nunca se implementó de verdad para esos componentes; solo el `MaterialTheme` raíz cambiaba.

Además, por la misma razón, los íconos de la barra de estado del sistema (reloj, batería) aparecían claros sobre el fondo claro de la app — visibles en la imagen 1 del reporte del usuario.

**Corrección aplicada**:
- `ui/theme/Theme.kt`: se eliminó el esquema oscuro y el parámetro `darkTheme`; `EcoCiclosTheme` ahora usa **siempre** el esquema claro, con un comentario explicando por qué (decisión de alcance documentada, no un descuido).
- `MainActivity.kt`: `enableEdgeToEdge()` ahora fija explícitamente `statusBarStyle`/`navigationBarStyle` a `SystemBarStyle.light(...)`, forzando íconos oscuros del sistema para que tengan contraste sobre el fondo claro de la app.

**Punto 4 (drag & drop "sigue intentando")**: se revisó a fondo la lógica de arrastre y detección de colisión en `RestauradorCiclo.kt` (aritmética de `origenEnRaiz + offset + mitad del tamaño` contra los `Rect` de cada destino) y no se encontró un bug estructural — la lógica es consistente. La explicación más probable, confirmada por la propia imagen 5 del usuario, es que **tanto las etiquetas de origen ("Sol", "Vapor de agua"...) como las de destino ("Lago", "Nube"...) eran invisibles por el mismo bug de tema oscuro**, así que el usuario arrastraba sin poder leer qué conectaba con qué. Se resuelve con la misma corrección de tema. **Pendiente de confirmación**: si tras esta corrección el problema de "sigue intentando" persiste con las etiquetas ya visibles, es un bug distinto y real que habría que investigar por separado.

**Alcance de la corrección de modo oscuro**: se optó por bloquear el tema a claro (en vez de auditar y corregir manualmente el color de cada `Text` en cada tarjeta) porque es la corrección más simple y con menor riesgo de dejar algún caso sin cubrir, dado que todo el sistema de ilustración de la app se diseñó contra un fondo claro. Soporte real de modo oscuro queda fuera de alcance de esta versión y debería ser un rediseño explícito, no un cambio incremental.

### Ejecución 4 — Prueba manual del APK con la corrección de tema claro — ⚠️ Reporte de producto real (no un bug de compilación)

El usuario confirmó que el APK ya se ve legible tras la corrección de la Ejecución 3. Reportó, con capturas de pantalla, un problema distinto: *"al momento de seleccionar un alias y un avatar y guardo, luego salgo del aplicativo y no puedo observar el avatar ni el alias reflejando esos ajustes"*.

**Diagnóstico**: se revisó de punta a punta el camino de guardado — `PerfilScreen` → `PerfilViewModel.actualizar()` → `EcoRepositoryImpl.actualizarPerfil()` → `PerfilDao.upsert()` (Room) → `PerfilDao.observar()` (Flow) → de vuelta al `StateFlow` del ViewModel. La lógica es correcta y ya estaba cubierta por `PerfilDaoTest`; no se encontró un bug de persistencia.

**El bug real**: la pantalla del Globo (el Dashboard, la primera pantalla que se ve después de la de Perfil) **nunca leía el perfil guardado**. El ícono superior derecho era un ícono genérico de "Persona" fijo, sin importar qué avatar hubiera elegido el niño, y no había ningún saludo con el alias en ningún lugar. El `GlobeUiState` sí traía el `perfil` cargado desde Room reactivamente — el bug era que la UI simplemente no lo mostraba en ninguna parte. Desde la perspectiva del usuario, esto es indistinguible de "no se guardó": abrías la app y no veías ningún rastro de tu elección en ningún lado excepto volviendo a entrar manualmente a Perfil.

**Corrección aplicada**:
- `GlobeDashboardScreen.kt`: el ícono de perfil en la barra superior ahora es el `AvatarIlustrado` real guardado (no un ícono genérico), y se agregó un saludo `"¡Hola, {alias}!"` en la parte superior del Dashboard. Ambos se actualizan automáticamente porque ya estaban conectados al `Flow` de Room — solo faltaba usarlos en la UI.
- Se añadieron 4 tests nuevos a `EcoRepositoryImplTest` (`data/EcoRepositoryImplTest.kt`) que verifican de punta a punta, contra Room real (en memoria): el valor semilla por defecto, que `actualizarPerfil` persiste y se relee correctamente, que actualizar dos veces no duplica filas (queda el último valor), y que `sumarXp` no resetea el alias/avatar ya guardados. Esto deja el camino de persistencia del perfil verificado con pruebas automatizadas, no solo por inspección de código.

**Pendiente de confirmación por el usuario**: con esta corrección, al reabrir la app después de guardar un alias/avatar, el Dashboard debería mostrar el avatar y el saludo inmediatamente. Falta confirmar en un dispositivo real.

### Ejecución 5 — `compileDebugKotlin` — ❌ FALLÓ (error simple de un solo carácter conceptual)

```
e: .../ui/globe/GlobeDashboardScreen.kt:86:69 Cannot find a parameter with this name: top
```

**Causa raíz**: en el saludo con el alias añadido en la corrección anterior, se escribió `Modifier.padding(horizontal = 20.dp, top = 16.dp)`. `Modifier.padding()` tiene dos "familias" de sobrecarga que no se pueden mezclar: `padding(horizontal, vertical)` **o** `padding(start, top, end, bottom)` — nunca `horizontal` junto con `top`.

**Corrección aplicada**: cambiado a `Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp)`, usando consistentemente la familia `start/top/end/bottom`. Se escaneó programáticamente el resto de `app/src/main/java` en busca del mismo patrón de mezcla — no se encontró ningún otro caso.

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
