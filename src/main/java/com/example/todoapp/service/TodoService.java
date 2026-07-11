package com.example.todoapp.service;

import com.example.todoapp.dto.TodoRequestDTO;
import com.example.todoapp.dto.TodoResponseDTO;

import java.util.List;

public interface TodoService {

    TodoResponseDTO createTodo(TodoRequestDTO requestDTO);

    List<TodoResponseDTO> getAllTodos(Boolean completed);

    TodoResponseDTO getTodoById(Long id);

    TodoResponseDTO updateTodo(Long id, TodoRequestDTO requestDTO);

    TodoResponseDTO toggleComplete(Long id);

    void deleteTodo(Long id);
}
