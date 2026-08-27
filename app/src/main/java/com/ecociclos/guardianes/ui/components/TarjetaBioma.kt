package com.ecociclos.guardianes.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ecociclos.guardianes.domain.model.Bioma
import com.ecociclos.guardianes.domain.model.EstadoModulo

/**
 * Tarjeta ilustrada de un bioma en el Globo/Dashboard. Nunca es solo
 * "título + botón": incluye color temático, estado visual con icono y texto
 * (regla 19 de MASTER_SPEC: no expresar el estado únicamente con color), y
 * barra de progreso real.
 */
@Composable
fun TarjetaBioma(bioma: Bioma, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val color = parseColorHex(bioma.colorHex)
    val bloqueado = bioma.estado == EstadoModulo.BLOQUEADO

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (bloqueado) Color(0xFFE6E4DA) else color.copy(alpha = 0.16f))
            .clickable(enabled = !bloqueado, onClick = onClick)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text(bioma.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(2.dp))
                Text(
                    text = textoEstado(bioma.estado),
                    style = MaterialTheme.typography.labelLarge,
                    color = if (bloqueado) Color(0xFF8A8A80) else color
                )
            }
            if (bloqueado) {
                Icon(Icons.Filled.Lock, contentDescription = "Bloqueado", tint = Color(0xFF8A8A80))
            } else {
                IconoElemento(clave = bioma.tipoCiclo.name, etiqueta = bioma.nombre, tamano = 44.dp)
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(bioma.descripcion, style = MaterialTheme.typography.bodyMedium, maxLines = 2)
        Spacer(Modifier.height(10.dp))
        BarraProgresoEco(porcentaje = bioma.porcentajeRestaurado, colorRelleno = color)
        Spacer(Modifier.height(4.dp))
        Text(
            "${bioma.nivelesCompletados}/${bioma.totalNiveles} retos · ${bioma.porcentajeRestaurado}% restaurado",
            style = MaterialTheme.typography.labelLarge
        )
    }
}

private fun textoEstado(estado: EstadoModulo): String = when (estado) {
    EstadoModulo.BLOQUEADO -> "Bloqueado"
    EstadoModulo.DISPONIBLE -> "Disponible"
    EstadoModulo.INICIADO -> "En progreso"
    EstadoModulo.COMPLETADO -> "Restaurado"
    EstadoModulo.DOMINADO -> "Dominado"
}
