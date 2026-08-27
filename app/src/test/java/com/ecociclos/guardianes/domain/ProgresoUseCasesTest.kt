package com.ecociclos.guardianes.domain

import com.ecociclos.guardianes.domain.model.Bioma
import com.ecociclos.guardianes.domain.model.EstadoModulo
import com.ecociclos.guardianes.domain.model.Nivel
import com.ecociclos.guardianes.domain.model.TipoCiclo
import com.ecociclos.guardianes.domain.model.TipoReto
import com.ecociclos.guardianes.domain.usecase.BiomaBaseInfo
import com.ecociclos.guardianes.domain.usecase.CalcularEstadoBiomasUseCase
import com.ecociclos.guardianes.domain.usecase.CalcularEstadoNivelesUseCase
import com.ecociclos.guardianes.domain.usecase.CalcularProgresoBiomaUseCase
import com.ecociclos.guardianes.domain.usecase.CalcularProgresoGlobalUseCase
import com.ecociclos.guardianes.domain.usecase.NivelBaseInfo
import com.ecociclos.guardianes.domain.usecase.ProgresoNivelInfo
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ProgresoUseCasesTest {

    private fun nivelDePrueba(id: Int, estado: EstadoModulo) = Nivel(
        id = id, biomaId = 1, orden = id, titulo = "N$id", tipoReto = TipoReto.RESTAURADOR,
        dificultad = 1, instruccion = "", estado = estado, estrellas = 0, intentos = 0
    )

    private fun biomaDePrueba(id: Int, porcentaje: Int) = Bioma(
        id = id, nombre = "B$id", descripcion = "", tipoCiclo = TipoCiclo.AGUA, orden = id,
        colorHex = "#000000", estado = EstadoModulo.DISPONIBLE, porcentajeRestaurado = porcentaje,
        totalNiveles = 5, nivelesCompletados = 0
    )

    // --- CalcularProgresoBiomaUseCase ---

    @Test
    fun `progreso de bioma vacio es cero`() {
        assertThat(CalcularProgresoBiomaUseCase()(emptyList())).isEqualTo(0)
    }

    @Test
    fun `progreso de bioma se calcula como porcentaje de niveles completados`() {
        val niveles = listOf(
            nivelDePrueba(1, EstadoModulo.COMPLETADO),
            nivelDePrueba(2, EstadoModulo.DOMINADO),
            nivelDePrueba(3, EstadoModulo.DISPONIBLE),
            nivelDePrueba(4, EstadoModulo.BLOQUEADO)
        )
        assertThat(CalcularProgresoBiomaUseCase()(niveles)).isEqualTo(50)
    }

    // --- CalcularProgresoGlobalUseCase ---

    @Test
    fun `progreso global es el promedio de todos los biomas`() {
        val biomas = listOf(biomaDePrueba(1, 100), biomaDePrueba(2, 50), biomaDePrueba(3, 0))
        assertThat(CalcularProgresoGlobalUseCase()(biomas)).isEqualTo(50)
    }

    @Test
    fun `progreso global con lista vacia es cero`() {
        assertThat(CalcularProgresoGlobalUseCase()(emptyList())).isEqualTo(0)
    }

    // --- CalcularEstadoNivelesUseCase ---

    @Test
    fun `el primer nivel siempre esta disponible sin progreso previo`() {
        val base = listOf(NivelBaseInfo(id = 1, orden = 1), NivelBaseInfo(id = 2, orden = 2))
        val resultado = CalcularEstadoNivelesUseCase()(base, emptyMap())

        assertThat(resultado.first { it.id == 1 }.estadoCalculado).isEqualTo(EstadoModulo.DISPONIBLE)
        assertThat(resultado.first { it.id == 2 }.estadoCalculado).isEqualTo(EstadoModulo.BLOQUEADO)
    }

    @Test
    fun `un nivel se desbloquea solo cuando el anterior fue completado`() {
        val base = listOf(NivelBaseInfo(id = 1, orden = 1), NivelBaseInfo(id = 2, orden = 2))
        val progreso = mapOf(1 to ProgresoNivelInfo(completado = true, estrellas = 2, intentos = 1))
        val resultado = CalcularEstadoNivelesUseCase()(base, progreso)

        assertThat(resultado.first { it.id == 1 }.estadoCalculado).isEqualTo(EstadoModulo.COMPLETADO)
        assertThat(resultado.first { it.id == 2 }.estadoCalculado).isEqualTo(EstadoModulo.DISPONIBLE)
    }

    @Test
    fun `un nivel completado con 3 estrellas se marca como dominado`() {
        val base = listOf(NivelBaseInfo(id = 1, orden = 1))
        val progreso = mapOf(1 to ProgresoNivelInfo(completado = true, estrellas = 3, intentos = 1))
        val resultado = CalcularEstadoNivelesUseCase()(base, progreso)

        assertThat(resultado.first().estadoCalculado).isEqualTo(EstadoModulo.DOMINADO)
    }

    // --- CalcularEstadoBiomasUseCase ---

    @Test
    fun `el primer bioma esta disponible desde el inicio`() {
        val biomas = listOf(BiomaBaseInfo(id = 1, orden = 1, porcentajeRestaurado = 0))
        val resultado = CalcularEstadoBiomasUseCase()(biomas)
        assertThat(resultado.first().estadoCalculado).isEqualTo(EstadoModulo.DISPONIBLE)
    }

    @Test
    fun `el segundo bioma permanece bloqueado hasta que el primero llega a 100`() {
        val biomas = listOf(
            BiomaBaseInfo(id = 1, orden = 1, porcentajeRestaurado = 60),
            BiomaBaseInfo(id = 2, orden = 2, porcentajeRestaurado = 0)
        )
        val resultado = CalcularEstadoBiomasUseCase()(biomas)

        assertThat(resultado.first { it.id == 1 }.estadoCalculado).isEqualTo(EstadoModulo.INICIADO)
        assertThat(resultado.first { it.id == 2 }.estadoCalculado).isEqualTo(EstadoModulo.BLOQUEADO)
    }

    @Test
    fun `el segundo bioma se desbloquea cuando el primero esta 100 por ciento restaurado`() {
        val biomas = listOf(
            BiomaBaseInfo(id = 1, orden = 1, porcentajeRestaurado = 100),
            BiomaBaseInfo(id = 2, orden = 2, porcentajeRestaurado = 0)
        )
        val resultado = CalcularEstadoBiomasUseCase()(biomas)

        assertThat(resultado.first { it.id == 1 }.estadoCalculado).isEqualTo(EstadoModulo.COMPLETADO)
        assertThat(resultado.first { it.id == 2 }.estadoCalculado).isEqualTo(EstadoModulo.DISPONIBLE)
    }
}
