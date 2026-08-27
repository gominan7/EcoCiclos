package com.ecociclos.guardianes.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "niveles",
    foreignKeys = [
        ForeignKey(
            entity = BiomaEntity::class,
            parentColumns = ["id"],
            childColumns = ["biomaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("biomaId")]
)
data class NivelEntity(
    @PrimaryKey val id: Int,
    val biomaId: Int,
    val orden: Int,
    val titulo: String,
    val tipoReto: String, // TipoReto.name
    val dificultad: Int,
    val instruccion: String
)
