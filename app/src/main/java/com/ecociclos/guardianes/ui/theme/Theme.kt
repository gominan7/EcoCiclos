package com.ecociclos.guardianes.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EsquemaClaroEcoCiclos = lightColorScheme(
    primary = HojaViva,
    onPrimary = TextoClaro,
    secondary = AguaViva,
    onSecondary = TextoClaro,
    tertiary = SolCalido,
    onTertiary = TextoOscuro,
    background = FondoClaro,
    onBackground = TextoOscuro,
    surface = Color.White,
    onSurface = TextoOscuro,
    error = AlertaSuave
)

private val EsquemaOscuroEcoCiclos = darkColorScheme(
    primary = HojaClara,
    onPrimary = TextoOscuro,
    secondary = AguaClara,
    onSecondary = TextoOscuro,
    tertiary = SolSuave,
    onTertiary = TextoOscuro,
    background = FondoOscuro,
    onBackground = TextoClaro,
    surface = FondoOscuro,
    onSurface = TextoClaro,
    error = AlertaSuave
)

@Composable
fun EcoCiclosTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) EsquemaOscuroEcoCiclos else EsquemaClaroEcoCiclos
    MaterialTheme(
        colorScheme = colorScheme,
        typography = EcoCiclosTypography,
        content = content
    )
}
