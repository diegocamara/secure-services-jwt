package com.example.web.issuer.controller

import com.example.domain.Hero
import com.example.service.JWTService
import io.javalin.Context
import io.javalin.apibuilder.ApiBuilder.get

class HeroController(private val jwtService: JWTService) {

    private fun findHero(context: Context) {
        val hero = Hero("Hero")
        val payloadToken = this.jwtService.sign(
            subject = hero,
            expirationTimeInSeconds = 25,
            issuer = "issuer-service"
        )
        context.result(payloadToken)
    }

    fun registerRoutes() {
        get("/hero", this::findHero)
    }

}