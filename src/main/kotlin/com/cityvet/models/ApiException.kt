package com.cityvet.models

import io.ktor.http.*

class ApiException(val status: HttpStatusCode, override val message: String) : Exception(message)
