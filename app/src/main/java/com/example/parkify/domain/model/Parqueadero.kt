package com.example.parkify.domain.model

data class Parqueadero(
    val id: String = "",
    val nombre: String = "Parkify",
    val totalEspaciosCarros: Int = 50,
    val totalEspaciosMotos: Int = 30,
    val telefonoAdmin: String = "",
    val emailAdmin: String = ""
)