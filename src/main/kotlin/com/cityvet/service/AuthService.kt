package com.cityvet.service

import com.cityvet.models.*
import com.cityvet.repository.MedicoRepository
import com.cityvet.repository.UserRepository
import io.ktor.http.*
import java.util.UUID

object AuthService {

    fun login(request: LoginRequest): AuthResponse {
        val user = UserRepository.findByCorreo(request.correo)
            ?: throw ApiException(HttpStatusCode.Unauthorized, "Credenciales inválidas")
        if (user.password != request.password) {
            throw ApiException(HttpStatusCode.Unauthorized, "Credenciales inválidas")
        }
        request.tipo?.let { tipo ->
            if (user.rol != tipo && user.rol != "admin") {
                throw ApiException(HttpStatusCode.Forbidden, "Acceso no permitido para este tipo de usuario")
            }
        }
        val token = UUID.randomUUID().toString()
        val updated = user.copy(token = token)
        UserRepository.save(updated)
        return AuthResponse(token = token, usuario = updated.toPublic())
    }

    fun register(request: RegisterRequest): AuthResponse {
        if (request.rol !in listOf("cliente", "medico")) {
            throw ApiException(HttpStatusCode.BadRequest, "Rol inválido. Use 'cliente' o 'medico'")
        }
        UserRepository.findByCorreo(request.correo)?.let {
            throw ApiException(HttpStatusCode.Conflict, "El correo ya está registrado")
        }
        val token = UUID.randomUUID().toString()
        val user = User(
            nombre = request.nombre,
            correo = request.correo,
            telefono = request.telefono,
            password = request.password,
            rol = request.rol,
            token = token
        )
        val saved = UserRepository.save(user)
        var medicoId = ""
        if (request.rol == "medico") {
            val medico = MedicoRepository.save(Medico(nombre = request.nombre, usuarioId = saved.id))
            medicoId = medico.id
            UserRepository.update(saved.id, mapOf("medicoId" to medicoId))
        }
        val finalUser = saved.copy(medicoId = medicoId)
        return AuthResponse(token = token, usuario = finalUser.toPublic())
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
