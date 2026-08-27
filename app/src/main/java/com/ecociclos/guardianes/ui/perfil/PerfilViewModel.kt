package com.ecociclos.guardianes.ui.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecociclos.guardianes.EcoCiclosApp
import com.ecociclos.guardianes.domain.model.Perfil
import com.ecociclos.guardianes.domain.repository.EcoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PerfilViewModel(private val repository: EcoRepository) : ViewModel() {

    val perfil: StateFlow<Perfil?> = repository.observarPerfil()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun actualizar(alias: String, avatarId: Int) {
        val aliasSeguro = alias.trim().ifBlank { "Guardián" }.take(16)
        viewModelScope.launch { repository.actualizarPerfil(aliasSeguro, avatarId) }
    }

    companion object {
        fun crear(app: EcoCiclosApp) = PerfilViewModel(app.repository)
        val AVATARES_DISPONIBLES = 1..8
    }
}
