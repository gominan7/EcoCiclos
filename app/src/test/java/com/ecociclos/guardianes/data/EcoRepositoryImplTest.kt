package com.ecociclos.guardianes.data

import com.ecociclos.guardianes.data.local.seed.DatabaseSeeder
import com.ecociclos.guardianes.data.repository.EcoRepositoryImpl
import com.ecociclos.guardianes.domain.model.EstadoModulo
import com.ecociclos.guardianes.domain.model.ResultadoNivel
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Tests de integración: base de datos Room real (en memoria) + repositorio +
 * use cases de progreso, verificando que las actualizaciones de StateFlow
 * reflejan correctamente cada acción del jugador (regla 7 de
 * PROMPT_ESPECIFICO).
 */
@RunWith(RobolectricTestRunner::class)
class EcoRepositoryImplTest : RoomTestBase() {

    private lateinit var repository: EcoRepositoryImpl

    @Before
    fun sembrarYPrepararRepositorio() = runTest {
        DatabaseSeeder(db).sembrarSiEsNecesario()
        repository = EcoRepositoryImpl(db)
    }

    private suspend fun completar(nivelId: Int) {
        repository.registrarResultadoNivel(
            ResultadoNivel(nivelId, totalConexiones = 1, conexionesCorrectas = 1, errores = 0, completado = true, estrellas = 3)
        )
    }

    @Test
    fun `completar un nivel actualiza el porcentaje restaurado del bioma`() = runTest {
        completar(101)

        val bioma = repository.obtenerBioma(1)

        assertThat(bioma?.porcentajeRestaurado).isEqualTo(20) // 1 de 5 niveles
        assertThat(bioma?.nivelesCompletados).isEqualTo(1)
    }

    @Test
    fun `completar los 5 niveles de un bioma lo deja al 100 por ciento y como COMPLETADO`() = runTest {
        listOf(101, 102, 103, 104, 105).forEach { completar(it) }

        val bioma = repository.obtenerBioma(1)

        assertThat(bioma?.porcentajeRestaurado).isEqualTo(100)
        assertThat(bioma?.estado).isEqualTo(EstadoModulo.COMPLETADO)
    }

    @Test
    fun `el segundo bioma se desbloquea solo cuando el primero llega a 100`() = runTest {
        val antes = repository.obtenerBioma(2)
        assertThat(antes?.estado).isEqualTo(EstadoModulo.BLOQUEADO)

        listOf(101, 102, 103, 104, 105).forEach { completar(it) }

        val despues = repository.obtenerBioma(2)
        assertThat(despues?.estado).isEqualTo(EstadoModulo.DISPONIBLE)
    }

    @Test
    fun `un nivel se desbloquea solo tras completar el anterior de su bioma`() = runTest {
        val antes = repository.obtenerNivel(102)
        assertThat(antes?.estado).isEqualTo(EstadoModulo.BLOQUEADO)

        completar(101)

        val despues = repository.obtenerNivel(102)
        assertThat(despues?.estado).isEqualTo(EstadoModulo.DISPONIBLE)
    }

    @Test
    fun `un intento fallido no completa el nivel pero cuenta como intento`() = runTest {
        repository.registrarResultadoNivel(
            ResultadoNivel(101, totalConexiones = 2, conexionesCorrectas = 1, errores = 1, completado = false, estrellas = 0)
        )

        val nivel = repository.obtenerNivel(101)

        assertThat(nivel?.estado).isEqualTo(EstadoModulo.DISPONIBLE)
        assertThat(nivel?.intentos).isEqualTo(1)
    }

    @Test
    fun `desbloquear la carta de un nivel es idempotente`() = runTest {
        repository.desbloquearCartaDeNivel(101)
        repository.desbloquearCartaDeNivel(101)

        val cartas = repository.observarCartas().first()
        assertThat(cartas.count { it.desbloqueada }).isEqualTo(1)
    }

    @Test
    fun `el progreso global es el promedio de los tres biomas`() = runTest {
        listOf(101, 102, 103, 104, 105).forEach { completar(it) }

        val progresoGlobal = repository.obtenerProgresoGlobal()

        assertThat(progresoGlobal).isEqualTo(33) // (100 + 0 + 0) / 3, redondeado hacia abajo
    }

    @Test
    fun `marcar un bioma como desbloqueado persiste el cambio en Room`() = runTest {
        repository.marcarBiomaDesbloqueado(2)
        assertThat(db.biomaDao().obtenerPorId(2)?.desbloqueado).isTrue()
    }
}
