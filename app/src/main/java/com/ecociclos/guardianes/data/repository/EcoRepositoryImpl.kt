package com.ecociclos.guardianes.data.repository

import com.ecociclos.guardianes.data.local.AppDatabase
import com.ecociclos.guardianes.data.local.entity.BiomaEntity
import com.ecociclos.guardianes.data.local.entity.CartaDesbloqueadaEntity
import com.ecociclos.guardianes.data.local.entity.InsigniaDesbloqueadaEntity
import com.ecociclos.guardianes.data.local.entity.NivelEntity
import com.ecociclos.guardianes.data.local.entity.ProgresoNivelEntity
import com.ecociclos.guardianes.data.local.seed.SeedData
import com.ecociclos.guardianes.domain.model.*
import com.ecociclos.guardianes.domain.repository.EcoRepository
import com.ecociclos.guardianes.domain.usecase.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class EcoRepositoryImpl(
    private val db: AppDatabase,
    private val calcularProgresoBioma: CalcularProgresoBiomaUseCase = CalcularProgresoBiomaUseCase(),
    private val calcularEstadoBiomas: CalcularEstadoBiomasUseCase = CalcularEstadoBiomasUseCase(),
    private val calcularEstadoNiveles: CalcularEstadoNivelesUseCase = CalcularEstadoNivelesUseCase(),
    private val calcularProgresoGlobal: CalcularProgresoGlobalUseCase = CalcularProgresoGlobalUseCase()
) : EcoRepository {

    // Los niveles semilla son estáticos tras la instalación: se cachean en memoria
    // para no repetir la misma consulta en cada emisión de Flow.
    private var nivelesCache: List<NivelEntity>? = null
    private suspend fun todosLosNiveles(): List<NivelEntity> =
        nivelesCache ?: db.nivelDao().obtenerTodos().also { nivelesCache = it }

    // ---------------------------------------------------------------- Biomas

    override fun observarBiomas(): Flow<List<Bioma>> =
        combine(db.biomaDao().observarTodos(), db.progresoNivelDao().observarTodos()) { biomasEnt, progresoEnt ->
            construirBiomasDominio(biomasEnt, todosLosNiveles(), progresoEnt)
        }

    override suspend fun obtenerBioma(biomaId: Int): Bioma? {
        val biomasEnt = db.biomaDao().observarTodos().first()
        val progresoEnt = db.progresoNivelDao().obtenerTodos()
        return construirBiomasDominio(biomasEnt, todosLosNiveles(), progresoEnt).find { it.id == biomaId }
    }

    override suspend fun marcarBiomaDesbloqueado(biomaId: Int) {
        db.biomaDao().actualizarDesbloqueado(biomaId, true)
    }

    private fun construirBiomasDominio(
        biomasEnt: List<BiomaEntity>,
        nivelesEnt: List<NivelEntity>,
        progresoEnt: List<ProgresoNivelEntity>
    ): List<Bioma> {
        val progresoPorNivel = progresoEnt.associateBy { it.nivelId }

        val porcentajePorBioma = biomasEnt.associate { bioma ->
            val nivelesDominio = nivelesEnt.filter { it.biomaId == bioma.id }.map { n ->
                val p = progresoPorNivel[n.id]
                n.aDominio(
                    estado = if (p?.completado == true) EstadoModulo.COMPLETADO else EstadoModulo.DISPONIBLE,
                    estrellas = p?.estrellas ?: 0,
                    intentos = p?.intentos ?: 0
                )
            }
            bioma.id to calcularProgresoBioma(nivelesDominio)
        }

        val baseInfos = biomasEnt.map { b ->
            BiomaBaseInfo(id = b.id, orden = b.orden, porcentajeRestaurado = porcentajePorBioma[b.id] ?: 0)
        }
        val estadosPorId = calcularEstadoBiomas(baseInfos).associateBy { it.id }

        return biomasEnt.map { b ->
            val nivelesDelBioma = nivelesEnt.filter { it.biomaId == b.id }
            val completados = nivelesDelBioma.count { progresoPorNivel[it.id]?.completado == true }
            b.aDominio(
                estado = estadosPorId[b.id]?.estadoCalculado ?: EstadoModulo.BLOQUEADO,
                porcentaje = porcentajePorBioma[b.id] ?: 0,
                totalNiveles = nivelesDelBioma.size,
                nivelesCompletados = completados
            )
        }
    }

    // ---------------------------------------------------------------- Niveles

    override fun observarNivelesDeBioma(biomaId: Int): Flow<List<Nivel>> =
        combine(db.nivelDao().observarPorBioma(biomaId), db.progresoNivelDao().observarTodos()) { nivelesEnt, progresoEnt ->
            construirNivelesDominio(nivelesEnt, progresoEnt)
        }

    private fun construirNivelesDominio(
        nivelesEnt: List<NivelEntity>,
        progresoEnt: List<ProgresoNivelEntity>
    ): List<Nivel> {
        val progresoPorNivel = progresoEnt.associateBy { it.nivelId }
        val baseInfos = nivelesEnt.map { NivelBaseInfo(id = it.id, orden = it.orden) }
        val progresoInfo = nivelesEnt.mapNotNull { n ->
            progresoPorNivel[n.id]?.let { p -> n.id to ProgresoNivelInfo(p.completado, p.estrellas, p.intentos) }
        }.toMap()

        val calculados = calcularEstadoNiveles(baseInfos, progresoInfo).associateBy { it.id }
        return nivelesEnt.map { n ->
            val c = calculados[n.id]
            n.aDominio(
                estado = c?.estadoCalculado ?: EstadoModulo.BLOQUEADO,
                estrellas = c?.estrellasCalculadas ?: 0,
                intentos = c?.intentosCalculados ?: 0
            )
        }
    }

    override suspend fun obtenerNivel(nivelId: Int): Nivel? {
        val entidad = db.nivelDao().obtenerPorId(nivelId) ?: return null
        val nivelesDelBioma = todosLosNiveles().filter { it.biomaId == entidad.biomaId }
        val progresoEnt = db.progresoNivelDao().obtenerTodos()
        return construirNivelesDominio(nivelesDelBioma, progresoEnt).find { it.id == nivelId }
    }

    override suspend fun obtenerPuzzleDeNivel(nivelId: Int): NivelPuzzle? {
        val nivel = obtenerNivel(nivelId) ?: return null
        return NivelPuzzle(
            nivel = nivel,
            elementos = db.elementoNivelDao().obtenerPorNivel(nivelId).map { it.aDominio() },
            nodosRuta = db.nodoRutaDao().obtenerPorNivel(nivelId).map { it.aDominio() },
            variables = db.variableLabDao().obtenerPorNivel(nivelId).map { it.aDominio() }
        )
    }

    // ---------------------------------------------------------------- Progreso

    override suspend fun registrarResultadoNivel(resultado: ResultadoNivel) {
        val actual = db.progresoNivelDao().obtenerPorNivel(resultado.nivelId)
        val intentosNuevos = (actual?.intentos ?: 0) + 1
        val mejorEstrellas = maxOf(actual?.estrellas ?: 0, resultado.estrellas)
        val completadoFinal = (actual?.completado == true) || resultado.completado

        db.progresoNivelDao().upsert(
            ProgresoNivelEntity(
                nivelId = resultado.nivelId,
                completado = completadoFinal,
                estrellas = mejorEstrellas,
                intentos = intentosNuevos,
                fechaCompletadoMillis = if (resultado.completado) System.currentTimeMillis() else actual?.fechaCompletadoMillis
            )
        )
    }

    override suspend fun obtenerProgresoGlobal(): Int {
        val biomasEnt = db.biomaDao().observarTodos().first()
        val progresoEnt = db.progresoNivelDao().obtenerTodos()
        val biomas = construirBiomasDominio(biomasEnt, todosLosNiveles(), progresoEnt)
        return calcularProgresoGlobal(biomas)
    }

    // ---------------------------------------------------------------- Cartas

    override fun observarCartas(): Flow<List<Carta>> =
        combine(db.cartaDao().observarTodas(), db.cartaDesbloqueadaDao().observarIdsDesbloqueadas()) { cartas, idsDesbloqueadas ->
            cartas.map { it.aDominio(desbloqueada = idsDesbloqueadas.contains(it.id)) }
        }

    override suspend fun desbloquearCartaDeNivel(nivelId: Int): Carta? {
        val cartaEnt = db.cartaDao().obtenerPorNivelDesbloqueo(nivelId) ?: return null
        val yaDesbloqueada = db.cartaDesbloqueadaDao().estaDesbloqueada(cartaEnt.id)
        if (!yaDesbloqueada) {
            db.cartaDesbloqueadaDao().insertar(CartaDesbloqueadaEntity(cartaEnt.id, System.currentTimeMillis()))
        }
        return cartaEnt.aDominio(desbloqueada = true)
    }

    // ---------------------------------------------------------------- Insignias

    override fun observarInsignias(): Flow<List<Insignia>> =
        combine(db.insigniaDao().observarTodas(), db.insigniaDesbloqueadaDao().observarIdsDesbloqueadas()) { insignias, ids ->
            insignias.map { it.aDominio(obtenida = ids.contains(it.id)) }
        }

    override suspend fun obtenerInsigniasCandidatas(biomaId: Int?): List<Insignia> {
        val candidatasEnt = db.insigniaDao().obtenerCandidatas(biomaId)
        val idsDesbloqueadas = db.insigniaDesbloqueadaDao().obtenerIdsUnaVez()
        return candidatasEnt.map { it.aDominio(obtenida = idsDesbloqueadas.contains(it.id)) }
    }

    override suspend fun desbloquearInsignias(ids: List<Int>) {
        if (ids.isEmpty()) return
        val ahora = System.currentTimeMillis()
        db.insigniaDesbloqueadaDao().insertarTodas(ids.map { InsigniaDesbloqueadaEntity(it, ahora) })
    }

    // ---------------------------------------------------------------- Perfil

    override fun observarPerfil(): Flow<Perfil> =
        db.perfilDao().observar().map { it?.aDominio() ?: SeedData.perfilInicial.aDominio() }

    override suspend fun actualizarPerfil(alias: String, avatarId: Int) {
        val actual = db.perfilDao().obtener() ?: SeedData.perfilInicial
        db.perfilDao().upsert(actual.copy(alias = alias, avatarId = avatarId))
    }

    override suspend fun sumarXp(cantidad: Int) {
        val actual = db.perfilDao().obtener() ?: SeedData.perfilInicial
        db.perfilDao().upsert(actual.copy(xpTotal = actual.xpTotal + cantidad))
    }
}
