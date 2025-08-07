#!/bin/bash

echo "Testing DTOs Implementation..."
echo "=============================="

# Start the application in background
echo "Starting application..."
mvn spring-boot:run > app_test.log 2>&1 &
APP_PID=$!

# Wait for application to start
echo "Waiting for application to start..."
sleep 20

# Function to test API endpoint
test_endpoint() {
    local endpoint=$1
    local description=$2
    local expected_fields=$3
    local unexpected_fields=$4
    
    echo ""
    echo "Testing: $description"
    echo "Endpoint: $endpoint"
    
    response=$(curl -s "$endpoint")
    
    if [ $? -eq 0 ]; then
        echo "✅ Request successful"
        
        # Check for expected fields
        for field in $expected_fields; do
            if echo "$response" | grep -q "\"$field\""; then
                echo "✅ Field '$field' present"
            else
                echo "❌ Field '$field' missing"
            fi
        done
        
        # Check for unexpected fields
        for field in $unexpected_fields; do
            if echo "$response" | grep -q "\"$field\""; then
                echo "❌ Unexpected field '$field' present"
            else
                echo "✅ Field '$field' correctly hidden"
            fi
        done
        
        echo "Response preview: $(echo "$response" | head -c 200)..."
    else
        echo "❌ Request failed"
    fi
}

# Test Usuario endpoints
echo ""
echo "=== TESTING USUARIO DTOs ==="

# Test get user by ID
test_endpoint "http://localhost:8080/api/usuarios/1" \
    "Get User by ID" \
    "id username nomeCompleto email ativo dataCriacao" \
    "password receitas enabled accountNonExpired credentialsNonExpired accountNonLocked authorities"

# Test get all users
test_endpoint "http://localhost:8080/api/usuarios" \
    "Get All Users" \
    "id username nomeCompleto email ativo dataCriacao" \
    "password receitas enabled accountNonExpired credentialsNonExpired accountNonLocked authorities"

# Test Receita endpoints (these might require authentication, so they may fail)
echo ""
echo "=== TESTING RECEITA DTOs ==="

# Test get all receitas (might fail due to auth)
test_endpoint "http://localhost:8080/api/receitas" \
    "Get All Receitas (Summary)" \
    "id nome descricao tempoPreparoMin porcoes dificuldade usuarioNome totalIngredientes totalPassos" \
    "usuario ingredientes passos"

# Test get receita by ID (might fail due to auth)
test_endpoint "http://localhost:8080/api/receitas/1" \
    "Get Receita by ID (Full)" \
    "id nome descricao tempoPreparoMin porcoes dificuldade usuario ingredientes passos" \
    ""

# Stop the application
echo ""
echo "Stopping application..."
kill $APP_PID
wait $APP_PID 2>/dev/null

echo ""
echo "Test completed."
echo ""
echo "Note: Some endpoints may fail due to authentication requirements."
echo "This is expected behavior for secured endpoints."
