package com.ecociclos.guardianes

import android.app.Application
import com.ecociclos.guardianes.data.local.AppDatabase
import com.ecociclos.guardianes.data.local.PreferenciasApp
import com.ecociclos.guardianes.data.local.seed.DatabaseSeeder
import com.ecociclos.guardianes.data.repository.EcoRepositoryImpl
import com.ecociclos.guardianes.domain.repository.EcoRepository
import com.ecociclos.guardianes.domain.usecase.CompletarNivelUseCase
import com.ecociclos.guardianes.domain.usecase.EvaluarLaboratorioUseCase
import com.ecociclos.guardianes.domain.usecase.ObtenerSiguienteNivelUseCase
import com.ecociclos.guardianes.domain.usecase.ValidarConexionRestauradorUseCase
import com.ecociclos.guardianes.domain.usecase.ValidarRutaEnrutadorUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Contenedor de dependencias manual y simple (regla 21/22 de MASTER_SPEC:
 * arquitectura MVVM + Clean, sin necesidad de un framework de DI para el
 * tamaño de este proyecto). Se siembra la base de datos una sola vez al
 * arrancar el proceso.
 */
class EcoCiclosApp : Application() {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    lateinit var database: AppDatabase
        private set
    lateinit var repository: EcoRepository
        private set
    lateinit var preferencias: PreferenciasApp
        private set

    // Use cases expuestos como singletons livianos: son clases sin estado mutable.
    val validarConexionRestaurador = ValidarConexionRestauradorUseCase()
    val validarRutaEnrutador = ValidarRutaEnrutadorUseCase()
    val evaluarLaboratorio = EvaluarLaboratorioUseCase()
    val obtenerSiguienteNivel = ObtenerSiguienteNivelUseCase()
    lateinit var completarNivel: CompletarNivelUseCase
        private set

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.obtener(this)
        repository = EcoRepositoryImpl(database)
        preferencias = PreferenciasApp(this)
        completarNivel = CompletarNivelUseCase(repository)

        appScope.launch {
            DatabaseSeeder(database).sembrarSiEsNecesario()
        }
    }
}
