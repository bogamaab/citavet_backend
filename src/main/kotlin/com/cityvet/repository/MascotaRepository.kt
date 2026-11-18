package com.cityvet.repository

import com.cityvet.models.HistorialItem
import com.cityvet.models.Mascota
import com.cityvet.models.Vacuna
import com.google.firebase.cloud.FirestoreClient

object MascotaRepository {

    private val db get() = FirestoreClient.getFirestore()

    fun findByClienteId(clienteId: String): List<Mascota> {
        return db.collection("mascotas")
            .whereEqualTo("clienteId", clienteId)
            .get().get().documents
            .map { it.toMascota() }
    }

    fun findById(id: String): Mascota? {
        val doc = db.collection("mascotas").document(id).get().get()
        return if (doc.exists()) doc.toMascota() else null
    }

    fun save(mascota: Mascota): Mascota {
        val ref = if (mascota.id.isEmpty()) db.collection("mascotas").document()
                  else db.collection("mascotas").document(mascota.id)
        val withId = mascota.copy(id = ref.id)
        ref.set(withId.toMap()).get()
        return withId
    }

    fun findAll(): List<Mascota> {
        return db.collection("mascotas").get().get().documents.map { it.toMascota() }
    }

    @Suppress("UNCHECKED_CAST")
    private fun com.google.cloud.firestore.DocumentSnapshot.toMascota() = Mascota(
        id = id,
        nombre = getString("nombre") ?: "",
        especie = getString("especie") ?: "",
        raza = getString("raza") ?: "",
        edad = getLong("edad")?.toInt() ?: 0,
        chip = getString("chip") ?: "",
        estado = getString("estado") ?: "Activo",
        clienteId = getString("clienteId") ?: "",
        notas = getString("notas") ?: "",
        medicacion = (get("medicacion") as? List<String>) ?: emptyList(),
        vacunas = ((get("vacunas") as? List<Map<String, Any>>) ?: emptyList())
            .map { v -> Vacuna(v["nombre"] as? String ?: "", v["fecha"] as? String ?: "", v["proxima"] as? String ?: "") },
        historial = ((get("historial") as? List<Map<String, Any>>) ?: emptyList())
            .map { h -> HistorialItem(h["fecha"] as? String ?: "", h["descripcion"] as? String ?: "", h["medico"] as? String ?: "") }
    )

    private fun Mascota.toMap(): Map<String, Any> = mapOf(
        "id" to id,
        "nombre" to nombre,
        "especie" to especie,
        "raza" to raza,
        "edad" to edad,
        "chip" to chip,
        "estado" to estado,
        "clienteId" to clienteId,
        "notas" to notas,
        "medicacion" to medicacion,
        "vacunas" to vacunas.map { mapOf("nombre" to it.nombre, "fecha" to it.fecha, "proxima" to it.proxima) },
        "historial" to historial.map { mapOf("fecha" to it.fecha, "descripcion" to it.descripcion, "medico" to it.medico) }
    )
}
