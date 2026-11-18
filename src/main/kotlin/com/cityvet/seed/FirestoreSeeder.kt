package com.cityvet.seed

import com.cityvet.config.FirebaseConfig
import com.google.firebase.cloud.FirestoreClient
import java.util.UUID

object FirestoreSeeder {

    private val db get() = FirestoreClient.getFirestore()

    fun run() {
        FirebaseConfig.initialize()
        println("🌱 Iniciando seed idempotente de Firestore CityVet...")
        seedUsuarios()
        seedServicios()
        seedMascotas()
        seedCitas()
        seedAdminMetricas()
        println("✅ Seed completado exitosamente.")
    }

    private fun seedUsuarios() {
        val usuarios = listOf(
            mapOf(
                "nombre" to "María González", "correo" to "maria.gonzalez@vetcare.com",
                "telefono" to "+51 999 111 222", "password" to "123456",
                "rol" to "cliente", "token" to UUID.randomUUID().toString(),
                "medicoId" to "", "direccion" to "Av. Los Álamos 123, Lima",
                "recordatorios" to true, "alertas" to true
            ),
            mapOf(
                "nombre" to "Fernando Ruiz", "correo" to "fernando.ruiz@vetcare.com",
                "telefono" to "+51 999 333 444", "password" to "123456",
                "rol" to "medico", "token" to UUID.randomUUID().toString(),
                "medicoId" to "medico_fernando", "direccion" to "Av. Veterinaria 456, Lima",
                "recordatorios" to true, "alertas" to true
            ),
            mapOf(
                "nombre" to "Admin CityVet", "correo" to "admin@vetcare.com",
                "telefono" to "+51 999 000 000", "password" to "admin123",
                "rol" to "admin", "token" to UUID.randomUUID().toString(),
                "medicoId" to "", "direccion" to "Sede Central CityVet",
                "recordatorios" to false, "alertas" to true
            )
        )
        for (u in usuarios) {
            val correo = u["correo"] as String
            val existing = db.collection("usuarios").whereEqualTo("correo", correo).get().get()
            if (existing.isEmpty) {
                val ref = db.collection("usuarios").document()
                ref.set(u + mapOf("id" to ref.id)).get()
                println("  → Usuario creado: $correo")
                if (u["rol"] == "medico") {
                    val medicoRef = db.collection("medicos").document("medico_fernando")
                    medicoRef.set(mapOf(
                        "id" to "medico_fernando",
                        "nombre" to u["nombre"],
                        "especialidad" to "Medicina General Veterinaria",
                        "usuarioId" to ref.id
                    )).get()
                    ref.update("medicoId", "medico_fernando").get()
                }
            } else {
                println("  → Usuario ya existe (skip): $correo")
            }
        }
    }

    private fun seedServicios() {
        val servicios = listOf(
            mapOf("nombre" to "Consulta General", "detalle" to "Revisión completa del estado de salud", "precio" to 50.0, "highlight" to false, "critico" to false, "medicoId" to "medico_fernando"),
            mapOf("nombre" to "Vacunación", "detalle" to "Aplicación de vacunas según calendario", "precio" to 80.0, "highlight" to true, "critico" to false, "medicoId" to "medico_fernando"),
            mapOf("nombre" to "Cirugía Menor", "detalle" to "Procedimientos quirúrgicos menores", "precio" to 350.0, "highlight" to false, "critico" to true, "medicoId" to "medico_fernando"),
            mapOf("nombre" to "Baño y Peluquería", "detalle" to "Servicio de estética canina y felina", "precio" to 45.0, "highlight" to false, "critico" to false, "medicoId" to "medico_fernando"),
            mapOf("nombre" to "Radiografía", "detalle" to "Diagnóstico por imagen digital", "precio" to 120.0, "highlight" to false, "critico" to false, "medicoId" to "medico_fernando"),
            mapOf("nombre" to "Emergencia 24h", "detalle" to "Atención de urgencias las 24 horas", "precio" to 200.0, "highlight" to true, "critico" to true, "medicoId" to "medico_fernando")
        )
        for (s in servicios) {
            val nombre = s["nombre"] as String
            val existing = db.collection("servicios").whereEqualTo("nombre", nombre).get().get()
            if (existing.isEmpty) {
                val ref = db.collection("servicios").document()
                ref.set(s + mapOf("id" to ref.id)).get()
                println("  → Servicio creado: $nombre")
            } else {
                println("  → Servicio ya existe (skip): $nombre")
            }
        }
    }

    private fun seedMascotas() {
        val clienteDoc = db.collection("usuarios")
            .whereEqualTo("correo", "maria.gonzalez@vetcare.com").get().get().documents.firstOrNull()
        val clienteId = clienteDoc?.id ?: return

        val mascotas = listOf(
            mapOf(
                "nombre" to "Max", "especie" to "perro", "raza" to "Golden Retriever",
                "edad" to 3, "chip" to "985112345678901", "estado" to "Activo",
                "clienteId" to clienteId, "notas" to "Alérgico al pollo",
                "medicacion" to listOf("Omega 3 - 1 cápsula diaria"),
                "vacunas" to listOf(
                    mapOf("nombre" to "Rabia", "fecha" to "2026-03-15", "proxima" to "2027-03-15"),
                    mapOf("nombre" to "Parvovirus", "fecha" to "2026-01-10", "proxima" to "2027-01-10")
                ),
                "historial" to listOf(
                    mapOf("fecha" to "2026-10-05", "descripcion" to "Consulta general - todo en orden", "medico" to "Dr. Fernando Ruiz"),
                    mapOf("fecha" to "2026-07-20", "descripcion" to "Vacunación anual completada", "medico" to "Dr. Fernando Ruiz")
                )
            ),
            mapOf(
                "nombre" to "Luna", "especie" to "gato", "raza" to "Siamés",
                "edad" to 5, "chip" to "985198765432100", "estado" to "Activo",
                "clienteId" to clienteId, "notas" to "Esterilizada, dieta especial",
                "medicacion" to emptyList<String>(),
                "vacunas" to listOf(
                    mapOf("nombre" to "Triple Felina", "fecha" to "2026-04-20", "proxima" to "2027-04-20")
                ),
                "historial" to listOf(
                    mapOf("fecha" to "2026-09-15", "descripcion" to "Control de peso", "medico" to "Dr. Fernando Ruiz")
                )
            )
        )
        for (m in mascotas) {
            val nombre = m["nombre"] as String
            val existing = db.collection("mascotas")
                .whereEqualTo("clienteId", clienteId)
                .whereEqualTo("nombre", nombre).get().get()
            if (existing.isEmpty) {
                val ref = db.collection("mascotas").document()
                ref.set(m + mapOf("id" to ref.id)).get()
                println("  → Mascota creada: $nombre")
            } else {
                println("  → Mascota ya existe (skip): $nombre")
            }
        }
    }

    private fun seedCitas() {
        val clienteDoc = db.collection("usuarios")
            .whereEqualTo("correo", "maria.gonzalez@vetcare.com").get().get().documents.firstOrNull()
        val clienteId = clienteDoc?.id ?: return
        val mascotaDoc = db.collection("mascotas")
            .whereEqualTo("clienteId", clienteId)
            .whereEqualTo("nombre", "Max").get().get().documents.firstOrNull()
        val mascotaId = mascotaDoc?.id ?: return
        val servicioDoc = db.collection("servicios")
            .whereEqualTo("nombre", "Consulta General").get().get().documents.firstOrNull()
        val servicioId = servicioDoc?.id ?: return

        val citas = listOf(
            mapOf(
                "mascotaId" to mascotaId, "servicioId" to servicioId,
                "clienteId" to clienteId, "medicoId" to "medico_fernando",
                "fecha" to "2026-11-19", "hora" to "10:00",
                "estado" to "Confirmada", "doctor" to "Dr. Fernando Ruiz"
            ),
            mapOf(
                "mascotaId" to mascotaId, "servicioId" to servicioId,
                "clienteId" to clienteId, "medicoId" to "medico_fernando",
                "fecha" to "2026-10-10", "hora" to "09:30",
                "estado" to "Completada", "doctor" to "Dr. Fernando Ruiz"
            )
        )
        for (c in citas) {
            val fecha = c["fecha"] as String
            val hora = c["hora"] as String
            val existing = db.collection("citas")
                .whereEqualTo("clienteId", clienteId)
                .whereEqualTo("fecha", fecha)
                .whereEqualTo("hora", hora).get().get()
            if (existing.isEmpty) {
                val ref = db.collection("citas").document()
                ref.set(c + mapOf("id" to ref.id)).get()
                println("  → Cita creada: $fecha $hora")
            } else {
                println("  → Cita ya existe (skip): $fecha $hora")
            }
        }
    }

    private fun seedAdminMetricas() {
        val metricas = listOf(
            mapOf("mes" to "Agosto", "area" to "Consultas", "citas" to 42, "costos" to 2100.0, "asistencia" to 95),
            mapOf("mes" to "Septiembre", "area" to "Cirugías", "citas" to 18, "costos" to 6300.0, "asistencia" to 100),
            mapOf("mes" to "Octubre", "area" to "Consultas", "citas" to 55, "costos" to 2750.0, "asistencia" to 92),
            mapOf("mes" to "Noviembre", "area" to "Consultas", "citas" to 23, "costos" to 1150.0, "asistencia" to 96)
        )
        for (m in metricas) {
            val mes = m["mes"] as String
            val existing = db.collection("admin_metricas").whereEqualTo("mes", mes).get().get()
            if (existing.isEmpty) {
                val ref = db.collection("admin_metricas").document()
                ref.set(m + mapOf("id" to ref.id)).get()
                println("  → Métrica creada: $mes")
            } else {
                println("  → Métrica ya existe (skip): $mes")
            }
        }
    }
}
