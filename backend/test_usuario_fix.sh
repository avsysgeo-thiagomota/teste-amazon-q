#!/bin/bash

echo "Testing Usuario API fix..."
echo "=========================="

# Start the application in background
echo "Starting application..."
mvn spring-boot:run > app_test.log 2>&1 &
APP_PID=$!

# Wait for application to start
echo "Waiting for application to start..."
sleep 15

# Test the API endpoint
echo "Testing GET /api/usuarios/1..."
response=$(curl -s http://localhost:8080/api/usuarios/1)

# Check if receitas field is present in response
if echo "$response" | grep -q '"receitas"'; then
    echo "❌ FAILED: receitas field is still present in response"
    echo "Response: $response"
else
    echo "✅ SUCCESS: receitas field is not present in response"
    echo "Response: $response"
fi

# Stop the application
echo "Stopping application..."
kill $APP_PID
wait $APP_PID 2>/dev/null

echo "Test completed."
