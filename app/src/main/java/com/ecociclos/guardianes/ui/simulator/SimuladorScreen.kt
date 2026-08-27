package com.ecociclos.guardianes.ui.simulator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ecociclos.guardianes.domain.model.TipoReto
import com.ecociclos.guardianes.ui.components.Brote
import com.ecociclos.guardianes.ui.components.EstrellasResultado
import com.ecociclos.guardianes.ui.components.Gota
import com.ecociclos.guardianes.ui.crearViewModel

@Composable
fun SimuladorScreen(
    nivelId: Int,
    onVolver: () -> Unit,
    onSiguienteNivel: (Int) -> Unit
) {
    val viewModel = crearViewModel { app -> SimuladorViewModel.crear(app, nivelId) }
    val estado by viewModel.uiState.collectAsState()
    val puzzle = estado.puzzle

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(puzzle?.nivel?.titulo ?: "Reto") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (estado.cargando || puzzle == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                return@Box
            }

            Column(Modifier.fillMaxSize()) {
                Text(
                    puzzle.nivel.instruccion,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )

                Box(modifier = Modifier.weight(1f)) {
                    when (puzzle.nivel.tipoReto) {
                        TipoReto.RESTAURADOR -> RestauradorCiclo(
                            elementos = puzzle.elementos,
                            conexiones = estado.conexionesRestaurador,
                            onConectar = viewModel::conectarRestaurador
                        )
                        TipoReto.ENRUTADOR -> EnrutadorRecursos(
                            nodos = puzzle.nodosRuta,
                            rutas = estado.rutasEnrutador,
                            onConectar = viewModel::conectarEnrutador
                        )
                        TipoReto.LABORATORIO -> {
                            val variable = puzzle.variables.firstOrNull()
                            if (variable != null) {
                                LaboratorioReacciones(
                                    variable = variable,
                                    valorActual = estado.valorLab ?: variable.valorInicial,
                                    efecto = estado.efectoLab,
                                    onValorCambiado = viewModel::actualizarValorLab
                                )
                            }
                        }
                    }
                }

                BotonAccion(
                    tipoReto = puzzle.nivel.tipoReto,
                    conexionesRestaurador = estado.conexionesRestaurador.size,
                    totalOrigenes = puzzle.elementos.count { it.esOrigen },
                    rutasEnrutador = estado.rutasEnrutador.size,
                    totalFuentes = puzzle.nodosRuta.count { it.esFuente },
                    severidadesObservadas = estado.severidadesObservadas.size,
                    onComprobarRestaurador = viewModel::comprobarRestaurador,
                    onComprobarEnrutador = viewModel::comprobarEnrutador,
                    onCompletarLab = viewModel::completarExperimentoLab
                )
            }

            AnimatedVisibility(visible = estado.mostrandoResultado) {
                PanelResultado(
                    estado = estado,
                    onReintentar = viewModel::reintentar,
                    onSiguiente = {
                        viewModel.reintentar()
                        onSiguienteNivel(nivelId)
                    },
                    onVolver = onVolver
                )
            }
        }
    }
}

@Composable
private fun BotonAccion(
    tipoReto: TipoReto,
    conexionesRestaurador: Int,
    totalOrigenes: Int,
    rutasEnrutador: Int,
    totalFuentes: Int,
    severidadesObservadas: Int,
    onComprobarRestaurador: () -> Unit,
    onComprobarEnrutador: () -> Unit,
    onCompletarLab: () -> Unit
) {
    val (habilitado, texto, accion) = when (tipoReto) {
        TipoReto.RESTAURADOR -> Triple(conexionesRestaurador >= totalOrigenes && totalOrigenes > 0, "Comprobar ciclo", onComprobarRestaurador)
        TipoReto.ENRUTADOR -> Triple(rutasEnrutador >= totalFuentes && totalFuentes > 0, "Comprobar rutas", onComprobarEnrutador)
        TipoReto.LABORATORIO -> Triple(severidadesObservadas >= 2, "Completar experimento", onCompletarLab)
    }
    Button(
        onClick = accion,
        enabled = habilitado,
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        Text(texto)
    }
}

@Composable
private fun PanelResultado(
    estado: SimuladorUiState,
    onReintentar: () -> Unit,
    onSiguiente: () -> Unit,
    onVolver: () -> Unit
) {
    val resultado = estado.resultado ?: return
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (resultado.completado) Gota(tamano = 72.dp) else Brote(tamano = 72.dp)
            Spacer(Modifier.height(10.dp))
            Text(
                if (resultado.completado) "¡Conexión correcta!" else "Casi lo tienes",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            if (resultado.completado) {
                EstrellasResultado(estrellas = resultado.estrellas)
            } else {
                Text(
                    "${resultado.conexionesCorrectas}/${resultado.totalConexiones} correctas. Revisa e inténtalo de nuevo.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            estado.consecuencias?.cartaDesbloqueada?.let { carta ->
                Spacer(Modifier.height(10.dp))
                Text("¡Nueva carta desbloqueada: ${carta.nombre}!", style = MaterialTheme.typography.titleMedium)
            }
            estado.consecuencias?.insigniasNuevas?.forEach { insignia ->
                Text("¡Insignia obtenida: ${insignia.nombre}!", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(Modifier.height(16.dp))
            if (resultado.completado) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = onVolver) { Text("Volver al bioma") }
                    Button(onClick = onSiguiente) { Text("Continuar") }
                }
            } else {
                Button(onClick = onReintentar) { Text("Reintentar") }
            }
        }
    }
}
