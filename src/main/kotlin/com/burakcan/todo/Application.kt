package com.burakcan.todo

import io.micronaut.runtime.Micronaut.build
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info

@OpenAPIDefinition(
    info = Info(
        title = "Todo",
        version = "0.1",
        description = "Simple Todo Application API built w/ Micronaut+Kotlin+Gradle(kt)"
    )
)
object Api {
}

fun main(args: Array<String>) {
    build()
        .args(*args)
        .packages("com.burakcan.todo")
        .start()
}

