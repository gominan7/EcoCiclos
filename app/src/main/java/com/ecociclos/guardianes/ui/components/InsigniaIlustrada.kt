package com.ecociclos.guardianes.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

/**
 * Insignia de la Biosfera dibujada como una medalla de escudo con una estrella
 * al centro. Cuando `obtenida` es false se dibuja en tono gris ("silueta
 * bloqueada"), nunca oculta por completo (regla 45 de MASTER_SPEC: coleccionar
 * debe sentirse real y visible como meta).
 */
@Composable
fun InsigniaIlustrada(
    obtenida: Boolean,
    colorHex: String,
    modifier: Modifier = Modifier,
    tamano: Dp = 72.dp
) {
    val colorBase = if (obtenida) parseColorHex(colorHex) else Color(0xFFBFBFB8)
    val colorBorde = if (obtenida) colorBase.copy(alpha = 0.85f) else Color(0xFF9C9C94)

    Canvas(modifier = modifier.size(tamano)) {
        val w = size.width
        val h = size.height
        val centro = Offset(w / 2f, h * 0.45f)
        val radio = w * 0.38f

        // Escudo
        val escudo = Path().apply {
            moveTo(w * 0.5f, h * 0.04f)
            lineTo(w * 0.85f, h * 0.18f)
            lineTo(w * 0.85f, h * 0.5f)
            cubicTo(w * 0.85f, h * 0.78f, w * 0.68f, h * 0.92f, w * 0.5f, h * 0.98f)
            cubicTo(w * 0.32f, h * 0.92f, w * 0.15f, h * 0.78f, w * 0.15f, h * 0.5f)
            lineTo(w * 0.15f, h * 0.18f)
            close()
        }
        drawPath(escudo, colorBase.copy(alpha = if (obtenida) 0.25f else 0.4f))
        drawPath(escudo, colorBorde, style = Stroke(width = w * 0.03f))

        // Estrella central
        val puntos = 5
        val star = Path()
        for (i in 0 until puntos * 2) {
            val r = if (i % 2 == 0) radio * 0.55f else radio * 0.24f
            val angulo = (Math.PI / puntos) * i - Math.PI / 2
            val x = centro.x + (r * cos(angulo)).toFloat()
            val y = centro.y + (r * sin(angulo)).toFloat()
            if (i == 0) star.moveTo(x, y) else star.lineTo(x, y)
        }
        star.close()
        drawPath(star, colorBase)
    }
}

internal fun parseColorHex(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: IllegalArgumentException) {
        Color(0xFF3E9B5C)
    }
}
