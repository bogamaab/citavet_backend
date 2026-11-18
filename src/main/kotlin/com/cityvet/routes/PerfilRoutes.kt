package com.cityvet.routes

import com.cityvet.config.AUTH_BEARER
import com.cityvet.config.UserPrincipal
import com.cityvet.models.PerfilUpdateRequest
import com.cityvet.service.PerfilService
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.perfilRoutes() {
    authenticate(AUTH_BEARER) {
        get("/perfil") {
            val principal = call.principal<UserPrincipal>()!!
            val perfil = PerfilService.getPerfil(principal.id)
            call.respond(perfil)
        }
        put("/perfil") {
            val principal = call.principal<UserPrincipal>()!!
            val request = call.receive<PerfilUpdateRequest>()
            val updated = PerfilService.updatePerfil(principal.id, request)
            call.respond(updated)
        }
    }
}
