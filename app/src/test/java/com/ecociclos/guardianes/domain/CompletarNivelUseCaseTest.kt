package com.ecociclos.guardianes.domain

import com.ecociclos.guardianes.domain.model.*
import com.ecociclos.guardianes.domain.usecase.CompletarNivelUseCase
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Test

class CompletarNivelUseCaseTest {

    private fun nivel(id: Int = 101, biomaId: Int = 1) = Nivel(
        id, biomaId, 1, "Nivel de prueba", TipoReto.RESTAURADOR, 1, "", EstadoModulo.DISPONIBLE, 0, 0
    )

    private fun bioma(porcentaje: Int, id: Int = 1) = Bioma(
        id, "Bioma", "", TipoCiclo.AGUA, 1, "#000000", EstadoModulo.DISPONIBLE, porcentaje, 5, 0
    )

    @Test
    fun `siempre registra el resultado aunque el nivel no se haya completado`() = runTest {
        val repo = FakeEcoRepository()
        val usecase = CompletarNivelUseCase(repo)
        val resultado = ResultadoNivel(101, 2, 1, 1, completado = false, estrellas = 0)

        usecase(resultado)

        assertThat(repo.resultadosRegistrados).containsExactly(resultado)
    }

    @Test
    fun `nivel completado sin terminar el bioma no desbloquea insignias`() = runTest {
        val repo = FakeEcoRepository().apply {
            nivelAsociado = nivel()
            biomaResultante = bioma(porcentaje = 40)
            cartaParaDesbloquear = Carta(101, "Sol", TipoCarta.ELEMENTO, 1, "", 101, desbloqueada = true)
            insigniasCandidatas = listOf(Insignia(1, "Guardián del Agua", "", 1, obtenida = false))
            progresoGlobalResultante = 13
        }
        val usecase = CompletarNivelUseCase(repo)
        val resultado = ResultadoNivel(101, 2, 2, 0, completado = true, estrellas = 3)

        val consecuencias = usecase(resultado)

        assertThat(consecuencias.cartaDesbloqueada?.nombre).isEqualTo("Sol")
        assertThat(consecuencias.biomaCompletado).isFalse()
        assertThat(consecuencias.insigniasNuevas).isEmpty()
        assertThat(repo.biomasDesbloqueados).isEmpty()
    }

    @Test
    fun `completar el ultimo nivel de un bioma desbloquea su insignia`() = runTest {
        val repo = FakeEcoRepository().apply {
            nivelAsociado = nivel(id = 105, biomaId = 1)
            biomaResultante = bioma(porcentaje = 100, id = 1)
            insigniasCandidatas = listOf(
                Insignia(1, "Guardián del Agua", "", 1, obtenida = false),
                Insignia(4, "Guardián del Planeta", "", null, obtenida = false)
            )
            progresoGlobalResultante = 33
        }
        val usecase = CompletarNivelUseCase(repo)
        val resultado = ResultadoNivel(105, 2, 2, 0, completado = true, estrellas = 3)

        val consecuencias = usecase(resultado)

        assertThat(consecuencias.biomaCompletado).isTrue()
        assertThat(consecuencias.insigniasNuevas.map { it.id }).containsExactly(1)
        assertThat(repo.biomasDesbloqueados).containsExactly(1)
        assertThat(repo.insigniasDesbloqueadasLlamadas).containsExactly(listOf(1))
    }

    @Test
    fun `completar el ultimo bioma con progreso global 100 tambien desbloquea la insignia global`() = runTest {
        val repo = FakeEcoRepository().apply {
            nivelAsociado = nivel(id = 305, biomaId = 3)
            biomaResultante = bioma(porcentaje = 100, id = 3)
            insigniasCandidatas = listOf(
                Insignia(3, "Guardián del Río", "", 3, obtenida = false),
                Insignia(4, "Guardián del Planeta", "", null, obtenida = false)
            )
            progresoGlobalResultante = 100
        }
        val usecase = CompletarNivelUseCase(repo)
        val resultado = ResultadoNivel(305, 1, 1, 0, completado = true, estrellas = 3)

        val consecuencias = usecase(resultado)

        assertThat(consecuencias.insigniasNuevas.map { it.id }).containsExactly(3, 4)
    }
}
