package com.cityvet

import com.cityvet.config.FirebaseConfig
import com.cityvet.config.configureCors
import com.cityvet.config.configureSerialization
import com.cityvet.config.configureStatusPages
import com.cityvet.config.configureAuth
import com.cityvet.routes.adminRoutes
import com.cityvet.routes.authRoutes
import com.cityvet.routes.citaRoutes
import com.cityvet.routes.diasHorariosRoutes
import com.cityvet.routes.mascotaRoutes
import com.cityvet.routes.perfilRoutes
import com.cityvet.routes.servicioRoutes
import com.cityvet.routes.veterinarioRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun main(args: Array<String>) {
    if (args.firstOrNull() == "seed") {
        com.cityvet.seed.FirestoreSeeder.run()
        return
    }
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    FirebaseConfig.initialize()
    configureCors()
    configureStatusPages()
    configureSerialization()
    configureAuth()
    routing {
        authRoutes()
        perfilRoutes()
        mascotaRoutes()
        servicioRoutes()
        diasHorariosRoutes()
        citaRoutes()
        veterinarioRoutes()
        adminRoutes()
    }
}
