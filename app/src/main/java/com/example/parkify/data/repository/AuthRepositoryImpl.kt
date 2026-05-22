package com.example.parkify.data.repository

import com.example.parkify.data.remote.firebase.FirebaseService
import com.example.parkify.data.remote.mappers.UsuarioMapper
import com.example.parkify.domain.model.Usuario
import com.example.parkify.domain.repository.IAuthRepository
import com.example.parkify.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebase: FirebaseService
) : IAuthRepository {

    companion object {
        const val MAX_INTENTOS = 5
    }

    override suspend fun login(email: String, password: String): Resource<Usuario> {
        return try {
            val result = firebase.login(email, password)
            val uid = result.user?.uid
            if (uid == null) {
                Resource.Error("UID nulo")
            } else {
                val dto = firebase.getUsuario(uid)
                when {
                    dto == null -> Resource.Error("Usuario no encontrado")
                    dto.bloqueado -> Resource.Error("BLOQUEADO")
                    else -> {
                        firebase.actualizarIntentosFallidos(uid, 0, false)
                        Resource.Success(UsuarioMapper.toDomain(dto))
                    }
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error de login")
        }
    }

    override suspend fun logout() = firebase.logout()

    override suspend fun getUsuarioActual(): Resource<Usuario> {
        return try {
            val uid = firebase.currentUserId()
            if (uid == null) {
                Resource.Error("No autenticado")
            } else {
                val dto = firebase.getUsuario(uid)
                if (dto == null) Resource.Error("Usuario no encontrado")
                else Resource.Success(UsuarioMapper.toDomain(dto))
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error")
        }
    }

    override suspend fun crearUsuario(usuario: Usuario, password: String): Resource<Usuario> {
        return try {
            val result = firebase.createUserAuth(usuario.email, password)
            val uid = result.user?.uid
            if (uid == null) {
                Resource.Error("No se pudo crear")
            } else {
                val dto = UsuarioMapper.toDTO(usuario.copy(uid = uid))
                firebase.saveUsuario(dto)
                Resource.Success(usuario.copy(uid = uid))
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error al crear usuario")
        }
    }

    override suspend fun registrarIntentoFallido(email: String): Resource<Unit> {
        return try {
            val usuarios = firebase.getUsuarios()
            val dto = usuarios.find { it.email == email }
            if (dto == null) {
                Resource.Success(Unit)
            } else {
                val nuevos = dto.intentosFallidos + 1
                val bloquear = nuevos >= MAX_INTENTOS
                firebase.actualizarIntentosFallidos(dto.uid, nuevos, bloquear)
                Resource.Success(Unit)
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error")
        }
    }

    override suspend fun desbloquearUsuario(uid: String): Resource<Unit> {
        return try {
            firebase.actualizarIntentosFallidos(uid, 0, false)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Error")
        }
    }

    override suspend fun cambiarPassword(uid: String, nuevaPassword: String): Resource<Unit> =
        Resource.Success(Unit)

    override fun observeAuthState(): Flow<Boolean> = firebase.authStateFlow()
}