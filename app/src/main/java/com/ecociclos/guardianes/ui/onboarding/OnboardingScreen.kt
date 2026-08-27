package com.ecociclos.guardianes.ui.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ecociclos.guardianes.EcoCiclosApp
import com.ecociclos.guardianes.ui.components.Brote
import com.ecociclos.guardianes.ui.components.Gota
import kotlinx.coroutines.launch

private data class PaginaOnboarding(val titulo: String, val descripcion: String)

private val PAGINAS = listOf(
    PaginaOnboarding("La Tierra necesita guardianes", "Los ciclos del agua, el carbono y el nitrógeno se han roto en varios biomas. Solo tú puedes restaurarlos."),
    PaginaOnboarding("Conoce a Brote y Gota", "Ellos te guiarán en cada bioma, te explicarán qué se rompió y celebrarán contigo cada avance."),
    PaginaOnboarding("Restaura, enruta y experimenta", "Arrastra elementos para reconstruir ciclos, traza rutas para los recursos y ajusta variables en el laboratorio."),
    PaginaOnboarding("Todo se guarda en tu dispositivo", "EcoCiclos funciona sin conexión. Tu progreso, cartas e insignias se guardan solo en este dispositivo.")
)

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun OnboardingScreen(onFinalizar: () -> Unit) {
    val context = LocalContext.current.applicationContext as EcoCiclosApp
    val pagerState = rememberPagerState(pageCount = { PAGINAS.size })
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF6F5EF))) {
        HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { pagina ->
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when (pagina) {
                    0 -> PlanetaDanado()
                    1 -> Row { Brote(tamano = 84.dp); Spacer(Modifier.width(12.dp)); Gota(tamano = 72.dp) }
                    2 -> Brote(tamano = 100.dp)
                    else -> Gota(tamano = 100.dp)
                }
                Spacer(Modifier.height(24.dp))
                Text(PAGINAS[pagina].titulo, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Spacer(Modifier.height(12.dp))
                Text(PAGINAS[pagina].descripcion, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(PAGINAS.size) { i ->
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(if (i == pagerState.currentPage) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(if (i == pagerState.currentPage) Color(0xFF3E9B5C) else Color(0xFFCFCFC2))
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = {
                scope.launch { context.preferencias.marcarOnboardingCompletado(); onFinalizar() }
            }) { Text("Saltar") }

            Button(onClick = {
                if (pagerState.currentPage < PAGINAS.lastIndex) {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                } else {
                    scope.launch { context.preferencias.marcarOnboardingCompletado(); onFinalizar() }
                }
            }) {
                Text(if (pagerState.currentPage < PAGINAS.lastIndex) "Continuar" else "Empezar la misión")
            }
        }
    }
}

@Composable
private fun PlanetaDanado() {
    androidx.compose.foundation.Canvas(modifier = Modifier.size(120.dp)) {
        drawCircle(Color(0xFF9C9C94), radius = size.minDimension / 2)
        drawCircle(Color(0xFF6B6459), radius = size.minDimension * 0.28f, center = androidx.compose.ui.geometry.Offset(size.width * 0.4f, size.height * 0.4f))
        drawCircle(Color(0xFF3E9B5C).copy(alpha = 0.6f), radius = size.minDimension * 0.18f, center = androidx.compose.ui.geometry.Offset(size.width * 0.65f, size.height * 0.62f))
    }
}
