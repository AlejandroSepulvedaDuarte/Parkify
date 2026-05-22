package com.example.parkify.domain.repository

import com.example.parkify.domain.model.Vehiculo
import com.example.parkify.domain.model.Tarifa
import com.example.parkify.domain.model.Parqueadero
import com.example.parkify.utils.Resource
import kotlinx.coroutines.flow.Flow

interface IParqueoRepository {
    fun observarVehiculosActivos(): Flow<List<Vehiculo>>
    suspend fun registrarEntrada(vehiculo: Vehiculo): Resource<Vehiculo>
    suspend fun registrarSalida(vehiculoId: String, operarioId: String, cobro: Double): Resource<Vehiculo>
    suspend fun buscarPorPlaca(placa: String): Resource<Vehiculo?>
    suspend fun getHistorial(desde: Long, hasta: Long): Resource<List<Vehiculo>>
    suspend fun getTarifas(): Resource<List<Tarifa>>
    suspend fun actualizarTarifa(tarifa: Tarifa): Resource<Unit>
    suspend fun getParqueaderoInfo(): Resource<Parqueadero>
    suspend fun actualizarParqueadero(parqueadero: Parqueadero): Resource<Unit>
    fun observarDisponibilidad(): Flow<Pair<Int, Int>>   // Pair(carros, motos) disponibles
}
