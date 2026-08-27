package com.ecociclos.guardianes.domain.model

/** Los tres ciclos de la materia que la app enseña, uno por bioma principal. */
enum class TipoCiclo {
    AGUA,
    CARBONO,
    NITROGENO
}

/** Las tres mecánicas educativas obligatorias definidas en el prompt específico. */
enum class TipoReto {
    RESTAURADOR,   // drag & drop de elementos del ciclo
    ENRUTADOR,     // trazado de rutas de recursos (Canvas)
    LABORATORIO    // manipulación de una variable y observación del efecto
}

/** Tipos de recurso que fluyen en el Enrutador de Recursos. */
enum class TipoRecurso {
    ORGANICO,
    PLASTICO,
    VIDRIO,
    METAL,
    CONTAMINANTE
}

enum class TipoCarta {
    PLANTA,
    ANIMAL,
    ELEMENTO
}

/** Estado visual de un módulo (bioma o nivel), regla 19 de MASTER_SPEC. */
enum class EstadoModulo {
    BLOQUEADO,
    DISPONIBLE,
    INICIADO,
    COMPLETADO,
    DOMINADO
}
