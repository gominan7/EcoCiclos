package com.ecociclos.guardianes.data

import com.ecociclos.guardianes.data.local.entity.BiomaEntity
import com.ecociclos.guardianes.data.local.entity.NivelEntity
import com.ecociclos.guardianes.data.local.entity.ProgresoNivelEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ProgresoNivelDaoTest : RoomTestBase() {

    private suspend fun sembrarNivel() {
        db.biomaDao().insertarTodos(listOf(BiomaEntity(1, "Bosque", "d", "AGUA", 1, "#000", true)))
        db.nivelDao().insertarTodos(listOf(NivelEntity(101, 1, 1, "N1", "RESTAURADOR", 1, "")))
    }

    @Test
    fun `un nivel sin progreso previo devuelve null (base de datos nueva)`() = runTest {
        sembrarNivel()
        assertThat(db.progresoNivelDao().obtenerPorNivel(101)).isNull()
    }

    @Test
    fun `upsert inserta si no existe progreso previo`() = runTest {
        sembrarNivel()
        db.progresoNivelDao().upsert(ProgresoNivelEntity(101, completado = true, estrellas = 3, intentos = 1, fechaCompletadoMillis = 1000L))

        val progreso = db.progresoNivelDao().obtenerPorNivel(101)

        assertThat(progreso?.completado).isTrue()
        assertThat(progreso?.estrellas).isEqualTo(3)
    }

    @Test
    fun `upsert actualiza el progreso existente en vez de duplicarlo`() = runTest {
        sembrarNivel()
        db.progresoNivelDao().upsert(ProgresoNivelEntity(101, completado = false, estrellas = 1, intentos = 1, fechaCompletadoMillis = null))
        db.progresoNivelDao().upsert(ProgresoNivelEntity(101, completado = true, estrellas = 3, intentos = 2, fechaCompletadoMillis = 2000L))

        assertThat(db.progresoNivelDao().obtenerTodos()).hasSize(1)
        assertThat(db.progresoNivelDao().obtenerPorNivel(101)?.intentos).isEqualTo(2)
    }

    @Test
    fun `doble toque rapido (dos intentos fallidos seguidos) se refleja en el conteo de intentos`() = runTest {
        sembrarNivel()
        db.progresoNivelDao().upsert(ProgresoNivelEntity(101, completado = false, estrellas = 0, intentos = 1, fechaCompletadoMillis = null))
        db.progresoNivelDao().upsert(ProgresoNivelEntity(101, completado = false, estrellas = 0, intentos = 2, fechaCompletadoMillis = null))

        assertThat(db.progresoNivelDao().obtenerPorNivel(101)?.intentos).isEqualTo(2)
        assertThat(db.progresoNivelDao().obtenerPorNivel(101)?.completado).isFalse()
    }
}
