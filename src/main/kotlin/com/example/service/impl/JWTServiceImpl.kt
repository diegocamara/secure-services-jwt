package com.example.service.impl

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.service.JWTService
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.*

class JWTServiceImpl(private val algorithm: Algorithm, private val objectMapper: ObjectMapper) : JWTService{

    override fun sign(subject: Any, expirationTimeInSeconds: Int, issuer: String): String {
        val subjectValue = objectMapper.writeValueAsString(subject)
        val expireAt = plusSeconds(expirationTimeInSeconds)
       return JWT.create().withSubject(subjectValue).withExpiresAt(expireAt).withIssuer(issuer).sign(algorithm)
    }

    private fun plusSeconds(seconds: Int): Date {
        val oneSecondInMillis = 1000
        val calendar = Calendar.getInstance()
        return Date(calendar.timeInMillis + (seconds * oneSecondInMillis))
    }

}