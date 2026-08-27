package com.ecociclos.guardianes.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "ecociclos_prefs")

/**
 * Preferencias locales simples (regla 16 de MASTER_SPEC: el onboarding no debe
 * repetirse en cada apertura). Usa DataStore real, no una variable en memoria.
 */
class PreferenciasApp(private val context: Context) {

    private object Claves {
        val ONBOARDING_COMPLETADO = booleanPreferencesKey("onboarding_completado")
        val SONIDO_ACTIVADO = booleanPreferencesKey("sonido_activado")
        val HAPTICA_ACTIVADA = booleanPreferencesKey("haptica_activada")
    }

    val onboardingCompletado: Flow<Boolean> =
        context.dataStore.data.map { it[Claves.ONBOARDING_COMPLETADO] ?: false }

    val sonidoActivado: Flow<Boolean> =
        context.dataStore.data.map { it[Claves.SONIDO_ACTIVADO] ?: true }

    val hapticaActivada: Flow<Boolean> =
        context.dataStore.data.map { it[Claves.HAPTICA_ACTIVADA] ?: true }

    suspend fun marcarOnboardingCompletado() {
        context.dataStore.edit { it[Claves.ONBOARDING_COMPLETADO] = true }
    }

    suspend fun establecerSonido(activado: Boolean) {
        context.dataStore.edit { it[Claves.SONIDO_ACTIVADO] = activado }
    }

    suspend fun establecerHaptica(activada: Boolean) {
        context.dataStore.edit { it[Claves.HAPTICA_ACTIVADA] = activada }
    }
}
