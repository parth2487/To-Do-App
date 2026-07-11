package com.example.todoapp.repository;

import com.example.todoapp.entity.Todo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase
class TodoRepositoryTest {

    @Autowired
    private TodoRepository todoRepository;

    private Todo todo1;
    private Todo todo2;

    @BeforeEach
    void setUp() {
        todoRepository.deleteAll();

        todo1 = Todo.builder()
                .title("Buy groceries")
                .description("Milk, eggs, bread")
                .completed(false)
                .build();

        todo2 = Todo.builder()
                .title("Learn Spring Boot")
                .description("Build a REST API")
                .completed(true)
                .build();

        todoRepository.save(todo1);
        todoRepository.save(todo2);
    }

    @Nested
    @DisplayName("save()")
    class Save {

        @Test
        @DisplayName("should save and assign generated ID")
        void shouldSaveAndAssignId() {
            Todo newTodo = Todo.builder()
                    .title("New Task")
                    .description("New Description")
                    .completed(false)
                    .build();

            Todo saved = todoRepository.save(newTodo);

            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getTitle()).isEqualTo("New Task");
        }

        @Test
        @DisplayName("should set timestamps on save via @PrePersist")
        void shouldSetTimestampsOnSave() {
            Todo newTodo = Todo.builder()
                    .title("Timestamp Task")
                    .completed(false)
                    .build();

            Todo saved = todoRepository.save(newTodo);

            assertThat(saved.getCreatedAt()).isNotNull();
            assertThat(saved.getUpdatedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("should find todo by ID")
        void shouldFindById() {
            Optional<Todo> found = todoRepository.findById(todo1.getId());

            assertThat(found).isPresent();
            assertThat(found.get().getTitle()).isEqualTo("Buy groceries");
        }

        @Test
        @DisplayName("should return empty when ID does not exist")
        void shouldReturnEmptyWhenNotFound() {
            Optional<Todo> found = todoRepository.findById(999L);

            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByCompleted()")
    class FindByCompleted {

        @Test
        @DisplayName("should return only completed todos")
        void shouldReturnCompletedTodos() {
            List<Todo> completed = todoRepository.findByCompleted(true);

            assertThat(completed).hasSize(1);
            assertThat(completed.get(0).getTitle()).isEqualTo("Learn Spring Boot");
        }

        @Test
        @DisplayName("should return only incomplete todos")
        void shouldReturnIncompleteTodos() {
            List<Todo> incomplete = todoRepository.findByCompleted(false);

            assertThat(incomplete).hasSize(1);
            assertThat(incomplete.get(0).getTitle()).isEqualTo("Buy groceries");
        }
    }

    @Nested
    @DisplayName("delete()")
    class Delete {

        @Test
        @DisplayName("should delete todo by ID")
        void shouldDeleteById() {
            Long id = todo1.getId();

            todoRepository.deleteById(id);
            Optional<Todo> found = todoRepository.findById(id);

            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("should reduce count after deletion")
        void shouldReduceCountAfterDeletion() {
            long initialCount = todoRepository.count();

            todoRepository.delete(todo1);

            assertThat(todoRepository.count()).isEqualTo(initialCount - 1);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAll {

        @Test
        @DisplayName("should return all todos")
        void shouldReturnAll() {
            List<Todo> all = todoRepository.findAll();

            assertThat(all).hasSize(2);
        }
    }
}
