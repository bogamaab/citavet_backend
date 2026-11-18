package com.cityvet.routes

import com.cityvet.config.AUTH_BEARER
import com.cityvet.config.UserPrincipal
import com.cityvet.config.requireRole
import com.cityvet.models.MascotaRequest
import com.cityvet.service.MascotaService
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.mascotaRoutes() {
    authenticate(AUTH_BEARER) {
        get("/mascotas") {
            val principal = call.principal<UserPrincipal>()!!
            call.authentication.requireRole("cliente", "admin")
            val mascotas = MascotaService.getMascotas(principal.id)
            call.respond(mascotas)
        }
        post("/mascotas") {
            val principal = call.principal<UserPrincipal>()!!
            call.authentication.requireRole("cliente", "admin")
            val request = call.receive<MascotaRequest>()
            val mascota = MascotaService.createMascota(principal.id, request)
            call.respond(mascota)
        }
    }
}
