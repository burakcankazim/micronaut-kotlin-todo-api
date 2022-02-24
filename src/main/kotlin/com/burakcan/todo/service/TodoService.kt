package com.burakcan.todo.service

import com.burakcan.todo.dto.TodoDto
import com.burakcan.todo.model.Task
import com.burakcan.todo.model.Todo
import com.burakcan.todo.repository.TodoRepository
import com.burakcan.todo.service.mapper.toDto
import io.micronaut.http.HttpStatus
import io.micronaut.http.exceptions.HttpStatusException
import jakarta.inject.Singleton

@Singleton
class TodoService(private val todoRepository: TodoRepository) {
    fun getTodos(): List<TodoDto> {
        return todoRepository.findAll().toDto()
    }

    fun addTodo(todo: TodoDto): TodoDto {
        val entity = Todo(todo.name, todo.description).apply {
            tasks = todo.tasks?.map { Task(it.name, it.description, this) }?.toMutableList() ?: mutableListOf()
        }
        return todoRepository.save(entity).toDto()
    }

    fun getTodo(id: Long): TodoDto {
        return todoRepository.findById(id).orElseThrow { HttpStatusException(HttpStatus.NOT_FOUND, "Todo not found") }
            .toDto()
    }

    fun updateTodo(id: Long, todoDto: TodoDto): TodoDto {
        val entity =
            todoRepository.findById(id).orElseThrow { HttpStatusException(HttpStatus.NOT_FOUND, "Todo not found") }
                .apply {
                    name = todoDto.name
                    description = todoDto.description
                    tasks = todoDto.tasks?.stream()?.map { Task(it.name, it.description, this) }?.toList()
                        ?: mutableListOf()
                }
        return todoRepository.update(entity).toDto()
    }

    fun deleteTodo(id: Long) {
        todoRepository.deleteById(id)
    }
}