package com.ecociclos.guardianes.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.ecociclos.guardianes.data.local.AppDatabase
import org.junit.After
import org.junit.Before
import org.robolectric.annotation.Config

/**
 * Base común para los tests de persistencia Room: crea una base de datos en
 * memoria con Robolectric (sin necesidad de un emulador/dispositivo real) y
 * la cierra al finalizar cada test.
 */
@Config(sdk = [34])
abstract class RoomTestBase {
    protected lateinit var db: AppDatabase

    @Before
    fun crearBaseDeDatos() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun cerrarBaseDeDatos() {
        db.close()
    }
}
