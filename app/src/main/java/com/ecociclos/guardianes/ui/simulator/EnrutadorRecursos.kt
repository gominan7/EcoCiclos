package com.ecociclos.guardianes.ui.simulator

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ecociclos.guardianes.domain.model.NodoRuta
import com.ecociclos.guardianes.ui.components.IconoElemento

/**
 * Puzzle "Enrutador de Recursos". El niño traza con el dedo una línea desde
 * cada nodo fuente hasta el nodo destino correcto, dibujada en tiempo real
 * con Canvas (regla 3 de PROMPT_ESPECIFICO: "mecánica de tuberías o caminos
 * usando Canvas de Compose").
 */
@Composable
fun EnrutadorRecursos(
    nodos: List<NodoRuta>,
    rutas: Map<String, String>,
    onConectar: (fuenteClave: String, destinoClave: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val fuentes = remember(nodos) { nodos.filter { it.esFuente } }
    val destinos = remember(nodos) { nodos.filter { !it.esFuente } }

    var contenedorRootPos by remember { mutableStateOf(Offset.Zero) }
    val centrosFuente = remember { mutableStateMapOf<String, Offset>() }
    val boundsDestino = remember { mutableStateMapOf<String, Rect>() }
    var arrastreActivo by remember { mutableStateOf<Pair<String, Offset>?>(null) } // fuenteClave a posición actual (root)

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            "Traza la ruta: toca una fuente y arrastra hasta su destino",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .onGloballyPositioned { contenedorRootPos = it.positionInRoot() }
        ) {
            // Líneas: confirmadas + la que se está arrastrando ahora mismo.
            Canvas(modifier = Modifier.fillMaxSize()) {
                rutas.forEach { (fuenteClave, destinoClave) ->
                    val origen = centrosFuente[fuenteClave] ?: return@forEach
                    val destinoRect = boundsDestino[destinoClave] ?: return@forEach
                    val destinoCentro = Offset(destinoRect.center.x, destinoRect.center.y)
                    drawLine(
                        color = Color(0xFF2FA8D9),
                        start = origen - contenedorRootPos,
                        end = destinoCentro - contenedorRootPos,
                        strokeWidth = 8f
                    )
                }
                arrastreActivo?.let { (fuenteClave, posicionActual) ->
                    val origen = centrosFuente[fuenteClave] ?: return@let
                    drawLine(
                        color = Color(0xFFF5B027),
                        start = origen - contenedorRootPos,
                        end = posicionActual - contenedorRootPos,
                        strokeWidth = 8f,
                        cap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                }
            }

            Row(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.weight(1f).padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    fuentes.forEach { fuente ->
                        val conectado = rutas.containsKey(fuente.clave)
                        NodoRutaVista(
                            nodo = fuente,
                            resaltado = conectado,
                            modifier = Modifier
                                .onGloballyPositioned { coords ->
                                    val pos = coords.positionInRoot()
                                    centrosFuente[fuente.clave] = Offset(
                                        pos.x + coords.size.width / 2f,
                                        pos.y + coords.size.height / 2f
                                    )
                                }
                                .pointerInput(fuente.clave) {
                                    detectDragGestures(
                                        onDragStart = {
                                            val centro = centrosFuente[fuente.clave] ?: return@detectDragGestures
                                            arrastreActivo = fuente.clave to centro
                                        },
                                        onDragEnd = {
                                            val actual = arrastreActivo
                                            if (actual != null) {
                                                val destino = boundsDestino.entries.find { it.value.contains(actual.second) }?.key
                                                if (destino != null) onConectar(fuente.clave, destino)
                                            }
                                            arrastreActivo = null
                                        },
                                        onDragCancel = { arrastreActivo = null },
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            val actual = arrastreActivo ?: return@detectDragGestures
                                            arrastreActivo = actual.first to (actual.second + dragAmount)
                                        }
                                    )
                                }
                        )
                    }
                }
                Column(
                    modifier = Modifier.weight(1f).padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    destinos.forEach { destino ->
                        val conectado = rutas.values.contains(destino.clave)
                        NodoRutaVista(
                            nodo = destino,
                            resaltado = conectado,
                            modifier = Modifier.onGloballyPositioned { coords ->
                                val pos = coords.positionInRoot()
                                boundsDestino[destino.clave] = Rect(pos, Size(coords.size.width.toFloat(), coords.size.height.toFloat()))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NodoRutaVista(nodo: NodoRuta, resaltado: Boolean, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (resaltado) Color(0xFFDCEFE0) else Color.White)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconoElemento(clave = nodo.clave, etiqueta = nodo.etiqueta, tamano = 36.dp)
        Spacer(Modifier.width(8.dp))
        Text(nodo.etiqueta, style = MaterialTheme.typography.labelLarge)
    }
}
