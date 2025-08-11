#!/bin/bash

echo "Starting Municipal Complaint Management System..."

# Check if MySQL is running
echo "Checking MySQL connection..."
if ! mysql -u complaint_user -pcomplaint_pass -h localhost -e "SELECT 1;" > /dev/null 2>&1; then
    echo "MySQL is not running or not accessible. Please start MySQL first."
    echo "You can use Docker to start MySQL:"
    echo "docker run --name mysql -e MYSQL_ROOT_PASSWORD=rootpassword -e MYSQL_DATABASE=complaint_management -e MYSQL_USER=complaint_user -e MYSQL_PASSWORD=complaint_pass -p 3306:3306 -d mysql:8.0"
    exit 1
fi

echo "MySQL is running. Starting services..."

# Function to start a service
start_service() {
    local service_name=$1
    local service_dir=$2
    local port=$3
    
    echo "Starting $service_name..."
    cd "$service_dir"
    
    # Check if service is already running
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null ; then
        echo "$service_name is already running on port $port"
    else
        # Start the service in background
        nohup mvn spring-boot:run > "../logs/$service_name.log" 2>&1 &
        echo "$service_name started. PID: $!"
        echo $! > "../logs/$service_name.pid"
    fi
    
    cd ..
}

# Create logs directory
mkdir -p logs

# Start services in order
echo "Starting Eureka Server..."
start_service "Eureka Server" "eureka-server" 8761

echo "Waiting for Eureka Server to start..."
sleep 10

echo "Starting Config Server..."
start_service "Config Server" "config-server" 8888

echo "Waiting for Config Server to start..."
sleep 10

echo "Starting User Service..."
start_service "User Service" "user-service" 8081

echo "Waiting for User Service to start..."
sleep 15

echo "Starting Department Service..."
start_service "Department Service" "department-service" 8083

echo "Waiting for Department Service to start..."
sleep 10

echo "Starting Complaint Service..."
start_service "Complaint Service" "complaint-service" 8082

echo "Waiting for Complaint Service to start..."
sleep 10

echo "Starting Notification Service..."
start_service "Notification Service" "notification-service" 8084

echo "Waiting for Notification Service to start..."
sleep 10

echo "Starting Gateway Service..."
start_service "Gateway Service" "gateway-service" 8080

echo "Waiting for Gateway Service to start..."
sleep 15

echo ""
echo "=========================================="
echo "Municipal Complaint Management System"
echo "=========================================="
echo ""
echo "Services are starting up. Please wait a few minutes for all services to be ready."
echo ""
echo "Access Points:"
echo "- Eureka Dashboard: http://localhost:8761"
echo "- API Gateway: http://localhost:8080"
echo "- User Service: http://localhost:8081"
echo "- Complaint Service: http://localhost:8082"
echo "- Department Service: http://localhost:8083"
echo "- Notification Service: http://localhost:8084"
echo ""
echo "Sample API Usage:"
echo ""
echo "1. Register a citizen:"
echo "curl -X POST http://localhost:8080/api/auth/register \\"
echo "  -H \"Content-Type: application/json\" \\"
echo "  -d '{\"name\":\"John Doe\",\"email\":\"john@example.com\",\"username\":\"johndoe\",\"password\":\"password123\",\"role\":\"CITIZEN\"}'"
echo ""
echo "2. Login:"
echo "curl -X POST http://localhost:8080/api/auth/login \\"
echo "  -H \"Content-Type: application/json\" \\"
echo "  -d '{\"username\":\"johndoe\",\"password\":\"password123\"}'"
echo ""
echo "3. File a complaint (use the token from login):"
echo "curl -X POST http://localhost:8080/api/complaints \\"
echo "  -H \"Content-Type: application/json\" \\"
echo "  -H \"Authorization: Bearer YOUR_JWT_TOKEN\" \\"
echo "  -d '{\"category\":\"ROADS\",\"description\":\"Pothole on Main Street\",\"location\":\"Main Street, Downtown\",\"userId\":1}'"
echo ""
echo "To stop all services, run: ./stop-system.sh"
echo ""