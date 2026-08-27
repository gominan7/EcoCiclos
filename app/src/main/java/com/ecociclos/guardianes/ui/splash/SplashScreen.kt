package com.ecociclos.guardianes.ui.splash

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ecociclos.guardianes.EcoCiclosApp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

/**
 * "Una semilla cayendo en la tierra que rápidamente brota hasta formar el
 * logo del juego" (regla 5.1 de PROMPT_ESPECIFICO). Toda la animación se
 * dibuja con Canvas, sin imágenes externas.
 */
@Composable
fun SplashScreen(onTerminado: (mostrarOnboarding: Boolean) -> Unit) {
    val context = LocalContext.current.applicationContext as EcoCiclosApp
    var progreso by remember { mutableStateOf(0f) }
    val animado by animateFloatAsState(
        targetValue = progreso,
        animationSpec = tween(1400, easing = LinearOutSlowInEasing),
        label = "splash"
    )

    LaunchedEffect(Unit) {
        progreso = 1f
        delay(1700)
        val yaVioOnboarding = context.preferencias.onboardingCompletado.first()
        onTerminado(!yaVioOnboarding)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B5E3F)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Canvas(modifier = Modifier.size(140.dp)) {
                val w = size.width
                val h = size.height
                val caidaSemilla = (1f - (animado.coerceIn(0f, 0.4f) / 0.4f))
                val crecimiento = ((animado - 0.4f).coerceIn(0f, 0.6f) / 0.6f)

                // Tierra
                drawRect(Color(0xFF3E2C1F), topLeft = Offset(0f, h * 0.82f), size = androidx.compose.ui.geometry.Size(w, h * 0.18f))

                // Semilla cayendo
                if (crecimiento < 1f) {
                    val ySemilla = h * 0.2f + (h * 0.6f * caidaSemilla)
                    drawOval(Color(0xFFD9A441), topLeft = Offset(w * 0.46f, ySemilla), size = androidx.compose.ui.geometry.Size(w * 0.08f, h * 0.1f))
                }

                // Tallo y hoja crecen desde la tierra
                if (crecimiento > 0f) {
                    val alturaTallo = h * 0.5f * crecimiento
                    drawLine(
                        color = Color(0xFF8FD9A8),
                        start = Offset(w * 0.5f, h * 0.82f),
                        end = Offset(w * 0.5f, h * 0.82f - alturaTallo),
                        strokeWidth = w * 0.035f
                    )
                    if (crecimiento > 0.5f) {
                        val hojaAlpha = ((crecimiento - 0.5f) / 0.5f).coerceIn(0f, 1f)
                        drawOval(
                            color = Color(0xFF8FD9A8).copy(alpha = hojaAlpha),
                            topLeft = Offset(w * 0.5f, h * 0.82f - alturaTallo - h * 0.06f),
                            size = androidx.compose.ui.geometry.Size(w * 0.22f, h * 0.14f)
                        )
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
            Text("EcoCiclos", style = MaterialTheme.typography.headlineLarge, color = Color.White, fontWeight = FontWeight.Bold)
            Text("Guardianes de la Materia", style = MaterialTheme.typography.titleMedium, color = Color(0xFFDCEFE0))
        }
    }
}
