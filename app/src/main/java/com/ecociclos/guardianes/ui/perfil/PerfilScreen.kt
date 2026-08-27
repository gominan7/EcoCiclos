package com.ecociclos.guardianes.ui.perfil

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ecociclos.guardianes.ui.components.AvatarIlustrado
import com.ecociclos.guardianes.ui.crearViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(onVolver: () -> Unit) {
    val viewModel = crearViewModel(PerfilViewModel::crear)
    val perfil by viewModel.perfil.collectAsState()

    var alias by remember(perfil?.alias) { mutableStateOf(perfil?.alias ?: "") }
    var avatarSeleccionado by remember(perfil?.avatarId) { mutableStateOf(perfil?.avatarId ?: 1) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tu perfil de guardián") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AvatarIlustrado(avatarId = avatarSeleccionado, tamano = 96.dp, seleccionado = true)
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = alias,
                onValueChange = { if (it.length <= 16) alias = it },
                label = { Text("Alias") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Nunca pidas ni escribas tu nombre real: elige un alias divertido.",
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(Modifier.height(24.dp))
            Text("Elige tu avatar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                items(PerfilViewModel.AVATARES_DISPONIBLES.toList()) { id ->
                    AvatarIlustrado(
                        avatarId = id,
                        seleccionado = id == avatarSeleccionado,
                        modifier = Modifier.clickable { avatarSeleccionado = id }
                    )
                }
            }

            perfil?.let { Text("XP total: ${it.xpTotal}", style = MaterialTheme.typography.bodyLarge) }

            Spacer(Modifier.height(12.dp))
            Button(
                onClick = { viewModel.actualizar(alias, avatarSeleccionado) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }
        }
    }
}
