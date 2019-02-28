package com.example.service.impl

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.example.domain.Hero
import com.example.service.JWTService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Before
import org.junit.Test
import java.security.KeyFactory
import java.security.KeyStore
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.*

class JWTServiceImplTest {

    lateinit var jwtService: JWTService
    lateinit var jwtVerifier: JWTVerifier

    val keystorePassword = "123456".toCharArray()
    val keyAlias = "test"
    val issuer = "hero-service"

    @Before
    fun setup() {
        val pkcs12 = pkcs12()
        this.jwtService = JWTServiceImpl(algorithm(pkcs12), ObjectMapper())
        this.jwtVerifier = jwtVerifier()
    }

    private fun algorithm(pkcs12: KeyStore): Algorithm {
        val privateKey = pkcs12.getKey(keyAlias, keystorePassword) as RSAPrivateKey
        val publicKey = pkcs12.getCertificate(keyAlias).publicKey as RSAPublicKey
        return Algorithm.RSA256(publicKey, privateKey)
    }

    private fun pkcs12(): KeyStore {
        val pkcs12 = KeyStore.getInstance("pkcs12")
        pkcs12.load(
            JWTServiceImplTest::class.java.getResourceAsStream("/test.pkcs12"),
            keystorePassword
        )
        return pkcs12
    }

    private fun jwtVerifier(): JWTVerifier {

        val base64PublicKey =
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmt6wEB1OeTGQDjNDv3pd" +
                    "YoLpOOvtBvFwE0fHB2bTbGjgq/uCCwpiWlNzoUPZMAxv0zlAHxgvVIqNuHoZSwe9" +
                    "IiBBrwhUvZJKYAu7hr/IAE4Q298pQIDs/IsVkLTYMFnQYVSCDMOF2nRVuqpS6AD1" +
                    "6WRjgutipGD0G6KXB9TKL9l3cjkPZo94vCRPWW5JcJ4CJVQHXB1IL3pbW5EVPTOp" +
                    "An5J4HpiRKC5tCa/551sHTjnkw8oWtZnh0TGtu6/jTxijc2c7B09B8OV+j7RRZvg" +
                    "14gs0C/D9ybFWNkYYikB615YuYTcDoIHR9d/CpdNe/GwQsmPiNGanVWCaIzgycZ5" +
                    "zwIDAQAB"

        val spec = X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey))
        val keyFactory = KeyFactory.getInstance("RSA")
        val publicKey = keyFactory.generatePublic(spec) as RSAPublicKey

        return JWT.require(Algorithm.RSA256(publicKey, null)).withIssuer(issuer).build()
    }

    @Test
    fun `given a jwt generated with rsa when check it with a valid public key then pass`() {

        val heroPayload = Hero("Super")

        val token = this.jwtService.sign(heroPayload, 20, issuer)

        this.jwtVerifier.verify(token)

    }

}