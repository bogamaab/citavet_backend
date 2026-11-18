package com.cityvet.repository

import com.cityvet.models.MesMetrica
import com.google.firebase.cloud.FirestoreClient

object AdminRepository {

    private val db get() = FirestoreClient.getFirestore()

    fun getMetricas(): List<MesMetrica> {
        return db.collection("admin_metricas").get().get().documents.map {
            MesMetrica(
                id = it.id,
                mes = it.getString("mes") ?: "",
                area = it.getString("area") ?: "",
                citas = it.getLong("citas")?.toInt() ?: 0,
                costos = it.getDouble("costos") ?: 0.0,
                asistencia = it.getLong("asistencia")?.toInt() ?: 0
            )
        }
    }
}
