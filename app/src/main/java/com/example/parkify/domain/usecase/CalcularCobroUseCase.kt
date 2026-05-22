package com.example.parkify.domain.usecase

import com.example.parkify.domain.model.ModalidadCobro
import com.example.parkify.domain.model.Tarifa
import com.example.parkify.domain.model.TipoVehiculo
import java.util.Date
import javax.inject.Inject
import kotlin.math.ceil

class CalcularCobroUseCase @Inject constructor() {

    operator fun invoke(
        horaEntrada: Date,
        horaSalida: Date,
        tipo: TipoVehiculo,
        modalidad: ModalidadCobro,
        tarifas: List<Tarifa>
    ): Double {
        val tipoStr = tipo.name
        val modalidadStr = modalidad.name
        val tarifa = tarifas.find { it.tipo == tipoStr && it.modalidad == modalidadStr }
            ?: return 0.0

        val diffMs = horaSalida.time - horaEntrada.time
        val diffHoras = diffMs / (1000.0 * 60 * 60)

        return when (modalidad) {
            ModalidadCobro.HORA -> ceil(diffHoras) * tarifa.precio
            ModalidadCobro.DIA -> {
                val dias = ceil(diffHoras / 24)
                dias * tarifa.precio
            }
            ModalidadCobro.SEMANA -> {
                val semanas = ceil(diffHoras / (24 * 7))
                semanas * tarifa.precio
            }
            ModalidadCobro.MES -> {
                val meses = ceil(diffHoras / (24 * 30))
                meses * tarifa.precio
            }
        }
    }
}
