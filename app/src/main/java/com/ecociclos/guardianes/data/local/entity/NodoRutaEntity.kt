package com.ecociclos.guardianes.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "nodos_ruta",
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
data class NodoRutaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nivelId: Int,
    val clave: String,
    val etiqueta: String,
    val tipoRecurso: String, // TipoRecurso.name
    val esFuente: Boolean,
    val destinoCorrectoClave: String?
)
