package com.example.service

import io.javalin.Javalin

interface WebApplication {

    fun start(
        port: Int = 7000
    ): WebApplication

    fun stop()

    fun webApp(): Javalin

}