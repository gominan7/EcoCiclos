package com.ecociclos.guardianes.data.local.dao

import androidx.room.*
import com.ecociclos.guardianes.data.local.entity.BiomaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BiomaDao {
    @Query("SELECT * FROM biomas ORDER BY orden ASC")
    fun observarTodos(): Flow<List<BiomaEntity>>

    @Query("SELECT * FROM biomas WHERE id = :biomaId")
    suspend fun obtenerPorId(biomaId: Int): BiomaEntity?

    @Query("UPDATE biomas SET desbloqueado = :desbloqueado WHERE id = :biomaId")
    suspend fun actualizarDesbloqueado(biomaId: Int, desbloqueado: Boolean)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarTodos(biomas: List<BiomaEntity>)

    @Query("DELETE FROM biomas WHERE id = :biomaId")
    suspend fun eliminar(biomaId: Int)

    @Query("SELECT COUNT(*) FROM biomas")
    suspend fun contar(): Int
}
