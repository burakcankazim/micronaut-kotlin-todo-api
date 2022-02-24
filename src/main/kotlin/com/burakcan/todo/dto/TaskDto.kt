package com.burakcan.todo.dto

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
data class TaskDto(
    @field:NotBlank val name: String,
    val description: String?
)