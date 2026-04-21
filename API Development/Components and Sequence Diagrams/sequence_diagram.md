# Sequence Diagram - Task Creation API Endpoint

## Overview
This sequence diagram illustrates the complete flow for creating a task through the Task Management API endpoint, including authentication, validation, business logic processing, and data persistence.

## Sequence Diagram

```mermaid
sequenceDiagram
    participant Client as API Consumer<br/>(Mobile/Web App)
    participant Gateway as API Gateway<br/>(Load Balancer)
    participant Auth as Authentication<br/>Service
    participant Controller as Task Controller<br/>(HTTP Layer)
    participant DTO as CreateTaskDto<br/>(Validation Layer)
    participant Service as Task Service<br/>(Business Logic)
    participant Cache as Redis Cache<br/>(Session/Data)
    participant DB as Task Database<br/>(PostgreSQL)
    participant Audit as Audit Service<br/>(Compliance)
    participant Notif as Notification<br/>Service

    Note over Client, Notif: Task Creation Flow - POST /api/tasks
    
    %% Authentication Phase
    Client->>+Gateway: POST /api/tasks<br/>Authorization: Bearer <jwt_token><br/>Content-Type: application/json
    Gateway->>Gateway: Rate Limiting Check<br/>(100 req/min per user)
    Gateway->>+Auth: Validate JWT Token<br/>X-Request-ID: correlation-id
    
    alt Valid Token
        Auth-->>-Gateway: Token Valid<br/>User: {id, roles, permissions}
        Gateway->>+Controller: Forward Request<br/>+ User Context
    else Invalid Token
        Auth-->>Gateway: 401 Unauthorized
        Gateway-->>Client: 401 Unauthorized<br/>{error: "UNAUTHORIZED"}
    end
    
    %% Request Validation Phase
    Controller->>+DTO: Validate Request Body<br/>{title, description, priority, etc.}
    DTO->>DTO: Field Validation<br/>- Title: required, max 255 chars<br/>- Priority: enum validation<br/>- DueDate: future date check
    
    alt Validation Success
        DTO-->>-Controller: Validation Passed<br/>Sanitized Data
    else Validation Failure
        DTO-->>Controller: 400 Bad Request<br/>Validation Errors
        Controller-->>Gateway: 400 Bad Request<br/>{error: "VALIDATION_ERROR"}
        Gateway-->>-Client: 400 Bad Request
    end
    
    %% Authorization Check
    Controller->>Controller: Check User Permissions<br/>Can user create tasks?
    
    alt Insufficient Permissions
        Controller-->>Gateway: 403 Forbidden<br/>{error: "FORBIDDEN"}
        Gateway-->>Client: 403 Forbidden
    end
    
    %% Business Logic Processing
    Controller->>+Service: createTask(createTaskDto, userContext)
    
    %% Cache Check for User/Assignee Validation
    Service->>+Cache: Check User Permissions<br/>Key: user:{userId}:permissions
    
    alt Cache Hit
        Cache-->>Service: User Permissions Data
    else Cache Miss
        Cache-->>Service: Cache Miss
        Service->>+Auth: Get User Permissions
        Auth-->>-Service: User Permissions
        Service->>Cache: Cache Permissions<br/>TTL: 5 minutes
    end
    
    Cache-->>-Service: Permission Data Retrieved
    
    %% Assignee Validation (if assigneeId provided)
    opt Assignee ID Provided
        Service->>+Cache: Validate Assignee<br/>Key: user:{assigneeId}:active
        
        alt Assignee Cache Hit
            Cache-->>Service: Assignee Status: Active
        else Assignee Cache Miss
            Cache-->>Service: Cache Miss
            Service->>+Auth: Validate Assignee Status
            Auth-->>-Service: Assignee Status
            Service->>Cache: Cache Assignee Status<br/>TTL: 1 minute
        end
        
        Cache-->>-Service: Assignee Validation Complete
        
        alt Inactive Assignee
            Service-->>Controller: 422 Unprocessable Entity<br/>{error: "INVALID_ASSIGNEE"}
            Controller-->>Gateway: 422 Unprocessable Entity
            Gateway-->>Client: 422 Unprocessable Entity
        end
    end
    
    %% Business Rule Validation
    Service->>Service: Apply Business Rules<br/>- Unique title per user<br/>- Due date constraints<br/>- Priority validation
    
    %% Check for Duplicate Task
    Service->>+DB: Check Duplicate Task<br/>SELECT COUNT(*) WHERE title ILIKE %title%<br/>AND createdBy = userId
    DB-->>-Service: Duplicate Check Result
    
    alt Duplicate Found
        Service-->>Controller: 409 Conflict<br/>{error: "DUPLICATE_TASK"}
        Controller-->>Gateway: 409 Conflict
        Gateway-->>Client: 409 Conflict
    end
    
    %% Data Persistence
    Service->>Service: Generate Task ID<br/>UUID v4
    Service->>Service: Prepare Task Entity<br/>+ Timestamps, User Context
    
    Service->>+DB: BEGIN TRANSACTION
    Service->>DB: INSERT INTO tasks<br/>(id, title, description, status, priority, dueDate, assigneeId, createdBy, createdAt, updatedAt)
    
    alt Database Success
        DB-->>Service: Task Created Successfully<br/>Returning: task_id, created_at
        Service->>DB: COMMIT TRANSACTION
        DB-->>-Service: Transaction Committed
        
        %% Audit Logging
        Service->>+Audit: Log Task Creation<br/>{action: "TASK_CREATED", taskId, userId, timestamp, metadata}
        Audit->>Audit: Store Audit Record<br/>Immutable Log Entry
        Audit-->>-Service: Audit Logged
        
        %% Cache Updates
        Service->>Cache: Invalidate User Task Cache<br/>Key: tasks:user:{userId}:*
        Service->>Cache: Cache New Task<br/>Key: task:{taskId}<br/>TTL: 1 hour
        
        %% Prepare Response
        Service->>Service: Build Task Response<br/>Include all task fields + metadata
        Service-->>-Controller: Task Created Successfully<br/>TaskResponse Object
        
    else Database Failure
        DB-->>Service: Database Error
        Service->>DB: ROLLBACK TRANSACTION
        DB-->>Service: Transaction Rolled Back
        Service-->>Controller: 500 Internal Server Error<br/>{error: "INTERNAL_SERVER_ERROR"}
        Controller-->>Gateway: 500 Internal Server Error
        Gateway-->>Client: 500 Internal Server Error
    end
    
    %% Success Response
    Controller->>Controller: Format HTTP Response<br/>Status: 201 Created<br/>Headers: Location, X-Request-ID
    Controller-->>-Gateway: 201 Created<br/>Location: /api/tasks/{taskId}<br/>Body: TaskResponse
    
    Gateway->>Gateway: Add Response Headers<br/>X-Rate-Limit-Remaining<br/>X-Rate-Limit-Reset
    Gateway-->>-Client: 201 Created<br/>Task Successfully Created
    
    %% Asynchronous Notifications (Fire and Forget)
    par Async Notifications
        Service->>+Notif: Send Task Creation Notification<br/>{taskId, assigneeId, createdBy, title}
        Notif->>Notif: Process Notification<br/>- Email to assignee<br/>- Push notification<br/>- Webhook to integrations
        Notif-->>-Service: Notification Queued
    end
    
    Note over Client, Notif: Task Creation Complete - Response Time Target: <200ms
```

## Flow Description

### 1. Authentication Phase
- **API Gateway** receives the request and performs rate limiting checks
- **Authentication Service** validates the JWT token and returns user context
- Invalid tokens result in immediate 401 Unauthorized response

### 2. Request Validation Phase
- **CreateTaskDto** performs comprehensive field validation
- Validates required fields, data types, formats, and constraints
- Returns detailed validation errors for any failures

### 3. Authorization Phase
- **Task Controller** checks if the user has permissions to create tasks
- Returns 403 Forbidden if insufficient permissions

### 4. Business Logic Processing
- **Task Service** orchestrates the business logic
- Validates assignee status (if provided) using cache-first approach
- Applies business rules including duplicate checking
- Handles all business rule violations with appropriate error responses

### 5. Data Persistence
- **Database Transaction** ensures data consistency
- **Audit Service** logs all task creation events for compliance
- **Cache Updates** maintain data consistency and performance

### 6. Response Generation
- **HTTP Response** formatted with proper status codes and headers
- **Rate Limiting Headers** included for client awareness
- **Asynchronous Notifications** triggered without blocking the response

## Error Handling Scenarios

| Error Type | HTTP Status | Response Time Impact | Recovery Action |
|------------|-------------|---------------------|------------------|
| Invalid Token | 401 | Minimal | Client re-authentication |
| Validation Error | 400 | Minimal | Client data correction |
| Insufficient Permissions | 403 | Minimal | Contact administrator |
| Duplicate Task | 409 | Low | Modify task title |
| Business Rule Violation | 422 | Low | Correct business data |
| Rate Limit Exceeded | 429 | Minimal | Client backoff/retry |
| Database Error | 500 | High | System recovery |

## Performance Characteristics

- **Target Response Time**: < 200ms for 95% of requests
- **Database Query Time**: < 50ms for task insertion
- **Cache Access Time**: < 5ms for permission checks
- **Authentication Time**: < 100ms for token validation
- **Audit Logging**: Asynchronous, no impact on response time

## Security Measures

- **JWT Token Validation** at API Gateway level
- **Rate Limiting** prevents abuse (100 req/min per user)
- **Input Sanitization** prevents injection attacks
- **Audit Trail** maintains compliance and security monitoring
- **Permission Caching** with appropriate TTL for security vs. performance balance

## Compliance Features

- **Complete Audit Trail** for all task creation events
- **Immutable Logging** for regulatory compliance
- **Data Validation** ensures data integrity
- **User Attribution** tracks all actions to specific users
- **Timestamp Accuracy** for legal and compliance requirements

---

**Diagram Version**: 1.0  
**Last Updated**: 2024  
**Architecture**: Microservices with API Gateway Pattern  
**Compliance**: GDPR, ISO 27001, SOX Ready  
**Performance Target**: <200ms Response Time