package com.ecociclos.guardianes.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ecociclos.guardianes.data.local.dao.*
import com.ecociclos.guardianes.data.local.entity.*

/**
 * Base de datos Room 100% local (regla 22-23 de MASTER_SPEC): nunca se conecta
 * a servicios remotos, no usa Firebase ni backend alguno.
 */
@Database(
    entities = [
        BiomaEntity::class,
        NivelEntity::class,
        ElementoNivelEntity::class,
        NodoRutaEntity::class,
        VariableLabEntity::class,
        CartaEntity::class,
        InsigniaEntity::class,
        ProgresoNivelEntity::class,
        CartaDesbloqueadaEntity::class,
        InsigniaDesbloqueadaEntity::class,
        PerfilEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun biomaDao(): BiomaDao
    abstract fun nivelDao(): NivelDao
    abstract fun elementoNivelDao(): ElementoNivelDao
    abstract fun nodoRutaDao(): NodoRutaDao
    abstract fun variableLabDao(): VariableLabDao
    abstract fun cartaDao(): CartaDao
    abstract fun cartaDesbloqueadaDao(): CartaDesbloqueadaDao
    abstract fun insigniaDao(): InsigniaDao
    abstract fun insigniaDesbloqueadaDao(): InsigniaDesbloqueadaDao
    abstract fun progresoNivelDao(): ProgresoNivelDao
    abstract fun perfilDao(): PerfilDao

    companion object {
        private const val NOMBRE_DB = "ecociclos.db"

        @Volatile
        private var instancia: AppDatabase? = null

        fun obtener(context: Context): AppDatabase {
            return instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    NOMBRE_DB
                ).build().also { instancia = it }
            }
        }

        /** Usada por los tests para crear una base de datos en memoria (Robolectric). */
        fun enMemoria(context: Context): AppDatabase {
            return Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        }
    }
}
