-- ============================================================================
-- EcoCiclos: Guardianes de la Materia — ESQUEMA DE BASE DE DATOS (Room/SQLite)
-- ============================================================================
-- Este archivo documenta el esquema tal como lo genera Room a partir de las
-- entidades en app/src/main/java/com/ecociclos/guardianes/data/local/entity/.
-- Room genera y gestiona las tablas automáticamente en tiempo de ejecución;
-- este .sql es una referencia legible para quien no quiera leer Kotlin.
-- 100% local — no existe ningún servidor ni base de datos remota.
-- ============================================================================

CREATE TABLE IF NOT EXISTS biomas (
    id INTEGER NOT NULL PRIMARY KEY,
    nombre TEXT NOT NULL,
    descripcion TEXT NOT NULL,
    tipoCiclo TEXT NOT NULL,          -- AGUA | CARBONO | NITROGENO
    orden INTEGER NOT NULL,
    colorHex TEXT NOT NULL,
    desbloqueado INTEGER NOT NULL     -- 0 = false, 1 = true
);

CREATE TABLE IF NOT EXISTS niveles (
    id INTEGER NOT NULL PRIMARY KEY,
    biomaId INTEGER NOT NULL,
    orden INTEGER NOT NULL,
    titulo TEXT NOT NULL,
    tipoReto TEXT NOT NULL,           -- RESTAURADOR | ENRUTADOR | LABORATORIO
    dificultad INTEGER NOT NULL,      -- 1..3
    instruccion TEXT NOT NULL,
    FOREIGN KEY (biomaId) REFERENCES biomas(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS index_niveles_biomaId ON niveles(biomaId);

CREATE TABLE IF NOT EXISTS elementos_nivel (
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    nivelId INTEGER NOT NULL,
    clave TEXT NOT NULL,
    etiqueta TEXT NOT NULL,
    esOrigen INTEGER NOT NULL,
    destinoCorrectoClave TEXT,
    FOREIGN KEY (nivelId) REFERENCES niveles(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS index_elementos_nivel_nivelId ON elementos_nivel(nivelId);

CREATE TABLE IF NOT EXISTS nodos_ruta (
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    nivelId INTEGER NOT NULL,
    clave TEXT NOT NULL,
    etiqueta TEXT NOT NULL,
    tipoRecurso TEXT NOT NULL,        -- ORGANICO | PLASTICO | VIDRIO | METAL | CONTAMINANTE
    esFuente INTEGER NOT NULL,
    destinoCorrectoClave TEXT,
    FOREIGN KEY (nivelId) REFERENCES niveles(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS index_nodos_ruta_nivelId ON nodos_ruta(nivelId);

CREATE TABLE IF NOT EXISTS variables_lab (
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    nivelId INTEGER NOT NULL,
    nombre TEXT NOT NULL,
    valorMin INTEGER NOT NULL,
    valorMax INTEGER NOT NULL,
    valorInicial INTEGER NOT NULL,
    umbralCritico INTEGER NOT NULL,
    mensajeNormal TEXT NOT NULL,
    mensajeCritico TEXT NOT NULL,
    FOREIGN KEY (nivelId) REFERENCES niveles(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS index_variables_lab_nivelId ON variables_lab(nivelId);

CREATE TABLE IF NOT EXISTS cartas (
    id INTEGER NOT NULL PRIMARY KEY,
    nombre TEXT NOT NULL,
    tipo TEXT NOT NULL,               -- PLANTA | ANIMAL | ELEMENTO
    biomaId INTEGER NOT NULL,
    descripcion TEXT NOT NULL,
    nivelDesbloqueoId INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS insignias (
    id INTEGER NOT NULL PRIMARY KEY,
    nombre TEXT NOT NULL,
    descripcion TEXT NOT NULL,
    biomaId INTEGER,                  -- NULL = insignia global
    criterio TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS progreso_nivel (
    nivelId INTEGER NOT NULL PRIMARY KEY,
    completado INTEGER NOT NULL,
    estrellas INTEGER NOT NULL,       -- 0..3
    intentos INTEGER NOT NULL,
    fechaCompletadoMillis INTEGER
);

CREATE TABLE IF NOT EXISTS cartas_desbloqueadas (
    cartaId INTEGER NOT NULL PRIMARY KEY,
    fechaMillis INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS insignias_desbloqueadas (
    insigniaId INTEGER NOT NULL PRIMARY KEY,
    fechaMillis INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS perfil (
    id INTEGER NOT NULL PRIMARY KEY,  -- siempre 0, fila única
    alias TEXT NOT NULL,
    avatarId INTEGER NOT NULL,
    xpTotal INTEGER NOT NULL
);
