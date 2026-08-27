# EcoCiclos: Guardianes de la Materia — reglas ProGuard/R8
# La app es 100% offline, sin reflexión dinámica compleja fuera de Room/Compose,
# por lo que las reglas por defecto de Android + estas específicas son suficientes.

# Room genera código en tiempo de compilación (KSP); no requiere reglas especiales
# más allá de mantener las entidades para que el esquema no se ofusque incorrectamente.
-keep class com.ecociclos.guardianes.data.local.entity.** { *; }

# Mantener nombres de enums de dominio usados en Room TypeConverters
-keepclassmembers enum com.ecociclos.guardianes.domain.model.** { *; }
