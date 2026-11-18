package com.cityvet.routes

import com.cityvet.config.AUTH_BEARER
import com.cityvet.config.UserPrincipal
import com.cityvet.config.requireRole
import com.cityvet.models.CitaRequest
import com.cityvet.models.RescheduleRequest
import com.cityvet.service.CitaService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.citaRoutes() {
    authenticate(AUTH_BEARER) {
        get("/citas") {
            val principal = call.principal<UserPrincipal>()!!
            call.authentication.requireRole("cliente", "admin")
            val tipo = call.request.queryParameters["tipo"] ?: "proximas"
            val citas = CitaService.getCitasCliente(principal.id, tipo)
            call.respond(citas)
        }
        post("/citas") {
            val principal = call.principal<UserPrincipal>()!!
            call.authentication.requireRole("cliente", "admin")
            val request = call.receive<CitaRequest>()
            val cita = CitaService.createCita(principal.id, request)
            call.respond(HttpStatusCode.Created, cita)
        }
        put("/citas/{id}") {
            val principal = call.principal<UserPrincipal>()!!
            call.authentication.requireRole("cliente", "admin")
            val citaId = call.parameters["id"]!!
            val request = call.receive<RescheduleRequest>()
            val cita = CitaService.rescheduleCita(principal.id, citaId, request)
            call.respond(cita)
        }
        delete("/citas/{id}") {
            val principal = call.principal<UserPrincipal>()!!
            call.authentication.requireRole("cliente", "admin")
            val citaId = call.parameters["id"]!!
            CitaService.cancelCita(principal.id, citaId)
            call.respond(HttpStatusCode.NoContent)
        }
    }
}
