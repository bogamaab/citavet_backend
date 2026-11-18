package com.cityvet.routes

import com.cityvet.config.AUTH_BEARER
import com.cityvet.config.requireRole
import com.cityvet.service.AdminService
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.adminRoutes() {
    authenticate(AUTH_BEARER) {
        get("/admin/resumen") {
            call.authentication.requireRole("admin")
            val resumen = AdminService.getResumen()
            call.respond(resumen)
        }
        get("/admin/especialistas") {
            call.authentication.requireRole("admin")
            val especialistas = AdminService.getEspecialistas()
            call.respond(especialistas)
        }
    }
}
