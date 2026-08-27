package com.ecociclos.guardianes.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cartas_desbloqueadas")
data class CartaDesbloqueadaEntity(
    @PrimaryKey val cartaId: Int,
    val fechaMillis: Long
)

@Entity(tableName = "insignias_desbloqueadas")
data class InsigniaDesbloqueadaEntity(
    @PrimaryKey val insigniaId: Int,
    val fechaMillis: Long
)
