# DTOs Implementation Documentation

## Overview
This document describes the Data Transfer Objects (DTOs) implemented to control what data is exposed to the frontend, improving security and performance by hiding sensitive information and unnecessary relationships.

## DTOs Created

### 1. UsuarioResponse
**Purpose**: Expose user information without sensitive data
**Location**: `src/main/java/org/avsytem/dto/UsuarioResponse.java`

**Fields Included**:
- `id` - User ID
- `username` - Username
- `nomeCompleto` - Full name
- `email` - Email address
- `ativo` - Active status
- `dataCriacao` - Creation date

**Fields Excluded**:
- `password` - Password hash (security)
- `receitas` - User's recipes (performance)
- `enabled`, `accountNonExpired`, etc. - Spring Security fields (not needed by frontend)

### 2. IngredienteResponse
**Purpose**: Expose ingredient information without recipe relationship
**Location**: `src/main/java/org/avsytem/dto/IngredienteResponse.java`

**Fields Included**:
- `id` - Ingredient ID
- `nome` - Ingredient name
- `quantidade` - Quantity
- `unidade` - Unit of measurement

**Fields Excluded**:
- `receita` - Recipe relationship (prevents circular references)

### 3. PassoResponse
**Purpose**: Expose recipe step information without recipe relationship
**Location**: `src/main/java/org/avsytem/dto/PassoResponse.java`

**Fields Included**:
- `id` - Step ID
- `ordem` - Step order
- `descricao` - Step description

**Fields Excluded**:
- `receita` - Recipe relationship (prevents circular references)

### 4. ReceitaResponse
**Purpose**: Complete recipe information with nested DTOs
**Location**: `src/main/java/org/avsytem/dto/ReceitaResponse.java`

**Fields Included**:
- `id` - Recipe ID
- `nome` - Recipe name
- `descricao` - Recipe description
- `tempoPreparoMin` - Preparation time in minutes
- `porcoes` - Number of servings
- `dificuldade` - Difficulty level
- `usuario` - User information (as UsuarioResponse DTO)
- `ingredientes` - List of ingredients (as IngredienteResponse DTOs)
- `passos` - List of steps (as PassoResponse DTOs)

**Benefits**:
- Complete recipe information
- Nested DTOs prevent sensitive data exposure
- No circular references

### 5. ReceitaSummaryResponse
**Purpose**: Lightweight recipe information for listing
**Location**: `src/main/java/org/avsytem/dto/ReceitaSummaryResponse.java`

**Fields Included**:
- `id` - Recipe ID
- `nome` - Recipe name
- `descricao` - Recipe description
- `tempoPreparoMin` - Preparation time
- `porcoes` - Number of servings
- `dificuldade` - Difficulty level
- `usuarioNome` - User's display name
- `totalIngredientes` - Count of ingredients
- `totalPassos` - Count of steps

**Benefits**:
- Optimized for listing views
- Includes summary statistics
- Minimal data transfer

## Controller Updates

### UsuarioController
- All endpoints now return `UsuarioResponse` instead of `Usuario` entity
- Sensitive information like passwords are automatically excluded
- List endpoints return `List<UsuarioResponse>`

### ReceitaController
- List endpoints return `List<ReceitaSummaryResponse>` for performance
- Detail endpoints return `ReceitaResponse` with complete information
- Create/Update operations return DTOs in response

## Benefits of This Implementation

### 1. Security
- Passwords and sensitive data are never exposed to frontend
- User relationships are controlled and limited

### 2. Performance
- Summary DTOs reduce data transfer for list views
- Lazy loading relationships are properly handled
- No unnecessary data is sent over the network

### 3. API Consistency
- Consistent response format across all endpoints
- Predictable data structure for frontend consumption
- Clear separation between internal entities and external API

### 4. Maintainability
- Easy to modify what data is exposed without changing entities
- Clear separation of concerns
- Easier to version APIs in the future

## Usage Examples

### Get User Information
```http
GET /api/usuarios/1
```
**Response**:
```json
{
    "id": 1,
    "username": "admin",
    "nomeCompleto": "Administrator",
    "email": "admin@example.com",
    "ativo": true,
    "dataCriacao": "2025-08-06T11:27:47.884562Z"
}
```

### Get Recipe Summary List
```http
GET /api/receitas
```
**Response**:
```json
[
    {
        "id": 1,
        "nome": "Bolo de Chocolate",
        "descricao": "Delicioso bolo de chocolate",
        "tempoPreparoMin": 60,
        "porcoes": 8,
        "dificuldade": "Médio",
        "usuarioNome": "Administrator",
        "totalIngredientes": 5,
        "totalPassos": 3
    }
]
```

### Get Complete Recipe
```http
GET /api/receitas/1
```
**Response**:
```json
{
    "id": 1,
    "nome": "Bolo de Chocolate",
    "descricao": "Delicioso bolo de chocolate",
    "tempoPreparoMin": 60,
    "porcoes": 8,
    "dificuldade": "Médio",
    "usuario": {
        "id": 1,
        "username": "admin",
        "nomeCompleto": "Administrator",
        "email": "admin@example.com",
        "ativo": true,
        "dataCriacao": "2025-08-06T11:27:47.884562Z"
    },
    "ingredientes": [
        {
            "id": 1,
            "nome": "Farinha de trigo",
            "quantidade": 2.0,
            "unidade": "xícaras"
        }
    ],
    "passos": [
        {
            "id": 1,
            "ordem": 1,
            "descricao": "Misture os ingredientes secos"
        }
    ]
}
```

## Testing
Run the test script to verify DTOs are working correctly:
```bash
./test_dtos.sh
```

This will test all endpoints and verify that:
- Expected fields are present
- Sensitive fields are excluded
- DTOs are properly structured
