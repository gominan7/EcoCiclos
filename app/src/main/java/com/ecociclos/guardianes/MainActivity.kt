package com.ecociclos.guardianes

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ecociclos.guardianes.ui.navigation.EcoNavGraph
import com.ecociclos.guardianes.ui.theme.EcoCiclosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // EcoCiclos usa siempre fondo claro (ver ui/theme/Theme.kt), así que
        // los íconos de la barra de estado/navegación deben ser OSCUROS para
        // tener contraste. Sin esto, Android puede dejarlos claros por
        // defecto y volverse invisibles sobre el fondo claro de la app —
        // el mismo tipo de bug reportado por un usuario probando el APK real.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT)
        )
        setContent {
            EcoCiclosRoot()
        }
    }
}

@Composable
private fun EcoCiclosRoot() {
    EcoCiclosTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            EcoNavGraph()
        }
    }
}

