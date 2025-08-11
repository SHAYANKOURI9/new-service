# Municipal Complaint Management System - Setup Guide

## Prerequisites

Before starting the system, ensure you have the following installed:

- **Java 17** or higher
- **Maven 3.6** or higher
- **MySQL 8.0** or higher
- **Docker** (optional, for containerized deployment)

## Quick Start

### 1. Database Setup

First, set up the MySQL database:

```bash
# Start MySQL (if using Docker)
docker run --name mysql \
  -e MYSQL_ROOT_PASSWORD=rootpassword \
  -e MYSQL_DATABASE=complaint_management \
  -e MYSQL_USER=complaint_user \
  -e MYSQL_PASSWORD=complaint_pass \
  -p 3306:3306 \
  -d mysql:8.0

# Or if you have MySQL installed locally, create the database:
mysql -u root -p
CREATE DATABASE complaint_management;
CREATE USER 'complaint_user'@'%' IDENTIFIED BY 'complaint_pass';
GRANT ALL PRIVILEGES ON complaint_management.* TO 'complaint_user'@'%';
FLUSH PRIVILEGES;
EXIT;
```

### 2. Build All Services

Build all microservices:

```bash
# Build shared library first
cd shared-lib
mvn clean install

# Build all services
cd ../eureka-server && mvn clean package
cd ../config-server && mvn clean package
cd ../user-service && mvn clean package
cd ../complaint-service && mvn clean package
cd ../department-service && mvn clean package
cd ../notification-service && mvn clean package
cd ../gateway-service && mvn clean package
cd ..
```

### 3. Start the System

Use the provided startup script:

```bash
./start-system.sh
```

This will start all services in the correct order:
1. Eureka Server (Port 8761)
2. Config Server (Port 8888)
3. User Service (Port 8081)
4. Department Service (Port 8083)
5. Complaint Service (Port 8082)
6. Notification Service (Port 8084)
7. Gateway Service (Port 8080)

### 4. Verify System Status

Check if all services are running:

```bash
# Check Eureka Dashboard
curl http://localhost:8761

# Check Gateway
curl http://localhost:8080/actuator/health
```

## Manual Startup (Alternative)

If you prefer to start services manually:

```bash
# Terminal 1 - Eureka Server
cd eureka-server && mvn spring-boot:run

# Terminal 2 - Config Server
cd config-server && mvn spring-boot:run

# Terminal 3 - User Service
cd user-service && mvn spring-boot:run

# Terminal 4 - Department Service
cd department-service && mvn spring-boot:run

# Terminal 5 - Complaint Service
cd complaint-service && mvn spring-boot:run

# Terminal 6 - Notification Service
cd notification-service && mvn spring-boot:run

# Terminal 7 - Gateway Service
cd gateway-service && mvn spring-boot:run
```

## Docker Deployment

To deploy using Docker:

```bash
# Build and run all services with Docker Compose
docker-compose up -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f
```

## API Testing

### 1. Register a Citizen

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "username": "johndoe",
    "password": "password123",
    "role": "CITIZEN"
  }'
```

### 2. Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "password": "password123"
  }'
```

### 3. File a Complaint

```bash
# Use the token from login response
curl -X POST http://localhost:8080/api/complaints \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "category": "ROADS",
    "description": "Pothole on Main Street",
    "location": "Main Street, Downtown",
    "userId": 1
  }'
```

### 4. Create a Department (Admin only)

```bash
curl -X POST http://localhost:8080/api/departments \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ADMIN_JWT_TOKEN" \
  -d '{
    "name": "Roads Department",
    "description": "Handles road maintenance and repairs",
    "contactEmail": "roads@municipality.gov",
    "contactPhone": "+1-555-0123"
  }'
```

### 5. Assign Complaint to Department

```bash
curl -X PUT "http://localhost:8080/api/complaints/1/assign?departmentId=1&staffId=2" \
  -H "Authorization: Bearer STAFF_JWT_TOKEN"
```

## System Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Client Apps   │    │   Web Browser   │    │   Mobile Apps   │
└─────────┬───────┘    └─────────┬───────┘    └─────────┬───────┘
          │                      │                      │
          └──────────────────────┼──────────────────────┘
                                 │
                    ┌─────────────▼─────────────┐
                    │    API Gateway (8080)     │
                    │   Spring Cloud Gateway    │
                    └─────────────┬─────────────┘
                                  │
        ┌─────────────────────────┼─────────────────────────┐
        │                         │                         │
┌───────▼────────┐    ┌───────────▼──────────┐    ┌────────▼────────┐
│  User Service  │    │ Complaint Service    │    │Department Service│
│     (8081)     │    │      (8082)          │    │     (8083)      │
└───────┬────────┘    └───────────┬──────────┘    └────────┬────────┘
        │                         │                        │
        └─────────────────────────┼────────────────────────┘
                                  │
                    ┌─────────────▼─────────────┐
                    │ Notification Service      │
                    │        (8084)             │
                    └─────────────┬─────────────┘
                                  │
                    ┌─────────────▼─────────────┐
                    │      MySQL Database       │
                    │      (Port 3306)          │
                    └───────────────────────────┘
```

## Service Details

### 1. Eureka Server (Port 8761)
- Service discovery and registration
- Dashboard: http://localhost:8761

### 2. Config Server (Port 8888)
- Centralized configuration management
- Stores configuration for all services

### 3. User Service (Port 8081)
- User registration and authentication
- JWT token generation and validation
- Role-based access control

### 4. Complaint Service (Port 8082)
- Complaint filing and management
- Status tracking and updates
- Comment system

### 5. Department Service (Port 8083)
- Department management
- Staff assignment
- Department-staff relationships

### 6. Notification Service (Port 8084)
- Email notifications
- Status change notifications
- Notification history

### 7. Gateway Service (Port 8080)
- API routing and load balancing
- Security enforcement
- Rate limiting

## User Roles

1. **CITIZEN**
   - File complaints
   - View own complaints
   - Add comments to own complaints

2. **STAFF**
   - Process assigned complaints
   - Update complaint status
   - Add comments to assigned complaints

3. **ADMIN**
   - Full system access
   - Manage departments and staff
   - View all complaints and users

## Troubleshooting

### Common Issues

1. **Service won't start**
   - Check if MySQL is running
   - Verify database credentials
   - Check port availability

2. **Service discovery issues**
   - Ensure Eureka Server is running first
   - Check network connectivity
   - Verify service registration

3. **JWT authentication issues**
   - Check JWT secret configuration
   - Verify token expiration
   - Ensure proper Authorization header

### Logs

Check service logs in the `logs` directory:
```bash
tail -f logs/User\ Service.log
tail -f logs/Complaint\ Service.log
```

### Health Checks

```bash
# Check individual service health
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
curl http://localhost:8084/actuator/health
curl http://localhost:8080/actuator/health
```

## Stopping the System

To stop all services:

```bash
./stop-system.sh
```

Or manually stop each service with Ctrl+C in their respective terminals.

## Configuration

### Environment Variables

You can override default configurations using environment variables:

```bash
export SPRING_PROFILES_ACTIVE=docker
export MYSQL_HOST=your-mysql-host
export MYSQL_PORT=3306
export MYSQL_USERNAME=your-username
export MYSQL_PASSWORD=your-password
```

### Email Configuration

For the notification service to work, update the email configuration in `notification-service/src/main/resources/application.yml`:

```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com
    password: your-app-password
```

## Security

- All passwords are encrypted using BCrypt
- JWT tokens are used for authentication
- Role-based access control is implemented
- Input validation is enforced at all endpoints

## Performance

- Services are stateless and can be scaled horizontally
- Database connections are pooled
- JWT tokens reduce database queries for authentication
- Service discovery enables load balancing

## Monitoring

- Eureka Dashboard for service discovery monitoring
- Actuator endpoints for health checks
- Application logs for debugging
- Database monitoring for performance

## Support

For issues and questions:
1. Check the logs in the `logs` directory
2. Verify all prerequisites are met
3. Ensure proper startup order
4. Check network connectivity between services