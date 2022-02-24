package com.burakcan.todo

import com.burakcan.todo.dto.TaskDto
import com.burakcan.todo.dto.TodoDto
import com.burakcan.todo.service.TodoService
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.kotlin.http.retrieveList
import io.micronaut.kotlin.http.retrieveObject
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

@MicronautTest
class TodoControllerTest {
    @Inject
    lateinit var todoService: TodoService

    @Inject
    @field:Client("/todos")
    lateinit var httpClient: HttpClient

    @Test
    fun `test get todos empty`() {
        `when`(todoService.getTodos())
            .thenReturn(
                listOf()
            )

        val request: HttpRequest<Any> = HttpRequest.GET("/")
        val response = httpClient.toBlocking().retrieveList<TodoDto>(request)

        Assertions.assertEquals(listOf<TodoDto>(), response)
    }

    @Test
    fun `test get todos`() {
        val todoList = listOf(
            TodoDto(
                name = "test todo name",
                description = "test todo desc",
                id = 1,
                tasks = listOf(
                    TaskDto(
                        name = "test task name",
                        description = "test task description"
                    )
                )
            ),
            TodoDto(
                name = "test todo 2 name",
                description = "test todo 2 desc",
                id = 2,
                tasks = listOf()
            )
        )

        `when`(todoService.getTodos())
            .thenReturn(todoList)

        val request: HttpRequest<Any> = HttpRequest.GET("/")
        val response = httpClient.toBlocking().retrieveList<TodoDto>(request)

        Assertions.assertEquals(todoList, response)
    }

    @Test
    fun `test add todo with empty name`() {
        val todo = TodoDto(
            id = null,
            name = "",
            description = "test todo desc",
            tasks = listOf(
                TaskDto(
                    name = "test task name",
                    description = "test task description"
                )
            )
        )

        val request: HttpRequest<Any> = HttpRequest.POST("/", todo)
        val exception = Assertions.assertThrows(HttpClientResponseException::class.java) {
            httpClient.toBlocking().retrieveObject<HttpClientResponseException>(request)
        }

        Assertions.assertEquals("Bad Request", exception.message)
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun `test add todo`() {
        val todo = TodoDto(
            id = null,
            name = "test todo name",
            description = "test todo desc",
            tasks = listOf(
                TaskDto(
                    name = "test task name",
                    description = "test task description"
                )
            )
        )

        val expectedTdo = TodoDto(
            id = 1,
            name = todo.name,
            description = todo.description,
            tasks = todo.tasks
        )


        `when`(todoService.addTodo(todo))
            .thenReturn(expectedTdo)

        val request: HttpRequest<Any> = HttpRequest.POST("/", todo)
        val returnedDto = httpClient.toBlocking().retrieveObject<TodoDto>(request)
        Assertions.assertEquals(expectedTdo, returnedDto)

        verify(todoService).addTodo(todo)
    }

    @Test
    fun `test get todo`() {
        val todoDto = TodoDto(
            id = 1,
            name = "test todo name",
            description = "test todo desc",
            tasks = listOf(
                TaskDto(
                    name = "test task name",
                    description = "test task description"
                )
            )
        )


        `when`(todoService.getTodo(anyLong()))
            .thenReturn(todoDto)


        val request: HttpRequest<Any> = HttpRequest.GET("/${todoDto.id}")
        val returnedDto = httpClient.toBlocking().retrieveObject<TodoDto>(request)

        Assertions.assertEquals(todoDto, returnedDto)
    }

    @Test
    fun `test update todo empty name`() {
        val todoId = 1

        val todo = TodoDto(
            id = null,
            name = "",
            description = "test todo desc",
            tasks = listOf(
                TaskDto(
                    name = "test task name",
                    description = "test task description"
                )
            )
        )

        val request: HttpRequest<Any> = HttpRequest.PUT("/${todoId}", todo)
        val exception = Assertions.assertThrows(HttpClientResponseException::class.java) {
            httpClient.toBlocking().retrieveObject<HttpClientResponseException>(request)
        }

        Assertions.assertEquals("Bad Request", exception.message)
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.status)
    }

    @Test
    fun `test update todo`() {
        val todoId: Long = 1
        val taskList = listOf(
            TaskDto(
                name = "Task Name",
                description = "Task Description"
            )
        )
        val todoDto = TodoDto(
            id = null,
            name = "Todo Name",
            description = "Todo Desc",
            tasks = taskList
        )
        val expectedDto = TodoDto(
            id = todoId,
            name = "Todo Name",
            description = "Todo Desc",
            tasks = taskList
        )
        `when`(todoService.updateTodo(todoId, todoDto))
            .thenReturn(expectedDto)

        val request: HttpRequest<Any> = HttpRequest.PUT("/$todoId", todoDto)
        val returnedDto = httpClient.toBlocking().retrieveObject<TodoDto>(request)

        Assertions.assertEquals(expectedDto, returnedDto)
    }

    @Test
    fun `delete todo`() {
        val todoId: Long = 1

        doNothing().`when`(todoService).deleteTodo(anyLong())

        val request: HttpRequest<Any> = HttpRequest.DELETE("/$todoId")
        val returnedDto = httpClient.toBlocking().exchange<Any, Any>(request)

        Assertions.assertNull(returnedDto.body.orElse(null))

        verify(todoService).deleteTodo(todoId)
    }
}

@Factory
open class TestFinalClassFactory {
    @Singleton
    @Replaces(TodoService::class)
    fun provide(): TodoService {
        return mock(TodoService::class.java)
    }
}