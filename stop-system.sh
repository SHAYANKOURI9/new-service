#!/bin/bash

echo "Stopping Municipal Complaint Management System..."

# Function to stop a service
stop_service() {
    local service_name=$1
    local pid_file="logs/$service_name.pid"
    
    if [ -f "$pid_file" ]; then
        local pid=$(cat "$pid_file")
        if ps -p $pid > /dev/null; then
            echo "Stopping $service_name (PID: $pid)..."
            kill $pid
            rm "$pid_file"
        else
            echo "$service_name is not running"
            rm "$pid_file"
        fi
    else
        echo "$service_name PID file not found"
    fi
}

# Stop services in reverse order
echo "Stopping Gateway Service..."
stop_service "Gateway Service"

echo "Stopping Notification Service..."
stop_service "Notification Service"

echo "Stopping Complaint Service..."
stop_service "Complaint Service"

echo "Stopping Department Service..."
stop_service "Department Service"

echo "Stopping User Service..."
stop_service "User Service"

echo "Stopping Config Server..."
stop_service "Config Server"

echo "Stopping Eureka Server..."
stop_service "Eureka Server"

echo ""
echo "All services have been stopped."
echo "You can check the logs in the 'logs' directory if needed."