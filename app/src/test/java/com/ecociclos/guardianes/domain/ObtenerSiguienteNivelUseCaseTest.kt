package com.ecociclos.guardianes.domain

import com.ecociclos.guardianes.domain.model.EstadoModulo
import com.ecociclos.guardianes.domain.model.Nivel
import com.ecociclos.guardianes.domain.model.TipoReto
import com.ecociclos.guardianes.domain.usecase.ObtenerSiguienteNivelUseCase
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ObtenerSiguienteNivelUseCaseTest {

    private val usecase = ObtenerSiguienteNivelUseCase()

    private fun nivel(id: Int, orden: Int, estado: EstadoModulo) = Nivel(
        id, 1, orden, "N$id", TipoReto.RESTAURADOR, 1, "", estado, 0, 0
    )

    @Test
    fun `elige el primer nivel disponible en orden`() {
        val niveles = listOf(
            nivel(1, 1, EstadoModulo.COMPLETADO),
            nivel(2, 2, EstadoModulo.DISPONIBLE),
            nivel(3, 3, EstadoModulo.BLOQUEADO)
        )
        assertThat(usecase(niveles)?.id).isEqualTo(2)
    }

    @Test
    fun `devuelve null si todos estan bloqueados o completados`() {
        val niveles = listOf(
            nivel(1, 1, EstadoModulo.COMPLETADO),
            nivel(2, 2, EstadoModulo.DOMINADO)
        )
        assertThat(usecase(niveles)).isNull()
    }

    @Test
    fun `un nivel iniciado tambien cuenta como siguiente jugable`() {
        val niveles = listOf(nivel(1, 1, EstadoModulo.INICIADO))
        assertThat(usecase(niveles)?.id).isEqualTo(1)
    }
}
