# Sequence Diagram - Task Management API
## Task Creation Flow

### Version: 1.0
### Date: 2024
### Generated from: HLD Document and API Contract Outline

---

## Overview

This sequence diagram illustrates the complete flow for creating a task through the Task Management API, including authentication, validation, business logic processing, data persistence, and audit logging.

## Primary Flow: Task Creation

```mermaid
sequenceDiagram
    participant Client as API Consumer
    participant Gateway as API Gateway/Load Balancer
    participant Auth as Authentication Service
    participant Controller as Task Controller
    participant Middleware as Validation Middleware
    participant Service as Task Service
    participant Database as PostgreSQL Database
    participant Audit as Audit Service
    participant Cache as Redis Cache
    
    Note over Client, Cache: Task Creation Request Flow
    
    Client->>Gateway: POST /api/tasks
    Note right of Client: Request includes:<br/>- JWT Bearer Token<br/>- Task payload<br/>- X-Request-ID header
    
    Gateway->>Gateway: Rate Limiting Check
    Note right of Gateway: Validate rate limits:<br/>- 100 requests/minute<br/>- Return 429 if exceeded
    
    Gateway->>Auth: Validate JWT Token
    Note right of Auth: Token validation:<br/>- Signature verification<br/>- Expiration check<br/>- User permissions
    
    alt Token Invalid
        Auth-->>Gateway: 401 Unauthorized
        Gateway-->>Client: 401 Unauthorized Response
    else Token Valid
        Auth-->>Gateway: Token Valid + User Context
        
        Gateway->>Controller: Forward Request with User Context
        Note right of Controller: Headers include:<br/>- Validated user ID<br/>- Request correlation ID<br/>- Rate limit info
        
        Controller->>Middleware: Validate Request DTO
        Note right of Middleware: Validation checks:<br/>- Required fields present<br/>- Data types correct<br/>- Field length constraints<br/>- Enum value validation
        
        alt Validation Fails
            Middleware-->>Controller: Validation Errors
            Controller-->>Gateway: 400 Bad Request
            Gateway-->>Client: 400 Bad Request Response
            Note left of Client: Response includes:<br/>- Field-specific errors<br/>- Error codes<br/>- Correlation ID
        else Validation Passes
            Middleware-->>Controller: Validated DTO
            
            Controller->>Service: createTask(validatedDto, userContext)
            Note right of Service: Business logic processing
            
            Service->>Service: Business Rule Validation
            Note right of Service: Checks include:<br/>- Due date in future<br/>- Title uniqueness per user<br/>- User assignment validation
            
            alt Business Rules Fail
                Service-->>Controller: Business Logic Error
                
                alt Conflict (Duplicate Title)
                    Controller-->>Gateway: 409 Conflict
                    Gateway-->>Client: 409 Conflict Response
                else Unprocessable Entity (Invalid Assignment)
                    Controller-->>Gateway: 422 Unprocessable Entity
                    Gateway-->>Client: 422 Unprocessable Entity Response
                end
            else Business Rules Pass
                Service->>Service: Data Sanitization
                Note right of Service: Security measures:<br/>- XSS prevention<br/>- SQL injection protection<br/>- Input sanitization
                
                Service->>Database: BEGIN TRANSACTION
                
                Service->>Database: INSERT INTO tasks
                Note right of Database: Task data:<br/>- Generated UUID<br/>- Sanitized content<br/>- Audit fields<br/>- Timestamps
                
                alt Database Error
                    Database-->>Service: Database Error
                    Service->>Database: ROLLBACK TRANSACTION
                    Service-->>Controller: 500 Internal Server Error
                    Controller-->>Gateway: 500 Internal Server Error
                    Gateway-->>Client: 500 Internal Server Error Response
                else Database Success
                    Database-->>Service: Task Created (with ID)
                    
                    Service->>Audit: Log Task Creation Event
                    Note right of Audit: Audit data:<br/>- User ID<br/>- Action: CREATE_TASK<br/>- Task ID<br/>- Timestamp<br/>- Request correlation ID
                    
                    Audit-->>Service: Audit Logged
                    
                    Service->>Database: COMMIT TRANSACTION
                    Database-->>Service: Transaction Committed
                    
                    Service->>Cache: Cache User Task Count
                    Note right of Cache: Performance optimization:<br/>- Update user statistics<br/>- Cache frequently accessed data
                    Cache-->>Service: Cache Updated
                    
                    Service-->>Controller: Task Created Successfully
                    
                    Controller->>Controller: Format Response
                    Note right of Controller: Response formatting:<br/>- Add Location header<br/>- Include rate limit headers<br/>- Add correlation ID
                    
                    Controller-->>Gateway: 201 Created Response
                    Gateway-->>Client: 201 Created Response
                    Note left of Client: Response includes:<br/>- Complete task object<br/>- Location header<br/>- Rate limit headers<br/>- Correlation ID
                end
            end
        end
    end
```

## Error Handling Flows

### Authentication Error Flow

```mermaid
sequenceDiagram
    participant Client as API Consumer
    participant Gateway as API Gateway
    participant Auth as Authentication Service
    
    Client->>Gateway: POST /api/tasks (Invalid/Missing Token)
    Gateway->>Auth: Validate JWT Token
    Auth-->>Gateway: 401 Token Invalid/Missing
    Gateway-->>Client: 401 Unauthorized
    Note left of Client: Error Response:<br/>{<br/>  "statusCode": 401,<br/>  "message": "Unauthorized",<br/>  "error": "Invalid or missing token"<br/>}
```

### Rate Limiting Flow

```mermaid
sequenceDiagram
    participant Client as API Consumer
    participant Gateway as API Gateway
    
    Client->>Gateway: POST /api/tasks (Rate Limit Exceeded)
    Gateway->>Gateway: Check Rate Limits
    Gateway-->>Client: 429 Too Many Requests
    Note left of Client: Headers include:<br/>- Retry-After: 60<br/>- X-Rate-Limit-Remaining: 0<br/>- X-Rate-Limit-Reset: timestamp
```

### Database Connection Error Flow

```mermaid
sequenceDiagram
    participant Service as Task Service
    participant Database as PostgreSQL Database
    participant Circuit as Circuit Breaker
    
    Service->>Circuit: Check Circuit State
    
    alt Circuit Open
        Circuit-->>Service: Circuit Open - Fail Fast
        Service-->>Service: Return Cached Error Response
    else Circuit Closed
        Circuit->>Database: Attempt Connection
        
        alt Connection Fails
            Database-->>Circuit: Connection Timeout/Error
            Circuit->>Circuit: Increment Failure Count
            Circuit-->>Service: Database Unavailable
            
            alt Failure Threshold Reached
                Circuit->>Circuit: Open Circuit
            end
        else Connection Success
            Database-->>Circuit: Connection Established
            Circuit->>Circuit: Reset Failure Count
            Circuit-->>Service: Proceed with Operation
        end
    end
```

## Performance Optimization Flows

### Caching Strategy Flow

```mermaid
sequenceDiagram
    participant Service as Task Service
    participant Cache as Redis Cache
    participant Database as PostgreSQL Database
    
    Service->>Cache: Check User Task Statistics
    
    alt Cache Hit
        Cache-->>Service: Return Cached Data
        Note right of Service: Fast response:<br/>< 10ms from cache
    else Cache Miss
        Cache-->>Service: Cache Miss
        Service->>Database: Query User Statistics
        Database-->>Service: Return Statistics
        Service->>Cache: Update Cache (TTL: 300s)
        Cache-->>Service: Cache Updated
    end
```

## Monitoring and Observability

### Request Tracing Flow

```mermaid
sequenceDiagram
    participant Client as API Consumer
    participant Gateway as API Gateway
    participant Service as Task Service
    participant Monitoring as APM System
    
    Client->>Gateway: POST /api/tasks (X-Request-ID: req-123)
    Gateway->>Gateway: Generate Trace ID
    Gateway->>Service: Forward Request (Trace-ID: trace-456)
    
    par Parallel Processing
        Service->>Service: Process Request
    and Monitoring Collection
        Gateway->>Monitoring: Log Request Start
        Service->>Monitoring: Log Processing Events
        Service->>Monitoring: Log Performance Metrics
    end
    
    Service-->>Gateway: Response
    Gateway->>Monitoring: Log Request Complete
    Gateway-->>Client: Response (X-Request-ID: req-123)
```

## Security Flow

### Input Sanitization and Validation Flow

```mermaid
sequenceDiagram
    participant Controller as Task Controller
    participant Validator as Input Validator
    participant Sanitizer as Data Sanitizer
    participant Service as Task Service
    
    Controller->>Validator: Validate Input DTO
    
    Validator->>Validator: Schema Validation
    Note right of Validator: Checks:<br/>- Required fields<br/>- Data types<br/>- Length constraints<br/>- Format validation
    
    Validator->>Validator: Security Validation
    Note right of Validator: Security checks:<br/>- Malicious pattern detection<br/>- Script injection attempts<br/>- SQL injection patterns
    
    alt Validation Fails
        Validator-->>Controller: Validation Errors
    else Validation Passes
        Validator-->>Sanitizer: Valid Input
        
        Sanitizer->>Sanitizer: Sanitize Input
        Note right of Sanitizer: Sanitization:<br/>- HTML encoding<br/>- Script removal<br/>- Special character handling
        
        Sanitizer-->>Service: Sanitized Data
    end
```

---

## Flow Characteristics

### Performance Targets
- **Total Response Time**: < 200ms (P95)
- **Authentication**: < 50ms
- **Validation**: < 20ms
- **Business Logic**: < 50ms
- **Database Operation**: < 80ms
- **Audit Logging**: Asynchronous (non-blocking)

### Error Recovery
- **Retry Strategy**: Exponential backoff for transient failures
- **Circuit Breaker**: Fail-fast for downstream service failures
- **Graceful Degradation**: Partial functionality during service outages

### Security Measures
- **Authentication**: JWT token validation on every request
- **Authorization**: Role-based access control (RBAC)
- **Input Validation**: Multi-layer validation (DTO + Business + Database)
- **Audit Trail**: Complete audit logging for compliance

### Scalability Considerations
- **Stateless Design**: No server-side session state
- **Horizontal Scaling**: Load balancing across multiple instances
- **Database Scaling**: Read replicas for query optimization
- **Caching**: Redis for performance optimization

---

## Compliance and Audit

### Audit Trail Requirements
- **User Actions**: All user actions logged with correlation IDs
- **Data Changes**: Before/after values for data modifications
- **Security Events**: Authentication, authorization, and access attempts
- **System Events**: Errors, performance issues, and system state changes

### GDPR Compliance
- **Data Minimization**: Only necessary data collected and processed
- **Consent Tracking**: User consent logged and tracked
- **Right to be Forgotten**: Support for data deletion requests
- **Data Portability**: Export capabilities for user data

---

**Document Metadata**
- **Generated From**: HLD Document v1.0, API Contract Outline v1.0
- **Architecture Pattern**: Layered Architecture with Microservices
- **Technology Stack**: Node.js/NestJS, PostgreSQL, Redis, JWT
- **Compliance**: GDPR, ISO27001, SOC2, PCI-DSS
- **Performance Target**: < 200ms response time, 99.9% availability
- **Security**: JWT authentication, input validation, audit logging