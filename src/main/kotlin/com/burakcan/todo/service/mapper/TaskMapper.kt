package com.burakcan.todo.service.mapper

import com.burakcan.todo.dto.TaskDto
import com.burakcan.todo.model.Task

fun Task.toDto() = TaskDto(
    name = name,
    description = description
)

fun List<Task>.toDto(): List<TaskDto> = stream()
    .map { it.toDto() }
    .toList()