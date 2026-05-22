package com.example.parkify.domain.repository

import com.example.parkify.domain.model.Usuario
import com.example.parkify.utils.Resource
import kotlinx.coroutines.flow.Flow

interface IAdminRepository {
    fun observarUsuarios(): Flow<List<Usuario>>
    suspend fun getUsuarios(): Resource<List<Usuario>>
    suspend fun actualizarUsuario(usuario: Usuario): Resource<Unit>
    suspend fun eliminarUsuario(uid: String): Resource<Unit>
    suspend fun getReporteIngresos(desde: Long, hasta: Long): Resource<Double>
}
