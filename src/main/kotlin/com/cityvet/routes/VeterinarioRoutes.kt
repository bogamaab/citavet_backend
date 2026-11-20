package com.cityvet.routes

import com.cityvet.config.AUTH_BEARER
import com.cityvet.config.UserPrincipal
import com.cityvet.config.requireRole
import com.cityvet.repository.MedicoRepository
import com.cityvet.service.AgendaService
import com.cityvet.service.MascotaService
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.veterinarioRoutes() {
    authenticate(AUTH_BEARER) {
        get("/veterinario/agenda") {
            val principal = call.principal<UserPrincipal>()!!
            call.authentication.requireRole("medico", "admin")
            val fecha = call.request.queryParameters["fecha"]
            val medicoId = resolveMedicoId(principal, call.request.queryParameters["medicoId"])
            val agenda = AgendaService.getAgenda(medicoId, fecha)
            call.respond(agenda)
        }
        get("/pacientes") {
            call.authentication.requireRole("medico", "admin")
            val busqueda = call.request.queryParameters["busqueda"] ?: ""
            val pacientes = MascotaService.searchPacientes(busqueda)
            call.respond(pacientes)
        }
        get("/pacientes/{id}") {
            call.authentication.requireRole("medico", "admin")
            val mascotaId = call.parameters["id"]!!
            val ficha = MascotaService.getMascotaFicha(mascotaId)
            call.respond(ficha)
        }
    }
}

private fun resolveMedicoId(principal: UserPrincipal, queryMedicoId: String?): String {
    if (!queryMedicoId.isNullOrBlank()) return queryMedicoId
    if (principal.medicoId.isNotBlank()) return principal.medicoId
    val porUsuarioId = MedicoRepository.findAll().find { it.usuarioId == principal.id }?.id
    if (!porUsuarioId.isNullOrBlank()) return porUsuarioId
    return MedicoRepository.findAll().firstOrNull()?.id ?: principal.id
}
