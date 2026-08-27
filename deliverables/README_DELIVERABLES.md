# deliverables/ — EcoCiclos v1.0.0

Contenido de esta carpeta, siguiendo la regla 34 de MASTER_SPEC. Nota: por
convención, los artefactos de build (el .zip del código fuente empaquetado y
el .apk) no se versionan dentro del propio repositorio — se generan y
entregan por separado — para evitar que el paquete fuente se contenga
recursivamente a sí mismo.

| Archivo | Estado |
|---|---|
| `MEMORIA_DESCRIPTIVA.pdf` | ✅ Incluido — generado desde `docs/MEMORIA_DESCRIPTIVA.md` |
| `MANUAL_USUARIO.pdf` | ✅ Incluido — generado desde `docs/MANUAL_USUARIO.md` |
| `MANUAL_TECNICO.pdf` | ✅ Incluido — generado desde `docs/MANUAL_TECNICO.md` |
| `EcoCiclos-v1.0.0-source.zip` | Entregado junto a este repositorio, no dentro de él (ver mensaje de entrega) |
| `EcoCiclos-v1.0.0.apk` | ❌ NO incluido — este entorno no tiene SDK de Android ni acceso a `maven.google.com`/`services.gradle.org`, así que no fue posible compilar un APK real. Generarlo simulando su contenido violaría la regla de honestidad de MASTER_SPEC (sección 37). |

## Cómo obtener el APK real

1. Sube este repositorio a GitHub.
2. El workflow `.github/workflows/android.yml` compila automáticamente `assembleDebug` y `assembleRelease` en un runner con SDK completo.
3. Descarga el APK desde **Actions → (tu ejecución) → Artifacts → EcoCiclos-debug-apk**.
4. Calcula su SHA-256 (`sha256sum EcoCiclos-v1.0.0.apk`) y actualiza `docs/BUILD_REPORT.md` con los datos reales de esa ejecución.
