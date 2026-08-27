package com.ecociclos.guardianes.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

/** Categorías visuales derivadas de la clave/etiqueta del elemento del puzzle. */
internal enum class FormaElemento { SOL, NUBE, AGUA, PLANTA, INDUSTRIA, BACTERIA, ANIMAL, RECICLAJE, AIRE, GENERICO }

internal fun clasificarForma(clave: String, etiqueta: String): FormaElemento {
    val texto = (clave + " " + etiqueta).lowercase()
    return when {
        "sol" in texto -> FormaElemento.SOL
        "nube" in texto -> FormaElemento.NUBE
        "lago" in texto || "agua" in texto || "rio" in texto || "río" in texto || "mar" in texto || "napa" in texto || "lluvia" in texto -> FormaElemento.AGUA
        "raiz" in texto || "planta" in texto || "hoja" in texto || "arbol" in texto || "árbol" in texto || "copa" in texto || "tronco" in texto || "semilla" in texto || "alga" in texto || "azucar" in texto -> FormaElemento.PLANTA
        "fabrica" in texto || "fábrica" in texto || "chimenea" in texto || "auto" in texto || "tubo" in texto || "carbon" in texto -> FormaElemento.INDUSTRIA
        "bacteria" in texto -> FormaElemento.BACTERIA
        "herbivoro" in texto || "herbívoro" in texto || "animal" in texto || "pez" in texto || "ciervo" in texto || "oso" in texto -> FormaElemento.ANIMAL
        "reciclaje" in texto || "compostera" in texto || "vidrio" in texto || "metal" in texto || "plastico" in texto || "plástico" in texto || "tratamiento" in texto || "lata" in texto || "envase" in texto || "pila" in texto -> FormaElemento.RECICLAJE
        "atmosfera" in texto || "atmósfera" in texto || "aire" in texto || "vapor" in texto -> FormaElemento.AIRE
        else -> FormaElemento.GENERICO
    }
}

private fun colorParaForma(forma: FormaElemento): Color = when (forma) {
    FormaElemento.SOL -> Color(0xFFF5B027)
    FormaElemento.NUBE -> Color(0xFFB9C6CC)
    FormaElemento.AGUA -> Color(0xFF2FA8D9)
    FormaElemento.PLANTA -> Color(0xFF3E9B5C)
    FormaElemento.INDUSTRIA -> Color(0xFF6B6459)
    FormaElemento.BACTERIA -> Color(0xFF8E5CC7)
    FormaElemento.ANIMAL -> Color(0xFFCB6B3E)
    FormaElemento.RECICLAJE -> Color(0xFF2E8B7C)
    FormaElemento.AIRE -> Color(0xFFA6E3F4)
    FormaElemento.GENERICO -> Color(0xFF8FA79B)
}

/**
 * Icono ilustrado (100% vectorial, offline) para un elemento del puzzle.
 * Se usa tanto en el Restaurador de Ciclos como en el Enrutador de Recursos
 * y en las Cartas de la Eco-pedia, para que cada clave tenga una silueta
 * reconocible en vez de un simple cuadro de texto (regla 32 de MASTER_SPEC).
 */
@Composable
fun IconoElemento(
    clave: String,
    etiqueta: String,
    modifier: Modifier = Modifier,
    tamano: Dp = 56.dp
) {
    val forma = clasificarForma(clave, etiqueta)
    val color = colorParaForma(forma)

    Canvas(modifier = modifier.size(tamano)) {
        val w = size.width
        val h = size.height
        val centro = Offset(w / 2f, h / 2f)

        when (forma) {
            FormaElemento.SOL -> {
                drawCircle(color, radius = w * 0.28f, center = centro)
                repeat(8) { i ->
                    val angulo = (i * 45f) * (Math.PI / 180f)
                    val r1 = w * 0.34f
                    val r2 = w * 0.46f
                    drawLine(
                        color = color,
                        start = Offset(centro.x + (r1 * cos(angulo)).toFloat(), centro.y + (r1 * sin(angulo)).toFloat()),
                        end = Offset(centro.x + (r2 * cos(angulo)).toFloat(), centro.y + (r2 * sin(angulo)).toFloat()),
                        strokeWidth = w * 0.05f
                    )
                }
            }
            FormaElemento.NUBE -> {
                drawCircle(color, radius = w * 0.2f, center = Offset(w * 0.38f, h * 0.55f))
                drawCircle(color, radius = w * 0.24f, center = Offset(w * 0.55f, h * 0.45f))
                drawCircle(color, radius = w * 0.18f, center = Offset(w * 0.68f, h * 0.56f))
                drawOval(color, topLeft = Offset(w * 0.22f, h * 0.5f), size = Size(w * 0.56f, h * 0.24f))
            }
            FormaElemento.AGUA -> {
                val path = Path().apply {
                    moveTo(w * 0.5f, h * 0.16f)
                    cubicTo(w * 0.82f, h * 0.5f, w * 0.74f, h * 0.86f, w * 0.5f, h * 0.86f)
                    cubicTo(w * 0.26f, h * 0.86f, w * 0.18f, h * 0.5f, w * 0.5f, h * 0.16f)
                    close()
                }
                drawPath(path, color)
            }
            FormaElemento.PLANTA -> {
                drawLine(Color(0xFF1B5E3F), Offset(centro.x, h * 0.85f), Offset(centro.x, h * 0.4f), strokeWidth = w * 0.05f)
                val hoja = Path().apply {
                    moveTo(centro.x, h * 0.4f)
                    cubicTo(w * 0.85f, h * 0.22f, w * 0.8f, h * 0.05f, w * 0.5f, h * 0.12f)
                    cubicTo(w * 0.2f, h * 0.05f, w * 0.15f, h * 0.22f, centro.x, h * 0.4f)
                    close()
                }
                drawPath(hoja, color)
            }
            FormaElemento.INDUSTRIA -> {
                drawRect(color, topLeft = Offset(w * 0.22f, h * 0.42f), size = Size(w * 0.56f, h * 0.44f))
                drawRect(color, topLeft = Offset(w * 0.4f, h * 0.12f), size = Size(w * 0.18f, h * 0.32f))
                drawOval(Color(0xFF9C9C94).copy(alpha = 0.7f), topLeft = Offset(w * 0.42f, h * 0.0f), size = Size(w * 0.3f, h * 0.2f))
            }
            FormaElemento.BACTERIA -> {
                drawOval(color, topLeft = Offset(w * 0.25f, h * 0.35f), size = Size(w * 0.5f, h * 0.3f))
                repeat(6) { i ->
                    val angulo = (i * 60f) * (Math.PI / 180f)
                    val origen = Offset(centro.x + (w * 0.25f * cos(angulo)).toFloat(), centro.y + (w * 0.15f * sin(angulo)).toFloat())
                    val destino = Offset(centro.x + (w * 0.42f * cos(angulo)).toFloat(), centro.y + (w * 0.26f * sin(angulo)).toFloat())
                    drawLine(color, origen, destino, strokeWidth = w * 0.025f)
                }
            }
            FormaElemento.ANIMAL -> {
                drawOval(color, topLeft = Offset(w * 0.2f, h * 0.35f), size = Size(w * 0.5f, h * 0.35f))
                drawCircle(color, radius = w * 0.16f, center = Offset(w * 0.72f, h * 0.42f))
                drawCircle(Color.White, radius = w * 0.03f, center = Offset(w * 0.76f, h * 0.4f))
            }
            FormaElemento.RECICLAJE -> {
                val radio = w * 0.28f
                repeat(3) { i ->
                    val inicio = 90f + i * 120f
                    val path = Path().apply {
                        addArc(
                            androidx.compose.ui.geometry.Rect(centro - Offset(radio, radio), Size(radio * 2, radio * 2)),
                            inicio, 90f
                        )
                    }
                    drawPath(path, color, style = Stroke(width = w * 0.06f))
                }
            }
            FormaElemento.AIRE -> {
                repeat(3) { i ->
                    val y = h * (0.35f + i * 0.16f)
                    val path = Path().apply {
                        moveTo(w * 0.18f, y)
                        cubicTo(w * 0.35f, y - h * 0.06f, w * 0.45f, y + h * 0.06f, w * 0.62f, y)
                        cubicTo(w * 0.72f, y - h * 0.04f, w * 0.78f, y + h * 0.02f, w * 0.85f, y)
                    }
                    drawPath(path, color, style = Stroke(width = w * 0.035f))
                }
            }
            FormaElemento.GENERICO -> {
                drawRoundRect(
                    color = color,
                    topLeft = Offset(w * 0.2f, h * 0.2f),
                    size = Size(w * 0.6f, h * 0.6f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.12f, w * 0.12f)
                )
            }
        }
    }
}
