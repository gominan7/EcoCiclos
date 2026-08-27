package com.ecociclos.guardianes.ui.globe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ecociclos.guardianes.ui.components.Brote
import com.ecociclos.guardianes.ui.components.BarraProgresoEco
import com.ecociclos.guardianes.ui.components.Gota
import com.ecociclos.guardianes.ui.components.TarjetaBioma
import com.ecociclos.guardianes.ui.crearViewModel
import com.ecociclos.guardianes.ui.theme.AguaClara
import com.ecociclos.guardianes.ui.theme.HojaClara
import com.ecociclos.guardianes.ui.theme.SolSuave

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobeDashboardScreen(
    onAbrirBioma: (Int) -> Unit,
    onAbrirEcopedia: () -> Unit,
    onAbrirPerfil: () -> Unit
) {
    val viewModel = crearViewModel(GlobeViewModel::crear)
    val estado by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("EcoCiclos", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onAbrirEcopedia) {
                        Icon(Icons.Filled.Menu, contentDescription = "Eco-pedia")
                    }
                    IconButton(onClick = onAbrirPerfil) {
                        Icon(Icons.Filled.Person, contentDescription = "Perfil")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(listOf(AguaClara.copy(alpha = 0.35f), HojaClara.copy(alpha = 0.25f), SolSuave.copy(alpha = 0.2f)))
                )
        ) {
            // Encabezado del globo: personajes guía + progreso del planeta
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Brote(tamano = 64.dp)
                Spacer(Modifier.width(4.dp))
                Gota(tamano = 56.dp)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Planeta restaurado", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    BarraProgresoEco(porcentaje = estado.progresoGlobal)
                    Spacer(Modifier.height(4.dp))
                    Text("${estado.progresoGlobal}% del planeta sanado", style = MaterialTheme.typography.labelLarge)
                }
            }

            Text(
                "Elige un bioma en peligro",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(estado.biomas, key = { it.id }) { bioma ->
                    TarjetaBioma(
                        bioma = bioma,
                        onClick = { onAbrirBioma(bioma.id) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}
