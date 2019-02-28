package com.example.service.impl

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.Clock
import com.example.domain.Hero
import com.example.web.resource.ClockImpl
import com.example.web.issuer.IssuerService
import com.example.web.resource.ServiceA
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.spyk
import org.joda.time.DateTime
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class IntegrationTest {

    lateinit var issuerService: IssuerService
    lateinit var serviceA: ServiceA
    lateinit var clock: Clock

    @Before
    fun setUp() {
        this.issuerService = IssuerService()
        this.issuerService.start(8080)

        this.clock = spyk(ClockImpl())
        this.serviceA = ServiceA(this.clock)
        this.serviceA.start(8081)
    }

    @After
    fun tearDown() {
        this.issuerService.stop()
        this.serviceA.stop()
    }

    @Test
    fun `when execute a get from issuer service then receive a jwt with a hero subject`() {

        val issueServiceResourceURL = "http://localhost:8080/hero"

        val hero = khttp.get(issueServiceResourceURL).let {
            val decodedJWT = JWT.decode(it.text)
            ObjectMapper().readValue(decodedJWT.subject, Hero::class.java)
        }

        assertNotNull(hero)

    }

    @Test
    fun `when execute a post in serviceA with jwt issued by issue service then 200 OK`() {

        val issueServiceResourceURL = "http://localhost:8080/hero"

        val heroToken = khttp.get(issueServiceResourceURL).let {
            it.text
        }

        val serviceAURL = "http://localhost:8081/hero"

        val responseCode = khttp.post(serviceAURL, data = heroToken).let {
            it.statusCode
        }

        assertEquals(200, responseCode)

    }

    @Test
    fun `when execute a post in serviceA with jwt modified then 401 Unauthorized`() {

        val issueServiceResourceURL = "http://localhost:8080/hero"

        val hero = khttp.get(issueServiceResourceURL).let {
            val decodedJWT = JWT.decode(it.text)
            ObjectMapper().readValue(decodedJWT.subject, Hero::class.java)
        }

        hero.name = "Super Hero"

        val modifiedHeroToken = JWT.create().withSubject(ObjectMapper().writeValueAsString(hero))
            .sign(Algorithm.HMAC256("123"))

        val serviceAURL = "http://localhost:8081/hero"

        val responseCode = khttp.post(serviceAURL, data = modifiedHeroToken).let {
            it.statusCode
        }

        assertEquals(401, responseCode)

    }

    @Test
    fun `when execute a post in serviceA with expired jwt issued by issuer service then 401 Unauthorized`() {

        val issueServiceResourceURL = "http://localhost:8080/hero"

        every { clock.today }.returns(DateTime.now().plusMinutes(1).toDate())

        val heroToken = khttp.get(issueServiceResourceURL).let {
            it.text
        }

        val serviceAURL = "http://localhost:8081/hero"

        val responseCode = khttp.post(serviceAURL, data = heroToken).let {
            it.statusCode
        }

        assertEquals(401, responseCode)

    }


}