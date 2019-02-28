package com.example.web.resource

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.Clock
import com.example.service.impl.AbstractWebApplication
import com.example.web.resource.handler.HeroHandler
import io.javalin.apibuilder.ApiBuilder.before
import io.javalin.apibuilder.ApiBuilder.post
import java.security.KeyFactory
import java.security.interfaces.RSAPublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.*

class ServiceA : AbstractWebApplication {

    var jwtVerifier: JWTVerifier

    constructor() : super() {
        this.jwtVerifier = jwtVerifier(null)
        registerRoutes()
    }

    constructor(clock: Clock? = null) : super() {
        this.jwtVerifier = jwtVerifier(clock)
        registerRoutes()
    }

    private fun registerRoutes() {
        webApp().routes {
            before("hero") {
                val token = it.body()
                try {
                    this.jwtVerifier.verify(token)
                } catch (jwtVerificationException: JWTVerificationException) {
                    it.status(401)
                }
            }
            post("hero", HeroHandler())
        }
    }

    private fun jwtVerifier(clock: Clock?): JWTVerifier {

        val issuerServiceBase64PublicKey =
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmt6wEB1OeTGQDjNDv3pd" +
                    "YoLpOOvtBvFwE0fHB2bTbGjgq/uCCwpiWlNzoUPZMAxv0zlAHxgvVIqNuHoZSwe9" +
                    "IiBBrwhUvZJKYAu7hr/IAE4Q298pQIDs/IsVkLTYMFnQYVSCDMOF2nRVuqpS6AD1" +
                    "6WRjgutipGD0G6KXB9TKL9l3cjkPZo94vCRPWW5JcJ4CJVQHXB1IL3pbW5EVPTOp" +
                    "An5J4HpiRKC5tCa/551sHTjnkw8oWtZnh0TGtu6/jTxijc2c7B09B8OV+j7RRZvg" +
                    "14gs0C/D9ybFWNkYYikB615YuYTcDoIHR9d/CpdNe/GwQsmPiNGanVWCaIzgycZ5" +
                    "zwIDAQAB"

        val spec = X509EncodedKeySpec(Base64.getDecoder().decode(issuerServiceBase64PublicKey))
        val keyFactory = KeyFactory.getInstance("RSA")
        val publicKey = keyFactory.generatePublic(spec) as RSAPublicKey

        val baseVerification =
            JWT.require(Algorithm.RSA256(publicKey, null)) as JWTVerifier.BaseVerification

        if (clock != null) {
            return baseVerification.build(clock)
        }

        return baseVerification.build()
    }


}

class ClockImpl : Clock {

    override fun getToday(): Date {
        return Date()
    }

}