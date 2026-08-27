package com.ecociclos.guardianes.domain

import com.ecociclos.guardianes.domain.model.VariableLab
import com.ecociclos.guardianes.domain.usecase.EvaluarLaboratorioUseCase
import com.ecociclos.guardianes.domain.usecase.SeveridadLab
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class EvaluarLaboratorioUseCaseTest {

    private val usecase = EvaluarLaboratorioUseCase()

    private val variable = VariableLab(
        id = 1, nivelId = 102, nombre = "Temperatura",
        valorMin = 15, valorMax = 45, valorInicial = 22, umbralCritico = 35,
        mensajeNormal = "Todo estable", mensajeCritico = "Sequía"
    )

    @Test
    fun `valor por debajo del umbral es normal`() {
        val efecto = usecase(variable, 20)
        assertThat(efecto.severidad).isEqualTo(SeveridadLab.NORMAL)
        assertThat(efecto.mensaje).isEqualTo("Todo estable")
    }

    @Test
    fun `valor igual o mayor al umbral es critico`() {
        val efecto = usecase(variable, 35)
        assertThat(efecto.severidad).isEqualTo(SeveridadLab.CRITICO)
        assertThat(efecto.mensaje).isEqualTo("Sequía")
    }

    @Test
    fun `valor cercano al umbral entra en alerta`() {
        val efecto = usecase(variable, 33)
        assertThat(efecto.severidad).isEqualTo(SeveridadLab.ALERTA)
    }

    @Test
    fun `valores fuera de rango se acotan al minimo y maximo`() {
        val efectoBajo = usecase(variable, -100)
        val efectoAlto = usecase(variable, 999)

        assertThat(efectoBajo.valorActual).isEqualTo(15)
        assertThat(efectoAlto.valorActual).isEqualTo(45)
    }

    @Test
    fun `la magnitud del efecto crece de 0 a 1 con el valor`() {
        val efectoMin = usecase(variable, 15)
        val efectoMax = usecase(variable, 45)

        assertThat(efectoMin.magnitudEfecto).isEqualTo(0f)
        assertThat(efectoMax.magnitudEfecto).isEqualTo(1f)
    }
}
