CREATE TABLE todo (
                      id BIGSERIAL PRIMARY KEY,
                      title VARCHAR(255) NOT NULL,
                      description TEXT,
                      completed BOOLEAN NOT NULL DEFAULT FALSE,
                      created_at TIMESTAMP NOT NULL,
                      updated_at TIMESTAMP NOT NULL
);

INSERT INTO todo (title, description, completed, created_at, updated_at) VALUES
                                                                             ('Learn Spring Boot', 'Build a REST API with Spring Boot 3', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                             ('Write unit tests', 'Cover service and controller layers', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                             ('Buy groceries', 'Milk, eggs, bread', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
