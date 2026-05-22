package com.example.parkify.domain.model

import java.util.Date

data class Vehiculo(
    val id: String = "",
    val placa: String = "",
    val tipo: TipoVehiculo = TipoVehiculo.CARRO,
    val propietario: String = "",
    val telefono: String = "",
    val horaEntrada: Date? = null,
    val horaSalida: Date? = null,
    val totalCobro: Double = 0.0,
    val estado: EstadoVehiculo = EstadoVehiculo.EN_PARQUEADERO,
    val modalidadCobro: ModalidadCobro = ModalidadCobro.HORA,
    val operarioEntradaId: String = "",
    val operarioSalidaId: String = ""
)

enum class TipoVehiculo { CARRO, MOTO }
enum class EstadoVehiculo { EN_PARQUEADERO, SALIDA }
enum class ModalidadCobro { HORA, DIA, SEMANA, MES }