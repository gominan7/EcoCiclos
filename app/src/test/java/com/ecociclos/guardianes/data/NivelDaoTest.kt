package com.ecociclos.guardianes.data

import com.ecociclos.guardianes.data.local.entity.BiomaEntity
import com.ecociclos.guardianes.data.local.entity.ElementoNivelEntity
import com.ecociclos.guardianes.data.local.entity.NivelEntity
import com.ecociclos.guardianes.data.local.entity.NodoRutaEntity
import com.ecociclos.guardianes.data.local.entity.VariableLabEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NivelDaoTest : RoomTestBase() {

    private suspend fun sembrarBioma() {
        db.biomaDao().insertarTodos(listOf(
            BiomaEntity(1, "Bosque", "desc", "AGUA", 1, "#3E9B5C", true)
        ))
    }

    @Test
    fun `los niveles de un bioma se devuelven ordenados`() = runTest {
        sembrarBioma()
        db.nivelDao().insertarTodos(listOf(
            NivelEntity(102, 1, 2, "N2", "LABORATORIO", 1, ""),
            NivelEntity(101, 1, 1, "N1", "RESTAURADOR", 1, "")
        ))

        val niveles = db.nivelDao().observarPorBioma(1).first()

        assertThat(niveles.map { it.id }).containsExactly(101, 102).inOrder()
    }

    @Test
    fun `borrar el bioma en cascada elimina sus niveles (foreign key CASCADE)`() = runTest {
        sembrarBioma()
        db.nivelDao().insertarTodos(listOf(NivelEntity(101, 1, 1, "N1", "RESTAURADOR", 1, "")))
        assertThat(db.nivelDao().contar()).isEqualTo(1)

        db.biomaDao().eliminar(1)

        assertThat(db.nivelDao().contar()).isEqualTo(0)
    }

    @Test
    fun `elementos del restaurador se guardan y recuperan por nivel`() = runTest {
        sembrarBioma()
        db.nivelDao().insertarTodos(listOf(NivelEntity(101, 1, 1, "N1", "RESTAURADOR", 1, "")))
        db.elementoNivelDao().insertarTodos(listOf(
            ElementoNivelEntity(nivelId = 101, clave = "sol", etiqueta = "Sol", esOrigen = true, destinoCorrectoClave = "lago"),
            ElementoNivelEntity(nivelId = 101, clave = "lago", etiqueta = "Lago", esOrigen = false, destinoCorrectoClave = null)
        ))

        val elementos = db.elementoNivelDao().obtenerPorNivel(101)

        assertThat(elementos).hasSize(2)
        assertThat(elementos.first { it.clave == "sol" }.destinoCorrectoClave).isEqualTo("lago")
    }

    @Test
    fun `nodos de ruta se guardan y recuperan por nivel`() = runTest {
        sembrarBioma()
        db.nivelDao().insertarTodos(listOf(NivelEntity(301, 1, 1, "N1", "ENRUTADOR", 1, "")))
        db.nodoRutaDao().insertarTodos(listOf(
            NodoRutaEntity(nivelId = 301, clave = "cascara", etiqueta = "Cáscara", tipoRecurso = "ORGANICO", esFuente = true, destinoCorrectoClave = "compostera")
        ))

        val nodos = db.nodoRutaDao().obtenerPorNivel(301)

        assertThat(nodos).hasSize(1)
        assertThat(nodos.first().tipoRecurso).isEqualTo("ORGANICO")
    }

    @Test
    fun `variables de laboratorio se guardan y recuperan por nivel`() = runTest {
        sembrarBioma()
        db.nivelDao().insertarTodos(listOf(NivelEntity(102, 1, 1, "N1", "LABORATORIO", 1, "")))
        db.variableLabDao().insertarTodos(listOf(
            VariableLabEntity(nivelId = 102, nombre = "Temperatura", valorMin = 15, valorMax = 45, valorInicial = 22, umbralCritico = 35, mensajeNormal = "ok", mensajeCritico = "mal")
        ))

        val variables = db.variableLabDao().obtenerPorNivel(102)

        assertThat(variables).hasSize(1)
        assertThat(variables.first().umbralCritico).isEqualTo(35)
    }

    @Test
    fun `consultar niveles de un bioma vacio devuelve lista vacia, no error`() = runTest {
        sembrarBioma()
        val niveles = db.nivelDao().observarPorBioma(1).first()
        assertThat(niveles).isEmpty()
    }
}
