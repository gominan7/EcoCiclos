package com.ecociclos.guardianes.domain.usecase

import com.ecociclos.guardianes.domain.model.VariableLab

/** Severidad del efecto observado en el Laboratorio de Reacciones. */
enum class SeveridadLab { NORMAL, ALERTA, CRITICO }

data class EfectoLaboratorio(
    val variable: VariableLab,
    val valorActual: Int,
    val severidad: SeveridadLab,
    val mensaje: String,
    val magnitudEfecto: Float // 0f..1f, para animar (ej. % de hielo derretido)
)

/**
 * Calcula el efecto visual/textual real a partir del valor elegido por el niño
 * para una variable (ej. temperatura global) y el umbral crítico configurado
 * en el nivel. No es un cambio de texto por botón: la magnitud se deriva
 * matemáticamente de la distancia al umbral.
 */
class EvaluarLaboratorioUseCase {

    operator fun invoke(variable: VariableLab, valorActual: Int): EfectoLaboratorio {
        val valorAcotado = valorActual.coerceIn(variable.valorMin, variable.valorMax)
        val rango = (variable.valorMax - variable.valorMin).coerceAtLeast(1)
        val magnitud = (valorAcotado - variable.valorMin).toFloat() / rango

        val severidad = when {
            valorAcotado >= variable.umbralCritico -> SeveridadLab.CRITICO
            valorAcotado >= variable.umbralCritico - (rango * 0.2f) -> SeveridadLab.ALERTA
            else -> SeveridadLab.NORMAL
        }

        val mensaje = if (severidad == SeveridadLab.CRITICO) variable.mensajeCritico else variable.mensajeNormal

        return EfectoLaboratorio(
            variable = variable,
            valorActual = valorAcotado,
            severidad = severidad,
            mensaje = mensaje,
            magnitudEfecto = magnitud
        )
    }
}
