#!/bin/bash

echo "=== Server Connectivity Check ==="

# Test 1: Check if port 8080 is open
echo "1. Checking if port 8080 is listening:"
netstat -an | grep :8080 || echo "Port 8080 is not listening"

echo -e "\n"

# Test 2: Test basic connectivity
echo "2. Testing basic connectivity to localhost:8080:"
curl -v http://localhost:8080 2>&1 | head -10

echo -e "\n"

# Test 3: Test with different endpoints
echo "3. Testing different endpoints:"

echo "3a. Testing root endpoint:"
curl -X GET http://localhost:8080 \
  -w "\nHTTP Status: %{http_code}\nTime: %{time_total}s\n" \
  -s --max-time 5

echo -e "\n"

echo "3b. Testing /api endpoint:"
curl -X GET http://localhost:8080/api \
  -w "\nHTTP Status: %{http_code}\nTime: %{time_total}s\n" \
  -s --max-time 5

echo -e "\n"

echo "3c. Testing actuator health (if available):"
curl -X GET http://localhost:8080/api/actuator/health \
  -w "\nHTTP Status: %{http_code}\nTime: %{time_total}s\n" \
  -s --max-time 5

echo -e "\n"

# Test 4: Check if Java process is running
echo "4. Checking for Java processes:"
ps aux | grep java | grep -v grep || echo "No Java processes found"

echo -e "\n"

# Test 5: Check if Spring Boot is running on different port
echo "5. Checking for other common ports:"
for port in 8081 8082 9090 9091; do
    echo "Testing port $port:"
    curl -X GET http://localhost:$port \
      -w "HTTP Status: %{http_code}\n" \
      -s --max-time 2 --connect-timeout 2
done

echo -e "\nServer check completed!"
