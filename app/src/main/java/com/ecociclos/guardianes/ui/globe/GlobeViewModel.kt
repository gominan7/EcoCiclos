package com.ecociclos.guardianes.ui.globe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecociclos.guardianes.EcoCiclosApp
import com.ecociclos.guardianes.domain.model.Bioma
import com.ecociclos.guardianes.domain.model.Perfil
import com.ecociclos.guardianes.domain.repository.EcoRepository
import com.ecociclos.guardianes.domain.usecase.CalcularProgresoGlobalUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class GlobeUiState(
    val biomas: List<Bioma> = emptyList(),
    val perfil: Perfil? = null,
    val progresoGlobal: Int = 0,
    val cargando: Boolean = true
)

class GlobeViewModel(private val repository: EcoRepository) : ViewModel() {

    private val calcularProgresoGlobal = CalcularProgresoGlobalUseCase()

    val uiState: StateFlow<GlobeUiState> = combine(
        repository.observarBiomas(),
        repository.observarPerfil()
    ) { biomas, perfil ->
        GlobeUiState(
            biomas = biomas,
            perfil = perfil,
            progresoGlobal = calcularProgresoGlobal(biomas),
            cargando = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GlobeUiState()
    )

    companion object {
        fun crear(app: EcoCiclosApp) = GlobeViewModel(app.repository)
    }
}
