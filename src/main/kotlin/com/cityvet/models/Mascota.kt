package com.cityvet.models

import kotlinx.serialization.Serializable

@Serializable
data class Mascota(
    val id: String = "",
    val nombre: String = "",
    val especie: String = "",
    val raza: String = "",
    val edad: Int = 0,
    val chip: String = "",
    val estado: String = "Activo",
    val clienteId: String = "",
    val notas: String = "",
    val medicacion: List<String> = emptyList(),
    val vacunas: List<Vacuna> = emptyList(),
    val historial: List<HistorialItem> = emptyList()
)

@Serializable
data class Vacuna(
    val nombre: String = "",
    val fecha: String = "",
    val proxima: String = ""
)

@Serializable
data class HistorialItem(
    val fecha: String = "",
    val descripcion: String = "",
    val medico: String = ""
)

@Serializable
data class MascotaRequest(
    val nombre: String,
    val especie: String,
    val raza: String,
    val edad: Int,
    val notas: String = ""
)

@Serializable
data class MascotaResumen(
    val id: String,
    val nombre: String,
    val especie: String,
    val especieTexto: String,
    val raza: String,
    val edad: Int,
    val chip: String,
    val estado: String,
    val cita: String
)

@Serializable
data class MascotaFicha(
    val id: String,
    val nombre: String,
    val especie: String,
    val especieTexto: String,
    val raza: String,
    val edad: Int,
    val chip: String,
    val estado: String,
    val notas: String,
    val medicacion: List<String>,
    val vacunas: List<Vacuna>,
    val historial: List<HistorialItem>,
    val tutor: TutorInfo
)

@Serializable
data class TutorInfo(
    val nombre: String,
    val telefono: String,
    val correo: String
)
