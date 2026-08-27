package com.ecociclos.guardianes.domain.model

/**
 * Un bioma dañado que el niño debe restaurar. Cada bioma se centra en un ciclo
 * principal de la materia (ver PROMPT_ESPECIFICO sección 6).
 */
data class Bioma(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val tipoCiclo: TipoCiclo,
    val orden: Int,
    val colorHex: String,
    val estado: EstadoModulo,
    val porcentajeRestaurado: Int, // 0..100, derivado de niveles completados
    val totalNiveles: Int,
    val nivelesCompletados: Int
)
