package com.example.web.issuer

import com.auth0.jwt.algorithms.Algorithm
import com.example.domain.Hero
import com.example.service.impl.AbstractWebApplication
import com.example.service.impl.JWTServiceImpl
import com.example.web.issuer.controller.HeroController
import com.fasterxml.jackson.databind.ObjectMapper
import io.javalin.apibuilder.ApiBuilder
import java.security.KeyStore
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

class IssuerService : AbstractWebApplication() {

    private val heroController = HeroController(JWTServiceImpl(algorithm(), ObjectMapper()))

    init {
        webApp().routes {
            this.heroController.registerRoutes()
        }
    }

    private fun algorithm(): Algorithm {
        val keystorePassword = "123456".toCharArray()
        val keyAlias = "test"
        val pkcs12 = pkcs12(keystorePassword)
        return algorithm(pkcs12, keyAlias, keystorePassword)
    }

    private fun pkcs12(keystorePassword: CharArray): KeyStore {
        val pkcs12 = KeyStore.getInstance("pkcs12")
        pkcs12.load(
            IssuerService::class.java.getResourceAsStream("/test.pkcs12"),
            keystorePassword
        )
        return pkcs12
    }

    private fun algorithm(
        pkcs12: KeyStore,
        keyAlias: String,
        keystorePassword: CharArray
    ): Algorithm {
        val privateKey = pkcs12.getKey(keyAlias, keystorePassword) as RSAPrivateKey
        val publicKey = pkcs12.getCertificate(keyAlias).publicKey as RSAPublicKey
        return Algorithm.RSA256(publicKey, privateKey)
    }

}