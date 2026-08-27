package com.ecociclos.guardianes.domain

import com.ecociclos.guardianes.domain.model.*
import com.ecociclos.guardianes.domain.repository.EcoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Doble de prueba en memoria para [EcoRepository]. Permite guionar los datos
 * que devuelve cada método y registra las llamadas de escritura, para que los
 * tests de use cases (ej. CompletarNivelUseCase) no dependan de Room ni de
 * Robolectric.
 */
class FakeEcoRepository : EcoRepository {

    var nivelAsociado: Nivel? = null
    var cartaParaDesbloquear: Carta? = null
    var biomaResultante: Bioma? = null
    var insigniasCandidatas: List<Insignia> = emptyList()
    var progresoGlobalResultante: Int = 0

    val resultadosRegistrados = mutableListOf<ResultadoNivel>()
    val biomasDesbloqueados = mutableListOf<Int>()
    val insigniasDesbloqueadasLlamadas = mutableListOf<List<Int>>()

    override fun observarBiomas(): Flow<List<Bioma>> = flowOf(emptyList())
    override suspend fun obtenerBioma(biomaId: Int): Bioma? = biomaResultante
    override suspend fun marcarBiomaDesbloqueado(biomaId: Int) { biomasDesbloqueados.add(biomaId) }

    override fun observarNivelesDeBioma(biomaId: Int): Flow<List<Nivel>> = flowOf(emptyList())
    override suspend fun obtenerNivel(nivelId: Int): Nivel? = nivelAsociado
    override suspend fun obtenerPuzzleDeNivel(nivelId: Int): NivelPuzzle? = null

    override suspend fun registrarResultadoNivel(resultado: ResultadoNivel) {
        resultadosRegistrados.add(resultado)
    }

    override suspend fun obtenerProgresoGlobal(): Int = progresoGlobalResultante

    override fun observarCartas(): Flow<List<Carta>> = flowOf(emptyList())
    override suspend fun desbloquearCartaDeNivel(nivelId: Int): Carta? = cartaParaDesbloquear

    override fun observarInsignias(): Flow<List<Insignia>> = flowOf(emptyList())
    override suspend fun obtenerInsigniasCandidatas(biomaId: Int?): List<Insignia> = insigniasCandidatas
    override suspend fun desbloquearInsignias(ids: List<Int>) { insigniasDesbloqueadasLlamadas.add(ids) }

    override fun observarPerfil(): Flow<Perfil> = flowOf(Perfil(0, "Guardián", 1, 0))
    override suspend fun actualizarPerfil(alias: String, avatarId: Int) {}
    override suspend fun sumarXp(cantidad: Int) {}
}
