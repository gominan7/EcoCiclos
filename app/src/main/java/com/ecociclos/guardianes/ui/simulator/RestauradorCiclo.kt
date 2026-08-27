package com.ecociclos.guardianes.ui.simulator

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ecociclos.guardianes.domain.model.ElementoNivel
import com.ecociclos.guardianes.ui.components.IconoElemento

/**
 * Puzzle "Restaurador de Ciclos". El niño arrastra con el dedo cada elemento
 * de origen (parte inferior) hasta soltarlo sobre la etapa del ciclo donde
 * corresponde (parte superior). Es arrastre real (pointerInput + detección de
 * colisión con el rectángulo del destino), no una selección por toques.
 */
@Composable
fun RestauradorCiclo(
    elementos: List<ElementoNivel>,
    conexiones: Map<String, String>,
    onConectar: (origenClave: String, destinoClave: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val origenes = remember(elementos) { elementos.filter { it.esOrigen } }
    val destinos = remember(elementos) { elementos.filter { !it.esOrigen } }

    // Rectángulos (en coordenadas raíz) de cada destino, para detectar dónde se soltó el dedo.
    val bounds = remember { mutableStateMapOf<String, Rect>() }

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            "Destinos del ciclo",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(destinos, key = { it.clave }) { destino ->
                val conectado = conexiones.values.contains(destino.clave)
                val origenConectado = conexiones.entries.find { it.value == destino.clave }?.key
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 96.dp)
                        .onGloballyPositioned { coords ->
                            val posicion = coords.positionInRoot()
                            bounds[destino.clave] = Rect(posicion, coords.size.toSize())
                        }
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (conectado) Color(0xFFDCEFE0) else Color(0xFFF1F0E9)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    IconoElemento(clave = destino.clave, etiqueta = destino.etiqueta, tamano = 40.dp)
                    Spacer(Modifier.height(4.dp))
                    Text(destino.etiqueta, style = MaterialTheme.typography.labelLarge, textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.padding(horizontal = 6.dp))
                    if (origenConectado != null) {
                        Spacer(Modifier.height(2.dp))
                        Text("← ${elementos.find { it.clave == origenConectado }?.etiqueta}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        Text(
            "Arrastra cada elemento hacia su destino",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            origenes.forEach { origen ->
                val yaConectado = conexiones.containsKey(origen.clave)
                ChipArrastrable(
                    elemento = origen,
                    resuelto = yaConectado,
                    obtenerDestino = { punto -> bounds.entries.find { it.value.contains(punto) }?.key },
                    onSoltarSobre = { destinoClave -> onConectar(origen.clave, destinoClave) }
                )
            }
        }
    }
}

@Composable
private fun ChipArrastrable(
    elemento: ElementoNivel,
    resuelto: Boolean,
    obtenerDestino: (Offset) -> String?,
    onSoltarSobre: (String) -> Unit
) {
    var offset by remember { mutableStateOf(Offset.Zero) }
    var origenEnRaiz by remember { mutableStateOf(Offset.Zero) }
    var tamanoPropio by remember { mutableStateOf(Offset.Zero) } // (ancho, alto) medidos en px
    var arrastrando by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .onGloballyPositioned {
                origenEnRaiz = it.positionInRoot()
                tamanoPropio = Offset(it.size.width.toFloat(), it.size.height.toFloat())
            }
            .graphicsLayer {
                translationX = offset.x
                translationY = offset.y
                scaleX = if (arrastrando) 1.08f else 1f
                scaleY = if (arrastrando) 1.08f else 1f
            }
            .clip(RoundedCornerShape(14.dp))
            .background(if (resuelto) Color(0xFFB9E4C3) else Color.White)
            .pointerInput(elemento.clave, resuelto) {
                if (resuelto) return@pointerInput
                detectDragGestures(
                    onDragStart = { arrastrando = true },
                    onDragEnd = {
                        arrastrando = false
                        val puntoActual = Offset(
                            origenEnRaiz.x + offset.x + tamanoPropio.x / 2f,
                            origenEnRaiz.y + offset.y + tamanoPropio.y / 2f
                        )
                        val destino = obtenerDestino(puntoActual)
                        if (destino != null) {
                            onSoltarSobre(destino)
                        }
                        offset = Offset.Zero
                    },
                    onDragCancel = { arrastrando = false; offset = Offset.Zero },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offset += dragAmount
                    }
                )
            }
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconoElemento(clave = elemento.clave, etiqueta = elemento.etiqueta, tamano = 48.dp)
        Spacer(Modifier.height(4.dp))
        Text(elemento.etiqueta, style = MaterialTheme.typography.labelLarge)
    }
}

private fun androidx.compose.ui.unit.IntSize.toSize() =
    androidx.compose.ui.geometry.Size(width.toFloat(), height.toFloat())
