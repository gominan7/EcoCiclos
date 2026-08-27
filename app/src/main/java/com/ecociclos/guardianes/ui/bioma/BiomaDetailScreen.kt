package com.ecociclos.guardianes.ui.bioma

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
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
import com.ecociclos.guardianes.domain.model.EstadoModulo
import com.ecociclos.guardianes.domain.model.Nivel
import com.ecociclos.guardianes.domain.model.TipoReto
import com.ecociclos.guardianes.ui.components.Brote
import com.ecociclos.guardianes.ui.components.EstrellasResultado
import com.ecociclos.guardianes.ui.components.IconoElemento
import com.ecociclos.guardianes.ui.components.parseColorHex
import com.ecociclos.guardianes.ui.crearViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BiomaDetailScreen(
    biomaId: Int,
    onVolver: () -> Unit,
    onJugarNivel: (Int) -> Unit
) {
    val viewModel = crearViewModel { app -> BiomaViewModel.crear(app, biomaId) }
    val estado by viewModel.uiState.collectAsState()
    val bioma = estado.bioma

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(bioma?.nombre ?: "Bioma") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (bioma != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(parseColorHex(bioma.colorHex).copy(alpha = 0.14f))
                        .padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Brote(tamano = 56.dp, animado = false)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Diagnóstico de Brote", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text(bioma.descripcion, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(estado.niveles, key = { it.id }) { nivel ->
                    TarjetaNivel(nivel = nivel, onClick = { onJugarNivel(nivel.id) })
                }
                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
private fun TarjetaNivel(nivel: Nivel, onClick: () -> Unit) {
    val bloqueado = nivel.estado == EstadoModulo.BLOQUEADO
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (bloqueado) Color(0xFFEDECE3) else Color(0xFFFFFFFF))
            .clickable(enabled = !bloqueado, onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconoElemento(clave = nivel.tipoReto.name, etiqueta = nivel.titulo, tamano = 44.dp)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(nivel.titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(2.dp))
            Text(textoTipoReto(nivel.tipoReto), style = MaterialTheme.typography.labelLarge, color = Color(0xFF6B6459))
            if (nivel.estado == EstadoModulo.COMPLETADO || nivel.estado == EstadoModulo.DOMINADO) {
                Spacer(Modifier.height(4.dp))
                EstrellasResultado(estrellas = nivel.estrellas, tamano = 16.dp)
            }
        }
        when {
            bloqueado -> Icon(Icons.Filled.Lock, contentDescription = "Bloqueado", tint = Color(0xFF9C9C94))
            nivel.estado == EstadoModulo.COMPLETADO || nivel.estado == EstadoModulo.DOMINADO ->
                Icon(Icons.Filled.CheckCircle, contentDescription = "Completado", tint = Color(0xFF3E9B5C))
            else -> Icon(Icons.Filled.PlayArrow, contentDescription = "Jugar", tint = Color(0xFF2FA8D9))
        }
    }
}

private fun textoTipoReto(tipo: TipoReto): String = when (tipo) {
    TipoReto.RESTAURADOR -> "Restaurador de Ciclos"
    TipoReto.ENRUTADOR -> "Enrutador de Recursos"
    TipoReto.LABORATORIO -> "Laboratorio de Reacciones"
}
