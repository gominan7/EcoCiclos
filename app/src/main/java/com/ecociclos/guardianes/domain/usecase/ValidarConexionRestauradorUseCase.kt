package com.ecociclos.guardianes.domain.usecase

import com.ecociclos.guardianes.domain.model.ElementoNivel
import com.ecociclos.guardianes.domain.model.ResultadoConexion
import com.ecociclos.guardianes.domain.model.ResultadoNivel

/**
 * Valida el puzzle "Restaurador de Ciclos" (drag & drop).
 * Cada elemento de origen (ej. "sol") debe soltarse sobre el destino correcto
 * (ej. "lago" -> Evaporación). No hay opción múltiple: la validación depende
 * de en qué destino se soltó cada elemento.
 */
class ValidarConexionRestauradorUseCase {

    operator fun invoke(
        elementos: List<ElementoNivel>,
        conexionesElegidas: Map<String, String>, // claveOrigen -> claveDestinoElegida
        explicaciones: Map<String, String> = emptyMap() // claveOrigen -> texto educativo
    ): ResultadoNivel {
        val origenes = elementos.filter { it.esOrigen && it.destinoCorrectoClave != null }
        require(origenes.isNotEmpty()) { "El nivel no tiene elementos de origen configurados" }

        val resultados = origenes.map { origen ->
            val elegido = conexionesElegidas[origen.clave]
            val correcta = elegido != null && elegido == origen.destinoCorrectoClave
            ResultadoConexion(
                claveOrigen = origen.clave,
                correcta = correcta,
                explicacion = explicaciones[origen.clave]
                    ?: if (correcta) "¡${origen.etiqueta} conectado correctamente!"
                    else "${origen.etiqueta} no va ahí. Piensa en qué parte del ciclo participa."
            )
        }

        val correctas = resultados.count { it.correcta }
        val errores = resultados.size - correctas
        val completado = errores == 0 && conexionesElegidas.size >= origenes.size
        val estrellas = calcularEstrellas(totalPasos = resultados.size, errores = errores, completado = completado)

        return ResultadoNivel(
            nivelId = origenes.first().nivelId,
            totalConexiones = resultados.size,
            conexionesCorrectas = correctas,
            errores = errores,
            completado = completado,
            estrellas = estrellas
        )
    }
}

/** Regla compartida de estrellas: 3 sin errores, 2 con 1 error, 1 con 2+ errores, 0 si no se completó. */
internal fun calcularEstrellas(totalPasos: Int, errores: Int, completado: Boolean): Int {
    if (!completado) return 0
    if (totalPasos <= 0) return 0
    return when {
        errores == 0 -> 3
        errores == 1 -> 2
        else -> 1
    }
}
