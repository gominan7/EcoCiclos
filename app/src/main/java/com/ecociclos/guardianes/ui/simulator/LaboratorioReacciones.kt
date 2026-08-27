package com.ecociclos.guardianes.ui.simulator

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ecociclos.guardianes.domain.model.VariableLab
import com.ecociclos.guardianes.domain.usecase.EfectoLaboratorio
import com.ecociclos.guardianes.domain.usecase.SeveridadLab

/**
 * Puzzle "Laboratorio de Reacciones". El niño mueve una variable real y
 * observa, sin texto aleatorio, un efecto calculado matemáticamente a partir
 * de la distancia al umbral crítico (regla 45 de MASTER_SPEC: un simulador
 * debe modificar variables y calcular resultados, no solo cambiar texto).
 */
@Composable
fun LaboratorioReacciones(
    variable: VariableLab,
    valorActual: Int,
    efecto: EfectoLaboratorio?,
    onValorCambiado: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text(variable.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        VisualizacionLaboratorio(
            magnitud = efecto?.magnitudEfecto ?: 0f,
            severidad = efecto?.severidad ?: SeveridadLab.NORMAL,
            modifier = Modifier.fillMaxWidth().height(180.dp)
        )

        Spacer(Modifier.height(20.dp))
        Text("Valor actual: $valorActual", style = MaterialTheme.typography.bodyLarge)
        Slider(
            value = valorActual.toFloat(),
            onValueChange = { onValorCambiado(it.toInt()) },
            valueRange = variable.valorMin.toFloat()..variable.valorMax.toFloat(),
            steps = (variable.valorMax - variable.valorMin - 1).coerceAtLeast(0)
        )

        Spacer(Modifier.height(12.dp))
        if (efecto != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(colorSeveridad(efecto.severidad).copy(alpha = 0.16f))
                    .padding(14.dp)
            ) {
                Text(
                    textoSeveridad(efecto.severidad),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorSeveridad(efecto.severidad)
                )
                Spacer(Modifier.height(4.dp))
                Text(efecto.mensaje, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun VisualizacionLaboratorio(magnitud: Float, severidad: SeveridadLab, modifier: Modifier = Modifier) {
    val animado by animateFloatAsState(targetValue = magnitud, animationSpec = tween(400), label = "labMagnitud")
    val color = colorSeveridad(severidad)

    Canvas(modifier = modifier.clip(RoundedCornerShape(16.dp)).background(Color(0xFFEFF6F1))) {
        val w = size.width
        val h = size.height

        // Barra de "salud del ecosistema": se reduce a medida que la magnitud crece.
        val alturaSana = h * (1f - animado)
        drawRect(
            color = Color(0xFF3E9B5C).copy(alpha = 0.35f),
            topLeft = Offset(0f, h - alturaSana),
            size = Size(w, alturaSana)
        )
        drawRect(
            color = color.copy(alpha = 0.55f),
            topLeft = Offset(0f, 0f),
            size = Size(w, h - alturaSana)
        )

        // Marcador de nivel actual
        val yMarcador = h - (h * animado)
        drawLine(
            color = color,
            start = Offset(0f, yMarcador),
            end = Offset(w, yMarcador),
            strokeWidth = 6f
        )
    }
}

private fun colorSeveridad(severidad: SeveridadLab): Color = when (severidad) {
    SeveridadLab.NORMAL -> Color(0xFF3E9B5C)
    SeveridadLab.ALERTA -> Color(0xFFF5B027)
    SeveridadLab.CRITICO -> Color(0xFFE2703A)
}

private fun textoSeveridad(severidad: SeveridadLab): String = when (severidad) {
    SeveridadLab.NORMAL -> "Ecosistema estable"
    SeveridadLab.ALERTA -> "Empieza a desequilibrarse"
    SeveridadLab.CRITICO -> "¡Estado crítico!"
}
