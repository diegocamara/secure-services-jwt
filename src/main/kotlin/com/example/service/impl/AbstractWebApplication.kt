package com.example.service.impl

import com.example.service.WebApplication
import io.javalin.Javalin

abstract class AbstractWebApplication : WebApplication {

    val javalinApp: Javalin = Javalin.create()

    override fun start(port: Int): WebApplication {
        this.javalinApp.start(port)
        return this
    }

    override fun stop(){
        this.javalinApp.stop()
    }

    override fun webApp(): Javalin {
        return this.javalinApp
    }

}