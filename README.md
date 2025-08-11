# Municipal Complaint Management System

A microservices-based online complaint management system for municipalities that supports citizen complaints on public issues like water, sanitation, and roads.

## Architecture Overview

The system consists of 5 microservices:

1. **Gateway Service** - API Gateway using Spring Cloud Gateway
2. **User Service** - User registration, authentication, and role management
3. **Complaint Service** - Complaint filing, tracking, and management
4. **Department Service** - Department management and staff assignment
5. **Notification Service** - Email and SMS notifications
6. **Eureka Server** - Service discovery
7. **Config Server** - Centralized configuration management

## Technology Stack

- **Spring Boot** - Microservices framework
- **Spring Cloud** - Service discovery, configuration, and gateway
- **Spring Security** - Authentication and authorization
- **JWT** - Token-based authentication
- **MySQL** - Database
- **Maven** - Build tool
- **Docker** - Containerization

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+
- MySQL 8.0+
- Docker (optional)

### Running the Application

1. **Start MySQL Database**
```bash
# Create database
mysql -u root -p
CREATE DATABASE complaint_management;
```

2. **Start Eureka Server**
```bash
cd eureka-server
mvn spring-boot:run
```

3. **Start Config Server**
```bash
cd config-server
mvn spring-boot:run
```

4. **Start All Microservices**
```bash
# In separate terminals
cd user-service && mvn spring-boot:run
cd complaint-service && mvn spring-boot:run
cd department-service && mvn spring-boot:run
cd notification-service && mvn spring-boot:run
cd gateway-service && mvn spring-boot:run
```

### Access Points

- **Eureka Dashboard**: http://localhost:8761
- **API Gateway**: http://localhost:8080
- **User Service**: http://localhost:8081
- **Complaint Service**: http://localhost:8082
- **Department Service**: http://localhost:8083
- **Notification Service**: http://localhost:8084

## API Documentation

Once the services are running, you can access the Swagger documentation at:
- Gateway API Docs: http://localhost:8080/swagger-ui.html

## User Roles

1. **CITIZEN** - Can file complaints and track their own complaints
2. **STAFF** - Can process assigned complaints and update status
3. **ADMIN** - Full system management capabilities

## Sample API Usage

### Register a Citizen
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "username": "johndoe",
    "password": "password123",
    "role": "CITIZEN"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "password": "password123"
  }'
```

### File a Complaint
```bash
curl -X POST http://localhost:8080/api/complaints \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "category": "ROADS",
    "description": "Pothole on Main Street",
    "location": "Main Street, Downtown"
  }'
```

## Testing

Run tests for each service:
```bash
cd [service-name]
mvn test
```

## Docker Deployment

Build and run with Docker:
```bash
docker-compose up -d
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request