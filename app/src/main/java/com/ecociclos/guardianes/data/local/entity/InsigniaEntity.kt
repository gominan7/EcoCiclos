package com.ecociclos.guardianes.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "insignias")
data class InsigniaEntity(
    @PrimaryKey val id: Int,
    val nombre: String,
    val descripcion: String,
    val biomaId: Int?, // null = insignia global
    val criterio: String
)
