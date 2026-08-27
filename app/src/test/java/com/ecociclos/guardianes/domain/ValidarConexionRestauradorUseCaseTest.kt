package com.ecociclos.guardianes.domain

import com.ecociclos.guardianes.domain.model.ElementoNivel
import com.ecociclos.guardianes.domain.usecase.ValidarConexionRestauradorUseCase
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ValidarConexionRestauradorUseCaseTest {

    private val usecase = ValidarConexionRestauradorUseCase()

    private val elementos = listOf(
        ElementoNivel(1, 101, "sol", "Sol", esOrigen = true, destinoCorrectoClave = "lago"),
        ElementoNivel(2, 101, "lago", "Lago", esOrigen = false, destinoCorrectoClave = null),
        ElementoNivel(3, 101, "vapor", "Vapor", esOrigen = true, destinoCorrectoClave = "nube"),
        ElementoNivel(4, 101, "nube", "Nube", esOrigen = false, destinoCorrectoClave = null)
    )

    @Test
    fun `todas las conexiones correctas completa el nivel con 3 estrellas`() {
        val resultado = usecase(elementos, mapOf("sol" to "lago", "vapor" to "nube"))

        assertThat(resultado.completado).isTrue()
        assertThat(resultado.errores).isEqualTo(0)
        assertThat(resultado.estrellas).isEqualTo(3)
        assertThat(resultado.conexionesCorrectas).isEqualTo(2)
    }

    @Test
    fun `una conexion incorrecta no completa el nivel y da 0 estrellas`() {
        val resultado = usecase(elementos, mapOf("sol" to "nube", "vapor" to "nube"))

        assertThat(resultado.completado).isFalse()
        assertThat(resultado.errores).isEqualTo(1)
        assertThat(resultado.estrellas).isEqualTo(0)
    }

    @Test
    fun `conexiones incompletas no marcan el nivel como completado`() {
        val resultado = usecase(elementos, mapOf("sol" to "lago"))

        assertThat(resultado.completado).isFalse()
        assertThat(resultado.conexionesCorrectas).isEqualTo(1)
    }

    @Test
    fun `elemento origen sin conexion elegida se marca incorrecto con explicacion`() {
        val resultado = usecase(elementos, emptyMap())

        assertThat(resultado.errores).isEqualTo(2)
        assertThat(resultado.completado).isFalse()
    }

    @Test(expected = IllegalArgumentException::class)
    fun `lanza excepcion si no hay elementos de origen configurados`() {
        val soloDestinos = elementos.filter { !it.esOrigen }
        usecase(soloDestinos, emptyMap())
    }
}
