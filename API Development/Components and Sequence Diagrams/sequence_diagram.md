# Sequence Diagrams
# Youth Account Management System

## Overview
This document contains sequence diagrams for the Youth Account Management System API endpoints, illustrating the interaction flow between system components for each major operation.

## 1. Youth Account Dashboard - Sequence Diagram
**Reference**: SCIB-26 - Dashboard API Implementation
**Endpoint**: GET /api/youth-accounts/{youthAccountId}/dashboard

```mermaid
sequenceDiagram
    participant Parent as Parent/Guardian
    participant WebApp as Web Application
    participant Gateway as API Gateway
    participant AuthSvc as Auth Service
    participant YouthAPI as Youth Account API
    participant Cache as Redis Cache
    participant DB as PostgreSQL DB
    participant CoreBank as Core Banking System
    
    Parent->>WebApp: Request dashboard view
    WebApp->>Gateway: GET /api/youth-accounts/{id}/dashboard
    Gateway->>AuthSvc: Validate JWT token
    AuthSvc-->>Gateway: Token valid + user permissions
    Gateway->>YouthAPI: Forward authenticated request
    
    YouthAPI->>Cache: Check cached dashboard data
    alt Cache Hit
        Cache-->>YouthAPI: Return cached data
    else Cache Miss
        YouthAPI->>DB: Query youth account settings
        DB-->>YouthAPI: Return account settings
        YouthAPI->>CoreBank: Get current balance & recent transactions
        CoreBank-->>YouthAPI: Return balance + transactions
        YouthAPI->>Cache: Store dashboard data (TTL: 5min)
    end
    
    YouthAPI->>YouthAPI: Aggregate dashboard data
    YouthAPI-->>Gateway: Dashboard response (200 OK)
    Gateway-->>WebApp: Forward response
    WebApp-->>Parent: Display dashboard
    
    Note over YouthAPI,CoreBank: SLA: < 500ms response time
    Note over Cache: Cache TTL: 5 minutes
    Note over DB: Read replica for performance
```

## 2. Fund Transfer - Sequence Diagram
**Reference**: SCIB-27 - Fund Transfer API Implementation
**Endpoint**: POST /api/youth-accounts/{youthAccountId}/fund-transfer

```mermaid
sequenceDiagram
    participant Parent as Parent/Guardian
    participant WebApp as Web Application
    participant Gateway as API Gateway
    participant AuthSvc as Auth Service
    participant YouthAPI as Youth Account API
    participant DB as PostgreSQL DB
    participant PaymentSvc as Payment Service
    participant CoreBank as Core Banking System
    participant AuditSvc as Audit Service
    
    Parent->>WebApp: Initiate fund transfer
    WebApp->>Gateway: POST /api/youth-accounts/{id}/fund-transfer
    Gateway->>AuthSvc: Validate JWT token & permissions
    AuthSvc-->>Gateway: Token valid + transfer permissions
    Gateway->>YouthAPI: Forward authenticated request
    
    YouthAPI->>YouthAPI: Validate transfer request
    YouthAPI->>DB: Begin transaction
    YouthAPI->>CoreBank: Check source account balance
    CoreBank-->>YouthAPI: Return balance info
    
    alt Sufficient Balance
        YouthAPI->>PaymentSvc: Initiate transfer
        PaymentSvc->>CoreBank: Execute fund transfer
        CoreBank-->>PaymentSvc: Transfer confirmation
        PaymentSvc-->>YouthAPI: Transfer successful
        
        YouthAPI->>DB: Log transfer record
        YouthAPI->>DB: Update youth account balance
        YouthAPI->>DB: Commit transaction
        YouthAPI->>AuditSvc: Log transfer event
        
        YouthAPI-->>Gateway: Transfer success (200 OK)
    else Insufficient Balance
        YouthAPI->>DB: Rollback transaction
        YouthAPI-->>Gateway: Insufficient funds error (400)
    end
    
    Gateway-->>WebApp: Forward response
    WebApp-->>Parent: Display transfer result
    
    Note over YouthAPI,PaymentSvc: Idempotent operations
    Note over DB: ACID compliance required
    Note over AuditSvc: Immutable audit trail
```

## 3. Spending Limit Configuration - Sequence Diagram
**Reference**: SCIB-28 - Spending Limit API Implementation
**Endpoint**: PUT /api/youth-accounts/{youthAccountId}/spending-limit

```mermaid
sequenceDiagram
    participant Parent as Parent/Guardian
    participant WebApp as Web Application
    participant Gateway as API Gateway
    participant AuthSvc as Auth Service
    participant YouthAPI as Youth Account API
    participant Cache as Redis Cache
    participant DB as PostgreSQL DB
    participant NotifySvc as Notification Service
    participant AuditSvc as Audit Service
    
    Parent->>WebApp: Configure spending limit
    WebApp->>Gateway: PUT /api/youth-accounts/{id}/spending-limit
    Gateway->>AuthSvc: Validate JWT token & permissions
    AuthSvc-->>Gateway: Token valid + admin permissions
    Gateway->>YouthAPI: Forward authenticated request
    
    YouthAPI->>YouthAPI: Validate limit parameters
    alt Valid Limit
        YouthAPI->>DB: Begin transaction
        YouthAPI->>DB: Query current settings
        DB-->>YouthAPI: Return current limit
        YouthAPI->>DB: Update spending limit settings
        YouthAPI->>DB: Commit transaction
        
        YouthAPI->>Cache: Invalidate cached settings
        YouthAPI->>NotifySvc: Send limit change notification
        YouthAPI->>AuditSvc: Log limit change event
        
        YouthAPI-->>Gateway: Update successful (200 OK)
    else Invalid Limit
        YouthAPI-->>Gateway: Validation error (422)
    end
    
    Gateway-->>WebApp: Forward response
    WebApp-->>Parent: Display update result
    
    Note over YouthAPI,DB: Optimistic locking
    Note over NotifySvc: Async notification
    Note over Cache: Cache invalidation strategy
```

## 4. Transaction History - Sequence Diagram
**Reference**: SCIB-29 - Transaction History API Implementation
**Endpoint**: GET /api/youth-accounts/{youthAccountId}/transactions

```mermaid
sequenceDiagram
    participant Parent as Parent/Guardian
    participant WebApp as Web Application
    participant Gateway as API Gateway
    participant AuthSvc as Auth Service
    participant YouthAPI as Youth Account API
    participant Cache as Redis Cache
    participant DB as PostgreSQL DB
    participant CoreBank as Core Banking System
    
    Parent->>WebApp: Request transaction history
    WebApp->>Gateway: GET /api/youth-accounts/{id}/transactions?params
    Gateway->>AuthSvc: Validate JWT token & permissions
    AuthSvc-->>Gateway: Token valid + read permissions
    Gateway->>YouthAPI: Forward authenticated request
    
    YouthAPI->>YouthAPI: Validate query parameters
    YouthAPI->>YouthAPI: Build cache key from params
    YouthAPI->>Cache: Check cached transaction data
    
    alt Cache Hit
        Cache-->>YouthAPI: Return cached transactions
    else Cache Miss
        YouthAPI->>DB: Query transaction history (paginated)
        DB-->>YouthAPI: Return transaction records
        YouthAPI->>CoreBank: Enrich with merchant details
        CoreBank-->>YouthAPI: Return enriched data
        YouthAPI->>Cache: Store paginated results (TTL: 10min)
    end
    
    YouthAPI->>YouthAPI: Format response with pagination
    YouthAPI-->>Gateway: Transaction list (200 OK)
    Gateway-->>WebApp: Forward response
    WebApp-->>Parent: Display transaction history
    
    Note over YouthAPI,DB: Read replica optimization
    Note over Cache: Pagination-aware caching
    Note over CoreBank: Rate limiting applied
```

## 5. Error Handling - Sequence Diagram
**Scenario**: System Error During Fund Transfer

```mermaid
sequenceDiagram
    participant Parent as Parent/Guardian
    participant WebApp as Web Application
    participant Gateway as API Gateway
    participant YouthAPI as Youth Account API
    participant DB as PostgreSQL DB
    participant PaymentSvc as Payment Service
    participant AuditSvc as Audit Service
    participant MonitorSvc as Monitoring Service
    
    Parent->>WebApp: Initiate fund transfer
    WebApp->>Gateway: POST /api/youth-accounts/{id}/fund-transfer
    Gateway->>YouthAPI: Forward request
    
    YouthAPI->>DB: Begin transaction
    YouthAPI->>PaymentSvc: Initiate transfer
    PaymentSvc-->>YouthAPI: Service unavailable (503)
    
    YouthAPI->>YouthAPI: Implement retry with backoff
    YouthAPI->>PaymentSvc: Retry transfer (attempt 2)
    PaymentSvc-->>YouthAPI: Timeout error
    
    YouthAPI->>DB: Rollback transaction
    YouthAPI->>AuditSvc: Log error event
    YouthAPI->>MonitorSvc: Send error metrics
    
    YouthAPI-->>Gateway: Service unavailable (503)
    Gateway-->>WebApp: Forward error response
    WebApp-->>Parent: Display retry message
    
    Note over YouthAPI: Circuit breaker pattern
    Note over DB: Transaction rollback
    Note over MonitorSvc: Real-time alerting
```

## 6. Authentication Flow - Sequence Diagram
**Scenario**: OAuth 2.0 Token Validation

```mermaid
sequenceDiagram
    participant User as User
    participant WebApp as Web Application
    participant Gateway as API Gateway
    participant AuthSvc as Auth Service
    participant YouthAPI as Youth Account API
    participant Cache as Redis Cache
    
    User->>WebApp: Login request
    WebApp->>AuthSvc: OAuth 2.0 authorization
    AuthSvc-->>WebApp: JWT access token
    WebApp->>WebApp: Store token securely
    
    User->>WebApp: API request
    WebApp->>Gateway: API call with Bearer token
    Gateway->>Cache: Check token cache
    
    alt Token Cached & Valid
        Cache-->>Gateway: Token valid + user context
    else Token Not Cached
        Gateway->>AuthSvc: Validate JWT token
        AuthSvc-->>Gateway: Token validation result
        Gateway->>Cache: Cache token validation (TTL: 5min)
    end
    
    alt Token Valid
        Gateway->>YouthAPI: Forward authenticated request
        YouthAPI-->>Gateway: API response
        Gateway-->>WebApp: Forward response
    else Token Invalid
        Gateway-->>WebApp: Unauthorized (401)
        WebApp->>AuthSvc: Refresh token request
    end
    
    Note over Cache: Token validation caching
    Note over AuthSvc: JWT signature verification
    Note over Gateway: Rate limiting per user
```

## Sequence Diagram Design Principles

### 1. Performance Considerations
- **Caching Strategy**: Redis cache with appropriate TTL values
- **Database Optimization**: Read replicas for query operations
- **Response Time SLAs**: < 500ms for dashboard, < 3s for transfers
- **Connection Pooling**: Efficient database connection management

### 2. Security Measures
- **Token Validation**: JWT token validation at API Gateway
- **Permission Checks**: Role-based access control (RBAC)
- **Audit Logging**: Complete audit trail for all operations
- **Input Validation**: Request validation before processing

### 3. Reliability Patterns
- **Transaction Management**: ACID compliance for financial operations
- **Retry Logic**: Exponential backoff for transient failures
- **Circuit Breaker**: Protection against cascading failures
- **Idempotent Operations**: Safe retry mechanisms

### 4. Monitoring & Observability
- **Request Correlation**: Unique request IDs across services
- **Performance Metrics**: Response time and throughput monitoring
- **Error Tracking**: Comprehensive error logging and alerting
- **Business Metrics**: Financial transaction monitoring

### 5. Scalability Features
- **Horizontal Scaling**: Stateless API design
- **Load Balancing**: Even distribution across instances
- **Async Processing**: Non-blocking operations where possible
- **Resource Optimization**: Efficient resource utilization

---

**Document Version**: 1.0
**Last Updated**: [Current Date]
**Created By**: Senior Solution Architect
**Compliance**: SOC2, PCI-DSS, GDPR
**Review Date**: [Quarterly Review]