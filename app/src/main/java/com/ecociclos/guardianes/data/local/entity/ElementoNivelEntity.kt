package com.ecociclos.guardianes.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "elementos_nivel",
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
data class ElementoNivelEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nivelId: Int,
    val clave: String,
    val etiqueta: String,
    val esOrigen: Boolean,
    val destinoCorrectoClave: String?
)
