package com.ecociclos.guardianes.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "variables_lab",
    foreignKeys = [
        ForeignKey(
            entity = NivelEntity::class,
            parentColumns = ["id"],
            childColumns = ["nivelId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("nivelId")]
)
data class VariableLabEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nivelId: Int,
    val nombre: String,
    val valorMin: Int,
    val valorMax: Int,
    val valorInicial: Int,
    val umbralCritico: Int,
    val mensajeNormal: String,
    val mensajeCritico: String
)
