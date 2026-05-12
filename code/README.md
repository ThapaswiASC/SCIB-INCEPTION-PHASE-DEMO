# MyProject - Task Management System

## Overview
High-performance task management system supporting creation of at least 10,000 tasks per user.

## Technology Stack
- Java 21
- Spring Boot 3.5.9
- Maven

## Build and Run

### Prerequisites
- JDK 21
- Maven 3.6+

### Build
```bash
mvn clean install
```

### Run
```bash
mvn spring-boot:run
```

### Test
```bash
mvn test
```

## API Documentation
The application exposes REST APIs for task management. See OpenAPI specification in `api/` folder.

## Configuration
Application configuration is available in `src/main/resources/application.properties`.
