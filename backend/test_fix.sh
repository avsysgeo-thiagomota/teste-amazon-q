#!/bin/bash

echo "Starting the application in background..."
mvn spring-boot:run > app.log 2>&1 &
APP_PID=$!

echo "Waiting for application to start..."
sleep 45

echo "Testing the API endpoint that was causing the MultipleBagFetchException..."

# First, let's try to login to get a token
echo "Attempting to login..."
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}')

echo "Login response: $LOGIN_RESPONSE"

# Extract token from response (assuming it's in JSON format)
TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)

if [ -n "$TOKEN" ]; then
    echo "Token obtained: ${TOKEN:0:20}..."
    
    # Test the endpoint that was causing the error
    echo "Testing GET /api/receitas/1 (this was causing MultipleBagFetchException)..."
    RECEITA_RESPONSE=$(curl -s -X GET http://localhost:8080/api/receitas/1 \
      -H "Authorization: Bearer $TOKEN" \
      -H "Content-Type: application/json")
    
    echo "Recipe response: $RECEITA_RESPONSE"
    
    # Check if the response contains error
    if echo "$RECEITA_RESPONSE" | grep -q "MultipleBagFetchException"; then
        echo "❌ ERROR: MultipleBagFetchException still occurs!"
    elif echo "$RECEITA_RESPONSE" | grep -q "error"; then
        echo "⚠️  WARNING: Some error occurred, but not MultipleBagFetchException"
        echo "Response: $RECEITA_RESPONSE"
    else
        echo "✅ SUCCESS: No MultipleBagFetchException! The fix works!"
    fi
else
    echo "❌ Could not obtain authentication token. Check if the application is running and credentials are correct."
fi

echo "Stopping the application..."
kill $APP_PID
wait $APP_PID 2>/dev/null

echo "Test completed."
