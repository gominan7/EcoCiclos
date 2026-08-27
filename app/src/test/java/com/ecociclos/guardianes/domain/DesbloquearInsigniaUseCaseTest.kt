package com.ecociclos.guardianes.domain

import com.ecociclos.guardianes.domain.model.Insignia
import com.ecociclos.guardianes.domain.usecase.DesbloquearInsigniaUseCase
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DesbloquearInsigniaUseCaseTest {

    private val usecase = DesbloquearInsigniaUseCase()

    @Test
    fun `desbloquea la insignia del bioma recien completado`() {
        val candidatas = listOf(
            Insignia(1, "Guardián del Agua", "", biomaId = 1, obtenida = false),
            Insignia(4, "Guardián del Planeta", "", biomaId = null, obtenida = false)
        )
        val nuevas = usecase(candidatas, biomaCompletadoAhora = 1, todosLosBiomasCompletados = false)

        assertThat(nuevas.map { it.id }).containsExactly(1)
    }

    @Test
    fun `desbloquea la insignia global solo cuando todos los biomas estan completos`() {
        val candidatas = listOf(Insignia(4, "Guardián del Planeta", "", biomaId = null, obtenida = false))

        val ningunaTodavia = usecase(candidatas, biomaCompletadoAhora = null, todosLosBiomasCompletados = false)
        val ahoraSi = usecase(candidatas, biomaCompletadoAhora = 3, todosLosBiomasCompletados = true)

        assertThat(ningunaTodavia).isEmpty()
        assertThat(ahoraSi.map { it.id }).containsExactly(4)
    }

    @Test
    fun `no vuelve a desbloquear una insignia ya obtenida`() {
        val candidatas = listOf(Insignia(1, "Guardián del Agua", "", biomaId = 1, obtenida = true))
        val nuevas = usecase(candidatas, biomaCompletadoAhora = 1, todosLosBiomasCompletados = false)

        assertThat(nuevas).isEmpty()
    }

    @Test
    fun `no desbloquea insignias de otros biomas`() {
        val candidatas = listOf(Insignia(2, "Maestro del Carbono", "", biomaId = 2, obtenida = false))
        val nuevas = usecase(candidatas, biomaCompletadoAhora = 1, todosLosBiomasCompletados = false)

        assertThat(nuevas).isEmpty()
    }
}
