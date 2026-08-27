package com.ecociclos.guardianes.domain.model

data class Carta(
    val id: Int,
    val nombre: String,
    val tipo: TipoCarta,
    val biomaId: Int,
    val descripcion: String,
    val nivelDesbloqueoId: Int,
    val desbloqueada: Boolean
)

data class Insignia(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val biomaId: Int?, // null = insignia global (ej. "Guardián del Planeta")
    val obtenida: Boolean
)

data class Perfil(
    val id: Int,
    val alias: String,
    val avatarId: Int,
    val xpTotal: Int
)
