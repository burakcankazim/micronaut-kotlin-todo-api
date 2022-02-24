package com.burakcan.todo

import com.burakcan.todo.dto.TaskDto
import com.burakcan.todo.dto.TodoDto
import com.burakcan.todo.model.Task
import com.burakcan.todo.model.Todo
import com.burakcan.todo.repository.TodoRepository
import com.burakcan.todo.service.TodoService
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpStatus
import io.micronaut.http.exceptions.HttpStatusException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.*
import java.util.*

@MicronautTest
class TodoServiceTest {
    @Inject
    lateinit var todoService: TodoService

    @Inject
    lateinit var todoRepository: TodoRepository

    @Test
    fun `return todo with id when added`() {
        val todoId: Long = 1
        val taskId: Long = 1
        val todoName = "Test Todo Name"
        val todoDesc = "Test Todo Desc"
        val taskName = "Test Task Name"
        val taskDesc = "Test Task Desc"

        val taskDto = TaskDto(
            name = taskName,
            description = taskDesc
        )

        val todoDto = TodoDto(
            id = null,
            name = todoName,
            description = todoDesc,
            tasks = listOf(taskDto)
        )

        val todoEntity = Todo(
            name = todoName,
            description = todoDesc
        ).apply {
            tasks = mutableListOf(
                Task(
                    name = taskName,
                    description = taskDesc,
                    todo = this
                )
            )
        }


        `when`(todoRepository.save(any(Todo::class.java)))
            .thenReturn(todoEntity.apply {
                id = todoId
                tasks[0].apply {
                    id = taskId
                }
            })

        val expectedDto = TodoDto(
            id = todoId,
            name = todoName,
            description = todoDesc,
            tasks = listOf(taskDto)
        )

        val returnDto = todoService.addTodo(todoDto)
        Assertions.assertEquals(expectedDto, returnDto)


        val argumentCaptor = ArgumentCaptor.forClass(Todo::class.java)

        verify(todoRepository).save(argumentCaptor.capture())
        val capturedArgument = argumentCaptor.value
        val expectedEntity = Todo(
            name = todoDto.name,
            description = todoDto.description
        ).apply {
            id = 1
            tasks = mutableListOf(Task(
                name = taskDto.name,
                description = taskDto.description,
                todo = this
            ).apply {
                id = 1
            })
        }
        Assertions.assertEquals(expectedEntity.name, capturedArgument.name)
        Assertions.assertEquals(expectedEntity.description, capturedArgument.description)
        Assertions.assertEquals(expectedEntity.tasks[0].name, capturedArgument.tasks[0].name)
        Assertions.assertEquals(expectedEntity.tasks[0].description, capturedArgument.tasks[0].description)
    }

    @Test
    fun `add todo with no tasks`() {
        val todoId: Long = 1
        val todoName = "Test Todo Name"
        val todoDesc = "Test Todo Desc"

        val todoDto = TodoDto(
            id = null,
            name = todoName,
            description = todoDesc,
            tasks = null
        )

        val todoEntity = Todo(
            name = todoName,
            description = todoDesc
        )


        `when`(todoRepository.save(any(Todo::class.java)))
            .thenReturn(todoEntity.apply {
                id = todoId
            })

        val expectedDto = TodoDto(
            id = todoId,
            name = todoName,
            description = todoDesc,
            tasks = listOf()
        )

        val returnDto = todoService.addTodo(todoDto)
        Assertions.assertEquals(expectedDto, returnDto)

    }

    @Test
    fun `get all todos empty`() {
        `when`(todoRepository.findAll())
            .thenReturn(listOf<Todo>())

        val returnList = todoService.getTodos()
        val expectedList = listOf<TodoDto>()
        Assertions.assertEquals(expectedList, returnList)
    }

    @Test
    fun `get all todos`() {
        val todos = listOf(
            Todo(
                name = "Test Todo Name 1",
                description = "Test Todo Desc 1"
            ).apply {
                id = 1
                tasks = mutableListOf(
                    Task(
                        name = "Todo 1 Task Name",
                        description = "Todo 1 Task Desc",
                        todo = this
                    ).apply { id = 1 }
                )
            },
            Todo(
                name = "Test Todo Name 2",
                description = "Test Todo Desc 2"
            ).apply {
                id = 2
                tasks = mutableListOf()
            }
        )

        `when`(todoRepository.findAll())
            .thenReturn(todos)

        val returnList = todoService.getTodos()

        val expectedList = todos
            .map { entity ->
                TodoDto(
                    id = entity.id,
                    name = entity.name,
                    description = entity.description,
                    tasks = entity.tasks.map {
                        TaskDto(
                            name = it.name,
                            description = it.description
                        )
                    }
                )
            }

        Assertions.assertEquals(expectedList, returnList)
    }

    @Test
    fun `get a todo non-existent`() {
        `when`(todoRepository.findById(anyLong()))
            .thenReturn(Optional.empty())

        val exception = Assertions.assertThrows(
            HttpStatusException::class.java
        ) { todoService.getTodo(1) }

        Assertions.assertEquals("Todo not found", exception.message)
        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.status)
    }

    @Test
    fun `get a todo`() {
        `when`(todoRepository.findById(1))
            .thenReturn(Optional.of(
                Todo(
                    name = "Test Todo Name 1",
                    description = "Test Todo Desc 1"
                ).apply {
                    id = 1
                    tasks = mutableListOf(
                        Task(
                            name = "Todo 1 Task Name",
                            description = "Todo 1 Task Desc",
                            todo = this
                        ).apply { id = 1 }
                    )
                }
            ))

        val returnedDto = todoService.getTodo(1)
        val expectedDto = TodoDto(
            id = 1,
            name = "Test Todo Name 1",
            description = "Test Todo Desc 1",
            tasks = mutableListOf(
                TaskDto(
                    name = "Todo 1 Task Name",
                    description = "Todo 1 Task Desc"
                )
            )
        )

        Assertions.assertEquals(expectedDto, returnedDto)
    }

    @Test
    fun `update a non-existent todo`() {
        `when`(todoRepository.findById(anyLong()))
            .thenReturn(Optional.empty())

        val taskId: Long = 1
        val todoName = "Test Todo Name"
        val todoDesc = "Test Todo Desc"
        val taskName = "Test Task Name"
        val taskDesc = "Test Task Desc"

        val taskDto = TaskDto(
            name = taskName,
            description = taskDesc
        )

        val todoDto = TodoDto(
            id = null,
            name = todoName,
            description = todoDesc,
            tasks = listOf(taskDto)
        )


        val exception = Assertions.assertThrows(
            HttpStatusException::class.java
        ) { todoService.updateTodo(taskId, todoDto) }

        Assertions.assertEquals("Todo not found", exception.message)
        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.status)
    }

    @Test
    fun `update a todo`() {
        `when`(todoRepository.findById(1))
            .thenReturn(Optional.of(
                Todo(
                    name = "Todo to be updated name",
                    description = "Todo to be updated description"
                ).apply {
                    id = 1
                    tasks = mutableListOf(
                        Task(
                            name = "Task to be updated name",
                            description = "Task to be updated description",
                            todo = this
                        ).apply {
                            id = 1
                        }
                    )
                }
            ))

        val taskDto = TaskDto(
            name = "Updated task name",
            description = "Updated task description"
        )

        val todoDto = TodoDto(
            id = null,
            name = "Updated todo name",
            description = "Updated todo description",
            tasks = mutableListOf(taskDto)
        )

        `when`(todoRepository.update(any(Todo::class.java)))
            .thenReturn(
                Todo(
                    name = todoDto.name,
                    description = todoDto.description
                ).apply {
                    id = 1
                    tasks = mutableListOf(
                        Task(
                            name = taskDto.name,
                            description = taskDto.description,
                            todo = this
                        )
                    )
                }
            )


        val expectedDto = TodoDto(
            id = 1,
            name = todoDto.name,
            description = todoDto.description,
            tasks = mutableListOf(taskDto)
        )

        val returnedDto = todoService.updateTodo(1, todoDto)
        Assertions.assertEquals(expectedDto, returnedDto)

        val argumentCaptor = ArgumentCaptor.forClass(Todo::class.java)

        verify(todoRepository).update(argumentCaptor.capture())
        val capturedArgument = argumentCaptor.value
        val expectedEntity = Todo(
            name = todoDto.name,
            description = todoDto.description
        ).apply {
            id = 1
            tasks = mutableListOf(Task(
                name = taskDto.name,
                description = taskDto.description,
                todo = this
            ).apply {
                id = 1
            })
        }
        Assertions.assertEquals(expectedEntity.id, capturedArgument.id)
        Assertions.assertEquals(expectedEntity.name, capturedArgument.name)
        Assertions.assertEquals(expectedEntity.description, capturedArgument.description)
        Assertions.assertEquals(expectedEntity.tasks[0].name, capturedArgument.tasks[0].name)
        Assertions.assertEquals(expectedEntity.tasks[0].description, capturedArgument.tasks[0].description)
    }

    @MockBean
    @Replaces(TodoRepository::class)
    fun mockRepo(): TodoRepository {
        return mock(TodoRepository::class.java)
    }
}