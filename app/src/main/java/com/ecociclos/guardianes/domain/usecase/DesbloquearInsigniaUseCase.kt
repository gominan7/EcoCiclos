package com.ecociclos.guardianes.domain.usecase

import com.ecociclos.guardianes.domain.model.Insignia

/**
 * Determina qué insignias nuevas se desbloquean. Una insignia de bioma se
 * otorga cuando ese bioma llega a 100%; una insignia global ("Guardián del
 * Planeta") se otorga cuando TODOS los biomas están al 100%.
 */
class DesbloquearInsigniaUseCase {

    operator fun invoke(
        candidatas: List<Insignia>,
        biomaCompletadoAhora: Int?,
        todosLosBiomasCompletados: Boolean
    ): List<Insignia> {
        return candidatas.filter { insignia ->
            if (insignia.obtenida) return@filter false
            when (insignia.biomaId) {
                null -> todosLosBiomasCompletados
                biomaCompletadoAhora -> true
                else -> false
            }
        }
    }
}
