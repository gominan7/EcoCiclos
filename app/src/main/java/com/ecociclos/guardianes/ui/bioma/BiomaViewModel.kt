package com.ecociclos.guardianes.ui.bioma

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecociclos.guardianes.EcoCiclosApp
import com.ecociclos.guardianes.domain.model.Bioma
import com.ecociclos.guardianes.domain.model.Nivel
import com.ecociclos.guardianes.domain.repository.EcoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class BiomaUiState(
    val bioma: Bioma? = null,
    val niveles: List<Nivel> = emptyList(),
    val cargando: Boolean = true
)

class BiomaViewModel(
    private val repository: EcoRepository,
    private val biomaId: Int
) : ViewModel() {

    private val nivelesFlow = repository.observarNivelesDeBioma(biomaId)
    private val biomaFlow = repository.observarBiomas()

    val uiState: StateFlow<BiomaUiState> = combine(biomaFlow, nivelesFlow) { biomas, niveles ->
        BiomaUiState(
            bioma = biomas.find { it.id == biomaId },
            niveles = niveles,
            cargando = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BiomaUiState())

    companion object {
        fun crear(app: EcoCiclosApp, biomaId: Int) = BiomaViewModel(app.repository, biomaId)
    }
}
