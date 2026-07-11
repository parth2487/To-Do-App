package com.example.todoapp.service;

import com.example.todoapp.dto.TodoRequestDTO;
import com.example.todoapp.dto.TodoResponseDTO;
import com.example.todoapp.entity.Todo;
import com.example.todoapp.exception.ResourceNotFoundException;
import com.example.todoapp.mapper.TodoMapper;
import com.example.todoapp.repository.TodoRepository;
import com.example.todoapp.service.impl.TodoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private TodoMapper todoMapper;

    @InjectMocks
    private TodoServiceImpl todoService;

    private Todo todo;
    private TodoRequestDTO requestDTO;
    private TodoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        todo = Todo.builder()
                .id(1L)
                .title("Test Todo")
                .description("Test Description")
                .completed(false)
                .createdAt(now)
                .updatedAt(now)
                .build();

        requestDTO = TodoRequestDTO.builder()
                .title("Test Todo")
                .description("Test Description")
                .completed(false)
                .build();

        responseDTO = TodoResponseDTO.builder()
                .id(1L)
                .title("Test Todo")
                .description("Test Description")
                .completed(false)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Nested
    @DisplayName("createTodo()")
    class CreateTodo {

        @Test
        @DisplayName("should create and return a todo")
        void shouldCreateTodo() {
            given(todoMapper.toEntity(requestDTO)).willReturn(todo);
            given(todoRepository.save(any(Todo.class))).willReturn(todo);
            given(todoMapper.toResponseDTO(todo)).willReturn(responseDTO);

            TodoResponseDTO result = todoService.createTodo(requestDTO);

            assertThat(result).isEqualTo(responseDTO);
            verify(todoRepository, times(1)).save(any(Todo.class));
        }
    }

    @Nested
    @DisplayName("getAllTodos()")
    class GetAllTodos {

        @Test
        @DisplayName("should return all todos when no filter")
        void shouldReturnAllTodos() {
            given(todoRepository.findAll()).willReturn(List.of(todo));
            given(todoMapper.toResponseDTO(todo)).willReturn(responseDTO);

            List<TodoResponseDTO> result = todoService.getAllTodos(null);

            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(responseDTO);
            verify(todoRepository, times(1)).findAll();
            verify(todoRepository, never()).findByCompleted(any());
        }

        @Test
        @DisplayName("should return filtered todos when completed filter is provided")
        void shouldReturnFilteredTodos() {
            given(todoRepository.findByCompleted(true)).willReturn(List.of(todo));
            given(todoMapper.toResponseDTO(todo)).willReturn(responseDTO);

            List<TodoResponseDTO> result = todoService.getAllTodos(true);

            assertThat(result).hasSize(1);
            verify(todoRepository, times(1)).findByCompleted(true);
            verify(todoRepository, never()).findAll();
        }

        @Test
        @DisplayName("should return empty list when no todos exist")
        void shouldReturnEmptyList() {
            given(todoRepository.findAll()).willReturn(List.of());

            List<TodoResponseDTO> result = todoService.getAllTodos(null);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getTodoById()")
    class GetTodoById {

        @Test
        @DisplayName("should return todo when found")
        void shouldReturnTodoWhenFound() {
            given(todoRepository.findById(1L)).willReturn(Optional.of(todo));
            given(todoMapper.toResponseDTO(todo)).willReturn(responseDTO);

            TodoResponseDTO result = todoService.getTodoById(1L);

            assertThat(result).isEqualTo(responseDTO);
            verify(todoRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when not found")
        void shouldThrowWhenNotFound() {
            given(todoRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> todoService.getTodoById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");

            verify(todoMapper, never()).toResponseDTO(any());
        }
    }

    @Nested
    @DisplayName("updateTodo()")
    class UpdateTodo {

        @Test
        @DisplayName("should update and return todo when found")
        void shouldUpdateWhenFound() {
            given(todoRepository.findById(1L)).willReturn(Optional.of(todo));
            given(todoRepository.save(any(Todo.class))).willReturn(todo);
            given(todoMapper.toResponseDTO(todo)).willReturn(responseDTO);

            TodoResponseDTO result = todoService.updateTodo(1L, requestDTO);

            assertThat(result).isEqualTo(responseDTO);
            verify(todoMapper, times(1)).updateEntityFromDTO(todo, requestDTO);
            verify(todoRepository, times(1)).save(any(Todo.class));
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when todo not found")
        void shouldThrowWhenNotFound() {
            given(todoRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> todoService.updateTodo(99L, requestDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");

            verify(todoRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("toggleComplete()")
    class ToggleComplete {

        @Test
        @DisplayName("should toggle completed from false to true")
        void shouldToggleToTrue() {
            given(todoRepository.findById(1L)).willReturn(Optional.of(todo));
            given(todoRepository.save(any(Todo.class))).willReturn(todo);
            given(todoMapper.toResponseDTO(todo)).willReturn(responseDTO);

            TodoResponseDTO result = todoService.toggleComplete(1L);

            assertThat(result).isEqualTo(responseDTO);
            assertThat(todo.getCompleted()).isTrue();
            verify(todoRepository, times(1)).save(any(Todo.class));
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when todo not found")
        void shouldThrowWhenNotFound() {
            given(todoRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> todoService.toggleComplete(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");

            verify(todoRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("deleteTodo()")
    class DeleteTodo {

        @Test
        @DisplayName("should delete todo when found")
        void shouldDeleteWhenFound() {
            given(todoRepository.findById(1L)).willReturn(Optional.of(todo));

            todoService.deleteTodo(1L);

            verify(todoRepository, times(1)).delete(todo);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when todo not found")
        void shouldThrowWhenNotFound() {
            given(todoRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> todoService.deleteTodo(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");

            verify(todoRepository, never()).delete(any());
        }
    }
}
