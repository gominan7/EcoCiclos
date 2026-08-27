package com.ecociclos.guardianes.data.local.dao

import androidx.room.*
import com.ecociclos.guardianes.data.local.entity.PerfilEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PerfilDao {
    @Query("SELECT * FROM perfil WHERE id = 0")
    fun observar(): Flow<PerfilEntity?>

    @Query("SELECT * FROM perfil WHERE id = 0")
    suspend fun obtener(): PerfilEntity?

    @Upsert
    suspend fun upsert(entity: PerfilEntity)
}
