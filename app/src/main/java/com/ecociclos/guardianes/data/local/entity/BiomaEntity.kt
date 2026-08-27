package com.ecociclos.guardianes.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "biomas")
data class BiomaEntity(
    @PrimaryKey val id: Int,
    val nombre: String,
    val descripcion: String,
    val tipoCiclo: String, // TipoCiclo.name
    val orden: Int,
    val colorHex: String,
    val desbloqueado: Boolean
)
