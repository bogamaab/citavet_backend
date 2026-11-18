package com.cityvet.service

import com.cityvet.models.*
import com.cityvet.repository.CitaRepository
import com.cityvet.repository.MascotaRepository
import com.cityvet.repository.ServicioRepository
import io.ktor.http.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object CitaService {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val meses = listOf(
        "Ene","Feb","Mar","Abr","May","Jun","Jul","Ago","Sep","Oct","Nov","Dic"
    )

    fun getCitasCliente(clienteId: String, tipo: String): List<CitaResumen> {
        val hoy = LocalDate.now().format(dateFormatter)
        val citas = CitaRepository.findByClienteId(clienteId)
        val filtradas = when (tipo) {
            "proximas" -> citas.filter { it.estado == "Confirmada" && it.fecha >= hoy }
            else -> citas.filter { it.estado != "Confirmada" || it.fecha < hoy }
        }
        return filtradas.sortedBy { it.fecha + it.hora }.map { cita ->
            val mascota = MascotaRepository.findById(cita.mascotaId)
            val servicio = ServicioRepository.findById(cita.servicioId)
            cita.toResumen(
                mascotaNombre = mascota?.nombre ?: "",
                mascotaRaza = mascota?.raza ?: "",
                servicioNombre = servicio?.nombre ?: ""
            )
        }
    }

    fun createCita(clienteId: String, request: CitaRequest): Cita {
        val servicio = ServicioRepository.findById(request.servicioId)
            ?: throw ApiException(HttpStatusCode.NotFound, "Servicio no encontrado")
        if (CitaRepository.isSlotTaken(servicio.medicoId, request.fecha, request.hora)) {
            throw ApiException(HttpStatusCode.Conflict, "Horario no disponible")
        }
        val mascota = MascotaRepository.findById(request.mascotaId)
            ?: throw ApiException(HttpStatusCode.NotFound, "Mascota no encontrada")
        val cita = Cita(
            mascotaId = request.mascotaId,
            servicioId = request.servicioId,
            clienteId = clienteId,
            medicoId = servicio.medicoId,
            fecha = request.fecha,
            hora = request.hora,
            estado = "Confirmada",
            doctor = "Dr. " + (servicio.medicoId.take(8))
        )
        return CitaRepository.save(cita)
    }

    fun rescheduleCita(clienteId: String, citaId: String, request: RescheduleRequest): Cita {
        val cita = CitaRepository.findById(citaId)
            ?: throw ApiException(HttpStatusCode.NotFound, "Cita no encontrada")
        if (cita.clienteId != clienteId) throw ApiException(HttpStatusCode.Forbidden, "No autorizado")
        if (cita.estado != "Confirmada") throw ApiException(HttpStatusCode.BadRequest, "Solo se puede reagendar citas confirmadas")
        if (CitaRepository.isSlotTaken(cita.medicoId, request.fecha, request.hora, citaId)) {
            throw ApiException(HttpStatusCode.Conflict, "Horario no disponible")
        }
        CitaRepository.update(citaId, mapOf("fecha" to request.fecha, "hora" to request.hora))
        return cita.copy(fecha = request.fecha, hora = request.hora)
    }

    fun cancelCita(clienteId: String, citaId: String) {
        val cita = CitaRepository.findById(citaId)
            ?: throw ApiException(HttpStatusCode.NotFound, "Cita no encontrada")
        if (cita.clienteId != clienteId) throw ApiException(HttpStatusCode.Forbidden, "No autorizado")
        if (cita.estado != "Confirmada") throw ApiException(HttpStatusCode.BadRequest, "Solo se puede cancelar citas confirmadas")
        CitaRepository.update(citaId, mapOf("estado" to "Cancelada"))
    }

    private fun Cita.toResumen(mascotaNombre: String, mascotaRaza: String, servicioNombre: String): CitaResumen {
        val hoy = LocalDate.now()
        val fechaCita = LocalDate.parse(fecha, dateFormatter)
        val diasDiff = ChronoUnit.DAYS.between(hoy, fechaCita)
        val fechaFormateada = when {
            diasDiff == 0L -> "Hoy • $hora"
            diasDiff == 1L -> "Mañana • $hora"
            else -> "${fechaCita.dayOfMonth} ${meses[fechaCita.monthValue - 1]} • $hora"
        }
        return CitaResumen(
            id = id,
            mascotaId = mascotaId,
            mascotaNombre = mascotaNombre,
            mascota = "$mascotaNombre ($mascotaRaza)",
            servicioId = servicioId,
            servicio = servicioNombre,
            estado = estado,
            fecha = fechaFormateada,
            fechaISO = fecha,
            hora = hora,
            doctor = doctor
        )
    }
}
