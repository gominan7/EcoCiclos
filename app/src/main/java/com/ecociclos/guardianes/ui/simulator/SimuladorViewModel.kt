package com.ecociclos.guardianes.ui.simulator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecociclos.guardianes.EcoCiclosApp
import com.ecociclos.guardianes.domain.model.ConsecuenciasCompletado
import com.ecociclos.guardianes.domain.model.NivelPuzzle
import com.ecociclos.guardianes.domain.model.ResultadoNivel
import com.ecociclos.guardianes.domain.model.TipoReto
import com.ecociclos.guardianes.domain.repository.EcoRepository
import com.ecociclos.guardianes.domain.usecase.CompletarNivelUseCase
import com.ecociclos.guardianes.domain.usecase.EfectoLaboratorio
import com.ecociclos.guardianes.domain.usecase.EvaluarLaboratorioUseCase
import com.ecociclos.guardianes.domain.usecase.SeveridadLab
import com.ecociclos.guardianes.domain.usecase.ValidarConexionRestauradorUseCase
import com.ecociclos.guardianes.domain.usecase.ValidarRutaEnrutadorUseCase
import com.ecociclos.guardianes.domain.usecase.calcularEstrellas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SimuladorUiState(
    val cargando: Boolean = true,
    val puzzle: NivelPuzzle? = null,
    val conexionesRestaurador: Map<String, String> = emptyMap(),
    val rutasEnrutador: Map<String, String> = emptyMap(),
    val valorLab: Int? = null,
    val efectoLab: EfectoLaboratorio? = null,
    val severidadesObservadas: Set<SeveridadLab> = emptySet(),
    val resultado: ResultadoNivel? = null,
    val consecuencias: ConsecuenciasCompletado? = null,
    val mostrandoResultado: Boolean = false
)

class SimuladorViewModel(
    private val repository: EcoRepository,
    private val nivelId: Int,
    private val validarRestaurador: ValidarConexionRestauradorUseCase,
    private val validarEnrutador: ValidarRutaEnrutadorUseCase,
    private val evaluarLaboratorio: EvaluarLaboratorioUseCase,
    private val completarNivel: CompletarNivelUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SimuladorUiState())
    val uiState: StateFlow<SimuladorUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val puzzle = repository.obtenerPuzzleDeNivel(nivelId)
            val valorInicial = puzzle?.variables?.firstOrNull()?.valorInicial
            val efectoInicial = if (puzzle != null && puzzle.variables.isNotEmpty()) {
                evaluarLaboratorio(puzzle.variables.first(), puzzle.variables.first().valorInicial)
            } else null
            _uiState.update {
                it.copy(
                    cargando = false,
                    puzzle = puzzle,
                    valorLab = valorInicial,
                    efectoLab = efectoInicial,
                    severidadesObservadas = efectoInicial?.let { e -> setOf(e.severidad) } ?: emptySet()
                )
            }
        }
    }

    // -------------------------------------------------- Restaurador de Ciclos

    fun conectarRestaurador(origenClave: String, destinoClave: String) {
        _uiState.update { it.copy(conexionesRestaurador = it.conexionesRestaurador + (origenClave to destinoClave)) }
    }

    fun comprobarRestaurador() {
        val puzzle = _uiState.value.puzzle ?: return
        val resultado = validarRestaurador(puzzle.elementos, _uiState.value.conexionesRestaurador)
        aplicarResultado(resultado)
    }

    // -------------------------------------------------- Enrutador de Recursos

    fun conectarEnrutador(fuenteClave: String, destinoClave: String) {
        _uiState.update { it.copy(rutasEnrutador = it.rutasEnrutador + (fuenteClave to destinoClave)) }
    }

    fun comprobarEnrutador() {
        val puzzle = _uiState.value.puzzle ?: return
        val resultado = validarEnrutador(puzzle.nodosRuta, _uiState.value.rutasEnrutador)
        aplicarResultado(resultado)
    }

    // -------------------------------------------------- Laboratorio de Reacciones

    fun actualizarValorLab(valor: Int) {
        val variable = _uiState.value.puzzle?.variables?.firstOrNull() ?: return
        val efecto = evaluarLaboratorio(variable, valor)
        _uiState.update {
            it.copy(
                valorLab = efecto.valorActual,
                efectoLab = efecto,
                severidadesObservadas = it.severidadesObservadas + efecto.severidad
            )
        }
    }

    fun completarExperimentoLab() {
        val puzzle = _uiState.value.puzzle ?: return
        val observadas = _uiState.value.severidadesObservadas
        val exploroAmbosExtremos = observadas.contains(SeveridadLab.NORMAL) && observadas.contains(SeveridadLab.CRITICO)
        val correctas = if (exploroAmbosExtremos) 2 else observadas.size.coerceAtMost(1)
        val resultado = ResultadoNivel(
            nivelId = puzzle.nivel.id,
            totalConexiones = 2,
            conexionesCorrectas = correctas,
            errores = 2 - correctas,
            completado = exploroAmbosExtremos,
            estrellas = calcularEstrellas(totalPasos = 2, errores = 2 - correctas, completado = exploroAmbosExtremos)
        )
        aplicarResultado(resultado)
    }

    // -------------------------------------------------- Común

    private fun aplicarResultado(resultado: ResultadoNivel) {
        _uiState.update { it.copy(resultado = resultado, mostrandoResultado = true) }
        viewModelScope.launch {
            val consecuencias = completarNivel(resultado)
            _uiState.update { it.copy(consecuencias = consecuencias) }
        }
    }

    fun reintentar() {
        _uiState.update {
            it.copy(
                conexionesRestaurador = emptyMap(),
                rutasEnrutador = emptyMap(),
                resultado = null,
                consecuencias = null,
                mostrandoResultado = false
            )
        }
    }

    companion object {
        fun crear(app: EcoCiclosApp, nivelId: Int) = SimuladorViewModel(
            repository = app.repository,
            nivelId = nivelId,
            validarRestaurador = app.validarConexionRestaurador,
            validarEnrutador = app.validarRutaEnrutador,
            evaluarLaboratorio = app.evaluarLaboratorio,
            completarNivel = app.completarNivel
        )
    }
}
