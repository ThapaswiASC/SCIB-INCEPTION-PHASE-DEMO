# Sequence Diagram - Task Management API
## Task Creation Endpoint Flow

### Version: 1.0
### Generated from: HLD-DEMO-2350 and API Contract
### Date: 2024

---

## Overview
This sequence diagram illustrates the complete flow for the Task Creation API endpoint (POST /api/v1/tasks), showing interactions between all system components from request initiation to response delivery.

---

## Primary Flow: Task Creation

```mermaid
sequenceDiagram
    participant Client as Mobile/Web Client
    participant Gateway as API Gateway
    participant Auth as Authentication Service
    participant Controller as TaskController
    participant Middleware as Middleware Layer
    participant DTO as CreateTaskDto
    participant Service as TaskService
    participant UserRepo as UserRepository
    participant TaskRepo as TaskRepository
    participant AuditRepo as AuditRepository
    participant Cache as Redis Cache
    participant DB as PostgreSQL Database
    participant Notification as Notification Service
    participant Analytics as Analytics Service

    Note over Client,Analytics: Task Creation Flow - Happy Path
    
    Client->>Gateway: POST /api/v1/tasks
    Note right of Client: Request with JWT token<br/>and task data
    
    Gateway->>Gateway: Rate Limiting Check
    Note right of Gateway: 100 requests/minute per user
    
    Gateway->>Auth: Validate JWT Token
    Auth->>Auth: Token Verification
    Auth-->>Gateway: Token Valid + User Context
    
    Gateway->>Controller: Forward Request
    Note right of Gateway: Add correlation ID<br/>and user context
    
    Controller->>Middleware: Request Processing
    Middleware->>Middleware: CORS Validation
    Middleware->>Middleware: Request Logging
    Middleware->>Middleware: Input Sanitization
    Middleware-->>Controller: Request Validated
    
    Controller->>DTO: Validate Request Body
    DTO->>DTO: Field Validation
    Note right of DTO: - Title: required, max 200 chars<br/>- Priority: enum validation<br/>- DueDate: future date check<br/>- AssignedTo: UUID format
    DTO-->>Controller: Validation Success
    
    Controller->>Service: createTask(taskData, userId)
    
    Service->>Service: Business Rule Validation
    Note right of Service: - Due date in future<br/>- Title uniqueness per user<br/>- Input sanitization
    
    Service->>UserRepo: validateAssignedUser(assignedTo)
    UserRepo->>Cache: Check User Cache
    
    alt User Found in Cache
        Cache-->>UserRepo: User Data
    else User Not in Cache
        UserRepo->>DB: SELECT user WHERE id = assignedTo
        DB-->>UserRepo: User Record
        UserRepo->>Cache: Cache User Data
    end
    
    UserRepo-->>Service: User Validation Result
    
    Service->>TaskRepo: checkTitleUniqueness(title, userId)
    TaskRepo->>DB: SELECT task WHERE title = ? AND created_by = ?
    DB-->>TaskRepo: Query Result
    TaskRepo-->>Service: Uniqueness Check Result
    
    Service->>Service: Generate Task ID (UUID)
    Service->>Service: Set Default Values
    Note right of Service: - Status: PENDING<br/>- CreatedAt: current timestamp<br/>- Version: 1
    
    Service->>DB: BEGIN TRANSACTION
    
    Service->>TaskRepo: createTask(taskEntity)
    TaskRepo->>DB: INSERT INTO tasks (...)
    DB-->>TaskRepo: Task Created
    
    Service->>AuditRepo: logTaskCreation(taskId, userId, taskData)
    AuditRepo->>DB: INSERT INTO task_audit (...)
    DB-->>AuditRepo: Audit Record Created
    
    Service->>DB: COMMIT TRANSACTION
    DB-->>Service: Transaction Committed
    
    Service->>Cache: Cache Task Data
    Cache-->>Service: Cache Updated
    
    Service-->>Controller: Task Created Successfully
    
    Controller->>Controller: Format Response
    Note right of Controller: - Add Location header<br/>- Include rate limit headers<br/>- Set correlation ID
    
    Controller-->>Gateway: HTTP 201 Created + Task Data
    
    par Asynchronous Operations
        Gateway->>Notification: Send Task Assignment Notification
        Note right of Notification: If task is assigned to user
        and
        Gateway->>Analytics: Send Task Creation Event
        Note right of Analytics: For metrics and reporting
    end
    
    Gateway-->>Client: HTTP 201 Created Response
    Note left of Client: Response includes:<br/>- Task ID and details<br/>- Location header<br/>- Rate limit info
```

---

## Error Flow: Validation Failure

```mermaid
sequenceDiagram
    participant Client as Mobile/Web Client
    participant Gateway as API Gateway
    participant Auth as Authentication Service
    participant Controller as TaskController
    participant DTO as CreateTaskDto
    participant ErrorHandler as Global Error Handler

    Note over Client,ErrorHandler: Validation Error Flow
    
    Client->>Gateway: POST /api/v1/tasks
    Note right of Client: Request with invalid data
    
    Gateway->>Gateway: Rate Limiting Check
    Gateway->>Auth: Validate JWT Token
    Auth-->>Gateway: Token Valid
    
    Gateway->>Controller: Forward Request
    Controller->>DTO: Validate Request Body
    
    DTO->>DTO: Field Validation
    Note right of DTO: Validation Failures:<br/>- Title empty<br/>- Due date in past<br/>- Invalid priority enum
    
    DTO-->>Controller: ValidationException
    
    Controller->>ErrorHandler: Handle ValidationException
    ErrorHandler->>ErrorHandler: Format Error Response
    Note right of ErrorHandler: - Error code: VALIDATION_ERROR<br/>- Field-specific messages<br/>- Request ID for tracing
    
    ErrorHandler-->>Controller: Formatted Error Response
    Controller-->>Gateway: HTTP 400 Bad Request
    Gateway-->>Client: HTTP 400 + Validation Errors
    
    Note left of Client: Response includes:<br/>- Detailed field errors<br/>- Corrective suggestions<br/>- Request ID for support
```

---

## Error Flow: Authentication Failure

```mermaid
sequenceDiagram
    participant Client as Mobile/Web Client
    participant Gateway as API Gateway
    participant Auth as Authentication Service
    participant ErrorHandler as Global Error Handler

    Note over Client,ErrorHandler: Authentication Error Flow
    
    Client->>Gateway: POST /api/v1/tasks
    Note right of Client: Request with invalid/expired token
    
    Gateway->>Gateway: Rate Limiting Check
    Gateway->>Auth: Validate JWT Token
    
    Auth->>Auth: Token Verification
    Note right of Auth: Token Issues:<br/>- Expired token<br/>- Invalid signature<br/>- Malformed token
    
    Auth-->>Gateway: AuthenticationException
    
    Gateway->>ErrorHandler: Handle AuthenticationException
    ErrorHandler->>ErrorHandler: Format Error Response
    Note right of ErrorHandler: - Error code: UNAUTHORIZED<br/>- Authentication guidance<br/>- Token refresh suggestion
    
    ErrorHandler-->>Gateway: Formatted Error Response
    Gateway-->>Client: HTTP 401 Unauthorized
    
    Note left of Client: Response includes:<br/>- Authentication error details<br/>- Token refresh instructions<br/>- Support contact info
```

---

## Error Flow: System Error

```mermaid
sequenceDiagram
    participant Client as Mobile/Web Client
    participant Gateway as API Gateway
    participant Auth as Authentication Service
    participant Controller as TaskController
    participant Service as TaskService
    participant DB as PostgreSQL Database
    participant ErrorHandler as Global Error Handler
    participant Monitoring as Monitoring System

    Note over Client,Monitoring: System Error Flow
    
    Client->>Gateway: POST /api/v1/tasks
    Gateway->>Auth: Validate JWT Token
    Auth-->>Gateway: Token Valid
    
    Gateway->>Controller: Forward Request
    Controller->>Service: createTask(taskData, userId)
    
    Service->>DB: Database Operation
    Note right of DB: Database Issues:<br/>- Connection timeout<br/>- Deadlock<br/>- Constraint violation
    
    DB-->>Service: DatabaseException
    Service-->>Controller: SystemException
    
    Controller->>ErrorHandler: Handle SystemException
    ErrorHandler->>ErrorHandler: Log Error Details
    ErrorHandler->>Monitoring: Send Error Alert
    
    ErrorHandler->>ErrorHandler: Format Generic Error Response
    Note right of ErrorHandler: - Error code: INTERNAL_SERVER_ERROR<br/>- Generic message (no sensitive info)<br/>- Request ID for tracking<br/>- Support contact
    
    ErrorHandler-->>Controller: Formatted Error Response
    Controller-->>Gateway: HTTP 500 Internal Server Error
    Gateway-->>Client: HTTP 500 + Generic Error
    
    Note left of Client: Response includes:<br/>- Generic error message<br/>- Request ID for support<br/>- Retry suggestions
```

---

## Rate Limiting Flow

```mermaid
sequenceDiagram
    participant Client as Mobile/Web Client
    participant Gateway as API Gateway
    participant RateLimit as Rate Limiter
    participant ErrorHandler as Global Error Handler

    Note over Client,ErrorHandler: Rate Limiting Flow
    
    Client->>Gateway: POST /api/v1/tasks
    Note right of Client: Exceeds rate limit<br/>(100 requests/minute)
    
    Gateway->>RateLimit: Check Rate Limit
    RateLimit->>RateLimit: Validate Request Count
    Note right of RateLimit: User exceeded:<br/>- 100 requests/minute<br/>- Current: 101 requests
    
    RateLimit-->>Gateway: RateLimitExceededException
    
    Gateway->>ErrorHandler: Handle RateLimitException
    ErrorHandler->>ErrorHandler: Format Rate Limit Response
    Note right of ErrorHandler: - Error code: RATE_LIMIT_EXCEEDED<br/>- Retry-After header<br/>- Current limit info
    
    ErrorHandler-->>Gateway: Formatted Error Response
    Gateway-->>Client: HTTP 429 Too Many Requests
    
    Note left of Client: Response includes:<br/>- Rate limit details<br/>- Retry-After: 60 seconds<br/>- Current usage stats
```

---

## Health Check Flow

```mermaid
sequenceDiagram
    participant Monitor as Monitoring System
    participant Gateway as API Gateway
    participant Controller as HealthController
    participant Service as HealthService
    participant DB as PostgreSQL Database
    participant Cache as Redis Cache
    participant Auth as Authentication Service

    Note over Monitor,Auth: Health Check Flow
    
    Monitor->>Gateway: GET /health
    Gateway->>Controller: Health Check Request
    
    Controller->>Service: performHealthCheck()
    
    par Parallel Health Checks
        Service->>DB: Database Connectivity Check
        DB-->>Service: Connection Status
        and
        Service->>Cache: Cache Connectivity Check
        Cache-->>Service: Cache Status
        and
        Service->>Auth: Authentication Service Check
        Auth-->>Service: Auth Service Status
    end
    
    Service->>Service: Aggregate Health Status
    Note right of Service: Overall Status:<br/>- Database: OK<br/>- Cache: OK<br/>- Auth Service: OK<br/>- Overall: HEALTHY
    
    Service-->>Controller: Health Check Result
    Controller-->>Gateway: HTTP 200 OK + Health Status
    Gateway-->>Monitor: Health Check Response
    
    Note left of Monitor: Response includes:<br/>- Overall health status<br/>- Component-level status<br/>- Response time metrics
```

---

## Sequence Diagram Specifications

### Key Components
1. **Mobile/Web Client**: End-user application initiating requests
2. **API Gateway**: Entry point handling rate limiting, routing, and cross-cutting concerns
3. **Authentication Service**: External service for JWT token validation
4. **TaskController**: NestJS controller handling HTTP requests and responses
5. **Middleware Layer**: Request processing, validation, and logging
6. **CreateTaskDto**: Data transfer object with validation decorators
7. **TaskService**: Business logic layer with rule validation
8. **Repository Layer**: Data access abstraction (UserRepository, TaskRepository, AuditRepository)
9. **PostgreSQL Database**: Primary data store with ACID compliance
10. **Redis Cache**: In-memory cache for performance optimization
11. **Notification Service**: Asynchronous notification handling
12. **Analytics Service**: Event streaming for metrics and reporting

### Flow Patterns
1. **Synchronous Operations**: Request-response patterns for core functionality
2. **Asynchronous Operations**: Fire-and-forget for notifications and analytics
3. **Error Handling**: Comprehensive error flows with proper HTTP status codes
4. **Caching Strategy**: Cache-aside pattern for performance optimization
5. **Transaction Management**: Database transactions for data consistency
6. **Audit Logging**: Complete audit trail for compliance requirements

### Performance Considerations
- **Response Time Target**: < 200ms for 95% of requests
- **Caching Strategy**: Redis for frequently accessed data
- **Connection Pooling**: Database connection optimization
- **Parallel Processing**: Asynchronous operations for non-critical paths

### Security Measures
- **JWT Authentication**: Bearer token validation
- **Input Validation**: Multi-layer validation approach
- **Rate Limiting**: DoS protection with configurable limits
- **Audit Logging**: Complete operation tracking
- **Error Sanitization**: No sensitive data in error responses

### Compliance Features
- **GDPR**: Data minimization and audit trails
- **SOX**: Complete audit logging for financial compliance
- **ISO 27001**: Security management system integration

---

**Document Control**
- **Version**: 1.0
- **Generated From**: HLD-DEMO-2350, API Contract Outline
- **Last Updated**: 2024
- **Diagram Format**: Mermaid Sequence Diagrams
- **Compliance**: Enterprise Architecture Standards