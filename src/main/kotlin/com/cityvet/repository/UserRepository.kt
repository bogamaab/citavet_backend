package com.cityvet.repository

import com.cityvet.models.User
import com.google.firebase.cloud.FirestoreClient

object UserRepository {

    private val db get() = FirestoreClient.getFirestore()

    fun findByToken(token: String): User? {
        val docs = db.collection("usuarios")
            .whereEqualTo("token", token)
            .get().get().documents
        return docs.firstOrNull()?.toUser()
    }

    fun findByCorreo(correo: String): User? {
        val docs = db.collection("usuarios")
            .whereEqualTo("correo", correo)
            .get().get().documents
        return docs.firstOrNull()?.toUser()
    }

    fun findById(id: String): User? {
        val doc = db.collection("usuarios").document(id).get().get()
        return if (doc.exists()) doc.toUser() else null
    }

    fun save(user: User): User {
        val ref = if (user.id.isEmpty()) db.collection("usuarios").document()
                  else db.collection("usuarios").document(user.id)
        val withId = user.copy(id = ref.id)
        ref.set(withId.toMap()).get()
        return withId
    }

    fun update(id: String, fields: Map<String, Any>) {
        db.collection("usuarios").document(id).update(fields).get()
    }

    private fun com.google.cloud.firestore.DocumentSnapshot.toUser() = User(
        id = id,
        nombre = getString("nombre") ?: "",
        correo = getString("correo") ?: "",
        telefono = getString("telefono") ?: "",
        password = getString("password") ?: "",
        rol = getString("rol") ?: "cliente",
        token = getString("token") ?: "",
        medicoId = getString("medicoId") ?: "",
        direccion = getString("direccion") ?: "",
        recordatorios = getBoolean("recordatorios") ?: true,
        alertas = getBoolean("alertas") ?: true
    )

    private fun User.toMap() = mapOf(
        "id" to id,
        "nombre" to nombre,
        "correo" to correo,
        "telefono" to telefono,
        "password" to password,
        "rol" to rol,
        "token" to token,
        "medicoId" to medicoId,
        "direccion" to direccion,
        "recordatorios" to recordatorios,
        "alertas" to alertas
    )
}
