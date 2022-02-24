package com.burakcan.todo.repository

import com.burakcan.todo.model.Todo
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface TodoRepository : JpaRepository<Todo, Long> {
}