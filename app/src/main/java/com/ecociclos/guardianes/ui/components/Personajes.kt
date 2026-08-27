package com.ecociclos.guardianes.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.ecociclos.guardianes.ui.theme.HojaProfunda
import com.ecociclos.guardianes.ui.theme.HojaViva
import com.ecociclos.guardianes.ui.theme.AguaProfunda
import com.ecociclos.guardianes.ui.theme.AguaViva
import kotlin.math.sin

/**
 * "Brote", el espíritu del bosque (ver PROMPT_ESPECIFICO sección 2). Se dibuja
 * enteramente con Canvas para no depender de assets externos (regla 4 de
 * MASTER_SPEC: ilustraciones locales cuando no hay vectores dedicados).
 */
@Composable
fun Brote(modifier: Modifier = Modifier, tamano: androidx.compose.ui.unit.Dp = 96.dp, animado: Boolean = true) {
    val transicion = rememberInfiniteTransition(label = "brote")
    val flotar by transicion.animateFloat(
        initialValue = 0f,
        targetValue = if (animado) 1f else 0f,
        animationSpec = infiniteRepeatable(tween(1600, easing = LinearEasing), RepeatMode.Reverse),
        label = "flotar"
    )

    Canvas(modifier = modifier.size(tamano)) {
        val w = size.width
        val h = size.height
        val despl = sin(flotar * Math.PI).toFloat() * h * 0.02f

        // Cuerpo
        drawOval(
            color = HojaViva,
            topLeft = Offset(w * 0.22f, h * 0.32f + despl),
            size = androidx.compose.ui.geometry.Size(w * 0.56f, h * 0.55f)
        )
        // Sombra de contorno
        drawOval(
            color = HojaProfunda,
            topLeft = Offset(w * 0.22f, h * 0.32f + despl),
            size = androidx.compose.ui.geometry.Size(w * 0.56f, h * 0.55f),
            style = Stroke(width = w * 0.015f)
        )
        // Ojos
        val ojoY = h * 0.52f + despl
        drawCircle(Color(0xFF1C2A22), radius = w * 0.035f, center = Offset(w * 0.42f, ojoY))
        drawCircle(Color(0xFF1C2A22), radius = w * 0.035f, center = Offset(w * 0.58f, ojoY))
        // Mejillas
        drawCircle(Color(0xFFF6C15A).copy(alpha = 0.6f), radius = w * 0.045f, center = Offset(w * 0.36f, ojoY + h * 0.07f))
        drawCircle(Color(0xFFF6C15A).copy(alpha = 0.6f), radius = w * 0.045f, center = Offset(w * 0.64f, ojoY + h * 0.07f))
        // Hoja en la cabeza
        drawOval(
            color = HojaProfunda,
            topLeft = Offset(w * 0.44f, h * 0.08f + despl),
            size = androidx.compose.ui.geometry.Size(w * 0.16f, h * 0.28f)
        )
        // Pies
        drawOval(HojaProfunda, topLeft = Offset(w * 0.28f, h * 0.82f + despl), size = androidx.compose.ui.geometry.Size(w * 0.16f, h * 0.1f))
        drawOval(HojaProfunda, topLeft = Offset(w * 0.56f, h * 0.82f + despl), size = androidx.compose.ui.geometry.Size(w * 0.16f, h * 0.1f))
    }
}

/** "Gota", la chispa de agua guía. */
@Composable
fun Gota(modifier: Modifier = Modifier, tamano: androidx.compose.ui.unit.Dp = 96.dp, animado: Boolean = true) {
    val transicion = rememberInfiniteTransition(label = "gota")
    val flotar by transicion.animateFloat(
        initialValue = 0f,
        targetValue = if (animado) 1f else 0f,
        animationSpec = infiniteRepeatable(tween(1400, easing = LinearEasing), RepeatMode.Reverse),
        label = "flotarGota"
    )

    Canvas(modifier = modifier.size(tamano)) {
        val w = size.width
        val h = size.height
        val despl = sin(flotar * Math.PI).toFloat() * h * 0.025f

        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.5f, h * 0.12f + despl)
            cubicTo(w * 0.85f, h * 0.5f + despl, w * 0.78f, h * 0.88f + despl, w * 0.5f, h * 0.88f + despl)
            cubicTo(w * 0.22f, h * 0.88f + despl, w * 0.15f, h * 0.5f + despl, w * 0.5f, h * 0.12f + despl)
            close()
        }
        drawPath(path, color = AguaViva)
        drawPath(path, color = AguaProfunda, style = Stroke(width = w * 0.02f))

        val ojoY = h * 0.58f + despl
        drawCircle(Color(0xFF0B2A38), radius = w * 0.035f, center = Offset(w * 0.42f, ojoY))
        drawCircle(Color(0xFF0B2A38), radius = w * 0.035f, center = Offset(w * 0.58f, ojoY))
        // Brillo
        drawOval(
            color = Color.White.copy(alpha = 0.5f),
            topLeft = Offset(w * 0.34f, h * 0.28f + despl),
            size = androidx.compose.ui.geometry.Size(w * 0.14f, h * 0.18f)
        )
    }
}
