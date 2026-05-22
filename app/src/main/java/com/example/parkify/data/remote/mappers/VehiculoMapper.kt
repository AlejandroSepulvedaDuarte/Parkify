package com.example.parkify.data.remote.mappers

import com.example.parkify.data.remote.firebase.models.VehiculoDTO
import com.example.parkify.domain.model.*
import java.util.Date

object VehiculoMapper {
    fun toDomain(dto: VehiculoDTO) = Vehiculo(
        id = dto.id,
        placa = dto.placa,
        tipo = TipoVehiculo.valueOf(dto.tipo),
        propietario = dto.propietario,
        telefono = dto.telefono,
        horaEntrada = dto.horaEntrada?.let { Date(it) },
        horaSalida = dto.horaSalida?.let { Date(it) },
        totalCobro = dto.totalCobro,
        estado = EstadoVehiculo.valueOf(dto.estado),
        modalidadCobro = ModalidadCobro.valueOf(dto.modalidadCobro),
        operarioEntradaId = dto.operarioEntradaId,
        operarioSalidaId = dto.operarioSalidaId
    )

    fun toDTO(v: Vehiculo) = VehiculoDTO(
        id = v.id,
        placa = v.placa.uppercase(),
        tipo = v.tipo.name,
        propietario = v.propietario,
        telefono = v.telefono,
        horaEntrada = v.horaEntrada?.time,
        horaSalida = v.horaSalida?.time,
        totalCobro = v.totalCobro,
        estado = v.estado.name,
        modalidadCobro = v.modalidadCobro.name,
        operarioEntradaId = v.operarioEntradaId,
        operarioSalidaId = v.operarioSalidaId
    )
}
