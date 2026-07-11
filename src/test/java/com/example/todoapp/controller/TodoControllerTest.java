package com.example.todoapp.controller;

import com.example.todoapp.dto.TodoRequestDTO;
import com.example.todoapp.dto.TodoResponseDTO;
import com.example.todoapp.exception.ResourceNotFoundException;
import com.example.todoapp.service.TodoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TodoService todoService;

    private final LocalDateTime now = LocalDateTime.now();

    private TodoResponseDTO buildResponseDTO() {
        return TodoResponseDTO.builder()
                .id(1L)
                .title("Test Todo")
                .description("Test Description")
                .completed(false)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    private TodoRequestDTO buildRequestDTO(String title, String description, Boolean completed) {
        return TodoRequestDTO.builder()
                .title(title)
                .description(description)
                .completed(completed)
                .build();
    }

    @Nested
    @DisplayName("POST /api/todos")
    class CreateTodo {

        @Test
        @DisplayName("should return 201 when request is valid")
        void shouldReturn201() throws Exception {
            TodoRequestDTO request = buildRequestDTO("Test Todo", "Test Description", false);
            TodoResponseDTO response = buildResponseDTO();

            given(todoService.createTodo(any(TodoRequestDTO.class))).willReturn(response);

            mockMvc.perform(post("/api/todos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.title").value("Test Todo"))
                    .andExpect(jsonPath("$.description").value("Test Description"))
                    .andExpect(jsonPath("$.completed").value(false));

            verify(todoService, times(1)).createTodo(any(TodoRequestDTO.class));
        }

        @Test
        @DisplayName("should return 400 when title is blank")
        void shouldReturn400WhenTitleBlank() throws Exception {
            TodoRequestDTO request = buildRequestDTO("", "Test Description", false);

            mockMvc.perform(post("/api/todos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.path").value("/api/todos"));

            verify(todoService, never()).createTodo(any());
        }

        @Test
        @DisplayName("should return 400 when title exceeds 100 characters")
        void shouldReturn400WhenTitleTooLong() throws Exception {
            String longTitle = "a".repeat(101);
            TodoRequestDTO request = buildRequestDTO(longTitle, "desc", false);

            mockMvc.perform(post("/api/todos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(todoService, never()).createTodo(any());
        }

        @Test
        @DisplayName("should return 400 when description exceeds 500 characters")
        void shouldReturn400WhenDescriptionTooLong() throws Exception {
            String longDescription = "a".repeat(501);
            TodoRequestDTO request = buildRequestDTO("Valid Title", longDescription, false);

            mockMvc.perform(post("/api/todos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(todoService, never()).createTodo(any());
        }
    }

    @Nested
    @DisplayName("GET /api/todos")
    class GetAllTodos {

        @Test
        @DisplayName("should return 200 with list of todos")
        void shouldReturn200WithList() throws Exception {
            given(todoService.getAllTodos(null)).willReturn(List.of(buildResponseDTO()));

            mockMvc.perform(get("/api/todos"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].title").value("Test Todo"));
        }

        @Test
        @DisplayName("should return 200 with filtered list when completed param is provided")
        void shouldReturn200WithFilter() throws Exception {
            given(todoService.getAllTodos(true)).willReturn(List.of(buildResponseDTO()));

            mockMvc.perform(get("/api/todos").param("completed", "true"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].completed").value(false));
        }

        @Test
        @DisplayName("should return 200 with empty array when no todos exist")
        void shouldReturn200EmptyArray() throws Exception {
            given(todoService.getAllTodos(null)).willReturn(List.of());

            mockMvc.perform(get("/api/todos"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    @Nested
    @DisplayName("GET /api/todos/{id}")
    class GetTodoById {

        @Test
        @DisplayName("should return 200 when todo is found")
        void shouldReturn200WhenFound() throws Exception {
            given(todoService.getTodoById(1L)).willReturn(buildResponseDTO());

            mockMvc.perform(get("/api/todos/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.title").value("Test Todo"));
        }

        @Test
        @DisplayName("should return 404 when todo is not found")
        void shouldReturn404WhenNotFound() throws Exception {
            given(todoService.getTodoById(99L))
                    .willThrow(new ResourceNotFoundException("Todo not found with id: 99"));

            mockMvc.perform(get("/api/todos/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value("Todo not found with id: 99"))
                    .andExpect(jsonPath("$.path").value("/api/todos/99"));
        }
    }

    @Nested
    @DisplayName("PUT /api/todos/{id}")
    class UpdateTodo {

        @Test
        @DisplayName("should return 200 when update is successful")
        void shouldReturn200WhenSuccessful() throws Exception {
            TodoRequestDTO request = buildRequestDTO("Updated Title", "Updated Desc", true);
            TodoResponseDTO response = buildResponseDTO();

            given(todoService.updateTodo(eq(1L), any(TodoRequestDTO.class))).willReturn(response);

            mockMvc.perform(put("/api/todos/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        @DisplayName("should return 404 when todo not found")
        void shouldReturn404WhenNotFound() throws Exception {
            TodoRequestDTO request = buildRequestDTO("Updated Title", "Updated Desc", true);

            given(todoService.updateTodo(eq(99L), any(TodoRequestDTO.class)))
                    .willThrow(new ResourceNotFoundException("Todo not found with id: 99"));

            mockMvc.perform(put("/api/todos/99")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("should return 400 when title is blank")
        void shouldReturn400WhenTitleBlank() throws Exception {
            TodoRequestDTO request = buildRequestDTO("", "desc", false);

            mockMvc.perform(put("/api/todos/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(todoService, never()).updateTodo(any(), any());
        }
    }

    @Nested
    @DisplayName("PATCH /api/todos/{id}/complete")
    class ToggleComplete {

        @Test
        @DisplayName("should return 200 when toggle is successful")
        void shouldReturn200WhenSuccessful() throws Exception {
            TodoResponseDTO response = buildResponseDTO();
            response.setCompleted(true);
            given(todoService.toggleComplete(1L)).willReturn(response);

            mockMvc.perform(patch("/api/todos/1/complete"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.completed").value(true));
        }

        @Test
        @DisplayName("should return 404 when todo not found")
        void shouldReturn404WhenNotFound() throws Exception {
            given(todoService.toggleComplete(99L))
                    .willThrow(new ResourceNotFoundException("Todo not found with id: 99"));

            mockMvc.perform(patch("/api/todos/99/complete"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }

    @Nested
    @DisplayName("DELETE /api/todos/{id}")
    class DeleteTodo {

        @Test
        @DisplayName("should return 204 when deletion is successful")
        void shouldReturn204WhenSuccessful() throws Exception {
            doNothing().when(todoService).deleteTodo(1L);

            mockMvc.perform(delete("/api/todos/1"))
                    .andExpect(status().isNoContent());

            verify(todoService, times(1)).deleteTodo(1L);
        }

        @Test
        @DisplayName("should return 404 when todo not found")
        void shouldReturn404WhenNotFound() throws Exception {
            doThrow(new ResourceNotFoundException("Todo not found with id: 99"))
                    .when(todoService).deleteTodo(99L);

            mockMvc.perform(delete("/api/todos/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }
}
