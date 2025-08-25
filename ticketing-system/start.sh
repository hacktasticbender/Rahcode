#!/bin/bash

echo "🚀 Starting TicketPro Ticketing System..."

# Check if Docker is available
if command -v docker &> /dev/null && command -v docker-compose &> /dev/null; then
    echo "📦 Starting with Docker Compose..."
    docker-compose up -d
    echo "✅ Application started!"
    echo "📊 Frontend: http://localhost:3000"
    echo "🔧 Backend API: http://localhost:8080/api"
    echo "🗄️ Database: PostgreSQL on localhost:5432"
else
    echo "⚠️  Docker not found. Please install Docker and Docker Compose or run manually:"
    echo ""
    echo "📋 Manual Setup Instructions:"
    echo "1. Setup PostgreSQL database (see README.md)"
    echo "2. Backend: cd backend && ./gradlew bootRun"
    echo "3. Frontend: cd frontend && npm install && npm run dev"
fi