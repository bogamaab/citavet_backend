package com.cityvet.routes

import com.cityvet.config.AUTH_BEARER
import com.cityvet.models.ApiException
import com.cityvet.service.HorarioService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.diasHorariosRoutes() {
    authenticate(AUTH_BEARER) {
        get("/dias") {
            val dias = HorarioService.getProximosDias()
            call.respond(dias)
        }
        get("/horarios") {
            val fecha = call.request.queryParameters["fecha"]
                ?: throw ApiException(HttpStatusCode.BadRequest, "Parámetro 'fecha' requerido")
            val servicioId = call.request.queryParameters["servicioId"]
                ?: throw ApiException(HttpStatusCode.BadRequest, "Parámetro 'servicioId' requerido")
            val horarios = HorarioService.getHorarios(fecha, servicioId)
            call.respond(horarios)
        }
    }
}
