package com.ecociclos.guardianes.data

import com.ecociclos.guardianes.data.local.seed.DatabaseSeeder
import com.ecociclos.guardianes.data.local.seed.SeedData
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DatabaseSeederTest : RoomTestBase() {

    @Test
    fun `sembrar una base de datos nueva inserta los 3 biomas y 15 niveles minimos`() = runTest {
        DatabaseSeeder(db).sembrarSiEsNecesario()

        assertThat(db.biomaDao().contar()).isEqualTo(3)
        assertThat(db.nivelDao().contar()).isEqualTo(15)
    }

    @Test
    fun `sembrar dos veces no duplica datos (caso limite de reinicio de la app)`() = runTest {
        DatabaseSeeder(db).sembrarSiEsNecesario()
        DatabaseSeeder(db).sembrarSiEsNecesario()

        assertThat(db.biomaDao().contar()).isEqualTo(3)
        assertThat(db.nivelDao().contar()).isEqualTo(15)
    }

    @Test
    fun `cada nivel semilla del tipo RESTAURADOR tiene al menos un elemento de origen`() = runTest {
        DatabaseSeeder(db).sembrarSiEsNecesario()

        val nivelesRestaurador = SeedData.niveles.filter { it.tipoReto == "RESTAURADOR" }
        nivelesRestaurador.forEach { nivel ->
            val elementos = db.elementoNivelDao().obtenerPorNivel(nivel.id)
            assertThat(elementos.any { it.esOrigen }).isTrue()
        }
    }

    @Test
    fun `cada nivel semilla del tipo ENRUTADOR tiene al menos un nodo fuente y uno destino`() = runTest {
        DatabaseSeeder(db).sembrarSiEsNecesario()

        val nivelesEnrutador = SeedData.niveles.filter { it.tipoReto == "ENRUTADOR" }
        nivelesEnrutador.forEach { nivel ->
            val nodos = db.nodoRutaDao().obtenerPorNivel(nivel.id)
            assertThat(nodos.any { it.esFuente }).isTrue()
            assertThat(nodos.any { !it.esFuente }).isTrue()
        }
    }

    @Test
    fun `cada nivel semilla del tipo LABORATORIO tiene exactamente una variable`() = runTest {
        DatabaseSeeder(db).sembrarSiEsNecesario()

        val nivelesLab = SeedData.niveles.filter { it.tipoReto == "LABORATORIO" }
        nivelesLab.forEach { nivel ->
            val variables = db.variableLabDao().obtenerPorNivel(nivel.id)
            assertThat(variables).hasSize(1)
        }
    }

    @Test
    fun `sembrar inserta el perfil inicial con alias por defecto`() = runTest {
        DatabaseSeeder(db).sembrarSiEsNecesario()
        assertThat(db.perfilDao().obtener()?.alias).isEqualTo(SeedData.perfilInicial.alias)
    }

    @Test
    fun `todas las cartas semilla apuntan a un nivel real existente (integridad de datos)`() = runTest {
        DatabaseSeeder(db).sembrarSiEsNecesario()

        val idsDeNiveles = SeedData.niveles.map { it.id }.toSet()
        SeedData.cartas.forEach { carta ->
            assertThat(idsDeNiveles).contains(carta.nivelDesbloqueoId)
        }
    }
}
