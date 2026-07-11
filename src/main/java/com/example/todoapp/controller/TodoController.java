package com.example.todoapp.controller;

import com.example.todoapp.dto.TodoRequestDTO;
import com.example.todoapp.dto.TodoResponseDTO;
import com.example.todoapp.service.TodoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
@Tag(name = "To-Do", description = "Endpoints for managing to-do items")
public class TodoController {

    private final TodoService todoService;

    @Operation(summary = "Create a new to-do", description = "Creates a new to-do item and returns it with a 201 status.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "To-do created",
                    content = @Content(schema = @Schema(implementation = TodoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<TodoResponseDTO> createTodo(@Valid @RequestBody TodoRequestDTO requestDTO) {
        TodoResponseDTO created = todoService.createTodo(requestDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all to-dos", description = "Returns all to-dos, optionally filtered by completion status.")
    @ApiResponse(responseCode = "200", description = "List of to-dos",
            content = @Content(schema = @Schema(implementation = TodoResponseDTO.class)))
    @GetMapping
    public ResponseEntity<List<TodoResponseDTO>> getAllTodos(
            @Parameter(description = "Filter by completed status") @RequestParam(required = false) Boolean completed) {
        List<TodoResponseDTO> todos = todoService.getAllTodos(completed);
        return ResponseEntity.ok(todos);
    }

    @Operation(summary = "Get a to-do by ID", description = "Returns a single to-do by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "To-do found",
                    content = @Content(schema = @Schema(implementation = TodoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "To-do not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<TodoResponseDTO> getTodoById(@PathVariable Long id) {
        TodoResponseDTO todo = todoService.getTodoById(id);
        return ResponseEntity.ok(todo);
    }

    @Operation(summary = "Update a to-do", description = "Performs a full update of an existing to-do.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "To-do updated",
                    content = @Content(schema = @Schema(implementation = TodoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "To-do not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<TodoResponseDTO> updateTodo(
            @PathVariable Long id, @Valid @RequestBody TodoRequestDTO requestDTO) {
        TodoResponseDTO updated = todoService.updateTodo(id, requestDTO);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Toggle completion status", description = "Toggles the completed flag of a to-do.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "To-do toggled",
                    content = @Content(schema = @Schema(implementation = TodoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "To-do not found", content = @Content)
    })
    @PatchMapping("/{id}/complete")
    public ResponseEntity<TodoResponseDTO> toggleComplete(@PathVariable Long id) {
        TodoResponseDTO toggled = todoService.toggleComplete(id);
        return ResponseEntity.ok(toggled);
    }

    @Operation(summary = "Delete a to-do", description = "Deletes a to-do by its ID. Returns 204 on success.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "To-do deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "To-do not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        todoService.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }
}
