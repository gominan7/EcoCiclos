package com.ecociclos.guardianes.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ecociclos.guardianes.ui.theme.HojaClara
import com.ecociclos.guardianes.ui.theme.HojaViva

/** Barra de progreso ilustrada, con animación al cambiar el valor (regla 11 de MASTER_SPEC). */
@Composable
fun BarraProgresoEco(
    porcentaje: Int,
    modifier: Modifier = Modifier,
    colorRelleno: Color = HojaViva,
    colorFondo: Color = HojaClara
) {
    val animado by animateFloatAsState(
        targetValue = (porcentaje.coerceIn(0, 100)) / 100f,
        animationSpec = tween(600),
        label = "progreso"
    )
    Canvas(modifier = modifier.fillMaxWidth().height(14.dp)) {
        val radio = CornerRadius(size.height / 2, size.height / 2)
        drawRoundRect(color = colorFondo, cornerRadius = radio)
        if (animado > 0f) {
            drawRoundRect(
                color = colorRelleno,
                size = Size(size.width * animado, size.height),
                cornerRadius = radio
            )
        }
    }
}

/** Tres estrellas de resultado, iluminadas según cuántas se obtuvieron. */
@Composable
fun EstrellasResultado(estrellas: Int, modifier: Modifier = Modifier, tamano: androidx.compose.ui.unit.Dp = 28.dp) {
    androidx.compose.foundation.layout.Row(modifier = modifier) {
        repeat(3) { i ->
            EstrellaIcono(activa = i < estrellas, tamano = tamano)
        }
    }
}

@Composable
private fun EstrellaIcono(activa: Boolean, tamano: androidx.compose.ui.unit.Dp) {
    Canvas(modifier = Modifier.size(tamano)) {
        val w = size.width
        val h = size.height
        val centro = Offset(w / 2f, h / 2f)
        val radioExt = w * 0.48f
        val radioInt = w * 0.2f
        val puntos = 5
        val path = androidx.compose.ui.graphics.Path()
        for (i in 0 until puntos * 2) {
            val r = if (i % 2 == 0) radioExt else radioInt
            val angulo = (Math.PI / puntos) * i - Math.PI / 2
            val x = centro.x + (r * kotlin.math.cos(angulo)).toFloat()
            val y = centro.y + (r * kotlin.math.sin(angulo)).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        drawPath(path, if (activa) Color(0xFFF5B027) else Color(0xFFE0DFD6))
    }
}
