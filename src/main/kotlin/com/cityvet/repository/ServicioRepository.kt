package com.cityvet.repository

import com.cityvet.models.Servicio
import com.google.firebase.cloud.FirestoreClient

object ServicioRepository {

    private val db get() = FirestoreClient.getFirestore()

    fun findAll(): List<Servicio> {
        return db.collection("servicios").get().get().documents.map { it.toServicio() }
    }

    fun findById(id: String): Servicio? {
        val doc = db.collection("servicios").document(id).get().get()
        return if (doc.exists()) doc.toServicio() else null
    }

    private fun com.google.cloud.firestore.DocumentSnapshot.toServicio() = Servicio(
        id = id,
        nombre = getString("nombre") ?: "",
        detalle = getString("detalle") ?: "",
        precio = getDouble("precio") ?: 0.0,
        highlight = getBoolean("highlight") ?: false,
        critico = getBoolean("critico") ?: false,
        medicoId = getString("medicoId") ?: ""
    )
}
