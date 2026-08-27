package com.ecociclos.guardianes.data

import com.ecociclos.guardianes.data.local.entity.BiomaEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BiomaDaoTest : RoomTestBase() {

    private fun bioma(id: Int, orden: Int, desbloqueado: Boolean) = BiomaEntity(
        id = id, nombre = "Bioma $id", descripcion = "desc", tipoCiclo = "AGUA",
        orden = orden, colorHex = "#3E9B5C", desbloqueado = desbloqueado
    )

    @Test
    fun `insertar y observar biomas devuelve la lista ordenada por orden`() = runTest {
        db.biomaDao().insertarTodos(listOf(bioma(2, 2, false), bioma(1, 1, true)))

        val lista = db.biomaDao().observarTodos().first()

        assertThat(lista.map { it.id }).containsExactly(1, 2).inOrder()
    }

    @Test
    fun `actualizar desbloqueado persiste el nuevo valor`() = runTest {
        db.biomaDao().insertarTodos(listOf(bioma(2, 2, false)))

        db.biomaDao().actualizarDesbloqueado(2, true)

        assertThat(db.biomaDao().obtenerPorId(2)?.desbloqueado).isTrue()
    }

    @Test
    fun `obtener por id inexistente devuelve null`() = runTest {
        assertThat(db.biomaDao().obtenerPorId(999)).isNull()
    }

    @Test
    fun `insertar con conflicto ignora el duplicado (base de datos nueva vs reinicio)`() = runTest {
        db.biomaDao().insertarTodos(listOf(bioma(1, 1, true)))
        db.biomaDao().insertarTodos(listOf(bioma(1, 1, true)))

        assertThat(db.biomaDao().contar()).isEqualTo(1)
    }
}
