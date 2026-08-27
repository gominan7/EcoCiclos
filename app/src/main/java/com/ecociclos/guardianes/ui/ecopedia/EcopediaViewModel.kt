package com.ecociclos.guardianes.ui.ecopedia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecociclos.guardianes.EcoCiclosApp
import com.ecociclos.guardianes.domain.model.Carta
import com.ecociclos.guardianes.domain.model.Insignia
import com.ecociclos.guardianes.domain.repository.EcoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class EcopediaUiState(
    val cartas: List<Carta> = emptyList(),
    val insignias: List<Insignia> = emptyList(),
    val cargando: Boolean = true
)

class EcopediaViewModel(repository: EcoRepository) : ViewModel() {
    val uiState: StateFlow<EcopediaUiState> = combine(
        repository.observarCartas(),
        repository.observarInsignias()
    ) { cartas, insignias ->
        EcopediaUiState(cartas = cartas, insignias = insignias, cargando = false)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), EcopediaUiState())

    companion object {
        fun crear(app: EcoCiclosApp) = EcopediaViewModel(app.repository)
    }
}
