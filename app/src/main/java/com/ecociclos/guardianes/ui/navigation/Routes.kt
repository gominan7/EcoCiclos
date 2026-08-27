package com.ecociclos.guardianes.ui.navigation

object Routes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val GLOBO = "globo"
    const val BIOMA_DETALLE = "bioma/{biomaId}"
    const val SIMULADOR = "simulador/{nivelId}"
    const val ECOPEDIA = "ecopedia"
    const val PERFIL = "perfil"

    fun biomaDetalle(biomaId: Int) = "bioma/$biomaId"
    fun simulador(nivelId: Int) = "simulador/$nivelId"
}
