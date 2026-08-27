package com.ecociclos.guardianes.domain

import com.ecociclos.guardianes.domain.model.NodoRuta
import com.ecociclos.guardianes.domain.model.TipoRecurso
import com.ecociclos.guardianes.domain.usecase.ValidarRutaEnrutadorUseCase
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ValidarRutaEnrutadorUseCaseTest {

    private val usecase = ValidarRutaEnrutadorUseCase()

    private val nodos = listOf(
        NodoRuta(1, 301, "cascara", "Cáscara", TipoRecurso.ORGANICO, esFuente = true, destinoCorrectoClave = "compostera"),
        NodoRuta(2, 301, "botella", "Botella", TipoRecurso.PLASTICO, esFuente = true, destinoCorrectoClave = "reciclaje"),
        NodoRuta(3, 301, "compostera", "Compostera", TipoRecurso.ORGANICO, esFuente = false, destinoCorrectoClave = null),
        NodoRuta(4, 301, "reciclaje", "Reciclaje", TipoRecurso.PLASTICO, esFuente = false, destinoCorrectoClave = null),
        NodoRuta(5, 301, "rio", "Río", TipoRecurso.CONTAMINANTE, esFuente = false, destinoCorrectoClave = null)
    )

    @Test
    fun `rutas correctas completan el nivel`() {
        val resultado = usecase(nodos, mapOf("cascara" to "compostera", "botella" to "reciclaje"))

        assertThat(resultado.completado).isTrue()
        assertThat(resultado.estrellas).isEqualTo(3)
    }

    @Test
    fun `enviar un residuo al rio contamina y no completa el nivel`() {
        val resultado = usecase(nodos, mapOf("cascara" to "rio", "botella" to "reciclaje"))

        assertThat(resultado.completado).isFalse()
        assertThat(resultado.conexionesCorrectas).isEqualTo(1)
    }

    @Test
    fun `tipo de recurso incompatible con destino cuenta como incorrecto aunque la clave exista`() {
        // "botella" (PLASTICO) enviada a "compostera" (acepta ORGANICO): la clave de destino
        // existe pero el tipo no coincide y tampoco es la clave correcta configurada.
        val resultado = usecase(nodos, mapOf("cascara" to "compostera", "botella" to "compostera"))

        assertThat(resultado.conexionesCorrectas).isEqualTo(1)
        assertThat(resultado.completado).isFalse()
    }

    @Test
    fun `fuente sin ruta trazada se cuenta como error, no lanza excepcion`() {
        val resultado = usecase(nodos, mapOf("cascara" to "compostera"))

        assertThat(resultado.errores).isEqualTo(1)
        assertThat(resultado.completado).isFalse()
    }

    @Test(expected = IllegalArgumentException::class)
    fun `lanza excepcion si no hay nodos fuente`() {
        val soloDestinos = nodos.filter { !it.esFuente }
        usecase(soloDestinos, emptyMap())
    }
}
