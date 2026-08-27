package com.ecociclos.guardianes.domain.model

/** Resultado de validar UNA conexión individual dentro de un puzzle. */
data class ResultadoConexion(
    val claveOrigen: String,
    val correcta: Boolean,
    val explicacion: String
)

/** Resultado agregado de intentar completar un nivel completo. */
data class ResultadoNivel(
    val nivelId: Int,
    val totalConexiones: Int,
    val conexionesCorrectas: Int,
    val errores: Int,
    val completado: Boolean,
    val estrellas: Int
)

/** Resultado de completar un nivel: qué se desbloqueó como consecuencia. */
data class ConsecuenciasCompletado(
    val resultado: ResultadoNivel,
    val cartaDesbloqueada: Carta?,
    val insigniasNuevas: List<Insignia>,
    val biomaCompletado: Boolean,
    val progresoBiomaPorcentaje: Int
)
