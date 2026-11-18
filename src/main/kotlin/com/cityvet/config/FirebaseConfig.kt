package com.cityvet.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

object FirebaseConfig {
    fun initialize() {
        if (FirebaseApp.getApps().isNotEmpty()) return
        val serviceAccount = FirebaseConfig::class.java.classLoader
            .getResourceAsStream("serviceAccountKey.json")
            ?: error("serviceAccountKey.json no encontrado en el classpath")
        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build()
        FirebaseApp.initializeApp(options)
    }
}
