-- ============================================================================
-- EcoCiclos: Guardianes de la Materia — DATOS DE EJEMPLO (semilla)
-- ============================================================================
-- Esta es una copia legible en SQL de los datos que el equipo diseñó en
-- app/src/main/java/com/ecociclos/guardianes/data/local/seed/SeedData.kt,
-- que es la fuente de verdad real que usa la app en tiempo de ejecución
-- (insertada por DatabaseSeeder la primera vez que se abre la app).
--
-- Para biomas, niveles, cartas, insignias, perfil y variables de laboratorio
-- se listan TODAS las filas. Para elementos_nivel y nodos_ruta (el contenido
-- de los puzzles de Restaurador/Enrutador, que suman decenas de filas) se
-- incluye una muestra representativa completa de un nivel de cada mecánica;
-- el resto de los 15 niveles sigue exactamente el mismo patrón y puede
-- consultarse íntegro en SeedData.kt.
-- ============================================================================

-- --------------------------------------------------------------------------
-- BIOMAS (3)
-- --------------------------------------------------------------------------
INSERT INTO biomas (id, nombre, descripcion, tipoCiclo, orden, colorHex, desbloqueado) VALUES
(1, 'El Bosque Seco', 'El ciclo del agua se rompió: los ríos bajan, las hojas se marchitan y la lluvia ya no llega.', 'AGUA', 1, '#3E9B5C', 1),
(2, 'La Ciudad Ahumada', 'El humo cubre el cielo. El ciclo del carbono está desequilibrado y el calor aumenta.', 'CARBONO', 2, '#F5B027', 0),
(3, 'El Río Estancado', 'Los residuos se mezclan en el agua y el nitrógeno se acumula: el río ya no fluye limpio.', 'NITROGENO', 3, '#2FA8D9', 0);

-- --------------------------------------------------------------------------
-- NIVELES (15 = 5 por bioma)
-- --------------------------------------------------------------------------
INSERT INTO niveles (id, biomaId, orden, titulo, tipoReto, dificultad, instruccion) VALUES
(101, 1, 1, 'El ciclo del agua roto', 'RESTAURADOR', 1, 'Arrastra cada elemento del ciclo del agua hacia la etapa donde ocurre.'),
(102, 1, 2, 'Temperatura y sequía', 'LABORATORIO', 1, 'Mueve el termómetro del bosque y observa qué le pasa al suelo.'),
(103, 1, 3, 'Raíces que buscan agua', 'RESTAURADOR', 2, 'Conecta el agua subterránea y el vapor con su etapa correcta del ciclo.'),
(104, 1, 4, 'Cobertura vegetal', 'LABORATORIO', 2, 'Ajusta cuántos árboles se talan y observa el efecto en la lluvia.'),
(105, 1, 5, 'El ciclo completo del bosque', 'RESTAURADOR', 3, 'Reconstruye las cinco etapas completas del ciclo del agua del bosque.'),
(201, 2, 1, 'El ciclo del carbono', 'RESTAURADOR', 1, 'Arrastra cada elemento hacia la etapa del ciclo del carbono donde participa.'),
(202, 2, 2, 'Temperatura global y hielo', 'LABORATORIO', 1, 'Sube o baja la temperatura global y observa qué le pasa al hielo polar.'),
(203, 2, 3, 'Enruta las emisiones de la fábrica', 'ENRUTADOR', 2, 'Traza la ruta correcta para el humo contaminante y para el aire limpio.'),
(204, 2, 4, 'Del combustible al aire', 'RESTAURADOR', 2, 'Conecta cada fuente de combustión con el lugar donde libera carbono.'),
(205, 2, 5, 'Áreas verdes urbanas', 'LABORATORIO', 3, 'Ajusta cuánta superficie de la ciudad no tiene vegetación.'),
(301, 3, 1, 'Separa los residuos de la orilla', 'ENRUTADOR', 1, 'Traza la ruta de cada residuo hacia el destino correcto, sin contaminar el río.'),
(302, 3, 2, 'El ciclo del nitrógeno', 'RESTAURADOR', 2, 'Conecta cada etapa del nitrógeno, desde el aire hasta el suelo y de vuelta.'),
(303, 3, 3, 'Fertilizante y algas', 'LABORATORIO', 2, 'Ajusta cuánto fertilizante llega al río y observa el efecto en los peces.'),
(304, 3, 4, 'Segunda ronda de reciclaje', 'ENRUTADOR', 3, 'Cuatro residuos, cinco destinos posibles: elige la ruta exacta para cada uno.'),
(305, 3, 5, 'Repaso: los tres ciclos', 'RESTAURADOR', 3, 'Un repaso final: una conexión de cada ciclo que aprendiste en tu misión.');

-- --------------------------------------------------------------------------
-- ELEMENTOS DEL RESTAURADOR — muestra completa del nivel 101
-- (el mismo patrón de 4 pares origen→destino se repite en los niveles
--  103, 105, 201, 204, 302 y 305 con sus propias claves; ver SeedData.kt)
-- --------------------------------------------------------------------------
INSERT INTO elementos_nivel (nivelId, clave, etiqueta, esOrigen, destinoCorrectoClave) VALUES
(101, 'sol', 'Sol', 1, 'lago'),
(101, 'lago', 'Lago (Evaporación)', 0, NULL),
(101, 'vapor', 'Vapor de agua', 1, 'nube'),
(101, 'nube', 'Nube (Condensación)', 0, NULL),
(101, 'nube_cargada', 'Nube cargada', 1, 'suelo'),
(101, 'suelo', 'Suelo (Precipitación)', 0, NULL),
(101, 'lluvia', 'Agua de lluvia', 1, 'raiz'),
(101, 'raiz', 'Raíz (Infiltración)', 0, NULL);

-- --------------------------------------------------------------------------
-- NODOS DEL ENRUTADOR — muestra completa del nivel 301
-- (el mismo patrón se repite en los niveles 203 y 304; ver SeedData.kt)
-- --------------------------------------------------------------------------
INSERT INTO nodos_ruta (nivelId, clave, etiqueta, tipoRecurso, esFuente, destinoCorrectoClave) VALUES
(301, 'cascara_fruta', 'Cáscara de fruta', 'ORGANICO', 1, 'compostera'),
(301, 'botella_plastico', 'Botella de plástico', 'PLASTICO', 1, 'planta_reciclaje'),
(301, 'frasco_vidrio', 'Frasco de vidrio', 'VIDRIO', 1, 'centro_vidrio'),
(301, 'compostera', 'Compostera', 'ORGANICO', 0, NULL),
(301, 'planta_reciclaje', 'Planta de reciclaje', 'PLASTICO', 0, NULL),
(301, 'centro_vidrio', 'Centro de vidrio', 'VIDRIO', 0, NULL),
(301, 'rio', 'Río (¡no tires nada aquí!)', 'CONTAMINANTE', 0, NULL);

-- --------------------------------------------------------------------------
-- VARIABLES DEL LABORATORIO (5, una por nivel LABORATORIO — todas incluidas)
-- --------------------------------------------------------------------------
INSERT INTO variables_lab (nivelId, nombre, valorMin, valorMax, valorInicial, umbralCritico, mensajeNormal, mensajeCritico) VALUES
(102, 'Temperatura del bosque (°C)', 15, 45, 22, 35,
 'La humedad del suelo se mantiene y los árboles crecen fuertes.',
 'El calor extremo evapora el agua del suelo más rápido de lo que llueve: comienza la sequía.'),
(104, 'Árboles talados (%)', 0, 100, 10, 60,
 'Las raíces retienen el suelo y el agua de lluvia se infiltra correctamente.',
 'Sin árboles, la lluvia arrastra el suelo fértil y el agua ya no se infiltra: el bosque se erosiona.'),
(202, 'Temperatura global (°C sobre el promedio)', 0, 5, 1, 3,
 'El hielo polar se mantiene estable durante todo el año.',
 'El calor derrite el hielo más rápido de lo que se forma en invierno: el nivel del mar sube.'),
(205, 'Superficie sin vegetación (%)', 50, 100, 60, 85,
 'Los árboles restantes siguen absorbiendo suficiente CO2 para equilibrar la ciudad.',
 'Sin vegetación suficiente, el CO2 se acumula en el aire y la ciudad se calienta más rápido.'),
(303, 'Fertilizante que llega al río (kg/semana)', 0, 200, 30, 120,
 'El nitrógeno se diluye en el agua y los peces del río están sanos.',
 'El exceso de nitrógeno provoca una explosión de algas que consume el oxígeno del agua: los peces sufren.');

-- --------------------------------------------------------------------------
-- CARTAS DE ELEMENTOS (15, todas incluidas)
-- --------------------------------------------------------------------------
INSERT INTO cartas (id, nombre, tipo, biomaId, descripcion, nivelDesbloqueoId) VALUES
(101, 'Sol', 'ELEMENTO', 1, 'Motor de todo el ciclo del agua: calienta lagos y océanos para iniciar la evaporación.', 101),
(102, 'Raíz Sedienta', 'PLANTA', 1, 'Busca agua profunda en el suelo cuando la superficie se seca.', 102),
(103, 'Ciervo del Bosque', 'ANIMAL', 1, 'Depende de los arroyos que nacen del agua infiltrada en el bosque.', 103),
(104, 'Hongo Descomponedor', 'ELEMENTO', 1, 'Ayuda a que el suelo retenga humedad y nutrientes tras la lluvia.', 104),
(105, 'Árbol Guardián', 'PLANTA', 1, 'Un árbol adulto puede liberar cientos de litros de vapor de agua al día.', 105),
(201, 'Nube de Humo', 'ELEMENTO', 2, 'Representa el CO2 liberado por la quema de combustibles.', 201),
(202, 'Oso Polar', 'ANIMAL', 2, 'Su hábitat de hielo depende directamente de la temperatura global.', 202),
(203, 'Filtro de Carbono', 'ELEMENTO', 2, 'Tecnología que captura CO2 antes de que llegue a la atmósfera.', 203),
(204, 'Semilla Urbana', 'PLANTA', 2, 'El inicio de cada nuevo árbol que ayuda a limpiar el aire de la ciudad.', 204),
(205, 'Parque Vertical', 'PLANTA', 2, 'Una pared cubierta de plantas que absorbe carbono en poco espacio.', 205),
(301, 'Pez Plateado', 'ANIMAL', 3, 'Muy sensible a los niveles de oxígeno y nitrógeno del agua.', 301),
(302, 'Bacteria Fijadora', 'ELEMENTO', 3, 'Convierte el nitrógeno del aire en una forma que las plantas pueden usar.', 302),
(303, 'Alga Verde', 'PLANTA', 3, 'Crece sin control cuando hay demasiado fertilizante en el agua.', 303),
(304, 'Centro de Reciclaje', 'ELEMENTO', 3, 'Transforma cada residuo separado en un nuevo recurso útil.', 304),
(305, 'Gota Guardiana', 'ELEMENTO', 3, 'El espíritu del agua que celebra contigo cada bioma restaurado.', 305);

-- --------------------------------------------------------------------------
-- INSIGNIAS DE LA BIOSFERA (4, todas incluidas)
-- --------------------------------------------------------------------------
INSERT INTO insignias (id, nombre, descripcion, biomaId, criterio) VALUES
(1, 'Guardián del Agua', 'Restauraste por completo El Bosque Seco.', 1, 'bioma_1_100'),
(2, 'Maestro del Carbono', 'Restauraste por completo La Ciudad Ahumada.', 2, 'bioma_2_100'),
(3, 'Guardián del Río', 'Restauraste por completo El Río Estancado.', 3, 'bioma_3_100'),
(4, 'Guardián del Planeta', 'Restauraste los tres biomas y equilibraste EcoCiclos.', NULL, 'planeta_100');

-- --------------------------------------------------------------------------
-- PERFIL INICIAL (fila única, id = 0)
-- --------------------------------------------------------------------------
INSERT INTO perfil (id, alias, avatarId, xpTotal) VALUES (0, 'Guardián', 1, 0);

-- progreso_nivel, cartas_desbloqueadas e insignias_desbloqueadas empiezan
-- vacías: se llenan con el juego real del niño, nunca con datos falsos.
