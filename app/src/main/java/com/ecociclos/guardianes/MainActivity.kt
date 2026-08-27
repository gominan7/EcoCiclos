package com.ecociclos.guardianes

import android.os.Bundle
import androidx.activity.ComponentActivity
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
        enableEdgeToEdge()
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
