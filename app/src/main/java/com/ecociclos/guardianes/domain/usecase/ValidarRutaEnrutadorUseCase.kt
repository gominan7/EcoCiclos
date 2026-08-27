package com.ecociclos.guardianes.domain.usecase

import com.ecociclos.guardianes.domain.model.NodoRuta
import com.ecociclos.guardianes.domain.model.ResultadoConexion
import com.ecociclos.guardianes.domain.model.ResultadoNivel

/**
 * Valida el puzzle "Enrutador de Recursos": el niño traza, para cada nodo fuente
 * (ej. "residuo orgánico"), una ruta hacia un nodo destino (ej. "compostera").
 * Se considera "contaminación" cuando el tipo de recurso de la fuente no coincide
 * con el tipo aceptado por el destino elegido (ej. plástico hacia el río).
 */
class ValidarRutaEnrutadorUseCase {

    operator fun invoke(
        nodos: List<NodoRuta>,
        rutasElegidas: Map<String, String> // claveFuente -> claveDestinoElegido
    ): ResultadoNivel {
        val fuentes = nodos.filter { it.esFuente }
        val destinosPorClave = nodos.filter { !it.esFuente }.associateBy { it.clave }
        require(fuentes.isNotEmpty()) { "El nivel no tiene nodos fuente configurados" }

        val resultados = fuentes.map { fuente ->
            val claveDestino = rutasElegidas[fuente.clave]
            val destino = claveDestino?.let { destinosPorClave[it] }

            val tipoCoincide = destino != null && destino.tipoRecurso == fuente.tipoRecurso
            val esRutaCorrecta = claveDestino != null && claveDestino == fuente.destinoCorrectoClave
            val correcta = esRutaCorrecta && tipoCoincide

            val explicacion = when {
                correcta -> "${fuente.etiqueta} llega limpio a ${destino?.etiqueta ?: "su destino"}."
                destino == null -> "${fuente.etiqueta} todavía no tiene una ruta trazada."
                !tipoCoincide -> "${fuente.etiqueta} contamina ${destino.etiqueta}: no es el tipo de recurso correcto."
                else -> "${fuente.etiqueta} no va hacia ${destino.etiqueta}. Revisa el mapa de flujo."
            }

            ResultadoConexion(claveOrigen = fuente.clave, correcta = correcta, explicacion = explicacion)
        }

        val correctas = resultados.count { it.correcta }
        val errores = resultados.size - correctas
        val completado = errores == 0
        val estrellas = calcularEstrellas(totalPasos = resultados.size, errores = errores, completado = completado)

        return ResultadoNivel(
            nivelId = fuentes.first().nivelId,
            totalConexiones = resultados.size,
            conexionesCorrectas = correctas,
            errores = errores,
            completado = completado,
            estrellas = estrellas
        )
    }
}
