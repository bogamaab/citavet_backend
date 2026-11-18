package com.cityvet.models

import kotlinx.serialization.Serializable

@Serializable
data class Cita(
    val id: String = "",
    val mascotaId: String = "",
    val servicioId: String = "",
    val clienteId: String = "",
    val medicoId: String = "",
    val fecha: String = "",
    val hora: String = "",
    val estado: String = "Confirmada",
    val doctor: String = ""
)

@Serializable
data class CitaRequest(
    val mascotaId: String,
    val servicioId: String,
    val fecha: String,
    val hora: String
)

@Serializable
data class RescheduleRequest(
    val fecha: String,
    val hora: String
)

@Serializable
data class CitaResumen(
    val id: String,
    val mascotaId: String,
    val mascotaNombre: String,
    val mascota: String,
    val servicioId: String,
    val servicio: String,
    val estado: String,
    val fecha: String,
    val fechaISO: String,
    val hora: String,
    val doctor: String
)

@Serializable
data class Servicio(
    val id: String = "",
    val nombre: String = "",
    val detalle: String = "",
    val precio: Double = 0.0,
    val highlight: Boolean = false,
    val critico: Boolean = false,
    val medicoId: String = ""
)

@Serializable
data class DiaDisponible(
    val fecha: String,
    val dia: String,
    val numero: Int,
    val mes: String,
    val anio: Int
)

@Serializable
data class HorarioSlot(
    val hora: String,
    val disponible: Boolean
)

@Serializable
data class HorariosResponse(
    val fecha: String,
    val horarios: List<HorarioSlot>
)

@Serializable
data class CitaAgenda(
    val id: String,
    val hora: String,
    val horaFin: String,
    val mascota: String,
    val detalle: String,
    val critico: Boolean
)

@Serializable
data class AgendaResponse(
    val fecha: String,
    val titulo: String,
    val citas: List<CitaAgenda>
)

@Serializable
data class PacienteResumen(
    val id: String,
    val nombre: String,
    val raza: String,
    val tutor: String
)

@Serializable
data class AdminStats(
    val citasHoy: Int,
    val vetsActivos: Int,
    val completadas: Int
)

@Serializable
data class MesMetrica(
    val id: String,
    val mes: String,
    val area: String,
    val citas: Int,
    val costos: Double,
    val asistencia: Int
)

@Serializable
data class AdminResumen(
    val stats: AdminStats,
    val meses: List<MesMetrica>
)

@Serializable
data class EspecialistaInfo(
    val id: String,
    val nombre: String,
    val especialidad: String,
    val citas: Int,
    val rating: Double
)

@Serializable
data class RecursoInfo(
    val nombre: String,
    val disponible: Boolean
)

@Serializable
data class AdminEspecialistas(
    val stats: AdminStats,
    val especialistas: List<EspecialistaInfo>,
    val recursos: List<RecursoInfo>
)

@Serializable
data class Medico(
    val id: String = "",
    val nombre: String = "",
    val especialidad: String = "",
    val usuarioId: String = ""
)
