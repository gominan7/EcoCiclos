package com.ecociclos.guardianes.ui.ecopedia

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ecociclos.guardianes.domain.model.Carta
import com.ecociclos.guardianes.domain.model.Insignia
import com.ecociclos.guardianes.ui.components.IconoElemento
import com.ecociclos.guardianes.ui.components.InsigniaIlustrada
import com.ecociclos.guardianes.ui.crearViewModel

private enum class PestanaEcopedia { CARTAS, INSIGNIAS }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcopediaScreen(onVolver: () -> Unit) {
    val viewModel = crearViewModel(EcopediaViewModel::crear)
    val estado by viewModel.uiState.collectAsState()
    var pestana by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(PestanaEcopedia.CARTAS) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Eco-pedia") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            val cartasDesbloqueadas = estado.cartas.count { it.desbloqueada }
            val insigniasObtenidas = estado.insignias.count { it.obtenida }

            TabRow(selectedTabIndex = pestana.ordinal) {
                Tab(
                    selected = pestana == PestanaEcopedia.CARTAS,
                    onClick = { pestana = PestanaEcopedia.CARTAS },
                    text = { Text("Cartas ($cartasDesbloqueadas/${estado.cartas.size})") }
                )
                Tab(
                    selected = pestana == PestanaEcopedia.INSIGNIAS,
                    onClick = { pestana = PestanaEcopedia.INSIGNIAS },
                    text = { Text("Insignias ($insigniasObtenidas/${estado.insignias.size})") }
                )
            }

            when (pestana) {
                PestanaEcopedia.CARTAS -> CuadriculaCartas(estado.cartas)
                PestanaEcopedia.INSIGNIAS -> CuadriculaInsignias(estado.insignias)
            }
        }
    }
}

@Composable
private fun CuadriculaCartas(cartas: List<Carta>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(cartas, key = { it.id }) { carta -> TarjetaCarta(carta) }
    }
}

@Composable
private fun TarjetaCarta(carta: Carta) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (carta.desbloqueada) Color(0xFFF1F0E9) else Color(0xFFE2E1D8))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (carta.desbloqueada) {
            IconoElemento(clave = carta.nombre, etiqueta = carta.nombre, tamano = 56.dp)
            Spacer(Modifier.height(6.dp))
            Text(carta.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(carta.descripcion, style = MaterialTheme.typography.bodyMedium)
        } else {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFBFBFB8)),
                contentAlignment = Alignment.Center
            ) {
                Text("?", style = MaterialTheme.typography.headlineMedium, color = Color.White)
            }
            Spacer(Modifier.height(6.dp))
            Text("Por descubrir", style = MaterialTheme.typography.titleMedium, color = Color(0xFF8A8A80))
        }
    }
}

@Composable
private fun CuadriculaInsignias(insignias: List<Insignia>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(insignias, key = { it.id }) { insignia ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                InsigniaIlustrada(obtenida = insignia.obtenida, colorHex = "#3E9B5C")
                Spacer(Modifier.height(4.dp))
                Text(
                    if (insignia.obtenida) insignia.nombre else "???",
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}
