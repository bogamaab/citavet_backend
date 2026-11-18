# PLAN.md — CityVet Backend

**Objetivo:** Implementar el backend REST completo de CityVet con Ktor 3.4.x + Firestore en Kotlin 2.3.x.

**Stack Detectado:** Kotlin 2.3.x · Ktor 3.4.x (Netty) · kotlinx.serialization · Firebase Admin SDK · JDK 21 · Gradle 8 (Kotlin DSL)

---

## Hitos de Ejecución

### [x] H1 — Scaffolding del proyecto Gradle + Application.kt
Archivos:
- `settings.gradle.kts`
- `build.gradle.kts`
- `src/main/kotlin/com/cityvet/Application.kt`
- `src/main/resources/application.yaml`

Verificación: Scaffolding y Gradle wrapper 9.5.1 configurados.

---

### [x] H2 — Config: Firebase · CORS · StatusPages · Auth Bearer
Archivos:
- `src/main/kotlin/com/cityvet/config/FirebaseConfig.kt`
- `src/main/kotlin/com/cityvet/config/CorsConfig.kt`
- `src/main/kotlin/com/cityvet/config/StatusPagesConfig.kt`
- `src/main/kotlin/com/cityvet/config/AuthConfig.kt`

Verificación: Clases de configuración y serialización implementadas.

---

### [x] H3 — Models + ApiException
Archivos:
- `src/main/kotlin/com/cityvet/models/User.kt`
- `src/main/kotlin/com/cityvet/models/Mascota.kt`
- `src/main/kotlin/com/cityvet/models/Cita.kt`
- `src/main/kotlin/com/cityvet/models/ApiException.kt`

Verificación: Modelos y DTOs serializables listos.

---

### [x] H4 — Repositories (Firestore CRUD)
Archivos:
- `src/main/kotlin/com/cityvet/repository/UserRepository.kt`
- `src/main/kotlin/com/cityvet/repository/MascotaRepository.kt`
- `src/main/kotlin/com/cityvet/repository/CitaRepository.kt`
- `src/main/kotlin/com/cityvet/repository/ServicioRepository.kt`

Verificación: Repositorios Firestore validados sintácticamente.

---

### [x] H5 — Services (lógica de negocio)
Archivos:
- `src/main/kotlin/com/cityvet/service/AuthService.kt`
- `src/main/kotlin/com/cityvet/service/PerfilService.kt`
- `src/main/kotlin/com/cityvet/service/CitaService.kt`
- `src/main/kotlin/com/cityvet/service/AgendaService.kt`

Verificación: Capa de servicios compilada sin errores en Kotlin 2.1.21.

---

### [x] H6 — Routes: auth + perfil + mascotas + servicios + días/horarios
Archivos:
- `src/main/kotlin/com/cityvet/routes/AuthRoutes.kt`
- `src/main/kotlin/com/cityvet/routes/PerfilRoutes.kt`
- `src/main/kotlin/com/cityvet/routes/MascotaRoutes.kt`
- `src/main/kotlin/com/cityvet/routes/ServicioRoutes.kt`

Verificación: Rutas de autenticación, perfil, mascotas y servicios implementadas con validación de roles.

---

### [x] H7 — Routes: citas + veterinario + pacientes + admin
Archivos:
- `src/main/kotlin/com/cityvet/routes/CitaRoutes.kt`
- `src/main/kotlin/com/cityvet/routes/VeterinarioRoutes.kt`
- `src/main/kotlin/com/cityvet/routes/AdminRoutes.kt`
- `src/main/kotlin/com/cityvet/routes/DiasHorariosRoutes.kt`

Verificación: Los 18 endpoints REST requeridos conectados e implementados.

---

### [x] H8 — Seed + Firestore Rules
Archivos:
- `src/main/kotlin/com/cityvet/seed/FirestoreSeeder.kt`
- `database/firestore.rules`
- `src/main/resources/serviceAccountKey.json` *(placeholder)*
- `.gitignore`

Verificación: `./gradlew compileKotlin` y `./gradlew shadowJar` exitosos. Generado `cityvet-backend-all.jar`.

---

## Criterio de Aceptación

`./gradlew shadowJar` genera `cityvet-backend-all.jar` ejecutable, y `curl -X POST http://localhost:8080/auth/login` responde JSON válido.
