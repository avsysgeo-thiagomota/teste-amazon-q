#!/bin/bash

echo "=== Fixed Authentication Test ==="

# Test 1: Signup with correct headers
echo "1. Testing signup with correct Content-Type:"
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "username": "testuser123",
    "password": "testpass123",
    "nomeCompleto": "Test User",
    "email": "test123@example.com"
  }' \
  -w "\nHTTP Status: %{http_code}\n" \
  -v

echo -e "\n"

# Test 2: Signin with correct headers
echo "2. Testing signin with correct Content-Type:"
RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "username": "testuser123",
    "password": "testpass123"
  }' \
  -w "\nHTTP_STATUS:%{http_code}")

echo "$RESPONSE"

# Extract token if login was successful
TOKEN=$(echo "$RESPONSE" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ ! -z "$TOKEN" ]; then
    echo -e "\n3. Login successful! Token: ${TOKEN:0:50}..."
    
    # Test protected endpoint
    echo -e "\n4. Testing protected endpoint with token:"
    curl -X GET http://localhost:8080/api/receitas \
      -H "Authorization: Bearer $TOKEN" \
      -H "Accept: application/json" \
      -w "\nHTTP Status: %{http_code}\n"
else
    echo -e "\n3. Login failed or no token received"
fi

echo -e "\nTest completed!"
