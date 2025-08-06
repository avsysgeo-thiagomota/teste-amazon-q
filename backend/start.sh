#!/bin/bash

echo "Starting Recipe Management API..."
echo "Make sure PostgreSQL is running and the database 'receitas_db' exists"
echo ""

# Check if Maven is available
if ! command -v mvn &> /dev/null; then
    echo "Maven is not installed or not in PATH"
    exit 1
fi

# Clean and compile
echo "Cleaning and compiling..."
mvn clean compile

if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    exit 1
fi

echo ""
echo "Starting Spring Boot application..."
echo "API will be available at: http://localhost:8080/api"
echo "Default login: admin / admin123"
echo ""
echo "Press Ctrl+C to stop the server"
echo ""

mvn spring-boot:run
