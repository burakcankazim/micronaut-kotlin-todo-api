package com.burakcan.todo.service.mapper

import com.burakcan.todo.dto.TodoDto
import com.burakcan.todo.model.Todo

fun Todo.toDto() = TodoDto(
    id = id,
    name = name,
    description = description,
    tasks = tasks.toDto()
)

fun List<Todo>.toDto(): MutableList<TodoDto> = stream()
    .map { it.toDto() }
    .toList()