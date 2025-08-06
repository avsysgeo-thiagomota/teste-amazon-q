#!/bin/bash

echo "=== Comprehensive Authentication Debug ==="

# Test 1: Health check endpoint
echo "1. Testing health endpoint (should work):"
curl -X GET http://localhost:8080/api/test/health \
  -H "Content-Type: application/json" \
  -w "\nHTTP Status: %{http_code}\n" \
  -s

echo -e "\n"

# Test 2: Public test endpoint
echo "2. Testing public endpoint (should work):"
curl -X GET http://localhost:8080/api/test/public \
  -H "Content-Type: application/json" \
  -w "\nHTTP Status: %{http_code}\n" \
  -s

echo -e "\n"

# Test 3: Auth signup endpoint
echo "3. Testing signup endpoint (should work):"
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "debuguser",
    "password": "debugpass123",
    "nomeCompleto": "Debug User",
    "email": "debug@example.com"
  }' \
  -w "\nHTTP Status: %{http_code}\n" \
  -s

echo -e "\n"

# Test 4: Auth signin endpoint
echo "4. Testing signin endpoint (should work):"
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "username": "debuguser",
    "password": "debugpass123"
  }' \
  -w "\nHTTP Status: %{http_code}\n" \
  -s

echo -e "\n"

# Test 5: Check if server is running
echo "5. Testing if server is responding:"
curl -X GET http://localhost:8080/api/test/health \
  -w "\nHTTP Status: %{http_code}\nTotal Time: %{time_total}s\n" \
  -s

echo -e "\n"

# Test 6: Test without /api prefix
echo "6. Testing without /api prefix:"
curl -X GET http://localhost:8080/test/health \
  -w "\nHTTP Status: %{http_code}\n" \
  -s

echo -e "\nDebug completed!"
