package com.example.service

interface JWTService {

    fun sign(subject: Any, expirationTimeInSeconds: Int, issuer: String): String

}