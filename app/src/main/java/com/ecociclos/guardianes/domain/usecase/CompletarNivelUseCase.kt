package com.ecociclos.guardianes.domain.usecase

import com.ecociclos.guardianes.domain.model.Carta
import com.ecociclos.guardianes.domain.model.ConsecuenciasCompletado
import com.ecociclos.guardianes.domain.model.Insignia
import com.ecociclos.guardianes.domain.model.ResultadoNivel
import com.ecociclos.guardianes.domain.repository.EcoRepository

/**
 * Orquesta lo que ocurre cuando un niño termina un intento de nivel:
 * 1) persiste el resultado (Room, vía repository)
 * 2) recalcula el % del bioma dueño de ese nivel
 * 3) si el bioma llegó a 100%, evalúa insignias nuevas
 * 4) si el nivel se completó, intenta desbloquear su carta asociada
 *
 * Este use case es el punto único de "victoria" del juego: todo lo que
 * cambia de estado permanente pasa por aquí, nunca directamente desde la UI.
 */
class CompletarNivelUseCase(
    private val repository: EcoRepository,
    private val calcularProgresoBioma: CalcularProgresoBiomaUseCase = CalcularProgresoBiomaUseCase(),
    private val calcularProgresoGlobal: CalcularProgresoGlobalUseCase = CalcularProgresoGlobalUseCase(),
    private val desbloquearInsignia: DesbloquearInsigniaUseCase = DesbloquearInsigniaUseCase()
) {
    suspend operator fun invoke(resultado: ResultadoNivel): ConsecuenciasCompletado {
        repository.registrarResultadoNivel(resultado)

        var cartaDesbloqueada: Carta? = null
        var biomaCompletado = false
        var progresoBiomaPorcentaje = 0
        var insigniasNuevas: List<Insignia> = emptyList()

        if (resultado.completado) {
            val nivel = repository.obtenerNivel(resultado.nivelId)
            cartaDesbloqueada = repository.desbloquearCartaDeNivel(resultado.nivelId)

            if (nivel != null) {
                val nivelesDelBioma = repository.observarNivelesDeBioma(nivel.biomaId)
                // Nota: en el ViewModel se usa la versión Flow; aquí forzamos una lectura puntual
                // a través del repositorio para mantener el use case simple y testeable.
                val listaActual = repository.obtenerBioma(nivel.biomaId)
                progresoBiomaPorcentaje = listaActual?.porcentajeRestaurado ?: 0
                biomaCompletado = progresoBiomaPorcentaje >= 100

                if (biomaCompletado) {
                    repository.marcarBiomaDesbloqueado(nivel.biomaId)
                }

                val candidatas = repository.obtenerInsigniasCandidatas(nivel.biomaId)
                val progresoGlobal = repository.obtenerProgresoGlobal()
                insigniasNuevas = desbloquearInsignia(
                    candidatas = candidatas,
                    biomaCompletadoAhora = if (biomaCompletado) nivel.biomaId else null,
                    todosLosBiomasCompletados = progresoGlobal >= 100
                )
                if (insigniasNuevas.isNotEmpty()) {
                    repository.desbloquearInsignias(insigniasNuevas.map { it.id })
                }
            }
        }

        return ConsecuenciasCompletado(
            resultado = resultado,
            cartaDesbloqueada = cartaDesbloqueada,
            insigniasNuevas = insigniasNuevas,
            biomaCompletado = biomaCompletado,
            progresoBiomaPorcentaje = progresoBiomaPorcentaje
        )
    }
}
