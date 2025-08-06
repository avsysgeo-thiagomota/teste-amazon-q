#!/bin/bash

echo "=== Spring Boot Application Check ==="

# Check if we're in the right directory
echo "1. Current directory: $(pwd)"
echo "2. Looking for Spring Boot application..."

# Look for the main application class
find . -name "*.java" -path "*/src/main/java/*" -exec grep -l "@SpringBootApplication" {} \; 2>/dev/null

echo -e "\n3. Looking for application.yml or application.properties:"
find . -name "application.yml" -o -name "application.properties" 2>/dev/null

echo -e "\n4. Checking if Maven/Gradle build files exist:"
ls -la pom.xml build.gradle 2>/dev/null || echo "No build files found in current directory"

echo -e "\n5. To start the Spring Boot application, try one of these commands:"
echo "   - If using Maven: mvn spring-boot:run"
echo "   - If using Gradle: ./gradlew bootRun"
echo "   - If you have a JAR file: java -jar target/*.jar"

echo -e "\n6. Check application logs for startup errors:"
echo "   - Look for 'Started [ApplicationName]' message"
echo "   - Check for port binding errors"
echo "   - Verify database connection"

echo -e "\n7. Common startup issues to check:"
echo "   - Database connection (PostgreSQL running?)"
echo "   - Port 8080 already in use"
echo "   - Missing dependencies"
echo "   - Configuration errors in application.yml"

echo -e "\nApplication check completed!"
