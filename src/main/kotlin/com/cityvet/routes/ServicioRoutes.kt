package com.cityvet.routes

import com.cityvet.config.AUTH_BEARER
import com.cityvet.repository.ServicioRepository
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.servicioRoutes() {
    authenticate(AUTH_BEARER) {
        get("/servicios") {
            val servicios = ServicioRepository.findAll()
            call.respond(servicios)
        }
    }
}
