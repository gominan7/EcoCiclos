package com.ecociclos.guardianes.data.local.dao

import androidx.room.*
import com.ecociclos.guardianes.data.local.entity.ProgresoNivelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgresoNivelDao {
    @Query("SELECT * FROM progreso_nivel WHERE nivelId = :nivelId")
    suspend fun obtenerPorNivel(nivelId: Int): ProgresoNivelEntity?

    @Query("SELECT * FROM progreso_nivel")
    fun observarTodos(): Flow<List<ProgresoNivelEntity>>

    @Query("SELECT * FROM progreso_nivel")
    suspend fun obtenerTodos(): List<ProgresoNivelEntity>

    @Upsert
    suspend fun upsert(entity: ProgresoNivelEntity)
}
