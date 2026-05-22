package com.example.parkify.data.repository

import com.example.parkify.data.remote.firebase.FirebaseService
import com.example.parkify.data.remote.mappers.UsuarioMapper
import com.example.parkify.domain.model.Usuario
import com.example.parkify.domain.repository.IAdminRepository
import com.example.parkify.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AdminRepositoryImpl @Inject constructor(
    private val firebase: FirebaseService
) : IAdminRepository {

    override fun observarUsuarios(): Flow<List<Usuario>> =
        firebase.observarUsuarios().map { list -> list.map { UsuarioMapper.toDomain(it) } }

    override suspend fun getUsuarios(): Resource<List<Usuario>> = try {
        val list = firebase.getUsuarios().map { UsuarioMapper.toDomain(it) }
        Resource.Success(list)
    } catch (e: Exception) {
        Resource.Error(e.localizedMessage ?: "Error")
    }

    override suspend fun actualizarUsuario(usuario: Usuario): Resource<Unit> = try {
        firebase.saveUsuario(UsuarioMapper.toDTO(usuario))
        Resource.Success(Unit)
    } catch (e: Exception) {
        Resource.Error(e.localizedMessage ?: "Error")
    }

    override suspend fun eliminarUsuario(uid: String): Resource<Unit> = try {
        // Solo desactiva; la eliminación real requiere Cloud Functions
        firebase.saveUsuario(
            com.example.parkify.data.remote.firebase.models.UsuarioDTO(uid = uid, activo = false)
        )
        Resource.Success(Unit)
    } catch (e: Exception) {
        Resource.Error(e.localizedMessage ?: "Error")
    }

    override suspend fun getReporteIngresos(desde: Long, hasta: Long): Resource<Double> = try {
        val vehiculos = firebase.getHistorialVehiculos(desde, hasta)
        val total = vehiculos.sumOf { it.totalCobro }
        Resource.Success(total)
    } catch (e: Exception) {
        Resource.Error(e.localizedMessage ?: "Error")
    }
}
