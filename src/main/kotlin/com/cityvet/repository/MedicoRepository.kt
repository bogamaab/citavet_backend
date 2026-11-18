package com.cityvet.repository

import com.cityvet.models.Medico
import com.google.firebase.cloud.FirestoreClient

object MedicoRepository {

    private val db get() = FirestoreClient.getFirestore()

    fun save(medico: Medico): Medico {
        val ref = if (medico.id.isEmpty()) db.collection("medicos").document()
                  else db.collection("medicos").document(medico.id)
        val withId = medico.copy(id = ref.id)
        ref.set(mapOf(
            "id" to withId.id,
            "nombre" to withId.nombre,
            "especialidad" to withId.especialidad,
            "usuarioId" to withId.usuarioId
        )).get()
        return withId
    }

    fun findAll(): List<Medico> {
        return db.collection("medicos").get().get().documents.map {
            Medico(
                id = it.id,
                nombre = it.getString("nombre") ?: "",
                especialidad = it.getString("especialidad") ?: "",
                usuarioId = it.getString("usuarioId") ?: ""
            )
        }
    }

    fun findById(id: String): Medico? {
        val doc = db.collection("medicos").document(id).get().get()
        return if (doc.exists()) Medico(
            id = doc.id,
            nombre = doc.getString("nombre") ?: "",
            especialidad = doc.getString("especialidad") ?: "",
            usuarioId = doc.getString("usuarioId") ?: ""
        ) else null
    }
}
