package com.cityvet.routes

import com.cityvet.models.LoginRequest
import com.cityvet.models.RegisterRequest
import com.cityvet.service.AuthService
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoutes() {
    route("/auth") {
        post("/login") {
            val request = call.receive<LoginRequest>()
            val response = AuthService.login(request)
            call.respond(response)
        }
        post("/register") {
            val request = call.receive<RegisterRequest>()
            val response = AuthService.register(request)
            call.respond(response)
        }
    }
}
