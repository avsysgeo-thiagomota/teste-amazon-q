#!/bin/bash

echo "=== Server Status Check ==="

# Check if port 8080 is listening
echo "1. Checking if port 8080 is listening:"
if command -v netstat >/dev/null 2>&1; then
    netstat -tuln | grep :8080 || echo "   Port 8080 is not listening"
elif command -v ss >/dev/null 2>&1; then
    ss -tuln | grep :8080 || echo "   Port 8080 is not listening"
else
    echo "   netstat/ss not available, trying curl test..."
fi

echo -e "\n2. Testing basic connectivity:"
curl -v --connect-timeout 5 --max-time 10 http://localhost:8080 2>&1 | head -15

echo -e "\n3. Checking for Java processes:"
ps aux | grep -i java | grep -v grep || echo "   No Java processes found"

echo -e "\n4. Checking current directory and Spring Boot files:"
echo "   Current directory: $(pwd)"
echo "   Looking for Spring Boot main class:"
find . -name "*.java" -path "*/src/main/java/*" -exec grep -l "@SpringBootApplication" {} \; 2>/dev/null || echo "   No @SpringBootApplication found"

echo -e "\n5. Checking for build files:"
if [ -f "pom.xml" ]; then
    echo "   ✅ Maven project (pom.xml found)"
elif [ -f "build.gradle" ]; then
    echo "   ✅ Gradle project (build.gradle found)"
else
    echo "   ❌ No build files found in current directory"
fi

echo -e "\n6. Checking for compiled JAR files:"
find . -name "*.jar" -path "*/target/*" 2>/dev/null || echo "   No JAR files found in target directory"

echo -e "\n7. Testing different ports:"
for port in 8080 8081 8082 9090; do
    echo -n "   Port $port: "
    curl -s --connect-timeout 2 --max-time 3 http://localhost:$port >/dev/null 2>&1 && echo "RESPONDING" || echo "NOT RESPONDING"
done

echo -e "\n8. How to start the Spring Boot application:"
echo "   If using Maven: mvn spring-boot:run"
echo "   If using Gradle: ./gradlew bootRun"
echo "   If you have a JAR: java -jar target/your-app.jar"

echo -e "\n9. Common issues to check:"
echo "   - Is the application running? (check Java processes above)"
echo "   - Database connection issues? (check application logs)"
echo "   - Port already in use? (check port status above)"
echo "   - Configuration errors? (check application.yml)"

echo -e "\nServer status check completed!"
