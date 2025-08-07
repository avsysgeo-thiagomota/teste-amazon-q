#!/bin/bash

echo "=== Spring Boot Server Startup Guide ==="

# Check if we're in the right directory
if [ ! -f "pom.xml" ] && [ ! -f "build.gradle" ]; then
    echo "❌ No pom.xml or build.gradle found in current directory"
    echo "Please navigate to your Spring Boot project directory first"
    echo "Example: cd /mnt/c/Users/Thiago\ Mota/Documents/GitHub/teste-amazon-q/backend"
    exit 1
fi

echo "✅ Found build configuration file"

# Check for Maven
if [ -f "pom.xml" ]; then
    echo -e "\n🚀 Starting Spring Boot with Maven..."
    echo "Running: mvn spring-boot:run"
    echo "This will:"
    echo "  1. Compile your Java code"
    echo "  2. Download dependencies"
    echo "  3. Start the Spring Boot application on port 8080"
    echo "  4. Show startup logs"
    echo ""
    echo "Press Ctrl+C to stop the server when running"
    echo ""
    echo "Starting in 3 seconds..."
    sleep 3
    
    mvn spring-boot:run
    
elif [ -f "build.gradle" ]; then
    echo -e "\n🚀 Starting Spring Boot with Gradle..."
    echo "Running: ./gradlew bootRun"
    echo "This will:"
    echo "  1. Compile your Java code"
    echo "  2. Download dependencies"
    echo "  3. Start the Spring Boot application on port 8080"
    echo "  4. Show startup logs"
    echo ""
    echo "Press Ctrl+C to stop the server when running"
    echo ""
    echo "Starting in 3 seconds..."
    sleep 3
    
    ./gradlew bootRun
fi
