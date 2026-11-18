package com.cityvet.service

import com.cityvet.models.*
import com.cityvet.repository.UserRepository
import io.ktor.http.*

object PerfilService {

    fun getPerfil(userId: String): UserPublic {
        val user = UserRepository.findById(userId)
            ?: throw ApiException(HttpStatusCode.NotFound, "Usuario no encontrado")
        return user.toPublic()
    }

    fun updatePerfil(userId: String, request: PerfilUpdateRequest): UserPublic {
        val user = UserRepository.findById(userId)
            ?: throw ApiException(HttpStatusCode.NotFound, "Usuario no encontrado")
        val updates = mutableMapOf<String, Any>()
        request.nombre?.let { updates["nombre"] = it }
        request.correo?.let { updates["correo"] = it }
        request.telefono?.let { updates["telefono"] = it }
        request.direccion?.let { updates["direccion"] = it }
        request.recordatorios?.let { updates["recordatorios"] = it }
        request.alertas?.let { updates["alertas"] = it }
        if (updates.isNotEmpty()) UserRepository.update(userId, updates)
        return user.copy(
            nombre = request.nombre ?: user.nombre,
            correo = request.correo ?: user.correo,
            telefono = request.telefono ?: user.telefono,
            direccion = request.direccion ?: user.direccion,
            recordatorios = request.recordatorios ?: user.recordatorios,
            alertas = request.alertas ?: user.alertas
        ).toPublic()
    }

    private fun User.toPublic() = UserPublic(
        id = id,
        nombre = nombre,
        correo = correo,
        telefono = telefono,
        rol = rol,
        medicoId = medicoId,
        direccion = direccion,
        recordatorios = recordatorios,
        alertas = alertas
    )
}
