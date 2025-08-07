#!/bin/bash

echo "=== Testing Recipe MultipleBagFetchException Fix ==="

# First, let's create a user and get a token
echo "1. Creating test user and getting token..."
SIGNUP_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser_recipe",
    "password": "testpass123",
    "nomeCompleto": "Test Recipe User",
    "email": "testrecipe@example.com"
  }')

echo "Signup response: $SIGNUP_RESPONSE"

# Login to get token
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser_recipe",
    "password": "testpass123"
  }')

TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
    echo "❌ Failed to get token. Login response: $LOGIN_RESPONSE"
    exit 1
fi

echo "✅ Token obtained: ${TOKEN:0:50}..."

echo -e "\n2. Creating a test recipe with ingredients and steps..."
CREATE_RESPONSE=$(curl -s -X POST http://localhost:8080/api/receitas \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "nome": "Test Recipe with Details",
    "descricao": "A test recipe to verify the MultipleBagFetchException fix",
    "tempoPreparoMin": 30,
    "porcoes": 4,
    "dificuldade": "Easy",
    "ingredientes": [
      {
        "nome": "Test Ingredient 1",
        "quantidade": 1.0,
        "unidade": "cup"
      },
      {
        "nome": "Test Ingredient 2",
        "quantidade": 2.0,
        "unidade": "pieces"
      }
    ],
    "passos": [
      {
        "ordem": 1,
        "descricao": "First step of the recipe"
      },
      {
        "ordem": 2,
        "descricao": "Second step of the recipe"
      }
    ]
  }')

echo "Create response: $CREATE_RESPONSE"

# Extract recipe ID
RECIPE_ID=$(echo "$CREATE_RESPONSE" | grep -o '"id":[0-9]*' | cut -d':' -f2)

if [ -z "$RECIPE_ID" ]; then
    echo "❌ Failed to create recipe or extract ID"
    exit 1
fi

echo "✅ Recipe created with ID: $RECIPE_ID"

echo -e "\n3. Testing GET recipe by ID (this should work now)..."
GET_RESPONSE=$(curl -s -X GET "http://localhost:8080/api/receitas/$RECIPE_ID" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nHTTP_STATUS:%{http_code}")

echo "$GET_RESPONSE"

# Check if we got a 200 status
if echo "$GET_RESPONSE" | grep -q "HTTP_STATUS:200"; then
    echo "✅ SUCCESS: Recipe retrieved without MultipleBagFetchException!"
else
    echo "❌ FAILED: Still getting error when retrieving recipe"
fi

echo -e "\n4. Testing GET all recipes with details..."
GET_ALL_RESPONSE=$(curl -s -X GET "http://localhost:8080/api/receitas?withDetails=true" \
  -H "Authorization: Bearer $TOKEN" \
  -w "\nHTTP_STATUS:%{http_code}")

echo "$GET_ALL_RESPONSE"

if echo "$GET_ALL_RESPONSE" | grep -q "HTTP_STATUS:200"; then
    echo "✅ SUCCESS: All recipes retrieved with details!"
else
    echo "❌ FAILED: Error when retrieving all recipes with details"
fi

echo -e "\nTest completed!"
