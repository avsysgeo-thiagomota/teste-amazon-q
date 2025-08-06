#!/bin/bash

echo "=== Testing Authentication Fix ==="

# Test the corrected endpoint
echo "Testing login with correct path: /api/auth/signin"
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "username": "Brown",
    "password": "Brown123"
  }' \
  -w "\nHTTP Status: %{http_code}\n" \
  -s

echo -e "\n"

# Test signup as well
echo "Testing signup with correct path: /api/auth/signup"
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "Brown",
    "password": "Brown123",
    "nomeCompleto": "Pedro Paulo Soares Pereira",
    "email": "manobrown@rapper.com.br"
  }' \
  -w "\nHTTP Status: %{http_code}\n" \
  -s

echo -e "\nTest completed!"
