package com.cityvet.service

import com.cityvet.models.*
import com.cityvet.repository.*
import io.ktor.http.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
private val diasSemana = mapOf(
    "MONDAY" to "Lunes", "TUESDAY" to "Martes", "WEDNESDAY" to "Miércoles",
    "THURSDAY" to "Jueves", "FRIDAY" to "Viernes", "SATURDAY" to "Sábado"
)
private val diasCortos = mapOf(
    "MONDAY" to "Lun", "TUESDAY" to "Mar", "WEDNESDAY" to "Mié",
    "THURSDAY" to "Jue", "FRIDAY" to "Vie", "SATURDAY" to "Sáb"
)
private val mesesLargo = listOf(
    "Enero","Febrero","Marzo","Abril","Mayo","Junio",
    "Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"
)
private val mesesCorto = listOf(
    "Ene","Feb","Mar","Abr","May","Jun","Jul","Ago","Sep","Oct","Nov","Dic"
)
private val especieTextos = mapOf(
    "perro" to "Perro", "gato" to "Gato", "ave" to "Ave",
    "conejo" to "Conejo", "reptil" to "Reptil", "otro" to "Otro"
)

object AgendaService {

    fun getAgenda(medicoId: String, fechaParam: String?): AgendaResponse {
        val fecha = if (!fechaParam.isNullOrBlank()) LocalDate.parse(fechaParam, dateFormatter)
                    else findNextDayWithCitas(medicoId) ?: LocalDate.now()
        val citas = CitaRepository.findByMedicoAndFecha(medicoId, fecha.format(dateFormatter))
        val diaNombre = diasSemana[fecha.dayOfWeek.name] ?: fecha.dayOfWeek.name
        val mes = mesesLargo[fecha.monthValue - 1]
        val titulo = "$diaNombre, ${fecha.dayOfMonth} de $mes"
        val citasAgenda = citas.sortedBy { it.hora }.map { cita ->
            val mascota = MascotaRepository.findById(cita.mascotaId)
            val servicio = ServicioRepository.findById(cita.servicioId)
            val horaFin = LocalTime.parse(cita.hora, timeFormatter).plusMinutes(30).format(timeFormatter)
            CitaAgenda(
                id = cita.id,
                hora = cita.hora,
                horaFin = horaFin,
                mascota = mascota?.nombre ?: "",
                detalle = "${mascota?.raza ?: ""} • ${servicio?.nombre ?: ""}",
                critico = servicio?.critico ?: false
            )
        }
        return AgendaResponse(fecha = fecha.format(dateFormatter), titulo = titulo, citas = citasAgenda)
    }

    private fun findNextDayWithCitas(medicoId: String): LocalDate? {
        val todasLasCitas = CitaRepository.findByMedicoId(medicoId)
        val hoy = LocalDate.now()
        return todasLasCitas
            .map { LocalDate.parse(it.fecha, dateFormatter) }
            .filter { it >= hoy }
            .minOrNull()
    }
}

object HorarioService {

    private val bloques = listOf("09:00","09:30","10:00","10:30","11:00","11:30","15:00","15:30","16:00")

    fun getProximosDias(): List<DiaDisponible> {
        val dias = mutableListOf<DiaDisponible>()
        var current = LocalDate.now().plusDays(1)
        while (dias.size < 6) {
            if (current.dayOfWeek != DayOfWeek.SUNDAY) {
                dias.add(DiaDisponible(
                    fecha = current.format(dateFormatter),
                    dia = diasCortos[current.dayOfWeek.name] ?: "",
                    numero = current.dayOfMonth,
                    mes = mesesLargo[current.monthValue - 1],
                    anio = current.year
                ))
            }
            current = current.plusDays(1)
        }
        return dias
    }

    fun getHorarios(fecha: String, servicioId: String): HorariosResponse {
        val servicio = ServicioRepository.findById(servicioId)
            ?: throw ApiException(HttpStatusCode.NotFound, "Servicio no encontrado")
        val horarios = bloques.map { hora ->
            val taken = CitaRepository.isSlotTaken(servicio.medicoId, fecha, hora)
            HorarioSlot(hora = hora, disponible = !taken)
        }
        return HorariosResponse(fecha = fecha, horarios = horarios)
    }
}

object MascotaService {

    fun getMascotas(clienteId: String): List<MascotaResumen> {
        val mascotas = MascotaRepository.findByClienteId(clienteId)
        val hoy = LocalDate.now().format(dateFormatter)
        return mascotas.map { mascota ->
            val proximaCita = CitaRepository.findByClienteId(clienteId)
                .filter { it.mascotaId == mascota.id && it.estado == "Confirmada" && it.fecha >= hoy }
                .sortedBy { it.fecha + it.hora }.firstOrNull()
            val citaTexto = formatCita(proximaCita)
            MascotaResumen(
                id = mascota.id, nombre = mascota.nombre, especie = mascota.especie,
                especieTexto = especieTextos[mascota.especie] ?: mascota.especie,
                raza = mascota.raza, edad = mascota.edad, chip = mascota.chip,
                estado = mascota.estado, cita = citaTexto
            )
        }
    }

    fun createMascota(clienteId: String, request: MascotaRequest): MascotaResumen {
        val mascota = Mascota(
            nombre = request.nombre, especie = request.especie, raza = request.raza,
            edad = request.edad, clienteId = clienteId, notas = request.notas
        )
        val saved = MascotaRepository.save(mascota)
        return MascotaResumen(
            id = saved.id, nombre = saved.nombre, especie = saved.especie,
            especieTexto = especieTextos[saved.especie] ?: saved.especie,
            raza = saved.raza, edad = saved.edad, chip = saved.chip,
            estado = saved.estado, cita = "Sin citas próximas"
        )
    }

    fun getMascotaFicha(mascotaId: String): MascotaFicha {
        val mascota = MascotaRepository.findById(mascotaId)
            ?: throw ApiException(HttpStatusCode.NotFound, "Mascota no encontrada")
        val tutor = UserRepository.findById(mascota.clienteId)
        return MascotaFicha(
            id = mascota.id, nombre = mascota.nombre, especie = mascota.especie,
            especieTexto = especieTextos[mascota.especie] ?: mascota.especie,
            raza = mascota.raza, edad = mascota.edad, chip = mascota.chip,
            estado = mascota.estado, notas = mascota.notas,
            medicacion = mascota.medicacion, vacunas = mascota.vacunas,
            historial = mascota.historial,
            tutor = TutorInfo(
                nombre = tutor?.nombre ?: "", telefono = tutor?.telefono ?: "", correo = tutor?.correo ?: ""
            )
        )
    }

    fun searchPacientes(busqueda: String): List<PacienteResumen> {
        val termino = busqueda.lowercase()
        val mascotas = MascotaRepository.findAll()
        val directas = mascotas.filter { it.nombre.lowercase().contains(termino) }.map { mascota ->
            val tutor = UserRepository.findById(mascota.clienteId)
            PacienteResumen(id = mascota.id, nombre = mascota.nombre, raza = mascota.raza, tutor = tutor?.nombre ?: "")
        }
        val porTutor = mascotas.filter { mascota ->
            val tutor = UserRepository.findById(mascota.clienteId)
            tutor?.nombre?.lowercase()?.contains(termino) == true && !mascota.nombre.lowercase().contains(termino)
        }.map { mascota ->
            val tutor = UserRepository.findById(mascota.clienteId)
            PacienteResumen(id = mascota.id, nombre = mascota.nombre, raza = mascota.raza, tutor = tutor?.nombre ?: "")
        }
        return directas + porTutor
    }

    private fun formatCita(cita: Cita?): String {
        if (cita == null) return "Sin citas próximas"
        val hoy = LocalDate.now()
        val fechaCita = LocalDate.parse(cita.fecha, dateFormatter)
        val diasDiff = ChronoUnit.DAYS.between(hoy, fechaCita)
        val fechaStr = when {
            diasDiff == 0L -> "Hoy"
            diasDiff == 1L -> "Mañana, ${fechaCita.dayOfMonth} ${mesesCorto[fechaCita.monthValue - 1]}"
            else -> "${fechaCita.dayOfMonth} ${mesesCorto[fechaCita.monthValue - 1]}"
        }
        return "Cita: $fechaStr • ${cita.hora}"
    }
}

object AdminService {

    fun getResumen(): AdminResumen {
        val hoy = LocalDate.now().format(dateFormatter)
        val todasCitas = CitaRepository.findAll()
        val citasHoy = todasCitas.count { it.fecha == hoy && it.estado == "Confirmada" }
        val completadas = todasCitas.count { it.estado == "Completada" }
        val vetsActivos = MedicoRepository.findAll().size
        val meses = AdminRepository.getMetricas()
        return AdminResumen(
            stats = AdminStats(citasHoy = citasHoy, vetsActivos = vetsActivos, completadas = completadas),
            meses = meses
        )
    }

    fun getEspecialistas(): AdminEspecialistas {
        val hoy = LocalDate.now().format(dateFormatter)
        val todasCitas = CitaRepository.findAll()
        val citasHoy = todasCitas.count { it.fecha == hoy && it.estado == "Confirmada" }
        val completadas = todasCitas.count { it.estado == "Completada" }
        val medicos = MedicoRepository.findAll()
        val especialistas = medicos.map { medico ->
            val citasMedico = todasCitas.count { it.medicoId == medico.id }
            EspecialistaInfo(
                id = medico.id, nombre = medico.nombre,
                especialidad = medico.especialidad, citas = citasMedico, rating = 4.8
            )
        }
        val recursos = listOf(
            RecursoInfo("Sala de Cirugía 1", true),
            RecursoInfo("Sala de Cirugía 2", false),
            RecursoInfo("Laboratorio", true),
            RecursoInfo("Radiografía", true)
        )
        return AdminEspecialistas(
            stats = AdminStats(citasHoy = citasHoy, vetsActivos = medicos.size, completadas = completadas),
            especialistas = especialistas, recursos = recursos
        )
    }
}
