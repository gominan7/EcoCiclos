package com.ecociclos.guardianes.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "progreso_nivel")
data class ProgresoNivelEntity(
    @PrimaryKey val nivelId: Int,
    val completado: Boolean,
    val estrellas: Int,
    val intentos: Int,
    val fechaCompletadoMillis: Long?
)
