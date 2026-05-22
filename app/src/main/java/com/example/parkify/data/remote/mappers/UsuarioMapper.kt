package com.example.parkify.data.remote.mappers

import com.example.parkify.data.remote.firebase.models.UsuarioDTO
import com.example.parkify.domain.model.Rol
import com.example.parkify.domain.model.Usuario

object UsuarioMapper {
    fun toDomain(dto: UsuarioDTO) = Usuario(
        uid = dto.uid,
        nombre = dto.nombre,
        email = dto.email,
        rol = Rol.valueOf(dto.rol),
        telefono = dto.telefono,
        activo = dto.activo,
        intentosFallidos = dto.intentosFallidos,
        bloqueado = dto.bloqueado
    )

    fun toDTO(u: Usuario) = UsuarioDTO(
        uid = u.uid,
        nombre = u.nombre,
        email = u.email,
        rol = u.rol.name,
        telefono = u.telefono,
        activo = u.activo,
        intentosFallidos = u.intentosFallidos,
        bloqueado = u.bloqueado
    )
}
