package com.example.parkify.domain.model

data class Usuario(
    val uid: String = "",
    val nombre: String = "",
    val email: String = "",
    val rol: Rol = Rol.OPERARIO,
    val telefono: String = "",
    val activo: Boolean = true,
    val intentosFallidos: Int = 0,
    val bloqueado: Boolean = false
)

enum class Rol { ADMINISTRADOR, OPERARIO }