package com.ecociclos.guardianes.data.local.dao

import androidx.room.*
import com.ecociclos.guardianes.data.local.entity.CartaDesbloqueadaEntity
import com.ecociclos.guardianes.data.local.entity.CartaEntity
import com.ecociclos.guardianes.data.local.entity.InsigniaDesbloqueadaEntity
import com.ecociclos.guardianes.data.local.entity.InsigniaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartaDao {
    @Query("SELECT * FROM cartas ORDER BY id ASC")
    fun observarTodas(): Flow<List<CartaEntity>>

    @Query("SELECT * FROM cartas WHERE nivelDesbloqueoId = :nivelId LIMIT 1")
    suspend fun obtenerPorNivelDesbloqueo(nivelId: Int): CartaEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarTodas(cartas: List<CartaEntity>)
}

@Dao
interface CartaDesbloqueadaDao {
    @Query("SELECT cartaId FROM cartas_desbloqueadas")
    fun observarIdsDesbloqueadas(): Flow<List<Int>>

    @Query("SELECT EXISTS(SELECT 1 FROM cartas_desbloqueadas WHERE cartaId = :cartaId)")
    suspend fun estaDesbloqueada(cartaId: Int): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(entity: CartaDesbloqueadaEntity)
}

@Dao
interface InsigniaDao {
    @Query("SELECT * FROM insignias ORDER BY id ASC")
    fun observarTodas(): Flow<List<InsigniaEntity>>

    @Query("SELECT * FROM insignias WHERE biomaId = :biomaId OR biomaId IS NULL")
    suspend fun obtenerCandidatas(biomaId: Int?): List<InsigniaEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarTodas(insignias: List<InsigniaEntity>)
}

@Dao
interface InsigniaDesbloqueadaDao {
    @Query("SELECT insigniaId FROM insignias_desbloqueadas")
    fun observarIdsDesbloqueadas(): Flow<List<Int>>

    @Query("SELECT insigniaId FROM insignias_desbloqueadas")
    suspend fun obtenerIdsUnaVez(): List<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarTodas(entidades: List<InsigniaDesbloqueadaEntity>)
}
