package com.ecociclos.guardianes.domain.usecase

import com.ecociclos.guardianes.domain.model.Bioma
import com.ecociclos.guardianes.domain.model.EstadoModulo
import com.ecociclos.guardianes.domain.model.Nivel

/** Calcula el % restaurado de un bioma a partir de sus niveles reales (no un número fijo). */
class CalcularProgresoBiomaUseCase {
    operator fun invoke(niveles: List<Nivel>): Int {
        if (niveles.isEmpty()) return 0
        val completados = niveles.count {
            it.estado == EstadoModulo.COMPLETADO || it.estado == EstadoModulo.DOMINADO
        }
        return ((completados.toFloat() / niveles.size) * 100).toInt()
    }
}

/** Calcula el % del planeta restaurado como el promedio de todos los biomas. */
class CalcularProgresoGlobalUseCase {
    operator fun invoke(biomas: List<Bioma>): Int {
        if (biomas.isEmpty()) return 0
        val suma = biomas.sumOf { it.porcentajeRestaurado }
        return suma / biomas.size
    }
}

/**
 * Deriva el estado visual de cada nivel de un bioma a partir del progreso persistido:
 * el primer nivel siempre está disponible; los siguientes se desbloquean solo cuando
 * el anterior fue completado (regla 18 de MASTER_SPEC: progresión, no todo desde el inicio).
 */
class CalcularEstadoNivelesUseCase {
    operator fun invoke(
        nivelesBase: List<NivelBaseInfo>,
        progreso: Map<Int, ProgresoNivelInfo>
    ): List<NivelBaseInfo> {
        val ordenados = nivelesBase.sortedBy { it.orden }
        var anteriorCompletado = true
        return ordenados.map { base ->
            val p = progreso[base.id]
            val estado = when {
                p?.completado == true && p.estrellas == 3 -> EstadoModulo.DOMINADO
                p?.completado == true -> EstadoModulo.COMPLETADO
                anteriorCompletado -> EstadoModulo.DISPONIBLE
                else -> EstadoModulo.BLOQUEADO
            }
            anteriorCompletado = p?.completado == true
            base.copy(estadoCalculado = estado, estrellasCalculadas = p?.estrellas ?: 0, intentosCalculados = p?.intentos ?: 0)
        }
    }
}

/** DTO mínimo para no acoplar el use case a Room. */
data class NivelBaseInfo(
    val id: Int,
    val orden: Int,
    val estadoCalculado: EstadoModulo = EstadoModulo.BLOQUEADO,
    val estrellasCalculadas: Int = 0,
    val intentosCalculados: Int = 0
)

data class ProgresoNivelInfo(val completado: Boolean, val estrellas: Int, val intentos: Int)

/**
 * Deriva el estado de cada bioma: el primero siempre disponible; los siguientes
 * se desbloquean cuando el bioma anterior alcanza 100% de restauración.
 */
class CalcularEstadoBiomasUseCase {
    operator fun invoke(biomasOrdenados: List<BiomaBaseInfo>): List<BiomaBaseInfo> {
        val ordenados = biomasOrdenados.sortedBy { it.orden }
        var anteriorCompletado = true
        return ordenados.map { bioma ->
            val estado = when {
                bioma.porcentajeRestaurado >= 100 -> EstadoModulo.COMPLETADO
                anteriorCompletado && bioma.porcentajeRestaurado > 0 -> EstadoModulo.INICIADO
                anteriorCompletado -> EstadoModulo.DISPONIBLE
                else -> EstadoModulo.BLOQUEADO
            }
            anteriorCompletado = bioma.porcentajeRestaurado >= 100
            bioma.copy(estadoCalculado = estado)
        }
    }
}

data class BiomaBaseInfo(
    val id: Int,
    val orden: Int,
    val porcentajeRestaurado: Int,
    val estadoCalculado: EstadoModulo = EstadoModulo.BLOQUEADO
)
