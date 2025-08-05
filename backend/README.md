# Recipe Management API - Spring Boot Backend

This is the REST API backend for the Recipe Management System, built with Spring Boot 3.x and Java 17.

## Features

- **JWT Authentication**: Secure authentication with JSON Web Tokens
- **User Management**: User registration, login, profile management
- **Recipe CRUD**: Complete recipe management with ingredients and steps
- **PostgreSQL Database**: Persistent data storage
- **RESTful API**: Clean REST endpoints
- **Security**: Spring Security with JWT tokens
- **Validation**: Request validation with Bean Validation
- **CORS Support**: Cross-origin resource sharing enabled

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.1**
- **Spring Security 6**
- **Spring Data JPA**
- **PostgreSQL**
- **JWT (JSON Web Tokens)**
- **Maven**

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+

## Database Setup

1. Create a PostgreSQL database named `receitas_db`
2. Update the database connection settings in `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/receitas_db
    username: your_username
    password: your_password
```

3. Run the database script from the original project to create tables and initial data:
```sql
-- Use the dataBase.sql from the root directory
```

## Running the Application

1. Clone the repository
2. Navigate to the backend directory
3. Run the application:

```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080/api`

## API Endpoints

### Authentication
- `POST /api/auth/signin` - User login
- `POST /api/auth/signup` - User registration
- `POST /api/auth/refresh` - Refresh JWT token

### Users
- `GET /api/usuarios` - Get all active users
- `GET /api/usuarios/me` - Get current user profile
- `GET /api/usuarios/{id}` - Get user by ID
- `PUT /api/usuarios/{id}` - Update user profile
- `DELETE /api/usuarios/{id}` - Deactivate user account

### Recipes
- `GET /api/receitas` - Get user's recipes (with optional filters)
- `GET /api/receitas/{id}` - Get recipe by ID with details
- `POST /api/receitas` - Create new recipe
- `PUT /api/receitas/{id}` - Update recipe
- `DELETE /api/receitas/{id}` - Delete recipe
- `GET /api/receitas/count` - Get user's recipe count

## Request/Response Examples

### Login Request
```json
{
  "username": "admin",
  "password": "admin123"
}
```

### Login Response
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "admin",
  "nomeCompleto": "Administrator",
  "email": "admin@example.com"
}
```

### Create Recipe Request
```json
{
  "nome": "Bolo de Chocolate",
  "descricao": "Um delicioso bolo de chocolate",
  "tempoPreparoMin": 60,
  "porcoes": 8,
  "dificuldade": "Médio",
  "ingredientes": [
    {
      "nome": "Farinha de trigo",
      "quantidade": 2.0,
      "unidade": "xícaras"
    },
    {
      "nome": "Chocolate em pó",
      "quantidade": 0.5,
      "unidade": "xícara"
    }
  ],
  "passos": [
    {
      "ordem": 1,
      "descricao": "Misture os ingredientes secos"
    },
    {
      "ordem": 2,
      "descricao": "Adicione os ingredientes líquidos"
    }
  ]
}
```

## Security

- All endpoints except `/auth/**` require JWT authentication
- JWT tokens expire after 24 hours
- Passwords are encrypted using BCrypt
- Users can only access their own recipes
- CORS is configured to allow requests from the Angular frontend

## Configuration

Key configuration properties in `application.yml`:

```yaml
jwt:
  secret: mySecretKey123456789012345678901234567890
  expiration: 86400000 # 24 hours

cors:
  allowed-origins: http://localhost:4200
```

## Development

To run in development mode with hot reload:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Testing

Run the tests:

```bash
mvn test
```

## Building for Production

```bash
mvn clean package
java -jar target/receitas-api-1.0.0.jar
```
