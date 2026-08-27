package com.ecociclos.guardianes.data

import com.ecociclos.guardianes.data.local.entity.PerfilEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PerfilDaoTest : RoomTestBase() {

    @Test
    fun `sin perfil guardado, obtener devuelve null (base de datos nueva)`() = runTest {
        assertThat(db.perfilDao().obtener()).isNull()
    }

    @Test
    fun `upsert crea el perfil si no existe`() = runTest {
        db.perfilDao().upsert(PerfilEntity(0, "Guardián", 1, 0))

        val perfil = db.perfilDao().obtener()

        assertThat(perfil?.alias).isEqualTo("Guardián")
        assertThat(perfil?.avatarId).isEqualTo(1)
    }

    @Test
    fun `upsert sobre un perfil existente actualiza en vez de crear un segundo registro`() = runTest {
        db.perfilDao().upsert(PerfilEntity(0, "Guardián", 1, 0))
        db.perfilDao().upsert(PerfilEntity(0, "EcoHéroe", 3, 50))

        assertThat(db.perfilDao().obtener()?.alias).isEqualTo("EcoHéroe")
        assertThat(db.perfilDao().obtener()?.xpTotal).isEqualTo(50)
    }

    @Test
    fun `observar perfil emite el valor actualizado como Flow`() = runTest {
        db.perfilDao().upsert(PerfilEntity(0, "Guardián", 1, 0))
        assertThat(db.perfilDao().observar().first()?.alias).isEqualTo("Guardián")
    }

    @Test
    fun `alias con texto largo se guarda igualmente (validacion de longitud vive en la capa UI)`() = runTest {
        val aliasLargo = "X".repeat(200)
        db.perfilDao().upsert(PerfilEntity(0, aliasLargo, 1, 0))

        assertThat(db.perfilDao().obtener()?.alias).hasLength(200)
    }
}
