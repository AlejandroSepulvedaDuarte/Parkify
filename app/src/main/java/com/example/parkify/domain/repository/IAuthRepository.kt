package com.example.parkify.domain.repository

import com.example.parkify.domain.model.Usuario
import com.example.parkify.utils.Resource
import kotlinx.coroutines.flow.Flow

interface IAuthRepository {
    suspend fun login(email: String, password: String): Resource<Usuario>
    suspend fun logout()
    suspend fun getUsuarioActual(): Resource<Usuario>
    suspend fun crearUsuario(usuario: Usuario, password: String): Resource<Usuario>
    suspend fun registrarIntentoFallido(email: String): Resource<Unit>
    suspend fun desbloquearUsuario(uid: String): Resource<Unit>
    suspend fun cambiarPassword(uid: String, nuevaPassword: String): Resource<Unit>
    fun observeAuthState(): Flow<Boolean>
}
