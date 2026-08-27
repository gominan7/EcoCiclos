package com.ecociclos.guardianes.domain.usecase

import com.ecociclos.guardianes.domain.model.EstadoModulo
import com.ecociclos.guardianes.domain.model.Nivel

/** Elige el próximo nivel jugable: el primero disponible que no esté ya dominado. */
class ObtenerSiguienteNivelUseCase {
    operator fun invoke(niveles: List<Nivel>): Nivel? {
        return niveles.sortedBy { it.orden }.firstOrNull {
            it.estado == EstadoModulo.DISPONIBLE || it.estado == EstadoModulo.INICIADO
        }
    }
}
