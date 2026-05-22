package com.example.parkify.data.remote.firebase.models

data class VehiculoDTO(
    val id: String = "",
    val placa: String = "",
    val tipo: String = "",
    val propietario: String = "",
    val telefono: String = "",
    val horaEntrada: Long? = null,
    val horaSalida: Long? = null,
    val totalCobro: Double = 0.0,
    val estado: String = "EN_PARQUEADERO",
    val modalidadCobro: String = "HORA",
    val operarioEntradaId: String = "",
    val operarioSalidaId: String = ""
)