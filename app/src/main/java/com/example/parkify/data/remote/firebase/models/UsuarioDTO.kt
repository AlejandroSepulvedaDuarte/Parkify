package com.example.parkify.data.remote.firebase.models

data class UsuarioDTO(
    val uid: String = "",
    val nombre: String = "",
    val email: String = "",
    val rol: String = "OPERARIO",
    val telefono: String = "",
    val activo: Boolean = true,
    val intentosFallidos: Int = 0,
    val bloqueado: Boolean = false
)