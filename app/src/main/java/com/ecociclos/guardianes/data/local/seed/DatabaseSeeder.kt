package com.ecociclos.guardianes.data.local.seed

import com.ecociclos.guardianes.data.local.AppDatabase

/**
 * Inserta los datos semilla la primera vez que se abre la app. Es idempotente:
 * si ya hay biomas guardados, no vuelve a insertar nada (evita duplicados en
 * cada reinicio, regla 29 de MASTER_SPEC: "reinicio" es un caso límite a probar).
 */
class DatabaseSeeder(private val db: AppDatabase) {

    suspend fun sembrarSiEsNecesario() {
        if (db.biomaDao().contar() > 0) return

        db.biomaDao().insertarTodos(SeedData.biomas)
        db.nivelDao().insertarTodos(SeedData.niveles)
        db.elementoNivelDao().insertarTodos(SeedData.elementosNivel)
        db.nodoRutaDao().insertarTodos(SeedData.nodosRuta)
        db.variableLabDao().insertarTodos(SeedData.variablesLab)
        db.cartaDao().insertarTodas(SeedData.cartas)
        db.insigniaDao().insertarTodas(SeedData.insignias)
        db.perfilDao().upsert(SeedData.perfilInicial)
    }
}
