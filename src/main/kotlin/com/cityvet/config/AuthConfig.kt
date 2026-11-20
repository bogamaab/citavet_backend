package com.cityvet.config

import com.cityvet.models.ApiException
import com.cityvet.repository.UserRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.AuthenticationContext

const val AUTH_BEARER = "bearer"

data class UserPrincipal(
    val id: String,
    val correo: String,
    val rol: String,
    val nombre: String,
    val medicoId: String = ""
)

fun Application.configureAuth() {
    install(Authentication) {
        bearer(AUTH_BEARER) {
            authenticate { tokenCredential ->
                val token = tokenCredential.token
                val user = UserRepository.findByToken(token)
                if (user != null) {
                    UserPrincipal(
                        id = user.id,
                        correo = user.correo,
                        rol = user.rol,
                        nombre = user.nombre,
                        medicoId = user.medicoId
                    )
                } else null
            }
        }
    }
}

fun AuthenticationContext.requireRole(vararg roles: String) {
    val principal = principal<UserPrincipal>()
        ?: throw ApiException(HttpStatusCode.Unauthorized, "No autenticado")
    if (principal.rol !in roles) {
        throw ApiException(HttpStatusCode.Forbidden, "Acceso no permitido para rol: ${principal.rol}")
    }
}
