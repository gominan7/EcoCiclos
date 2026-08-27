package com.ecociclos.guardianes.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cartas")
data class CartaEntity(
    @PrimaryKey val id: Int,
    val nombre: String,
    val tipo: String, // TipoCarta.name
    val biomaId: Int,
    val descripcion: String,
    val nivelDesbloqueoId: Int
)
