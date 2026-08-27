package com.ecociclos.guardianes.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val PALETA_AVATARES = listOf(
    Color(0xFF3E9B5C), Color(0xFF2FA8D9), Color(0xFFF5B027), Color(0xFFCB6B3E),
    Color(0xFF8E5CC7), Color(0xFF2E8B7C), Color(0xFFE2703A), Color(0xFF6B9BD1)
)

/**
 * Avatar local (sin fotos ni cámara): 8 combinaciones de color y accesorio,
 * suficiente variedad para que el niño elija identidad sin exponer datos
 * reales (regla 17 de MASTER_SPEC: nunca exigir nombre real ni foto).
 */
@Composable
fun AvatarIlustrado(avatarId: Int, modifier: Modifier = Modifier, tamano: Dp = 64.dp, seleccionado: Boolean = false) {
    val color = PALETA_AVATARES[(avatarId - 1).coerceIn(0, PALETA_AVATARES.size - 1)]
    Canvas(modifier = modifier.size(tamano)) {
        val w = size.width
        val h = size.height
        val centro = Offset(w / 2f, h / 2f)

        drawCircle(color.copy(alpha = 0.25f), radius = w * 0.48f, center = centro)
        drawCircle(color, radius = w * 0.34f, center = centro)

        // Accesorio distintivo según el número de avatar: forma simple en la parte superior.
        when (avatarId % 4) {
            1 -> drawCircle(Color.White, radius = w * 0.08f, center = Offset(centro.x, h * 0.22f)) // gorro punta
            2 -> drawRect(Color.White, topLeft = Offset(w * 0.4f, h * 0.14f), size = androidx.compose.ui.geometry.Size(w * 0.2f, h * 0.08f)) // banda
            3 -> drawOval(Color.White, topLeft = Offset(w * 0.32f, h * 0.14f), size = androidx.compose.ui.geometry.Size(w * 0.36f, h * 0.12f)) // visera
            else -> {} // sin accesorio
        }

        // Ojos
        drawCircle(Color(0xFF1C2A22), radius = w * 0.035f, center = Offset(centro.x - w * 0.08f, centro.y))
        drawCircle(Color(0xFF1C2A22), radius = w * 0.035f, center = Offset(centro.x + w * 0.08f, centro.y))

        if (seleccionado) {
            drawCircle(Color.White, radius = w * 0.48f, center = centro, style = androidx.compose.ui.graphics.drawscope.Stroke(width = w * 0.05f))
        }
    }
}
