package com.example.parkify.data.repository

import com.example.parkify.data.remote.firebase.FirebaseService
import com.example.parkify.data.remote.firebase.models.TarifaDTO
import com.example.parkify.data.remote.mappers.VehiculoMapper
import com.example.parkify.domain.model.*
import com.example.parkify.domain.repository.IParqueoRepository
import com.example.parkify.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class ParqueoRepositoryImpl @Inject constructor(
    private val firebase: FirebaseService
) : IParqueoRepository {

    override fun observarVehiculosActivos(): Flow<List<Vehiculo>> =
        firebase.observarVehiculosActivos().map { list -> list.map { VehiculoMapper.toDomain(it) } }

    override suspend fun registrarEntrada(vehiculo: Vehiculo): Resource<Vehiculo> = try {
        val id = UUID.randomUUID().toString()
        val v = vehiculo.copy(
            id = id,
            horaEntrada = Date(),
            estado = EstadoVehiculo.EN_PARQUEADERO
        )
        firebase.saveVehiculo(VehiculoMapper.toDTO(v))
        Resource.Success(v)
    } catch (e: Exception) {
        Resource.Error(e.localizedMessage ?: "Error al registrar entrada")
    }

    override suspend fun registrarSalida(vehiculoId: String, operarioId: String, cobro: Double): Resource<Vehiculo> = try {
        val horaSalida = Date()
        val campos = mapOf(
            "estado" to "SALIDA",
            "horaSalida" to horaSalida.time,
            "operarioSalidaId" to operarioId,
            "totalCobro" to cobro
        )
        firebase.updateVehiculo(vehiculoId, campos)
        Resource.Success(Vehiculo(id = vehiculoId))
    } catch (e: Exception) {
        Resource.Error(e.localizedMessage ?: "Error al registrar salida")
    }

    override suspend fun buscarPorPlaca(placa: String): Resource<Vehiculo?> = try {
        val dto = firebase.getVehiculoPorPlaca(placa)
        Resource.Success(dto?.let { VehiculoMapper.toDomain(it) })
    } catch (e: Exception) {
        Resource.Error(e.localizedMessage ?: "Error")
    }

    override suspend fun getHistorial(desde: Long, hasta: Long): Resource<List<Vehiculo>> = try {
        val list = firebase.getHistorialVehiculos(desde, hasta).map { VehiculoMapper.toDomain(it) }
        Resource.Success(list)
    } catch (e: Exception) {
        Resource.Error(e.localizedMessage ?: "Error al cargar historial")
    }

    override suspend fun getTarifas(): Resource<List<Tarifa>> = try {
        val list = firebase.getTarifas().map {
            Tarifa(id = it.id, tipo = it.tipo, modalidad = it.modalidad, precio = it.precio)
        }
        Resource.Success(list)
    } catch (e: Exception) {
        Resource.Error(e.localizedMessage ?: "Error al cargar tarifas")
    }

    override suspend fun actualizarTarifa(tarifa: Tarifa): Resource<Unit> = try {
        firebase.saveTarifa(TarifaDTO(id = tarifa.id, tipo = tarifa.tipo, modalidad = tarifa.modalidad, precio = tarifa.precio))
        Resource.Success(Unit)
    } catch (e: Exception) {
        Resource.Error(e.localizedMessage ?: "Error al guardar tarifa")
    }

    override suspend fun getParqueaderoInfo(): Resource<Parqueadero> = try {
        val data = firebase.getParqueaderoInfo()
        val p = if (data != null) Parqueadero(
            nombre = data["nombre"] as? String ?: "Parkify",
            totalEspaciosCarros = (data["totalEspaciosCarros"] as? Long)?.toInt() ?: 50,
            totalEspaciosMotos = (data["totalEspaciosMotos"] as? Long)?.toInt() ?: 30,
            telefonoAdmin = data["telefonoAdmin"] as? String ?: "",
            emailAdmin = data["emailAdmin"] as? String ?: ""
        ) else Parqueadero()
        Resource.Success(p)
    } catch (e: Exception) {
        Resource.Error(e.localizedMessage ?: "Error")
    }

    override suspend fun actualizarParqueadero(parqueadero: Parqueadero): Resource<Unit> = try {
        val data = mapOf(
            "nombre" to parqueadero.nombre,
            "totalEspaciosCarros" to parqueadero.totalEspaciosCarros,
            "totalEspaciosMotos" to parqueadero.totalEspaciosMotos,
            "telefonoAdmin" to parqueadero.telefonoAdmin,
            "emailAdmin" to parqueadero.emailAdmin
        )
        firebase.updateParqueaderoInfo(data)
        Resource.Success(Unit)
    } catch (e: Exception) {
        Resource.Error(e.localizedMessage ?: "Error")
    }

    override fun observarDisponibilidad(): Flow<Pair<Int, Int>> =
        observarVehiculosActivos().map { activos ->
            val carrosOcupados = activos.count { it.tipo == TipoVehiculo.CARRO }
            val motosOcupadas = activos.count { it.tipo == TipoVehiculo.MOTO }
            Pair(50 - carrosOcupados, 30 - motosOcupadas) // defaults; en prod lee de config
        }
}
