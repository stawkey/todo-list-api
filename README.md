# Todo List API

A RESTful API for managing tasks and user authentication, built with Java and Spring Boot. The API allows users to register, authenticate, and manage their personal todo tasks securely, with JWT-based authentication and optional rate limiting.

## Features

- **User Registration & Authentication:** Users can register with email and password, and authenticate using JWT tokens.
- **Task Management:** 
  - Create, read, update, and delete tasks.
  - Tasks are scoped to the authenticated user.
  - Pagination and filtering by title are supported.
- **Security:** 
  - JWT token-based authentication.
  - Passwords are stored securely using password encoding.
  - Custom security filters and exception handling for unauthorized access.
- **Rate Limiting:** Optional filter to limit request rates.
- **API Documentation:** OpenAPI/Swagger documentation.

## Technologies

- Java
- Spring Boot
- Spring Security (JWT)
- JPA/Hibernate
- OpenAPI/Swagger

## Getting Started

### Prerequisites

- Java 21
- Maven
- (Optional) Docker, if running with a container

### Running with Docker

1. **Clone the repository:**
   ```bash
   git clone https://github.com/stawkey/todo-list-api.git
   cd todo-list-api
   ```

2. **Configure the application:**
   - Edit environmental variables in `docker-compose.yml`.

3. **Run:**
   ```bash
   docker-compose up
   ```

### Running Locally

1. **Clone the repository:**
   ```bash
   git clone https://github.com/stawkey/todo-list-api.git
   cd todo-list-api
   ```
2. **Configure the application:**
   - Edit `src/main/resources/application.properties` to set your database and JWT secret settings.

3. **Build and run:**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

## **Access API documentation:**

Open [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) in your browser.

## API Endpoints

- `POST /auth/register` – Register a new user.
- `POST /auth/login` – Authenticate and receive a JWT.
- `GET /tasks` – Get paginated tasks for the current user.
- `POST /tasks` – Add a new task.
- `PUT /tasks/{id}` – Update an existing task.
- `DELETE /tasks/{id}` – Delete a task.

## Security

- All endpoints (except `/auth/register`, `/auth/login`, and API docs) require a valid JWT token in the `Authorization: Bearer <token>` header.
- Passwords are hashed using BCrypt.
- Security configuration is handled in `WebSecurityConfig.java`.
