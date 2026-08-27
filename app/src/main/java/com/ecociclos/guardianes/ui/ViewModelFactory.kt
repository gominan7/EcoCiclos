package com.ecociclos.guardianes.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.ecociclos.guardianes.EcoCiclosApp

/**
 * Fábrica genérica y liviana: el proyecto no usa un framework de inyección de
 * dependencias (Hilt/Koin) porque su tamaño no lo justifica (regla 21/22 de
 * MASTER_SPEC pide MVVM + Repository, no exige un framework de DI concreto).
 * Cada ViewModel recibe las dependencias que necesita desde [EcoCiclosApp].
 */
class SimpleViewModelFactory(
    private val crear: (EcoCiclosApp) -> ViewModel
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val app = appInstanciaGlobal ?: error("EcoCiclosApp aún no se inicializó")
        return crear(app) as T
    }

    companion object {
        var appInstanciaGlobal: EcoCiclosApp? = null
    }
}

@Composable
fun appContainer(): EcoCiclosApp {
    val context = LocalContext.current.applicationContext as EcoCiclosApp
    SimpleViewModelFactory.appInstanciaGlobal = context
    return context
}

@Composable
inline fun <reified T : ViewModel> crearViewModel(noinline crear: (EcoCiclosApp) -> T): T {
    val app = appContainer()
    return viewModel(factory = SimpleViewModelFactory { crear(app) })
}
