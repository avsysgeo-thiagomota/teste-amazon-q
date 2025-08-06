#!/bin/bash

echo "Starting Recipe Management Frontend..."
echo "Make sure the backend API is running at http://localhost:8080/api"
echo ""

# Check if Node.js is available
if ! command -v node &> /dev/null; then
    echo "Node.js is not installed or not in PATH"
    exit 1
fi

# Check if npm is available
if ! command -v npm &> /dev/null; then
    echo "npm is not installed or not in PATH"
    exit 1
fi

# Install dependencies if node_modules doesn't exist
if [ ! -d "node_modules" ]; then
    echo "Installing dependencies..."
    npm install
    
    if [ $? -ne 0 ]; then
        echo "Failed to install dependencies!"
        exit 1
    fi
fi

echo ""
echo "Starting Angular development server..."
echo "Application will be available at: http://localhost:4200"
echo "Default login: admin / admin123"
echo ""
echo "Press Ctrl+C to stop the server"
echo ""

npm start
