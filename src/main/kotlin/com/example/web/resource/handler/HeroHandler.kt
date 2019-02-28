package com.example.web.resource.handler

import io.javalin.Context
import io.javalin.Handler

class HeroHandler : Handler {

    override fun handle(context: Context) {
        context.result("Super")
    }

}