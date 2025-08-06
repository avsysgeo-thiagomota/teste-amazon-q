# API Documentation - Recipe Management System

This document provides detailed information about all available API endpoints in the Recipe Management System with easy-to-understand examples using PostgreSQL.

## Base URL
```
http://localhost:8080/api
```

## Authentication
Most endpoints require JWT authentication. Include the token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

## Configuration Setup

### Application Properties (application.yml)
```yaml
server:
  port: 8080
  servlet:
    context-path: /

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/recipe_db
    username: recipe_user
    password: recipe_pass
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
    database-platform: org.hibernate.dialect.PostgreSQLDialect

jwt:
  secret: mySecretKey123456789
  expiration: 86400000 # 24 hours in milliseconds

logging:
  level:
    org.avsytem: DEBUG
    org.springframework.security: DEBUG
```

### Database Schema (PostgreSQL)
```sql
-- Create database
CREATE DATABASE recipe_db;

-- Connect to database
\c recipe_db;

-- Create users table
CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    nome_completo VARCHAR(100),
    email VARCHAR(100),
    ativo BOOLEAN DEFAULT true,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create recipes table
CREATE TABLE receitas (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    tempo_preparo_min INTEGER,
    porcoes INTEGER,
    dificuldade VARCHAR(50),
    usuario_id INTEGER REFERENCES usuarios(id),
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create ingredients table
CREATE TABLE ingredientes (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    quantidade DECIMAL(10,2),
    unidade VARCHAR(50),
    receita_id INTEGER REFERENCES receitas(id) ON DELETE CASCADE
);

-- Create steps table
CREATE TABLE passos (
    id SERIAL PRIMARY KEY,
    ordem INTEGER NOT NULL,
    descricao TEXT NOT NULL,
    receita_id INTEGER REFERENCES receitas(id) ON DELETE CASCADE
);
```

### Docker Configuration
```yaml
# docker-compose.yml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/recipe_db
      - SPRING_DATASOURCE_USERNAME=recipe_user
      - SPRING_DATASOURCE_PASSWORD=recipe_pass
    depends_on:
      - db
    networks:
      - recipe-network

  db:
    image: postgres:15
    environment:
      - POSTGRES_DB=recipe_db
      - POSTGRES_USER=recipe_user
      - POSTGRES_PASSWORD=recipe_pass
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - recipe-network

volumes:
  postgres_data:

networks:
  recipe-network:
    driver: bridge
```

## Test Data Overview
The examples below use simple, easy-to-understand test data:
- **Users**: user1, user2, admin
- **Recipes**: Simple dishes like "Pasta", "Salad", "Soup"
- **Difficulty Levels**: Easy, Medium, Hard

---

## 1. Authentication Controller (`/auth`)

### 1.1 Sign In
**Endpoint:** `POST /auth/signin`  
**Description:** Authenticate user and receive JWT token  
**Authentication:** Not required  

#### Generic Example
**Request Body:**
```json
{
  "username": "string",
  "password": "string"
}
```

**Success Response (200):**
```json
{
  "token": "jwt_token_string",
  "type": "Bearer",
  "id": 1,
  "username": "string",
  "nomeCompleto": "string",
  "email": "string"
}
```

#### Example with Fake Data
**Request Body:**
```json
{
  "username": "user1",
  "password": "password123"
}
```

**Success Response (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMSIsImlhdCI6MTY5MTU4NDgwMCwiZXhwIjoxNjkxNjcxMjAwfQ.example_token_hash",
  "type": "Bearer",
  "id": 1,
  "username": "user1",
  "nomeCompleto": "John Smith",
  "email": "user1@example.com"
}
```

**Error Response (400):**
```json
{
  "message": "Credenciais inválidas"
}
```

**cURL Examples:**
```bash
# Generic example
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "username": "your_username",
    "password": "your_password"
  }'

# Example with fake data
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user1",
    "password": "password123"
  }'
```

### 1.2 Sign Up
**Endpoint:** `POST /auth/signup`  
**Description:** Register a new user  
**Authentication:** Not required  

#### Generic Example
**Request Body:**
```json
{
  "username": "string",
  "password": "string",
  "nomeCompleto": "string",
  "email": "string"
}
```

**Success Response (200):**
```json
{
  "message": "Usuário registrado com sucesso!",
  "username": "string"
}
```

#### Example with Fake Data
**Request Body:**
```json
{
  "username": "newuser",
  "password": "password123",
  "nomeCompleto": "Jane Doe",
  "email": "jane@example.com"
}
```

**Success Response (200):**
```json
{
  "message": "Usuário registrado com sucesso!",
  "username": "newuser"
}
```

**Error Response (400):**
```json
{
  "message": "Username já existe"
}
```

**cURL Examples:**
```bash
# Generic example
curl -X POST http://localhost:8080/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "your_username",
    "password": "your_password",
    "nomeCompleto": "Your Full Name",
    "email": "your@email.com"
  }'

# Example with fake data
curl -X POST http://localhost:8080/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "password123",
    "nomeCompleto": "Jane Doe",
    "email": "jane@example.com"
  }'
```

### 1.3 Refresh Token
**Endpoint:** `POST /auth/refresh`  
**Description:** Refresh JWT token  
**Authentication:** Required (Bearer token in header)  

#### Generic Example
**Headers:**
```
Authorization: Bearer your_jwt_token_here
```

**Success Response (200):**
```json
{
  "token": "new_jwt_token_string",
  "type": "Bearer"
}
```

#### Example with Fake Data
**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMSIsImlhdCI6MTY5MTU4NDgwMCwiZXhwIjoxNjkxNjcxMjAwfQ.example_token_hash
```

**Success Response (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMSIsImlhdCI6MTY5MTU4ODQwMCwiZXhwIjoxNjkxNjc0ODAwfQ.new_example_token_hash",
  "type": "Bearer"
}
```

**Error Response (400):**
```json
{
  "message": "Token inválido"
}
```

**cURL Examples:**
```bash
# Generic example
curl -X POST http://localhost:8080/auth/refresh \
  -H "Authorization: Bearer your_jwt_token_here"

# Example with fake data
curl -X POST http://localhost:8080/auth/refresh \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMSIsImlhdCI6MTY5MTU4NDgwMCwiZXhwIjoxNjkxNjcxMjAwfQ.example_token_hash"
```

---

## 2. Recipe Controller (`/receitas`)

### 2.1 Get All Recipes
**Endpoint:** `GET /receitas`  
**Description:** Get all recipes for the authenticated user  
**Authentication:** Required  

**Query Parameters:**
- `nome` (optional): Filter by recipe name
- `dificuldade` (optional): Filter by difficulty level
- `withDetails` (optional, default: false): Include ingredients and steps

#### Generic Example
**Success Response (200):**
```json
[
  {
    "id": 1,
    "nome": "string",
    "descricao": "string",
    "tempoPreparoMin": 30,
    "porcoes": 4,
    "dificuldade": "string",
    "dataCriacao": "2024-08-06T10:30:00",
    "usuario": {
      "id": 1,
      "username": "string"
    }
  }
]
```

#### Example with Fake Data
**Success Response (200):**
```json
[
  {
    "id": 1,
    "nome": "Spaghetti Pasta",
    "descricao": "Simple and delicious pasta dish",
    "tempoPreparoMin": 30,
    "porcoes": 4,
    "dificuldade": "Easy",
    "dataCriacao": "2024-08-06T10:30:00",
    "usuario": {
      "id": 1,
      "username": "user1"
    }
  },
  {
    "id": 2,
    "nome": "Caesar Salad",
    "descricao": "Fresh salad with caesar dressing",
    "tempoPreparoMin": 15,
    "porcoes": 2,
    "dificuldade": "Easy",
    "dataCriacao": "2024-08-06T11:00:00",
    "usuario": {
      "id": 1,
      "username": "user1"
    }
  }
]
```

**cURL Examples:**
```bash
# Generic example
curl -X GET http://localhost:8080/receitas \
  -H "Authorization: Bearer your_jwt_token_here"

# Example with fake data
curl -X GET http://localhost:8080/receitas \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMSIsImlhdCI6MTY5MTU4NDgwMCwiZXhwIjoxNjkxNjcxMjAwfQ.example_token_hash"

# Filter by name
curl -X GET "http://localhost:8080/receitas?nome=pasta" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMSIsImlhdCI6MTY5MTU4NDgwMCwiZXhwIjoxNjkxNjcxMjAwfQ.example_token_hash"

# Filter by difficulty
curl -X GET "http://localhost:8080/receitas?dificuldade=Easy" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMSIsImlhdCI6MTY5MTU4NDgwMCwiZXhwIjoxNjkxNjcxMjAwfQ.example_token_hash"
```
### 2.2 Get Recipe by ID
**Endpoint:** `GET /receitas/{id}`  
**Description:** Get a specific recipe with full details  
**Authentication:** Required  

#### Generic Example
**URL:** `GET /receitas/{id}`

**Success Response (200):**
```json
{
  "id": 1,
  "nome": "string",
  "descricao": "string",
  "tempoPreparoMin": 30,
  "porcoes": 4,
  "dificuldade": "string",
  "dataCriacao": "2024-08-06T10:30:00",
  "usuario": {
    "id": 1,
    "username": "string"
  },
  "ingredientes": [
    {
      "id": 1,
      "nome": "string",
      "quantidade": 1.0,
      "unidade": "string"
    }
  ],
  "passos": [
    {
      "id": 1,
      "ordem": 1,
      "descricao": "string"
    }
  ]
}
```

#### Example with Fake Data
**URL:** `GET /receitas/1`

**Success Response (200):**
```json
{
  "id": 1,
  "nome": "Spaghetti Pasta",
  "descricao": "Simple and delicious pasta dish",
  "tempoPreparoMin": 30,
  "porcoes": 4,
  "dificuldade": "Easy",
  "dataCriacao": "2024-08-06T10:30:00",
  "usuario": {
    "id": 1,
    "username": "user1"
  },
  "ingredientes": [
    {
      "id": 1,
      "nome": "Spaghetti",
      "quantidade": 400.0,
      "unidade": "grams"
    },
    {
      "id": 2,
      "nome": "Tomato Sauce",
      "quantidade": 200.0,
      "unidade": "ml"
    },
    {
      "id": 3,
      "nome": "Garlic",
      "quantidade": 2.0,
      "unidade": "cloves"
    }
  ],
  "passos": [
    {
      "id": 1,
      "ordem": 1,
      "descricao": "Boil water and cook spaghetti for 10 minutes"
    },
    {
      "id": 2,
      "ordem": 2,
      "descricao": "Heat tomato sauce with garlic"
    },
    {
      "id": 3,
      "ordem": 3,
      "descricao": "Mix pasta with sauce and serve"
    }
  ]
}
```

**Error Response (404):**
```json
{
  "message": "Receita não encontrada"
}
```

**cURL Examples:**
```bash
# Generic example
curl -X GET http://localhost:8080/receitas/1 \
  -H "Authorization: Bearer your_jwt_token_here"

# Example with fake data
curl -X GET http://localhost:8080/receitas/1 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMSIsImlhdCI6MTY5MTU4NDgwMCwiZXhwIjoxNjkxNjcxMjAwfQ.example_token_hash"
```

### 2.3 Create Recipe
**Endpoint:** `POST /receitas`  
**Description:** Create a new recipe  
**Authentication:** Required  

#### Generic Example
**Request Body:**
```json
{
  "nome": "string",
  "descricao": "string",
  "tempoPreparoMin": 30,
  "porcoes": 4,
  "dificuldade": "string",
  "ingredientes": [
    {
      "nome": "string",
      "quantidade": 1.0,
      "unidade": "string"
    }
  ],
  "passos": [
    {
      "ordem": 1,
      "descricao": "string"
    }
  ]
}
```

**Success Response (200):**
```json
{
  "message": "Receita criada com sucesso!",
  "receita": {
    "id": 1,
    "nome": "string",
    "descricao": "string",
    "tempoPreparoMin": 30,
    "porcoes": 4,
    "dificuldade": "string",
    "dataCriacao": "2024-08-06T11:00:00",
    "usuario": {
      "id": 1,
      "username": "string"
    }
  }
}
```

#### Example with Fake Data
**Request Body:**
```json
{
  "nome": "Chicken Soup",
  "descricao": "Warm and comforting chicken soup",
  "tempoPreparoMin": 45,
  "porcoes": 6,
  "dificuldade": "Medium",
  "ingredientes": [
    {
      "nome": "Chicken Breast",
      "quantidade": 500.0,
      "unidade": "grams"
    },
    {
      "nome": "Carrots",
      "quantidade": 2.0,
      "unidade": "pieces"
    },
    {
      "nome": "Onion",
      "quantidade": 1.0,
      "unidade": "piece"
    },
    {
      "nome": "Water",
      "quantidade": 1.0,
      "unidade": "liter"
    }
  ],
  "passos": [
    {
      "ordem": 1,
      "descricao": "Cut chicken and vegetables into small pieces"
    },
    {
      "ordem": 2,
      "descricao": "Boil water in a large pot"
    },
    {
      "ordem": 3,
      "descricao": "Add chicken and cook for 20 minutes"
    },
    {
      "ordem": 4,
      "descricao": "Add vegetables and cook for 15 more minutes"
    },
    {
      "ordem": 5,
      "descricao": "Season with salt and pepper, serve hot"
    }
  ]
}
```

**Success Response (200):**
```json
{
  "message": "Receita criada com sucesso!",
  "receita": {
    "id": 3,
    "nome": "Chicken Soup",
    "descricao": "Warm and comforting chicken soup",
    "tempoPreparoMin": 45,
    "porcoes": 6,
    "dificuldade": "Medium",
    "dataCriacao": "2024-08-06T11:00:00",
    "usuario": {
      "id": 1,
      "username": "user1"
    }
  }
}
```

**cURL Examples:**
```bash
# Generic example
curl -X POST http://localhost:8080/receitas \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your_jwt_token_here" \
  -d '{
    "nome": "Recipe Name",
    "descricao": "Recipe Description",
    "tempoPreparoMin": 30,
    "porcoes": 4,
    "dificuldade": "Easy",
    "ingredientes": [
      {
        "nome": "Ingredient Name",
        "quantidade": 1.0,
        "unidade": "unit"
      }
    ],
    "passos": [
      {
        "ordem": 1,
        "descricao": "Step description"
      }
    ]
  }'

# Example with fake data
curl -X POST http://localhost:8080/receitas \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMSIsImlhdCI6MTY5MTU4NDgwMCwiZXhwIjoxNjkxNjcxMjAwfQ.example_token_hash" \
  -d '{
    "nome": "Chicken Soup",
    "descricao": "Warm and comforting chicken soup",
    "tempoPreparoMin": 45,
    "porcoes": 6,
    "dificuldade": "Medium",
    "ingredientes": [
      {
        "nome": "Chicken Breast",
        "quantidade": 500.0,
        "unidade": "grams"
      },
      {
        "nome": "Carrots",
        "quantidade": 2.0,
        "unidade": "pieces"
      }
    ],
    "passos": [
      {
        "ordem": 1,
        "descricao": "Cut chicken and vegetables into small pieces"
      },
      {
        "ordem": 2,
        "descricao": "Boil water and add ingredients"
      }
    ]
  }'
```

### 2.4 Update Recipe
**Endpoint:** `PUT /receitas/{id}`  
**Description:** Update an existing recipe  
**Authentication:** Required  

#### Generic Example
**URL:** `PUT /receitas/{id}`

**Request Body:** Same as Create Recipe

**Success Response (200):**
```json
{
  "message": "Receita atualizada com sucesso!",
  "receita": {
    "id": 1,
    "nome": "string",
    "descricao": "string",
    "tempoPreparoMin": 30,
    "porcoes": 4,
    "dificuldade": "string",
    "dataCriacao": "2024-08-06T10:30:00",
    "dataAtualizacao": "2024-08-06T12:00:00",
    "usuario": {
      "id": 1,
      "username": "string"
    }
  }
}
```

#### Example with Fake Data
**URL:** `PUT /receitas/1`

**Request Body:**
```json
{
  "nome": "Spaghetti Pasta Deluxe",
  "descricao": "Enhanced pasta dish with extra ingredients",
  "tempoPreparoMin": 40,
  "porcoes": 4,
  "dificuldade": "Medium"
}
```

**Success Response (200):**
```json
{
  "message": "Receita atualizada com sucesso!",
  "receita": {
    "id": 1,
    "nome": "Spaghetti Pasta Deluxe",
    "descricao": "Enhanced pasta dish with extra ingredients",
    "tempoPreparoMin": 40,
    "porcoes": 4,
    "dificuldade": "Medium",
    "dataCriacao": "2024-08-06T10:30:00",
    "dataAtualizacao": "2024-08-06T12:00:00",
    "usuario": {
      "id": 1,
      "username": "user1"
    }
  }
}
```

**cURL Examples:**
```bash
# Generic example
curl -X PUT http://localhost:8080/receitas/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your_jwt_token_here" \
  -d '{
    "nome": "Updated Recipe Name",
    "descricao": "Updated description",
    "tempoPreparoMin": 35,
    "porcoes": 4,
    "dificuldade": "Medium"
  }'

# Example with fake data
curl -X PUT http://localhost:8080/receitas/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMSIsImlhdCI6MTY5MTU4NDgwMCwiZXhwIjoxNjkxNjcxMjAwfQ.example_token_hash" \
  -d '{
    "nome": "Spaghetti Pasta Deluxe",
    "descricao": "Enhanced pasta dish with extra ingredients",
    "tempoPreparoMin": 40,
    "porcoes": 4,
    "dificuldade": "Medium"
  }'
```

### 2.5 Delete Recipe
**Endpoint:** `DELETE /receitas/{id}`  
**Description:** Delete a recipe  
**Authentication:** Required  

#### Generic Example
**URL:** `DELETE /receitas/{id}`

**Success Response (200):**
```json
{
  "message": "Receita deletada com sucesso!"
}
```

#### Example with Fake Data
**URL:** `DELETE /receitas/3`

**Success Response (200):**
```json
{
  "message": "Receita deletada com sucesso!"
}
```

**Error Response (400):**
```json
{
  "message": "Receita não encontrada"
}
```

**cURL Examples:**
```bash
# Generic example
curl -X DELETE http://localhost:8080/receitas/1 \
  -H "Authorization: Bearer your_jwt_token_here"

# Example with fake data
curl -X DELETE http://localhost:8080/receitas/3 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMSIsImlhdCI6MTY5MTU4NDgwMCwiZXhwIjoxNjkxNjcxMjAwfQ.example_token_hash"
```

### 2.6 Get Recipe Count
**Endpoint:** `GET /receitas/count`  
**Description:** Get the total number of recipes for the authenticated user  
**Authentication:** Required  

#### Generic Example
**Success Response (200):**
```json
{
  "count": 10
}
```

#### Example with Fake Data
**Success Response (200):**
```json
{
  "count": 5
}
```

**cURL Examples:**
```bash
# Generic example
curl -X GET http://localhost:8080/receitas/count \
  -H "Authorization: Bearer your_jwt_token_here"

# Example with fake data
curl -X GET http://localhost:8080/receitas/count \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMSIsImlhdCI6MTY5MTU4NDgwMCwiZXhwIjoxNjkxNjcxMjAwfQ.example_token_hash"
```

---

## 3. User Controller (`/usuarios`)

### 3.1 Get All Users
**Endpoint:** `GET /usuarios`  
**Description:** Get all active users  
**Authentication:** Required  

#### Generic Example
**Success Response (200):**
```json
[
  {
    "id": 1,
    "username": "string",
    "nomeCompleto": "string",
    "email": "string",
    "ativo": true,
    "dataCriacao": "2024-08-06T10:00:00"
  }
]
```

#### Example with Fake Data
**Success Response (200):**
```json
[
  {
    "id": 1,
    "username": "user1",
    "nomeCompleto": "John Smith",
    "email": "user1@example.com",
    "ativo": true,
    "dataCriacao": "2024-08-01T10:00:00"
  },
  {
    "id": 2,
    "username": "user2",
    "nomeCompleto": "Jane Doe",
    "email": "user2@example.com",
    "ativo": true,
    "dataCriacao": "2024-08-02T14:30:00"
  },
  {
    "id": 3,
    "username": "admin",
    "nomeCompleto": "Admin User",
    "email": "admin@example.com",
    "ativo": true,
    "dataCriacao": "2024-07-01T08:00:00"
  }
]
```

**cURL Examples:**
```bash
# Generic example
curl -X GET http://localhost:8080/usuarios \
  -H "Authorization: Bearer your_jwt_token_here"

# Example with fake data
curl -X GET http://localhost:8080/usuarios \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMSIsImlhdCI6MTY5MTU4NDgwMCwiZXhwIjoxNjkxNjcxMjAwfQ.example_token_hash"
```

### 3.2 Get User by ID
**Endpoint:** `GET /usuarios/{id}`  
**Description:** Get a specific user by ID  
**Authentication:** Required  

#### Generic Example
**URL:** `GET /usuarios/{id}`

**Success Response (200):**
```json
{
  "id": 1,
  "username": "string",
  "nomeCompleto": "string",
  "email": "string",
  "ativo": true,
  "dataCriacao": "2024-08-06T10:00:00"
}
```

#### Example with Fake Data
**URL:** `GET /usuarios/1`

**Success Response (200):**
```json
{
  "id": 1,
  "username": "user1",
  "nomeCompleto": "John Smith",
  "email": "user1@example.com",
  "ativo": true,
  "dataCriacao": "2024-08-01T10:00:00"
}
```

**Error Response (404):**
```json
{
  "message": "Usuário não encontrado"
}
```

**cURL Examples:**
```bash
# Generic example
curl -X GET http://localhost:8080/usuarios/1 \
  -H "Authorization: Bearer your_jwt_token_here"

# Example with fake data
curl -X GET http://localhost:8080/usuarios/1 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMSIsImlhdCI6MTY5MTU4NDgwMCwiZXhwIjoxNjkxNjcxMjAwfQ.example_token_hash"
```

### 3.3 Get Current User
**Endpoint:** `GET /usuarios/me`  
**Description:** Get the current authenticated user's information  
**Authentication:** Required  

#### Generic Example
**Success Response (200):**
```json
{
  "id": 1,
  "username": "string",
  "nomeCompleto": "string",
  "email": "string",
  "ativo": true,
  "dataCriacao": "2024-08-06T10:00:00"
}
```

#### Example with Fake Data
**Success Response (200):**
```json
{
  "id": 1,
  "username": "user1",
  "nomeCompleto": "John Smith",
  "email": "user1@example.com",
  "ativo": true,
  "dataCriacao": "2024-08-01T10:00:00"
}
```

**cURL Examples:**
```bash
# Generic example
curl -X GET http://localhost:8080/usuarios/me \
  -H "Authorization: Bearer your_jwt_token_here"

# Example with fake data
curl -X GET http://localhost:8080/usuarios/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMSIsImlhdCI6MTY5MTU4NDgwMCwiZXhwIjoxNjkxNjcxMjAwfQ.example_token_hash"
```

### 3.4 Update User
**Endpoint:** `PUT /usuarios/{id}`  
**Description:** Update user information (users can only update their own profile)  
**Authentication:** Required  

#### Generic Example
**URL:** `PUT /usuarios/{id}`

**Request Body:**
```json
{
  "username": "string",
  "password": "string",
  "nomeCompleto": "string",
  "email": "string"
}
```

**Success Response (200):**
```json
{
  "message": "Usuário atualizado com sucesso!"
}
```

#### Example with Fake Data
**URL:** `PUT /usuarios/1`

**Request Body:**
```json
{
  "username": "user1_updated",
  "password": "newpassword123",
  "nomeCompleto": "John Smith Updated",
  "email": "john.updated@example.com"
}
```

**Success Response (200):**
```json
{
  "message": "Usuário atualizado com sucesso!"
}
```

**Error Response (400):**
```json
{
  "message": "Você só pode editar seu próprio perfil"
}
```

**cURL Examples:**
```bash
# Generic example
curl -X PUT http://localhost:8080/usuarios/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your_jwt_token_here" \
  -d '{
    "username": "updated_username",
    "password": "new_password",
    "nomeCompleto": "Updated Name",
    "email": "updated@email.com"
  }'

# Example with fake data
curl -X PUT http://localhost:8080/usuarios/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMSIsImlhdCI6MTY5MTU4NDgwMCwiZXhwIjoxNjkxNjcxMjAwfQ.example_token_hash" \
  -d '{
    "username": "user1_updated",
    "password": "newpassword123",
    "nomeCompleto": "John Smith Updated",
    "email": "john.updated@example.com"
  }'
```

### 3.5 Delete User
**Endpoint:** `DELETE /usuarios/{id}`  
**Description:** Deactivate user account (users can only delete their own profile)  
**Authentication:** Required  

#### Generic Example
**URL:** `DELETE /usuarios/{id}`

**Success Response (200):**
```json
{
  "message": "Usuário desativado com sucesso!"
}
```

#### Example with Fake Data
**URL:** `DELETE /usuarios/1`

**Success Response (200):**
```json
{
  "message": "Usuário desativado com sucesso!"
}
```

**Error Response (400):**
```json
{
  "message": "Você só pode deletar seu próprio perfil"
}
```

**cURL Examples:**
```bash
# Generic example
curl -X DELETE http://localhost:8080/usuarios/1 \
  -H "Authorization: Bearer your_jwt_token_here"

# Example with fake data
curl -X DELETE http://localhost:8080/usuarios/1 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMSIsImlhdCI6MTY5MTU4NDgwMCwiZXhwIjoxNjkxNjcxMjAwfQ.example_token_hash"
```

### 3.6 Activate User (Admin Only)
**Endpoint:** `POST /usuarios/{id}/activate`  
**Description:** Activate a deactivated user account  
**Authentication:** Required (Admin role)  

#### Generic Example
**URL:** `POST /usuarios/{id}/activate`

**Success Response (200):**
```json
{
  "message": "Usuário ativado com sucesso!"
}
```

#### Example with Fake Data
**URL:** `POST /usuarios/1/activate`

**Success Response (200):**
```json
{
  "message": "Usuário ativado com sucesso!"
}
```

**Error Response (403):**
```json
{
  "message": "Acesso negado. Apenas administradores podem ativar usuários."
}
```

**cURL Examples:**
```bash
# Generic example (admin token required)
curl -X POST http://localhost:8080/usuarios/1/activate \
  -H "Authorization: Bearer admin_jwt_token_here"

# Example with fake data
curl -X POST http://localhost:8080/usuarios/1/activate \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTY5MTU4NDgwMCwiZXhwIjoxNjkxNjcxMjAwfQ.admin_token_hash"
```
---

## Error Handling

### Common HTTP Status Codes
- **200 OK**: Request successful
- **400 Bad Request**: Invalid request data or business logic error
- **401 Unauthorized**: Authentication required or invalid token
- **403 Forbidden**: Insufficient permissions
- **404 Not Found**: Resource not found
- **500 Internal Server Error**: Server error

### Common Error Response Format
```json
{
  "message": "Error description in Portuguese"
}
```

### Validation Error Example
**Request with invalid data:**
```json
{
  "username": "",
  "password": "123",
  "email": "invalid-email"
}
```

**Response (400):**
```json
{
  "timestamp": "2024-08-06T11:00:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": [
    {
      "field": "username",
      "message": "Username é obrigatório"
    },
    {
      "field": "password",
      "message": "Password deve ter no mínimo 6 caracteres"
    },
    {
      "field": "email",
      "message": "Email deve ter um formato válido"
    }
  ]
}
```

---

## Validation Rules

### User Registration/Update
- `username`: Required, max 50 characters, unique
- `password`: Required, min 6 characters
- `nomeCompleto`: Optional, max 100 characters
- `email`: Optional, valid email format, max 100 characters

### Recipe Creation/Update
- `nome`: Required, max 255 characters
- `descricao`: Optional, text
- `tempoPreparoMin`: Optional, positive integer
- `porcoes`: Optional, positive integer
- `dificuldade`: Optional, max 50 characters
- `ingredientes[].nome`: Required for each ingredient
- `passos[].descricao`: Required for each step

### Authentication
- `username`: Required, not blank
- `password`: Required, not blank

---

## Sample Test Data for PostgreSQL

### Insert Test Users
```sql
INSERT INTO usuarios (username, password, nome_completo, email, ativo, data_criacao) VALUES
('user1', '$2a$10$encrypted_password_hash_1', 'John Smith', 'user1@example.com', true, '2024-08-01 10:00:00'),
('user2', '$2a$10$encrypted_password_hash_2', 'Jane Doe', 'user2@example.com', true, '2024-08-02 14:30:00'),
('admin', '$2a$10$encrypted_password_hash_3', 'Admin User', 'admin@example.com', true, '2024-07-01 08:00:00');
```

### Insert Test Recipes
```sql
INSERT INTO receitas (nome, descricao, tempo_preparo_min, porcoes, dificuldade, usuario_id, data_criacao) VALUES
('Spaghetti Pasta', 'Simple and delicious pasta dish', 30, 4, 'Easy', 1, '2024-08-01 10:30:00'),
('Caesar Salad', 'Fresh salad with caesar dressing', 15, 2, 'Easy', 1, '2024-08-02 11:00:00'),
('Chicken Soup', 'Warm and comforting chicken soup', 45, 6, 'Medium', 2, '2024-08-03 16:45:00');
```

### Insert Test Ingredients
```sql
INSERT INTO ingredientes (nome, quantidade, unidade, receita_id) VALUES
('Spaghetti', 400.0, 'grams', 1),
('Tomato Sauce', 200.0, 'ml', 1),
('Garlic', 2.0, 'cloves', 1),
('Lettuce', 1.0, 'head', 2),
('Caesar Dressing', 100.0, 'ml', 2),
('Croutons', 50.0, 'grams', 2);
```

### Insert Test Steps
```sql
INSERT INTO passos (ordem, descricao, receita_id) VALUES
(1, 'Boil water and cook spaghetti for 10 minutes', 1),
(2, 'Heat tomato sauce with garlic', 1),
(3, 'Mix pasta with sauce and serve', 1),
(1, 'Wash and chop lettuce', 2),
(2, 'Add caesar dressing and croutons', 2),
(3, 'Toss salad and serve immediately', 2);
```

---

## Testing Examples

### Complete Test Scenario
```bash
#!/bin/bash

echo "=== Recipe Management API Test ==="

# 1. Register a new user
echo "1. Registering new user..."
curl -X POST http://localhost:8080/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "testpass123",
    "nomeCompleto": "Test User",
    "email": "test@example.com"
  }'

echo -e "\n"

# 2. Login with the new user
echo "2. Logging in..."
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "testpass123"
  }')

TOKEN=$(echo $LOGIN_RESPONSE | jq -r '.token')
echo "Token: $TOKEN"

echo -e "\n"

# 3. Create a recipe
echo "3. Creating a recipe..."
curl -X POST http://localhost:8080/receitas \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "nome": "Test Recipe",
    "descricao": "A simple test recipe",
    "tempoPreparoMin": 20,
    "porcoes": 2,
    "dificuldade": "Easy",
    "ingredientes": [
      {
        "nome": "Test Ingredient",
        "quantidade": 1.0,
        "unidade": "piece"
      }
    ],
    "passos": [
      {
        "ordem": 1,
        "descricao": "Mix all ingredients"
      },
      {
        "ordem": 2,
        "descricao": "Cook for 20 minutes"
      }
    ]
  }'

echo -e "\n"

# 4. Get all recipes
echo "4. Getting all recipes..."
curl -X GET http://localhost:8080/receitas \
  -H "Authorization: Bearer $TOKEN"

echo -e "\n"

# 5. Get recipe count
echo "5. Getting recipe count..."
curl -X GET http://localhost:8080/receitas/count \
  -H "Authorization: Bearer $TOKEN"

echo -e "\n"

# 6. Get current user info
echo "6. Getting current user info..."
curl -X GET http://localhost:8080/usuarios/me \
  -H "Authorization: Bearer $TOKEN"

echo -e "\nTest completed!"
```

### Postman Collection Setup

#### Environment Variables
```json
{
  "baseUrl": "http://localhost:8080",
  "token": "",
  "userId": "",
  "recipeId": ""
}
```

#### Pre-request Script for Protected Endpoints
```javascript
// Add Authorization header if token exists
if (pm.environment.get("token")) {
    pm.request.headers.add({
        key: 'Authorization',
        value: 'Bearer ' + pm.environment.get('token')
    });
}
```

#### Post-response Script for Login
```javascript
// Save token after successful login
if (pm.response.code === 200) {
    const responseJson = pm.response.json();
    if (responseJson.token) {
        pm.environment.set("token", responseJson.token);
        pm.environment.set("userId", responseJson.id);
        console.log("Token saved:", responseJson.token);
    }
}
```

---

## Security Notes

1. **JWT Token**: Include in Authorization header as `Bearer <token>`
2. **Token Expiration**: Tokens expire after 24 hours and need to be refreshed
3. **User Isolation**: Users can only access their own recipes and profile
4. **Admin Functions**: Some endpoints require admin role
5. **Password Encryption**: Passwords are encrypted using BCrypt
6. **CORS**: Configure allowed origins in production

### Token Management Example (JavaScript)
```javascript
class ApiClient {
    constructor(baseUrl) {
        this.baseUrl = baseUrl;
        this.token = localStorage.getItem('authToken');
    }

    setToken(token) {
        this.token = token;
        localStorage.setItem('authToken', token);
    }

    async request(endpoint, options = {}) {
        const url = `${this.baseUrl}${endpoint}`;
        const config = {
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            },
            ...options
        };

        if (this.token) {
            config.headers.Authorization = `Bearer ${this.token}`;
        }

        try {
            const response = await fetch(url, config);
            
            if (response.status === 401) {
                // Token expired, redirect to login
                localStorage.removeItem('authToken');
                window.location.href = '/login';
                return;
            }

            return await response.json();
        } catch (error) {
            console.error('API request failed:', error);
            throw error;
        }
    }

    // Authentication methods
    async login(username, password) {
        const response = await this.request('/auth/signin', {
            method: 'POST',
            body: JSON.stringify({ username, password })
        });
        
        if (response.token) {
            this.setToken(response.token);
        }
        
        return response;
    }

    async register(userData) {
        return await this.request('/auth/signup', {
            method: 'POST',
            body: JSON.stringify(userData)
        });
    }

    // Recipe methods
    async getRecipes(filters = {}) {
        const params = new URLSearchParams(filters);
        return await this.request(`/receitas?${params}`);
    }

    async createRecipe(recipeData) {
        return await this.request('/receitas', {
            method: 'POST',
            body: JSON.stringify(recipeData)
        });
    }

    async updateRecipe(id, recipeData) {
        return await this.request(`/receitas/${id}`, {
            method: 'PUT',
            body: JSON.stringify(recipeData)
        });
    }

    async deleteRecipe(id) {
        return await this.request(`/receitas/${id}`, {
            method: 'DELETE'
        });
    }
}

// Usage example
const api = new ApiClient('http://localhost:8080');

// Login
api.login('user1', 'password123').then(response => {
    console.log('Logged in:', response);
});

// Create recipe
api.createRecipe({
    nome: 'New Recipe',
    descricao: 'Recipe description',
    tempoPreparoMin: 30,
    porcoes: 4,
    dificuldade: 'Easy'
}).then(response => {
    console.log('Recipe created:', response);
});
```

---

## Production Deployment

### Environment Variables
```bash
# .env file
SPRING_PROFILES_ACTIVE=production
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/recipe_db
SPRING_DATASOURCE_USERNAME=recipe_user
SPRING_DATASOURCE_PASSWORD=secure_password
JWT_SECRET=very_secure_jwt_secret_key_256_bits
JWT_EXPIRATION=86400000
CORS_ORIGINS=https://yourdomain.com
```

### Docker Compose for Production
```yaml
version: '3.8'

services:
  app:
    image: recipe-management:latest
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=production
      - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/recipe_db
      - SPRING_DATASOURCE_USERNAME=recipe_user
      - SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD}
      - JWT_SECRET=${JWT_SECRET}
    depends_on:
      - db
    restart: unless-stopped

  db:
    image: postgres:15
    environment:
      - POSTGRES_DB=recipe_db
      - POSTGRES_USER=recipe_user
      - POSTGRES_PASSWORD=${DB_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql
    restart: unless-stopped

volumes:
  postgres_data:
```

This documentation now provides:

✅ **PostgreSQL Configuration** - Complete setup with proper SQL syntax  
✅ **Generic Examples** - Template structures for all endpoints  
✅ **Fake Data Examples** - Simple, easy-to-understand test data  
✅ **Clear Structure** - Each endpoint has both generic and specific examples  
✅ **Complete Testing** - Full test scenarios and Postman setup  
✅ **Production Ready** - Docker, security, and deployment configurations  

The documentation is now much cleaner and easier to understand, with simple fake data like "user1", "Spaghetti Pasta", and "Chicken Soup" that anyone can quickly grasp and use for testing! 🚀
