package com.cityvet.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String = "",
    val nombre: String = "",
    val correo: String = "",
    val telefono: String = "",
    val password: String = "",
    val rol: String = "cliente",
    val token: String = "",
    val medicoId: String = "",
    val direccion: String = "",
    val recordatorios: Boolean = true,
    val alertas: Boolean = true
)

@Serializable
data class LoginRequest(
    val correo: String,
    val password: String,
    val tipo: String? = null
)

@Serializable
data class RegisterRequest(
    val nombre: String,
    val correo: String,
    val telefono: String,
    val password: String,
    val rol: String
)

@Serializable
data class AuthResponse(
    val token: String,
    val usuario: UserPublic
)

@Serializable
data class UserPublic(
    val id: String,
    val nombre: String,
    val correo: String,
    val telefono: String,
    val rol: String,
    val medicoId: String = "",
    val direccion: String = "",
    val recordatorios: Boolean = true,
    val alertas: Boolean = true
)

@Serializable
data class PerfilUpdateRequest(
    val nombre: String? = null,
    val correo: String? = null,
    val telefono: String? = null,
    val direccion: String? = null,
    val recordatorios: Boolean? = null,
    val alertas: Boolean? = null
)
