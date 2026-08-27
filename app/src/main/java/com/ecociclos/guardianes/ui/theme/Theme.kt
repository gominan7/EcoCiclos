package com.ecociclos.guardianes.ui.theme

import androidx.compose.material3.MaterialTheme
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

/**
 * EcoCiclos usa siempre el esquema de color CLARO, sin importar el modo
 * oscuro del sistema del dispositivo.
 *
 * Motivo (bug real reportado por un usuario probando el APK en un teléfono
 * en modo oscuro, ver docs/BUILD_REPORT.md): todo el sistema de ilustración
 * y color de la app — Brote, Gota, las tarjetas de bioma, las tarjetas del
 * Restaurador/Enrutador — se diseñó y probó únicamente contra fondos claros
 * fijos. El modo oscuro nunca se implementó de verdad para esos componentes:
 * solo el `MaterialTheme` raíz cambiaba de esquema, así que el texto heredaba
 * color claro (blanco) del tema oscuro mientras las tarjetas seguían con
 * fondo claro fijo, dejando texto blanco sobre fondo blanco — invisible para
 * un niño. Bloquear el tema a claro es la corrección honesta hasta que el
 * modo oscuro se diseñe y pruebe por completo en cada componente.
 */
@Composable
fun EcoCiclosTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EsquemaClaroEcoCiclos,
        typography = EcoCiclosTypography,
        content = content
    )
}

