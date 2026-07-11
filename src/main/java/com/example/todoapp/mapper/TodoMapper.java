package com.example.todoapp.mapper;

import com.example.todoapp.dto.TodoRequestDTO;
import com.example.todoapp.dto.TodoResponseDTO;
import com.example.todoapp.entity.Todo;
import org.springframework.stereotype.Component;

@Component
public class TodoMapper {

    public Todo toEntity(TodoRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }
        return Todo.builder()
                .title(requestDTO.getTitle())
                .description(requestDTO.getDescription())
                .completed(requestDTO.getCompleted() != null ? requestDTO.getCompleted() : false)
                .build();
    }

    public TodoResponseDTO toResponseDTO(Todo todo) {
        if (todo == null) {
            return null;
        }
        return TodoResponseDTO.builder()
                .id(todo.getId())
                .title(todo.getTitle())
                .description(todo.getDescription())
                .completed(todo.getCompleted())
                .createdAt(todo.getCreatedAt())
                .updatedAt(todo.getUpdatedAt())
                .build();
    }

    public void updateEntityFromDTO(Todo todo, TodoRequestDTO requestDTO) {
        if (todo == null || requestDTO == null) {
            return;
        }
        todo.setTitle(requestDTO.getTitle());
        todo.setDescription(requestDTO.getDescription());
        if (requestDTO.getCompleted() != null) {
            todo.setCompleted(requestDTO.getCompleted());
        }
    }
}
