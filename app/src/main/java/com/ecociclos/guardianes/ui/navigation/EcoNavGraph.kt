package com.ecociclos.guardianes.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ecociclos.guardianes.ui.bioma.BiomaDetailScreen
import com.ecociclos.guardianes.ui.ecopedia.EcopediaScreen
import com.ecociclos.guardianes.ui.globe.GlobeDashboardScreen
import com.ecociclos.guardianes.ui.onboarding.OnboardingScreen
import com.ecociclos.guardianes.ui.perfil.PerfilScreen
import com.ecociclos.guardianes.ui.simulator.SimuladorScreen
import com.ecociclos.guardianes.ui.splash.SplashScreen

@Composable
fun EcoNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.SPLASH) {

        composable(Routes.SPLASH) {
            SplashScreen(onTerminado = { mostrarOnboarding ->
                val destino = if (mostrarOnboarding) Routes.ONBOARDING else Routes.GLOBO
                navController.navigate(destino) { popUpTo(Routes.SPLASH) { inclusive = true } }
            })
        }

        composable(Routes.ONBOARDING) {
            OnboardingScreen(onFinalizar = {
                navController.navigate(Routes.GLOBO) { popUpTo(Routes.ONBOARDING) { inclusive = true } }
            })
        }

        composable(Routes.GLOBO) {
            GlobeDashboardScreen(
                onAbrirBioma = { biomaId -> navController.navigate(Routes.biomaDetalle(biomaId)) },
                onAbrirEcopedia = { navController.navigate(Routes.ECOPEDIA) },
                onAbrirPerfil = { navController.navigate(Routes.PERFIL) }
            )
        }

        composable(
            route = Routes.BIOMA_DETALLE,
            arguments = listOf(navArgument("biomaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val biomaId = backStackEntry.arguments?.getInt("biomaId") ?: return@composable
            BiomaDetailScreen(
                biomaId = biomaId,
                onVolver = { navController.popBackStack() },
                onJugarNivel = { nivelId -> navController.navigate(Routes.simulador(nivelId)) }
            )
        }

        composable(
            route = Routes.SIMULADOR,
            arguments = listOf(navArgument("nivelId") { type = NavType.IntType })
        ) { backStackEntry ->
            val nivelId = backStackEntry.arguments?.getInt("nivelId") ?: return@composable
            SimuladorScreen(
                nivelId = nivelId,
                onVolver = { navController.popBackStack() },
                onSiguienteNivel = { navController.popBackStack() }
            )
        }

        composable(Routes.ECOPEDIA) {
            EcopediaScreen(onVolver = { navController.popBackStack() })
        }

        composable(Routes.PERFIL) {
            PerfilScreen(onVolver = { navController.popBackStack() })
        }
    }
}
