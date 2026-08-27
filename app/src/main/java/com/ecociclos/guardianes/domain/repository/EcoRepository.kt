package com.ecociclos.guardianes.domain.repository

import com.ecociclos.guardianes.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * Puerto de dominio hacia la persistencia. La implementación real (Room) vive en
 * data/repository/EcoRepositoryImpl.kt. Ninguna Composable ni ViewModel debe
 * hablar con Room directamente (regla 22 de MASTER_SPEC).
 */
interface EcoRepository {

    // --- Biomas ---
    fun observarBiomas(): Flow<List<Bioma>>
    suspend fun obtenerBioma(biomaId: Int): Bioma?
    suspend fun marcarBiomaDesbloqueado(biomaId: Int)

    // --- Niveles ---
    fun observarNivelesDeBioma(biomaId: Int): Flow<List<Nivel>>
    suspend fun obtenerNivel(nivelId: Int): Nivel?
    suspend fun obtenerPuzzleDeNivel(nivelId: Int): NivelPuzzle?

    // --- Progreso ---
    suspend fun registrarResultadoNivel(resultado: ResultadoNivel)
    suspend fun obtenerProgresoGlobal(): Int // 0..100

    // --- Cartas ---
    fun observarCartas(): Flow<List<Carta>>
    suspend fun desbloquearCartaDeNivel(nivelId: Int): Carta?

    // --- Insignias ---
    fun observarInsignias(): Flow<List<Insignia>>
    suspend fun obtenerInsigniasCandidatas(biomaId: Int?): List<Insignia>
    suspend fun desbloquearInsignias(ids: List<Int>)

    // --- Perfil ---
    fun observarPerfil(): Flow<Perfil>
    suspend fun actualizarPerfil(alias: String, avatarId: Int)
    suspend fun sumarXp(cantidad: Int)
}
