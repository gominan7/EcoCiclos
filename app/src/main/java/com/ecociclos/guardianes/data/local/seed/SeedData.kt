package com.ecociclos.guardianes.data.local.seed

import com.ecociclos.guardianes.data.local.entity.*
import com.ecociclos.guardianes.domain.model.TipoCarta
import com.ecociclos.guardianes.domain.model.TipoCiclo
import com.ecociclos.guardianes.domain.model.TipoReto
import com.ecociclos.guardianes.domain.model.TipoRecurso

/**
 * Datos semilla obligatorios (regla 6 de PROMPT_ESPECIFICO y regla 24 de MASTER_SPEC):
 * 3 biomas y 15 niveles interactivos con contenido real y jugable desde la
 * primera instalación, sin depender de red.
 */
object SeedData {

    // ---------------------------------------------------------------------
    // BIOMAS
    // ---------------------------------------------------------------------
    val biomas = listOf(
        BiomaEntity(
            id = 1,
            nombre = "El Bosque Seco",
            descripcion = "El ciclo del agua se rompió: los ríos bajan, las hojas se marchitan y la lluvia ya no llega.",
            tipoCiclo = TipoCiclo.AGUA.name,
            orden = 1,
            colorHex = "#3E9B5C",
            desbloqueado = true
        ),
        BiomaEntity(
            id = 2,
            nombre = "La Ciudad Ahumada",
            descripcion = "El humo cubre el cielo. El ciclo del carbono está desequilibrado y el calor aumenta.",
            tipoCiclo = TipoCiclo.CARBONO.name,
            orden = 2,
            colorHex = "#F5B027",
            desbloqueado = false
        ),
        BiomaEntity(
            id = 3,
            nombre = "El Río Estancado",
            descripcion = "Los residuos se mezclan en el agua y el nitrógeno se acumula: el río ya no fluye limpio.",
            tipoCiclo = TipoCiclo.NITROGENO.name,
            orden = 3,
            colorHex = "#2FA8D9",
            desbloqueado = false
        )
    )

    // ---------------------------------------------------------------------
    // NIVELES (5 por bioma = 15 en total)
    // ---------------------------------------------------------------------
    val niveles = listOf(
        // --- Bosque Seco ---
        NivelEntity(101, 1, 1, "El ciclo del agua roto", TipoReto.RESTAURADOR.name, 1,
            "Arrastra cada elemento del ciclo del agua hacia la etapa donde ocurre."),
        NivelEntity(102, 1, 2, "Temperatura y sequía", TipoReto.LABORATORIO.name, 1,
            "Mueve el termómetro del bosque y observa qué le pasa al suelo."),
        NivelEntity(103, 1, 3, "Raíces que buscan agua", TipoReto.RESTAURADOR.name, 2,
            "Conecta el agua subterránea y el vapor con su etapa correcta del ciclo."),
        NivelEntity(104, 1, 4, "Cobertura vegetal", TipoReto.LABORATORIO.name, 2,
            "Ajusta cuántos árboles se talan y observa el efecto en la lluvia."),
        NivelEntity(105, 1, 5, "El ciclo completo del bosque", TipoReto.RESTAURADOR.name, 3,
            "Reconstruye las cinco etapas completas del ciclo del agua del bosque."),

        // --- Ciudad Ahumada ---
        NivelEntity(201, 2, 1, "El ciclo del carbono", TipoReto.RESTAURADOR.name, 1,
            "Arrastra cada elemento hacia la etapa del ciclo del carbono donde participa."),
        NivelEntity(202, 2, 2, "Temperatura global y hielo", TipoReto.LABORATORIO.name, 1,
            "Sube o baja la temperatura global y observa qué le pasa al hielo polar."),
        NivelEntity(203, 2, 3, "Enruta las emisiones de la fábrica", TipoReto.ENRUTADOR.name, 2,
            "Traza la ruta correcta para el humo contaminante y para el aire limpio."),
        NivelEntity(204, 2, 4, "Del combustible al aire", TipoReto.RESTAURADOR.name, 2,
            "Conecta cada fuente de combustión con el lugar donde libera carbono."),
        NivelEntity(205, 2, 5, "Áreas verdes urbanas", TipoReto.LABORATORIO.name, 3,
            "Ajusta cuánta superficie de la ciudad no tiene vegetación."),

        // --- Río Estancado ---
        NivelEntity(301, 3, 1, "Separa los residuos de la orilla", TipoReto.ENRUTADOR.name, 1,
            "Traza la ruta de cada residuo hacia el destino correcto, sin contaminar el río."),
        NivelEntity(302, 3, 2, "El ciclo del nitrógeno", TipoReto.RESTAURADOR.name, 2,
            "Conecta cada etapa del nitrógeno, desde el aire hasta el suelo y de vuelta."),
        NivelEntity(303, 3, 3, "Fertilizante y algas", TipoReto.LABORATORIO.name, 2,
            "Ajusta cuánto fertilizante llega al río y observa el efecto en los peces."),
        NivelEntity(304, 3, 4, "Segunda ronda de reciclaje", TipoReto.ENRUTADOR.name, 3,
            "Cuatro residuos, cinco destinos posibles: elige la ruta exacta para cada uno."),
        NivelEntity(305, 3, 5, "Repaso: los tres ciclos", TipoReto.RESTAURADOR.name, 3,
            "Un repaso final: una conexión de cada ciclo que aprendiste en tu misión.")
    )

    // ---------------------------------------------------------------------
    // ELEMENTOS DEL RESTAURADOR DE CICLOS
    // ---------------------------------------------------------------------
    val elementosNivel = listOf(
        // Nivel 101 — ciclo del agua básico
        ElementoNivelEntity(nivelId = 101, clave = "sol", etiqueta = "Sol", esOrigen = true, destinoCorrectoClave = "lago"),
        ElementoNivelEntity(nivelId = 101, clave = "lago", etiqueta = "Lago (Evaporación)", esOrigen = false, destinoCorrectoClave = null),
        ElementoNivelEntity(nivelId = 101, clave = "vapor", etiqueta = "Vapor de agua", esOrigen = true, destinoCorrectoClave = "nube"),
        ElementoNivelEntity(nivelId = 101, clave = "nube", etiqueta = "Nube (Condensación)", esOrigen = false, destinoCorrectoClave = null),
        ElementoNivelEntity(nivelId = 101, clave = "nube_cargada", etiqueta = "Nube cargada", esOrigen = true, destinoCorrectoClave = "suelo"),
        ElementoNivelEntity(nivelId = 101, clave = "suelo", etiqueta = "Suelo (Precipitación)", esOrigen = false, destinoCorrectoClave = null),
        ElementoNivelEntity(nivelId = 101, clave = "lluvia", etiqueta = "Agua de lluvia", esOrigen = true, destinoCorrectoClave = "raiz"),
        ElementoNivelEntity(nivelId = 101, clave = "raiz", etiqueta = "Raíz (Infiltración)", esOrigen = false, destinoCorrectoClave = null),

        // Nivel 103 — agua subterránea y transpiración
        ElementoNivelEntity(nivelId = 103, clave = "raiz_profunda", etiqueta = "Raíz profunda", esOrigen = true, destinoCorrectoClave = "napa"),
        ElementoNivelEntity(nivelId = 103, clave = "napa", etiqueta = "Napa subterránea (Absorción)", esOrigen = false, destinoCorrectoClave = null),
        ElementoNivelEntity(nivelId = 103, clave = "hoja", etiqueta = "Hoja", esOrigen = true, destinoCorrectoClave = "aire"),
        ElementoNivelEntity(nivelId = 103, clave = "aire", etiqueta = "Aire (Transpiración)", esOrigen = false, destinoCorrectoClave = null),
        ElementoNivelEntity(nivelId = 103, clave = "rio", etiqueta = "Río", esOrigen = true, destinoCorrectoClave = "mar"),
        ElementoNivelEntity(nivelId = 103, clave = "mar", etiqueta = "Mar (Escorrentía)", esOrigen = false, destinoCorrectoClave = null),
        ElementoNivelEntity(nivelId = 103, clave = "mar_calido", etiqueta = "Mar cálido", esOrigen = true, destinoCorrectoClave = "atmosfera"),
        ElementoNivelEntity(nivelId = 103, clave = "atmosfera", etiqueta = "Atmósfera (Evaporación oceánica)", esOrigen = false, destinoCorrectoClave = null),

        // Nivel 105 — repaso completo del bosque (5 conexiones)
        ElementoNivelEntity(nivelId = 105, clave = "sol2", etiqueta = "Sol", esOrigen = true, destinoCorrectoClave = "lago2"),
        ElementoNivelEntity(nivelId = 105, clave = "lago2", etiqueta = "Lago (Evaporación)", esOrigen = false, destinoCorrectoClave = null),
        ElementoNivelEntity(nivelId = 105, clave = "vapor2", etiqueta = "Vapor de agua", esOrigen = true, destinoCorrectoClave = "nube2"),
        ElementoNivelEntity(nivelId = 105, clave = "nube2", etiqueta = "Nube (Condensación)", esOrigen = false, destinoCorrectoClave = null),
        ElementoNivelEntity(nivelId = 105, clave = "nube_cargada2", etiqueta = "Nube cargada", esOrigen = true, destinoCorrectoClave = "copa"),
        ElementoNivelEntity(nivelId = 105, clave = "copa", etiqueta = "Copa del árbol (Precipitación)", esOrigen = false, destinoCorrectoClave = null),
        ElementoNivelEntity(nivelId = 105, clave = "lluvia_suelo2", etiqueta = "Agua en el suelo", esOrigen = true, destinoCorrectoClave = "raiz2"),
        ElementoNivelEntity(nivelId = 105, clave = "raiz2", etiqueta = "Raíz (Infiltración)", esOrigen = false, destinoCorrectoClave = null),
        ElementoNivelEntity(nivelId = 105, clave = "agua_raiz2", etiqueta = "Agua absorbida", esOrigen = true, destinoCorrectoClave = "hoja2"),
        ElementoNivelEntity(nivelId = 105, clave = "hoja2", etiqueta = "Hoja (Transpiración)", esOrigen = false, destinoCorrectoClave = null),

        // Nivel 201 — ciclo del carbono
        ElementoNivelEntity(nivelId = 201, clave = "fabrica", etiqueta = "Fábrica", esOrigen = true, destinoCorrectoClave = "atmosfera_c"),
        ElementoNivelEntity(nivelId = 201, clave = "atmosfera_c", etiqueta = "Atmósfera (Emisión de CO2)", esOrigen = false, destinoCorrectoClave = null),
        ElementoNivelEntity(nivelId = 201, clave = "co2_aire", etiqueta = "CO2 en el aire", esOrigen = true, destinoCorrectoClave = "hoja_c"),
        ElementoNivelEntity(nivelId = 201, clave = "hoja_c", etiqueta = "Hoja (Fotosíntesis)", esOrigen = false, destinoCorrectoClave = null),
        ElementoNivelEntity(nivelId = 201, clave = "azucar", etiqueta = "Azúcar de la hoja", esOrigen = true, destinoCorrectoClave = "tronco"),
        ElementoNivelEntity(nivelId = 201, clave = "tronco", etiqueta = "Tronco (Almacenamiento)", esOrigen = false, destinoCorrectoClave = null),
        ElementoNivelEntity(nivelId = 201, clave = "hojarasca", etiqueta = "Hojarasca caída", esOrigen = true, destinoCorrectoClave = "suelo_c"),
        ElementoNivelEntity(nivelId = 201, clave = "suelo_c", etiqueta = "Suelo (Descomposición)", esOrigen = false, destinoCorrectoClave = null),

        // Nivel 204 — fuentes de combustión
        ElementoNivelEntity(nivelId = 204, clave = "auto", etiqueta = "Automóvil", esOrigen = true, destinoCorrectoClave = "tubo_escape"),
        ElementoNivelEntity(nivelId = 204, clave = "tubo_escape", etiqueta = "Tubo de escape (Combustión)", esOrigen = false, destinoCorrectoClave = null),
        ElementoNivelEntity(nivelId = 204, clave = "planta_carbon", etiqueta = "Planta de carbón", esOrigen = true, destinoCorrectoClave = "chimenea"),
        ElementoNivelEntity(nivelId = 204, clave = "chimenea", etiqueta = "Chimenea (Quema de carbón fósil)", esOrigen = false, destinoCorrectoClave = null),
        ElementoNivelEntity(nivelId = 204, clave = "co2_extra", etiqueta = "CO2 extra en el aire", esOrigen = true, destinoCorrectoClave = "hoja_parque"),
        ElementoNivelEntity(nivelId = 204, clave = "hoja_parque", etiqueta = "Hoja del parque (Absorción)", esOrigen = false, destinoCorrectoClave = null),

        // Nivel 302 — ciclo del nitrógeno
        ElementoNivelEntity(nivelId = 302, clave = "bacteria_fijadora", etiqueta = "Bacteria fijadora", esOrigen = true, destinoCorrectoClave = "raiz_planta"),
        ElementoNivelEntity(nivelId = 302, clave = "raiz_planta", etiqueta = "Raíz de la planta (Fijación)", esOrigen = false, destinoCorrectoClave = null),
        ElementoNivelEntity(nivelId = 302, clave = "planta_n", etiqueta = "Planta con nitrógeno", esOrigen = true, destinoCorrectoClave = "herbivoro"),
        ElementoNivelEntity(nivelId = 302, clave = "herbivoro", etiqueta = "Herbívoro (Asimilación)", esOrigen = false, destinoCorrectoClave = null),
        ElementoNivelEntity(nivelId = 302, clave = "desecho_animal", etiqueta = "Desechos del animal", esOrigen = true, destinoCorrectoClave = "suelo_bacteria"),
        ElementoNivelEntity(nivelId = 302, clave = "suelo_bacteria", etiqueta = "Suelo (Amonificación)", esOrigen = false, destinoCorrectoClave = null),
        ElementoNivelEntity(nivelId = 302, clave = "bacteria_suelo", etiqueta = "Bacteria del suelo", esOrigen = true, destinoCorrectoClave = "atmosfera_n2"),
        ElementoNivelEntity(nivelId = 302, clave = "atmosfera_n2", etiqueta = "Atmósfera (Desnitrificación)", esOrigen = false, destinoCorrectoClave = null),

        // Nivel 305 — repaso final de los tres ciclos
        ElementoNivelEntity(nivelId = 305, clave = "sol_final", etiqueta = "Sol", esOrigen = true, destinoCorrectoClave = "lago_final"),
        ElementoNivelEntity(nivelId = 305, clave = "lago_final", etiqueta = "Lago (Ciclo del agua)", esOrigen = false, destinoCorrectoClave = null),
        ElementoNivelEntity(nivelId = 305, clave = "fabrica_final", etiqueta = "Fábrica", esOrigen = true, destinoCorrectoClave = "hoja_final"),
        ElementoNivelEntity(nivelId = 305, clave = "hoja_final", etiqueta = "Hoja (Ciclo del carbono)", esOrigen = false, destinoCorrectoClave = null),
        ElementoNivelEntity(nivelId = 305, clave = "bacteria_final", etiqueta = "Bacteria", esOrigen = true, destinoCorrectoClave = "raiz_final"),
        ElementoNivelEntity(nivelId = 305, clave = "raiz_final", etiqueta = "Raíz (Ciclo del nitrógeno)", esOrigen = false, destinoCorrectoClave = null)
    )

    // ---------------------------------------------------------------------
    // NODOS DEL ENRUTADOR DE RECURSOS
    // ---------------------------------------------------------------------
    val nodosRuta = listOf(
        // Nivel 203 — emisiones de la fábrica
        NodoRutaEntity(nivelId = 203, clave = "humo_fabrica", etiqueta = "Humo de la fábrica", tipoRecurso = TipoRecurso.CONTAMINANTE.name, esFuente = true, destinoCorrectoClave = "planta_captura"),
        NodoRutaEntity(nivelId = 203, clave = "aire_arboles", etiqueta = "Aire filtrado por árboles", tipoRecurso = TipoRecurso.ORGANICO.name, esFuente = true, destinoCorrectoClave = "atmosfera_limpia"),
        NodoRutaEntity(nivelId = 203, clave = "planta_captura", etiqueta = "Planta de captura de carbono", tipoRecurso = TipoRecurso.CONTAMINANTE.name, esFuente = false, destinoCorrectoClave = null),
        NodoRutaEntity(nivelId = 203, clave = "atmosfera_limpia", etiqueta = "Atmósfera limpia", tipoRecurso = TipoRecurso.ORGANICO.name, esFuente = false, destinoCorrectoClave = null),

        // Nivel 301 — separación básica de residuos en la orilla
        NodoRutaEntity(nivelId = 301, clave = "cascara_fruta", etiqueta = "Cáscara de fruta", tipoRecurso = TipoRecurso.ORGANICO.name, esFuente = true, destinoCorrectoClave = "compostera"),
        NodoRutaEntity(nivelId = 301, clave = "botella_plastico", etiqueta = "Botella de plástico", tipoRecurso = TipoRecurso.PLASTICO.name, esFuente = true, destinoCorrectoClave = "planta_reciclaje"),
        NodoRutaEntity(nivelId = 301, clave = "frasco_vidrio", etiqueta = "Frasco de vidrio", tipoRecurso = TipoRecurso.VIDRIO.name, esFuente = true, destinoCorrectoClave = "centro_vidrio"),
        NodoRutaEntity(nivelId = 301, clave = "compostera", etiqueta = "Compostera", tipoRecurso = TipoRecurso.ORGANICO.name, esFuente = false, destinoCorrectoClave = null),
        NodoRutaEntity(nivelId = 301, clave = "planta_reciclaje", etiqueta = "Planta de reciclaje", tipoRecurso = TipoRecurso.PLASTICO.name, esFuente = false, destinoCorrectoClave = null),
        NodoRutaEntity(nivelId = 301, clave = "centro_vidrio", etiqueta = "Centro de vidrio", tipoRecurso = TipoRecurso.VIDRIO.name, esFuente = false, destinoCorrectoClave = null),
        NodoRutaEntity(nivelId = 301, clave = "rio", etiqueta = "Río (¡no tires nada aquí!)", tipoRecurso = TipoRecurso.CONTAMINANTE.name, esFuente = false, destinoCorrectoClave = null),

        // Nivel 304 — segunda ronda, más difícil (4 fuentes, 5 destinos con un señuelo)
        NodoRutaEntity(nivelId = 304, clave = "resto_comida", etiqueta = "Resto de comida", tipoRecurso = TipoRecurso.ORGANICO.name, esFuente = true, destinoCorrectoClave = "compostera2"),
        NodoRutaEntity(nivelId = 304, clave = "envase_plastico", etiqueta = "Envase de plástico", tipoRecurso = TipoRecurso.PLASTICO.name, esFuente = true, destinoCorrectoClave = "reciclaje_plastico2"),
        NodoRutaEntity(nivelId = 304, clave = "lata_metal", etiqueta = "Lata de metal", tipoRecurso = TipoRecurso.METAL.name, esFuente = true, destinoCorrectoClave = "centro_metal"),
        NodoRutaEntity(nivelId = 304, clave = "pila_usada", etiqueta = "Pila usada", tipoRecurso = TipoRecurso.CONTAMINANTE.name, esFuente = true, destinoCorrectoClave = "planta_tratamiento"),
        NodoRutaEntity(nivelId = 304, clave = "compostera2", etiqueta = "Compostera", tipoRecurso = TipoRecurso.ORGANICO.name, esFuente = false, destinoCorrectoClave = null),
        NodoRutaEntity(nivelId = 304, clave = "reciclaje_plastico2", etiqueta = "Planta de plástico", tipoRecurso = TipoRecurso.PLASTICO.name, esFuente = false, destinoCorrectoClave = null),
        NodoRutaEntity(nivelId = 304, clave = "centro_metal", etiqueta = "Centro de metal", tipoRecurso = TipoRecurso.METAL.name, esFuente = false, destinoCorrectoClave = null),
        NodoRutaEntity(nivelId = 304, clave = "planta_tratamiento", etiqueta = "Planta de tratamiento especial", tipoRecurso = TipoRecurso.CONTAMINANTE.name, esFuente = false, destinoCorrectoClave = null),
        NodoRutaEntity(nivelId = 304, clave = "rio2", etiqueta = "Río (señuelo, no es un destino real)", tipoRecurso = TipoRecurso.CONTAMINANTE.name, esFuente = false, destinoCorrectoClave = null)
    )

    // ---------------------------------------------------------------------
    // VARIABLES DEL LABORATORIO DE REACCIONES
    // ---------------------------------------------------------------------
    val variablesLab = listOf(
        VariableLabEntity(
            nivelId = 102, nombre = "Temperatura del bosque (°C)",
            valorMin = 15, valorMax = 45, valorInicial = 22, umbralCritico = 35,
            mensajeNormal = "La humedad del suelo se mantiene y los árboles crecen fuertes.",
            mensajeCritico = "El calor extremo evapora el agua del suelo más rápido de lo que llueve: comienza la sequía."
        ),
        VariableLabEntity(
            nivelId = 104, nombre = "Árboles talados (%)",
            valorMin = 0, valorMax = 100, valorInicial = 10, umbralCritico = 60,
            mensajeNormal = "Las raíces retienen el suelo y el agua de lluvia se infiltra correctamente.",
            mensajeCritico = "Sin árboles, la lluvia arrastra el suelo fértil y el agua ya no se infiltra: el bosque se erosiona."
        ),
        VariableLabEntity(
            nivelId = 202, nombre = "Temperatura global (°C sobre el promedio)",
            valorMin = 0, valorMax = 5, valorInicial = 1, umbralCritico = 3,
            mensajeNormal = "El hielo polar se mantiene estable durante todo el año.",
            mensajeCritico = "El calor derrite el hielo más rápido de lo que se forma en invierno: el nivel del mar sube."
        ),
        VariableLabEntity(
            nivelId = 205, nombre = "Superficie sin vegetación (%)",
            valorMin = 50, valorMax = 100, valorInicial = 60, umbralCritico = 85,
            mensajeNormal = "Los árboles restantes siguen absorbiendo suficiente CO2 para equilibrar la ciudad.",
            mensajeCritico = "Sin vegetación suficiente, el CO2 se acumula en el aire y la ciudad se calienta más rápido."
        ),
        VariableLabEntity(
            nivelId = 303, nombre = "Fertilizante que llega al río (kg/semana)",
            valorMin = 0, valorMax = 200, valorInicial = 30, umbralCritico = 120,
            mensajeNormal = "El nitrógeno se diluye en el agua y los peces del río están sanos.",
            mensajeCritico = "El exceso de nitrógeno provoca una explosión de algas que consume el oxígeno del agua: los peces sufren."
        )
    )

    // ---------------------------------------------------------------------
    // CARTAS DE ELEMENTOS (Eco-pedia)
    // ---------------------------------------------------------------------
    val cartas = listOf(
        CartaEntity(101, "Sol", TipoCarta.ELEMENTO.name, 1, "Motor de todo el ciclo del agua: calienta lagos y océanos para iniciar la evaporación.", 101),
        CartaEntity(102, "Raíz Sedienta", TipoCarta.PLANTA.name, 1, "Busca agua profunda en el suelo cuando la superficie se seca.", 102),
        CartaEntity(103, "Ciervo del Bosque", TipoCarta.ANIMAL.name, 1, "Depende de los arroyos que nacen del agua infiltrada en el bosque.", 103),
        CartaEntity(104, "Hongo Descomponedor", TipoCarta.ELEMENTO.name, 1, "Ayuda a que el suelo retenga humedad y nutrientes tras la lluvia.", 104),
        CartaEntity(105, "Árbol Guardián", TipoCarta.PLANTA.name, 1, "Un árbol adulto puede liberar cientos de litros de vapor de agua al día.", 105),

        CartaEntity(201, "Nube de Humo", TipoCarta.ELEMENTO.name, 2, "Representa el CO2 liberado por la quema de combustibles.", 201),
        CartaEntity(202, "Oso Polar", TipoCarta.ANIMAL.name, 2, "Su hábitat de hielo depende directamente de la temperatura global.", 202),
        CartaEntity(203, "Filtro de Carbono", TipoCarta.ELEMENTO.name, 2, "Tecnología que captura CO2 antes de que llegue a la atmósfera.", 203),
        CartaEntity(204, "Semilla Urbana", TipoCarta.PLANTA.name, 2, "El inicio de cada nuevo árbol que ayuda a limpiar el aire de la ciudad.", 204),
        CartaEntity(205, "Parque Vertical", TipoCarta.PLANTA.name, 2, "Una pared cubierta de plantas que absorbe carbono en poco espacio.", 205),

        CartaEntity(301, "Pez Plateado", TipoCarta.ANIMAL.name, 3, "Muy sensible a los niveles de oxígeno y nitrógeno del agua.", 301),
        CartaEntity(302, "Bacteria Fijadora", TipoCarta.ELEMENTO.name, 3, "Convierte el nitrógeno del aire en una forma que las plantas pueden usar.", 302),
        CartaEntity(303, "Alga Verde", TipoCarta.PLANTA.name, 3, "Crece sin control cuando hay demasiado fertilizante en el agua.", 303),
        CartaEntity(304, "Centro de Reciclaje", TipoCarta.ELEMENTO.name, 3, "Transforma cada residuo separado en un nuevo recurso útil.", 304),
        CartaEntity(305, "Gota Guardiana", TipoCarta.ELEMENTO.name, 3, "El espíritu del agua que celebra contigo cada bioma restaurado.", 305)
    )

    // ---------------------------------------------------------------------
    // INSIGNIAS DE LA BIOSFERA
    // ---------------------------------------------------------------------
    val insignias = listOf(
        InsigniaEntity(1, "Guardián del Agua", "Restauraste por completo El Bosque Seco.", 1, "bioma_1_100"),
        InsigniaEntity(2, "Maestro del Carbono", "Restauraste por completo La Ciudad Ahumada.", 2, "bioma_2_100"),
        InsigniaEntity(3, "Guardián del Río", "Restauraste por completo El Río Estancado.", 3, "bioma_3_100"),
        InsigniaEntity(4, "Guardián del Planeta", "Restauraste los tres biomas y equilibraste EcoCiclos.", null, "planeta_100")
    )

    val perfilInicial = PerfilEntity(id = 0, alias = "Guardián", avatarId = 1, xpTotal = 0)
}
