package com.cityvet.repository

import com.cityvet.models.Cita
import com.google.firebase.cloud.FirestoreClient

object CitaRepository {

    private val db get() = FirestoreClient.getFirestore()

    fun findByClienteId(clienteId: String): List<Cita> {
        return db.collection("citas")
            .whereEqualTo("clienteId", clienteId)
            .get().get().documents
            .map { it.toCita() }
    }

    fun findByMedicoAndFecha(medicoId: String, fecha: String): List<Cita> {
        return db.collection("citas")
            .whereEqualTo("medicoId", medicoId)
            .whereEqualTo("fecha", fecha)
            .whereEqualTo("estado", "Confirmada")
            .get().get().documents
            .map { it.toCita() }
    }

    fun findById(id: String): Cita? {
        val doc = db.collection("citas").document(id).get().get()
        return if (doc.exists()) doc.toCita() else null
    }

    fun isSlotTaken(medicoId: String, fecha: String, hora: String, excludeId: String = ""): Boolean {
        val docs = db.collection("citas")
            .whereEqualTo("medicoId", medicoId)
            .whereEqualTo("fecha", fecha)
            .whereEqualTo("hora", hora)
            .whereEqualTo("estado", "Confirmada")
            .get().get().documents
        return docs.any { it.id != excludeId }
    }

    fun save(cita: Cita): Cita {
        val ref = if (cita.id.isEmpty()) db.collection("citas").document()
                  else db.collection("citas").document(cita.id)
        val withId = cita.copy(id = ref.id)
        ref.set(withId.toMap()).get()
        return withId
    }

    fun update(id: String, fields: Map<String, Any>) {
        db.collection("citas").document(id).update(fields).get()
    }

    fun findByMedicoId(medicoId: String): List<Cita> {
        return db.collection("citas")
            .whereEqualTo("medicoId", medicoId)
            .whereEqualTo("estado", "Confirmada")
            .get().get().documents
            .map { it.toCita() }
    }

    fun findAll(): List<Cita> {
        return db.collection("citas").get().get().documents.map { it.toCita() }
    }

    private fun com.google.cloud.firestore.DocumentSnapshot.toCita() = Cita(
        id = id,
        mascotaId = getString("mascotaId") ?: "",
        servicioId = getString("servicioId") ?: "",
        clienteId = getString("clienteId") ?: "",
        medicoId = getString("medicoId") ?: "",
        fecha = getString("fecha") ?: "",
        hora = getString("hora") ?: "",
        estado = getString("estado") ?: "Confirmada",
        doctor = getString("doctor") ?: ""
    )

    private fun Cita.toMap() = mapOf(
        "id" to id,
        "mascotaId" to mascotaId,
        "servicioId" to servicioId,
        "clienteId" to clienteId,
        "medicoId" to medicoId,
        "fecha" to fecha,
        "hora" to hora,
        "estado" to estado,
        "doctor" to doctor
    )
}
