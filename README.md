# CityVet Backend 🐾

Backend oficial de la plataforma **CityVet**, desarrollado con **Ktor**, **Kotlin** y **Cloud Firestore** a través de **Firebase Admin SDK**. Proporciona una API REST modular para autenticación Bearer, gestión de perfiles, mascotas, citas veterinarias, agenda médica y métricas administrativas.

---

## 📌 1. Versiones y Tecnologías del Proyecto

| Herramienta / Librería         | Versión  | Propósito                                                 |
| ------------------------------ | -------- | --------------------------------------------------------- |
| **JDK (Java Development Kit)** | `21`     | Toolchain de compilación y ejecución (`jvmToolchain(21)`) |
| **Kotlin**                     | `2.1.21` | Lenguaje de programación principal                        |
| **Ktor**                       | `3.1.3`  | Framework HTTP asíncrono sobre Netty                      |
| **Gradle**                     | `9.5.1`  | Motor de construcción y gestión de dependencias           |
| **Firebase Admin SDK**         | `9.4.2`  | Conexión e integración con Cloud Firestore                |
| **kotlinx.serialization**      | `1.8.1`  | Negociación y serialización JSON nativa                   |
| **Logback Classic**            | `1.5.18` | Motor de logging para consola                             |
| **Gradle Shadow Plugin**       | `8.3.6`  | Empaquetado en Fat JAR (`cityvet-backend-all.jar`)        |

---

## 📦 2. Dependencias del Proyecto

Gestionadas de forma centralizada en `gradle/libs.versions.toml`:

```toml
[libraries]
ktor-server-core = { module = "io.ktor:ktor-server-core", version = "3.1.3" }
ktor-server-netty = { module = "io.ktor:ktor-server-netty", version = "3.1.3" }
ktor-server-content-negotiation = { module = "io.ktor:ktor-server-content-negotiation", version = "3.1.3" }
ktor-serialization-kotlinx-json = { module = "io.ktor:ktor-serialization-kotlinx-json", version = "3.1.3" }
ktor-server-cors = { module = "io.ktor:ktor-server-cors", version = "3.1.3" }
ktor-server-status-pages = { module = "io.ktor:ktor-server-status-pages", version = "3.1.3" }
ktor-server-auth = { module = "io.ktor:ktor-server-auth", version = "3.1.3" }
ktor-server-config-yaml = { module = "io.ktor:ktor-server-config-yaml", version = "3.1.3" }
firebase-admin = { module = "com.google.firebase:firebase-admin", version = "9.4.2" }
kotlinx-serialization-json = { module = "org.jetbrains.kotlinx:kotlinx-serialization-json", version = "1.8.1" }
logback-classic = { module = "ch.qos.logback:logback-classic", version = "1.5.18" }
ktor-server-test-host = { module = "io.ktor:ktor-server-test-host", version = "3.1.3" }
kotlin-test-junit = { module = "org.jetbrains.kotlin:kotlin-test-junit", version = "2.1.21" }
```

---

## ⚙️ 3. Requisitos Previos

1. **Java JDK 21** instalado y configurado en tu sistema (`JAVA_HOME` apuntando al JDK 21).
   ```bash
   java -version
   # Debe indicar openjdk version "21.x.x"
   ```
2. **Clave de Cuenta de Servicio Firebase (serviceAccountKey.json):**
   - Descarga la credencial de cuenta de servicio desde la consola de Firebase (**Configuración del proyecto > Cuentas de servicio > Generar nueva clave privada**).
   - Coloca el archivo en:
     ```text
     src/main/resources/serviceAccountKey.json
     ```

---

## 🚀 4. Formas de Ejecución

El servidor por defecto escucha en **`http://0.0.0.0:8080`**.

### Opción A: Mediante Terminal con Gradle Wrapper (`./gradlew run`)

1. **Poblar la Base de Datos Inicial (Seeder):**
   ```bash
   ./gradlew run --args="seed"
   ```
2. **Iniciar el Servidor en Modo Desarrollo:**
   ```bash
   ./gradlew run
   ```
3. **Generar y Ejecutar el Fat JAR de Producción:**
   ```bash
   ./gradlew shadowJar
   java -jar build/libs/cityvet-backend-all.jar
   ```

---

### Opción B: Mediante IntelliJ IDEA (Intérprete JetBrains)

1. Abre **IntelliJ IDEA** y selecciona **Open** > selecciona la carpeta `cityvet-backend`.
2. Deja que Gradle sincronice el proyecto.
3. Asegúrate de configurar el SDK del proyecto en Java 21:
   - Ve a **File > Project Structure > Project > SDK** y selecciona **Java 21**.
   - En **Settings / Preferences > Build, Execution, Deployment > Build Tools > Gradle**, selecciona **Gradle JVM: Java 21**.
4. **Ejecutar:**
   - Navega en el árbol de archivos hasta `src/main/kotlin/com/cityvet/Application.kt`.
   - Haz clic en el ícono de triángulo verde (▶) al lado de `fun main(args: Array<String>)` y pulsa **Run 'ApplicationKt'**.
   - Alternativamente, en la ventana de herramientas de Gradle (derecha): `Tasks > application > run`.

---

### Opción C: Mediante Android Studio

1. Abre **Android Studio** y elige **Open** > selecciona la carpeta `cityvet-backend`.
2. Espera a que Gradle complete la sincronización inicial.
3. Verifica la versión de Java del entorno:
   - Abre **Settings / Preferences > Build, Execution, Deployment > Build Tools > Gradle**.
   - Asegúrate de que **Gradle JDK** esté configurado en **Version 21** (o Embedded JDK si corresponde a Java 21).
4. Abre `src/main/kotlin/com/cityvet/Application.kt` y presiona el botón verde de ejecución **Run 'ApplicationKt'**.

---

## 📂 5. Estructura del Proyecto

```text
cityvet-backend/
├── build.gradle.kts                   # Configuración de compilación, plugins y dependencias
├── settings.gradle.kts                # Configuración de módulos y repositorios Gradle
├── gradlew / gradlew.bat              # Wrapper ejecutable de Gradle 9.5.1
├── gradle/
│   ├── libs.versions.toml             # Catálogo de versiones y dependencias centralizadas
│   └── wrapper/                       # Configuración del Gradle Wrapper
├── database/
│   └── firestore.rules                # Reglas de seguridad para Cloud Firestore
├── src/
│   └── main/
│       ├── resources/
│       │   ├── application.yaml       # Configuración de puerto (8080) y módulos Ktor
│       │   ├── logback.xml            # Formato y niveles de logs de consola
│       │   └── serviceAccountKey.json # Credencial privada de Firebase Admin SDK
│       └── kotlin/com/cityvet/
│           ├── Application.kt         # Punto de entrada (main) y registro de routing
│           ├── config/                # Módulos de configuración Ktor
│           │   ├── AuthConfig.kt      # Autenticación Bearer y validación de roles
│           │   ├── CorsConfig.kt      # Políticas de Cross-Origin Resource Sharing
│           │   ├── FirebaseConfig.kt  # Inicialización del SDK de Firebase
│           │   ├── SerializationConfig.kt # Soporte JSON con kotlinx.serialization
│           │   └── StatusPagesConfig.kt   # Manejador global de excepciones ApiException
│           ├── models/                # Data classes y DTOs serializables
│           │   ├── ApiException.kt    # Excepción estándar con código HTTP y mensaje
│           │   ├── Cita.kt            # Modelos de Cita, CitaRequest y Resumen
│           │   ├── Mascota.kt         # Modelos de Mascota, Ficha e Historial clínico
│           │   └── User.kt            # Modelos de Usuario, Autenticación y Médicos
│           ├── repository/            # Acceso a datos en colecciones Cloud Firestore
│           │   ├── AdminRepository.kt
│           │   ├── CitaRepository.kt
│           │   ├── MascotaRepository.kt
│           │   ├── MedicoRepository.kt
│           │   ├── ServicioRepository.kt
│           │   └── UserRepository.kt
│           ├── service/               # Lógica de negocio de la aplicación
│           │   ├── AdminService.kt
│           │   ├── AgendaService.kt   # Horarios disponibles y agenda médica
│           │   ├── AuthService.kt     # Login, registro y emisión de tokens
│           │   ├── CitaService.kt     # Agendamiento, reprogramación y cancelación
│           │   ├── MascotaService.kt  # Gestión y búsqueda de pacientes
│           │   └── PerfilService.kt   # Edición y consulta de datos del tutor
│           ├── routes/                # Controladores HTTP y endpoints REST
│           │   ├── AdminRoutes.kt
│           │   ├── AuthRoutes.kt
│           │   ├── CitaRoutes.kt
│           │   ├── DiasHorariosRoutes.kt
│           │   ├── MascotaRoutes.kt
│           │   ├── PerfilRoutes.kt
│           │   ├── ServicioRoutes.kt
│           │   └── VeterinarioRoutes.kt
│           └── seed/
│               └── FirestoreSeeder.kt # Poblador idempotente de datos iniciales
```

---

## 🌐 6. Catálogo de Rutas y Endpoints

### 🔑 Autenticación (`/auth`)

| Método | Endpoint         | Acceso  | Descripción                                                       |
| ------ | ---------------- | ------- | ----------------------------------------------------------------- |
| `POST` | `/auth/login`    | Público | Autentica un usuario con email y contraseña, retorna token Bearer |
| `POST` | `/auth/register` | Público | Registra un nuevo usuario (`cliente` o `medico`)                  |

### 👤 Perfil (`/perfil`)

| Método | Endpoint  | Acceso         | Descripción                                          |
| ------ | --------- | -------------- | ---------------------------------------------------- |
| `GET`  | `/perfil` | Bearer (Todos) | Consulta los datos del usuario autenticado           |
| `PUT`  | `/perfil` | Bearer (Todos) | Actualiza datos personales y preferencias de alertas |

### 🐶 Mascotas (`/mascotas`)

| Método | Endpoint    | Acceso                      | Descripción                                      |
| ------ | ----------- | --------------------------- | ------------------------------------------------ |
| `GET`  | `/mascotas` | Bearer (`cliente`, `admin`) | Lista las mascotas del tutor con su próxima cita |
| `POST` | `/mascotas` | Bearer (`cliente`, `admin`) | Registra una nueva mascota                       |

### 🩺 Servicios (`/servicios`)

| Método | Endpoint     | Acceso         | Descripción                                              |
| ------ | ------------ | -------------- | -------------------------------------------------------- |
| `GET`  | `/servicios` | Bearer (Todos) | Lista los servicios veterinarios disponibles con precios |

### 📅 Días y Horarios (`/dias`, `/horarios`)

| Método | Endpoint    | Acceso         | Descripción                                                          |
| ------ | ----------- | -------------- | -------------------------------------------------------------------- |
| `GET`  | `/dias`     | Bearer (Todos) | Lista los próximos 6 días hábiles con citas disponibles              |
| `GET`  | `/horarios` | Bearer (Todos) | Consulta bloques de horarios y disponibilidad según fecha y servicio |

### 🗓️ Citas (`/citas`)

| Método   | Endpoint                          | Acceso                      | Descripción                                            |
| -------- | --------------------------------- | --------------------------- | ------------------------------------------------------ |
| `GET`    | `/citas?tipo=proximas\|historial` | Bearer (`cliente`, `admin`) | Lista citas confirmadas futuras o completadas/pasadas  |
| `POST`   | `/citas`                          | Bearer (`cliente`, `admin`) | Agenda una nueva cita asociándola al servicio y médico |
| `PUT`    | `/citas/{id}`                     | Bearer (`cliente`, `admin`) | Reagenda fecha y hora de una cita confirmada           |
| `DELETE` | `/citas/{id}`                     | Bearer (`cliente`, `admin`) | Cancela una cita activa                                |

### 👨‍⚕️ Veterinario y Pacientes (`/veterinario`, `/pacientes`)

| Método | Endpoint                               | Acceso                     | Descripción                                          |
| ------ | -------------------------------------- | -------------------------- | ---------------------------------------------------- |
| `GET`  | `/veterinario/agenda?fecha=YYYY-MM-DD` | Bearer (`medico`, `admin`) | Agenda diaria del profesional autenticado            |
| `GET`  | `/pacientes?busqueda=XXX`              | Bearer (`medico`, `admin`) | Búsqueda rápida de mascotas y tutores                |
| `GET`  | `/pacientes/{id}`                      | Bearer (`medico`, `admin`) | Ficha clínica completa, medicación y datos del tutor |

### 📊 Panel de Administración (`/admin`)

| Método | Endpoint               | Acceso           | Descripción                                                |
| ------ | ---------------------- | ---------------- | ---------------------------------------------------------- |
| `GET`  | `/admin/resumen`       | Bearer (`admin`) | Métricas generales, citas diarias y comparativas mensuales |
| `GET`  | `/admin/especialistas` | Bearer (`admin`) | Estado de actividad de médicos y disponibilidad de salas   |

---

## 👥 7. Usuarios de Prueba Preconfigurados

| Rol         | Correo                       | Contraseña | Capacidades                                                |
| ----------- | ---------------------------- | ---------- | ---------------------------------------------------------- |
| **Cliente** | `maria.gonzalez@vetcare.com` | `123456`   | Gestión de mascotas (Max y Luna), agendar y cancelar citas |
| **Médico**  | `fernando.ruiz@vetcare.com`  | `123456`   | Agenda del día, búsqueda de pacientes y fichas clínicas    |
| **Admin**   | `admin@vetcare.com`          | `admin123` | Dashboard general, métricas y disponibilidad de quirófanos |
