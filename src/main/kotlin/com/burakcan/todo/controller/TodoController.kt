package com.burakcan.todo.controller

import com.burakcan.todo.dto.TodoDto
import com.burakcan.todo.service.TodoService
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.validation.Validated
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import javax.validation.Valid

@Controller("/todos")
@Validated
class TodoController(private val todoService: TodoService) {
    @Get
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Return all todos", description = "Return all Todos stored in the database as a json list.")
    @ApiResponses(
        ApiResponse(
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = Schema(implementation = TodoDto::class)
            )]
        )
    )
    @Tag(name = "todo")
    fun getTodos(): List<TodoDto> {
        return todoService.getTodos()
    }

    @Post
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
        summary = "Add a todo",
        description = "Add a new todo by providing a todo object as json, returns the created todo with id as json"
    )
    @ApiResponses(
        ApiResponse(
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = Schema(implementation = TodoDto::class)
            )]
        ),
        ApiResponse(responseCode = "400", description = "Empty or null name provided.")
    )
    @Tag(name = "todo")
    fun addTodo(
        @Body @Valid @Parameter(
            name = "Todo",
            description = "Todo object to create",
            required = true
        ) todo: TodoDto
    ): TodoDto {
        return todoService.addTodo(todo)
    }

    @Get(value = "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
        summary = "Get a todo",
        description = "Get a todo by providing the id as a path parameter. Returns the todo as a json object"
    )
    @ApiResponses(
        ApiResponse(
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = Schema(implementation = TodoDto::class)
            )]
        ),
        ApiResponse(responseCode = "404", description = "Todo not found")
    )
    @Tag(name = "todo")
    fun getTodo(
        @PathVariable(name = "id") @Parameter(
            name = "Todo Id",
            description = "Id of the Todo wanted to get."
        ) id: Long
    ): TodoDto {
        return todoService.getTodo(id)
    }


    @Put(value = "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
        summary = "Update a todo",
        description = "Replaces a todo with provided todo object, all fields of the old todo will be replaced including tasks."
    )
    @ApiResponses(
        ApiResponse(
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = Schema(implementation = TodoDto::class)
            )]
        ),
        ApiResponse(responseCode = "400", description = "Empty or null name provided."),
        ApiResponse(responseCode = "404", description = "Todo not found")
    )
    @Tag(name = "todo")
    fun updateTodo(
        @PathVariable(name = "id") @Parameter(
            name = "Todo Id",
            description = "Id of the Todo wanted to get."
        ) id: Long,
        @Body @Valid @Parameter(
            name = "Todo",
            description = "Todo object that you want to replace old todo with."
        ) todoDto: TodoDto
    ): TodoDto {
        return todoService.updateTodo(id, todoDto)
    }

    @Operation(
        summary = "Delete a todo",
        description = "Deletes a todo by its id."
    )
    @Delete(value = "/{id}")
    @Tag(name = "todo")
    fun deleteTodo(
        @PathVariable(name = "id") @Parameter(
            name = "Todo Id",
            description = "Id of the Todo wanted to get."
        ) id: Long
    ) {
        todoService.deleteTodo(id)
    }
}