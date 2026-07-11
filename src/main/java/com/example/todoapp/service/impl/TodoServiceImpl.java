package com.example.todoapp.service.impl;

import com.example.todoapp.dto.TodoRequestDTO;
import com.example.todoapp.dto.TodoResponseDTO;
import com.example.todoapp.entity.Todo;
import com.example.todoapp.exception.ResourceNotFoundException;
import com.example.todoapp.mapper.TodoMapper;
import com.example.todoapp.repository.TodoRepository;
import com.example.todoapp.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TodoServiceImpl implements TodoService {

    private static final String TODO_NOT_FOUND_MSG = "Todo not found with id: ";

    private final TodoRepository todoRepository;
    private final TodoMapper todoMapper;

    @Override
    public TodoResponseDTO createTodo(TodoRequestDTO requestDTO) {
        Todo todo = todoMapper.toEntity(requestDTO);
        Todo saved = todoRepository.save(todo);
        return todoMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TodoResponseDTO> getAllTodos(Boolean completed) {
        List<Todo> todos = (completed != null)
                ? todoRepository.findByCompleted(completed)
                : todoRepository.findAll();
        return todos.stream()
                .map(todoMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TodoResponseDTO getTodoById(Long id) {
        Todo todo = findTodoById(id);
        return todoMapper.toResponseDTO(todo);
    }

    @Override
    public TodoResponseDTO updateTodo(Long id, TodoRequestDTO requestDTO) {
        Todo todo = findTodoById(id);
        todoMapper.updateEntityFromDTO(todo, requestDTO);
        Todo updated = todoRepository.save(todo);
        return todoMapper.toResponseDTO(updated);
    }

    @Override
    public TodoResponseDTO toggleComplete(Long id) {
        Todo todo = findTodoById(id);
        todo.setCompleted(!todo.getCompleted());
        Todo updated = todoRepository.save(todo);
        return todoMapper.toResponseDTO(updated);
    }

    @Override
    public void deleteTodo(Long id) {
        Todo todo = findTodoById(id);
        todoRepository.delete(todo);
    }

    private Todo findTodoById(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(TODO_NOT_FOUND_MSG + id));
    }
}
