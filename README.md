# To-Do List REST API

A simple To-Do List application built with Spring Boot 3.2.x, providing full CRUD operations via a REST API. The project follows a clean layered architecture (controller → service → repository) and includes unit and integration tests.

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 17 | Language |
| Spring Boot 3.2.x | Application framework |
| Maven | Build tool |
| Spring Web | REST endpoints |
| Spring Data JPA | Data access layer |
| H2 Database | In-memory database for local dev and testing |
| Lombok | Boilerplate reduction |
| Bean Validation | Request validation |
| JUnit 5 + Mockito | Unit testing |
| Spring Boot Test + MockMvc | Integration / slice testing |
| springdoc-openapi | Swagger UI API documentation |

## Project Structure

```
todo-app/
├── pom.xml
├── README.md
├── src/
│   ├── main/
│   │   ├── java/com/example/todoapp/
│   │   │   ├── TodoAppApplication.java
│   │   │   ├── controller/
│   │   │   │   └── TodoController.java
│   │   │   ├── service/
│   │   │   │   ├── TodoService.java
│   │   │   │   └── impl/TodoServiceImpl.java
│   │   │   ├── repository/
│   │   │   │   └── TodoRepository.java
│   │   │   ├── entity/
│   │   │   │   └── Todo.java
│   │   │   ├── dto/
│   │   │   │   ├── TodoRequestDTO.java
│   │   │   │   └── TodoResponseDTO.java
│   │   │   ├── mapper/
│   │   │   │   └── TodoMapper.java
│   │   │   ├── exception/
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   ├── ErrorResponse.java
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   └── config/
│   │   │       └── OpenApiConfig.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── data.sql
│   └── test/
│       └── java/com/example/todoapp/
│           ├── controller/TodoControllerTest.java
│           ├── service/TodoServiceTest.java
│           └── repository/TodoRepositoryTest.java
```

## Prerequisites

- **Java 17** (or later)
- **Maven 3.8+**

Verify your setup:

```bash
java -version
mvn -version
```

## Running the Application

From the `todo-app` directory:

```bash
mvn spring-boot:run
```

The application starts on **http://localhost:8080**.

## Running Tests

Run all unit and integration tests:

```bash
mvn test
```

### Test Coverage

| Layer | Test Class | Approach |
|---|---|---|
| Service | `TodoServiceTest` | Mockito unit tests (repository mocked) — covers create, get all (with/without filter), get by ID (found/not found), update (found/not found), toggle complete, delete (found/not found) |
| Controller | `TodoControllerTest` | `@WebMvcTest` with MockMvc — tests each endpoint's success and failure responses (200, 201, 204, 404, 400 validation) with JSON structure verification |
| Repository | `TodoRepositoryTest` | `@DataJpaTest` against H2 — verifies save, findById, findByCompleted, delete, and findAll |

## API Documentation (Swagger UI)

Once the app is running, open:

```
http://localhost:8080/swagger-ui.html
```

The OpenAPI JSON is available at:

```
http://localhost:8080/v3/api-docs
```

## H2 Console

The H2 in-memory database console is enabled for development:

```
http://localhost:8080/h2-console
```

Connection settings:

| Field | Value |
|---|---|
| JDBC URL | `jdbc:h2:mem:todo_db` |
| User Name | `sa` |
| Password | *(leave empty)* |

## REST API Endpoints

Base path: `/api/todos`

| Method | Endpoint | Description | Success | Failure |
|---|---|---|---|---|
| POST | `/api/todos` | Create a new todo | 201 | 400 (validation) |
| GET | `/api/todos` | Get all todos (optional `?completed=true/false`) | 200 | — |
| GET | `/api/todos/{id}` | Get a todo by ID | 200 | 404 |
| PUT | `/api/todos/{id}` | Full update of a todo | 200 | 404, 400 |
| PATCH | `/api/todos/{id}/complete` | Toggle completion status | 200 | 404 |
| DELETE | `/api/todos/{id}` | Delete a todo | 204 | 404 |

### Sample curl Commands

**Create a todo:**

```bash
curl -X POST http://localhost:8080/api/todos \
  -H "Content-Type: application/json" \
  -d '{"title":"Learn Spring Boot","description":"Build a REST API","completed":false}'
```

**Get all todos:**

```bash
curl -X GET http://localhost:8080/api/todos
```

**Get completed todos only:**

```bash
curl -X GET "http://localhost:8080/api/todos?completed=true"
```

**Get a todo by ID:**

```bash
curl -X GET http://localhost:8080/api/todos/1
```

**Update a todo:**

```bash
curl -X PUT http://localhost:8080/api/todos/1 \
  -H "Content-Type: application/json" \
  -d '{"title":"Updated Title","description":"Updated description","completed":true}'
```

**Toggle completion:**

```bash
curl -X PATCH http://localhost:8080/api/todos/1/complete
```

**Delete a todo:**

```bash
curl -X DELETE http://localhost:8080/api/todos/1
```

## Error Response Format

All errors return a consistent JSON structure:

```json
{
  "timestamp": "2024-01-01T12:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Todo not found with id: 99",
  "path": "/api/todos/99"
}
```

## Seed Data

The `data.sql` file inserts three sample todos on startup so the API has data to work with immediately.
