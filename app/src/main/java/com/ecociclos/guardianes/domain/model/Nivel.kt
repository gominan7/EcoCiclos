package com.ecociclos.guardianes.domain.model

/** Un reto/nivel jugable dentro de un bioma. */
data class Nivel(
    val id: Int,
    val biomaId: Int,
    val orden: Int,
    val titulo: String,
    val tipoReto: TipoReto,
    val dificultad: Int, // 1..3
    val instruccion: String,
    val estado: EstadoModulo,
    val estrellas: Int, // 0..3, mejor resultado guardado
    val intentos: Int
)

/** Un elemento arrastrable del puzzle Restaurador de Ciclos. */
data class ElementoNivel(
    val id: Int,
    val nivelId: Int,
    val clave: String,
    val etiqueta: String,
    val esOrigen: Boolean,
    val destinoCorrectoClave: String?
)

/** Un nodo del puzzle Enrutador de Recursos. */
data class NodoRuta(
    val id: Int,
    val nivelId: Int,
    val clave: String,
    val etiqueta: String,
    val tipoRecurso: TipoRecurso,
    val esFuente: Boolean,
    val destinoCorrectoClave: String?
)

/** Una variable manipulable del Laboratorio de Reacciones. */
data class VariableLab(
    val id: Int,
    val nivelId: Int,
    val nombre: String,
    val valorMin: Int,
    val valorMax: Int,
    val valorInicial: Int,
    val umbralCritico: Int,
    val mensajeNormal: String,
    val mensajeCritico: String
)

/** El contenido jugable completo de un nivel, ya ensamblado para la UI. */
data class NivelPuzzle(
    val nivel: Nivel,
    val elementos: List<ElementoNivel> = emptyList(),
    val nodosRuta: List<NodoRuta> = emptyList(),
    val variables: List<VariableLab> = emptyList()
)
