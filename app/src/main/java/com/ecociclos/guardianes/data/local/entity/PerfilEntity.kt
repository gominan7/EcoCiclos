package com.ecociclos.guardianes.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "perfil")
data class PerfilEntity(
    @PrimaryKey val id: Int = 0,
    val alias: String,
    val avatarId: Int,
    val xpTotal: Int
)
