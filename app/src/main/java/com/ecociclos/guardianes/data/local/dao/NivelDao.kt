package com.ecociclos.guardianes.data.local.dao

import androidx.room.*
import com.ecociclos.guardianes.data.local.entity.ElementoNivelEntity
import com.ecociclos.guardianes.data.local.entity.NivelEntity
import com.ecociclos.guardianes.data.local.entity.NodoRutaEntity
import com.ecociclos.guardianes.data.local.entity.VariableLabEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NivelDao {
    @Query("SELECT * FROM niveles WHERE biomaId = :biomaId ORDER BY orden ASC")
    fun observarPorBioma(biomaId: Int): Flow<List<NivelEntity>>

    @Query("SELECT * FROM niveles WHERE id = :nivelId")
    suspend fun obtenerPorId(nivelId: Int): NivelEntity?

    @Query("SELECT * FROM niveles ORDER BY biomaId, orden")
    suspend fun obtenerTodos(): List<NivelEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarTodos(niveles: List<NivelEntity>)

    @Query("SELECT COUNT(*) FROM niveles")
    suspend fun contar(): Int
}

@Dao
interface ElementoNivelDao {
    @Query("SELECT * FROM elementos_nivel WHERE nivelId = :nivelId")
    suspend fun obtenerPorNivel(nivelId: Int): List<ElementoNivelEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarTodos(elementos: List<ElementoNivelEntity>)
}

@Dao
interface NodoRutaDao {
    @Query("SELECT * FROM nodos_ruta WHERE nivelId = :nivelId")
    suspend fun obtenerPorNivel(nivelId: Int): List<NodoRutaEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarTodos(nodos: List<NodoRutaEntity>)
}

@Dao
interface VariableLabDao {
    @Query("SELECT * FROM variables_lab WHERE nivelId = :nivelId")
    suspend fun obtenerPorNivel(nivelId: Int): List<VariableLabEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarTodos(variables: List<VariableLabEntity>)
}
