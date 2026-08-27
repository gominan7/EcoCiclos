package com.ecociclos.guardianes.data

import com.ecociclos.guardianes.data.local.entity.BiomaEntity
import com.ecociclos.guardianes.data.local.entity.CartaDesbloqueadaEntity
import com.ecociclos.guardianes.data.local.entity.CartaEntity
import com.ecociclos.guardianes.data.local.entity.InsigniaDesbloqueadaEntity
import com.ecociclos.guardianes.data.local.entity.InsigniaEntity
import com.ecociclos.guardianes.data.local.entity.NivelEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ColeccionablesDaoTest : RoomTestBase() {

    private suspend fun sembrarBiomaYNivel() {
        db.biomaDao().insertarTodos(listOf(BiomaEntity(1, "Bosque", "d", "AGUA", 1, "#000", true)))
        db.nivelDao().insertarTodos(listOf(NivelEntity(101, 1, 1, "N1", "RESTAURADOR", 1, "")))
    }

    // --- Cartas ---

    @Test
    fun `una carta recien insertada no esta desbloqueada por defecto`() = runTest {
        sembrarBiomaYNivel()
        db.cartaDao().insertarTodas(listOf(CartaEntity(101, "Sol", "ELEMENTO", 1, "desc", 101)))

        assertThat(db.cartaDesbloqueadaDao().estaDesbloqueada(101)).isFalse()
    }

    @Test
    fun `desbloquear una carta la marca como desbloqueada`() = runTest {
        sembrarBiomaYNivel()
        db.cartaDao().insertarTodas(listOf(CartaEntity(101, "Sol", "ELEMENTO", 1, "desc", 101)))

        db.cartaDesbloqueadaDao().insertar(CartaDesbloqueadaEntity(101, 12345L))

        assertThat(db.cartaDesbloqueadaDao().estaDesbloqueada(101)).isTrue()
        assertThat(db.cartaDesbloqueadaDao().observarIdsDesbloqueadas().first()).containsExactly(101)
    }

    @Test
    fun `obtener carta por nivel de desbloqueo inexistente devuelve null`() = runTest {
        sembrarBiomaYNivel()
        assertThat(db.cartaDao().obtenerPorNivelDesbloqueo(999)).isNull()
    }

    @Test
    fun `desbloquear la misma carta dos veces no falla ni duplica (doble toque)`() = runTest {
        sembrarBiomaYNivel()
        db.cartaDao().insertarTodas(listOf(CartaEntity(101, "Sol", "ELEMENTO", 1, "desc", 101)))

        db.cartaDesbloqueadaDao().insertar(CartaDesbloqueadaEntity(101, 1000L))
        db.cartaDesbloqueadaDao().insertar(CartaDesbloqueadaEntity(101, 2000L))

        assertThat(db.cartaDesbloqueadaDao().observarIdsDesbloqueadas().first()).hasSize(1)
    }

    // --- Insignias ---

    @Test
    fun `insignias candidatas incluyen las del bioma y las globales`() = runTest {
        db.insigniaDao().insertarTodas(listOf(
            InsigniaEntity(1, "Guardián del Agua", "d", 1, "bioma_1_100"),
            InsigniaEntity(2, "Maestro del Carbono", "d", 2, "bioma_2_100"),
            InsigniaEntity(4, "Guardián del Planeta", "d", null, "planeta_100")
        ))

        val candidatas = db.insigniaDao().obtenerCandidatas(1)

        assertThat(candidatas.map { it.id }).containsExactly(1, 4)
    }

    @Test
    fun `desbloquear varias insignias a la vez las persiste todas`() = runTest {
        db.insigniaDao().insertarTodas(listOf(InsigniaEntity(1, "A", "d", 1, "c"), InsigniaEntity(4, "B", "d", null, "c")))

        db.insigniaDesbloqueadaDao().insertarTodas(listOf(
            InsigniaDesbloqueadaEntity(1, 1000L),
            InsigniaDesbloqueadaEntity(4, 1000L)
        ))

        assertThat(db.insigniaDesbloqueadaDao().obtenerIdsUnaVez()).containsExactly(1, 4)
    }
}
