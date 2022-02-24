package com.burakcan.todo.dto

import io.micronaut.core.annotation.Introspected
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@Introspected
data class TodoDto(
    val id: Long?,
    @Valid @field:NotBlank val name: String,
    val description: String?,
    val tasks: List<TaskDto>? = listOf()
)