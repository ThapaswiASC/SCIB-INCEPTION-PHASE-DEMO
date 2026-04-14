# Sequence Diagrams
## Youth Account Management System

### Document Information
- **Version**: 1.0
- **Date**: 2024
- **Project**: Youth Account Management System (SCIB-25)
- **Generated From**: HLD Document and API Contract Outline

---

## 1. Youth Account Dashboard Sequence
**Mapped to**: SCIB-26 - Dashboard API

```mermaid
sequenceDiagram
    participant Parent as Parent/Guardian
    participant WebApp as Web Application
    participant APIGateway as API Gateway
    participant YouthService as Youth Account Service
    participant Cache as Redis Cache
    participant Database as PostgreSQL DB
    participant TransactionService as Transaction Service
    
    Parent->>WebApp: Request Dashboard
    WebApp->>APIGateway: GET /api/youth-accounts/{id}/dashboard
    APIGateway->>APIGateway: Validate OAuth 2.0 Token
    APIGateway->>APIGateway: Rate Limit Check (1000/hour)
    APIGateway->>YouthService: Forward Request
    
    YouthService->>Cache: Check Cache for Account Data
    alt Cache Hit
        Cache-->>YouthService: Return Cached Data
    else Cache Miss
        YouthService->>Database: Query Youth Account Details
        Database-->>YouthService: Account Data
        YouthService->>Cache: Store in Cache (TTL: 5min)
    end
    
    YouthService->>TransactionService: Get Recent Transactions
    TransactionService->>Database: Query Recent Transactions
    Database-->>TransactionService: Transaction Data
    TransactionService-->>YouthService: Recent Transactions
    
    YouthService->>YouthService: Aggregate Dashboard Data
    YouthService-->>APIGateway: Dashboard Response (200 OK)
    APIGateway-->>WebApp: JSON Response
    WebApp-->>Parent: Dashboard View
    
    Note over Parent, Database: Target Response Time: <500ms (95th percentile)
```

---

## 2. Fund Transfer Sequence
**Mapped to**: SCIB-27 - Fund Transfer API

```mermaid
sequenceDiagram
    participant Parent as Parent/Guardian
    participant WebApp as Web Application
    participant APIGateway as API Gateway
    participant TransferService as Fund Transfer Service
    participant YouthService as Youth Account Service
    participant CoreBanking as Core Banking System
    participant PaymentGateway as Payment Gateway
    participant MessageQueue as Message Queue
    participant NotificationService as Notification Service
    participant AuditSystem as Audit System
    
    Parent->>WebApp: Initiate Fund Transfer
    WebApp->>APIGateway: POST /api/youth-accounts/{id}/fund-transfer
    APIGateway->>APIGateway: Validate OAuth 2.0 Token
    APIGateway->>APIGateway: Check Idempotency Key
    APIGateway->>TransferService: Forward Transfer Request
    
    TransferService->>TransferService: Validate Transfer Amount ($1-$500)
    TransferService->>TransferService: Validate Business Rules
    
    TransferService->>CoreBanking: Check Source Account Balance
    CoreBanking-->>TransferService: Balance Information
    
    alt Sufficient Funds
        TransferService->>YouthService: Validate Youth Account
        YouthService-->>TransferService: Account Valid
        
        TransferService->>PaymentGateway: Process Transfer
        PaymentGateway->>CoreBanking: Execute Transfer
        CoreBanking-->>PaymentGateway: Transfer Confirmation
        PaymentGateway-->>TransferService: Transfer Success
        
        TransferService->>TransferService: Update Account Balances
        TransferService->>MessageQueue: Publish Transfer Event
        
        par Async Notifications
            MessageQueue->>NotificationService: Transfer Notification
            NotificationService->>NotificationService: Send Email/SMS
        and Audit Logging
            MessageQueue->>AuditSystem: Log Transfer Event
            AuditSystem->>AuditSystem: Store Audit Record
        end
        
        TransferService-->>APIGateway: Transfer Success (201 Created)
        
    else Insufficient Funds
        TransferService-->>APIGateway: Insufficient Funds Error (402)
        
    else Invalid Account
        TransferService-->>APIGateway: Account Not Found (404)
    end
    
    APIGateway-->>WebApp: Transfer Response
    WebApp-->>Parent: Transfer Result
    
    Note over Parent, AuditSystem: Target Response Time: <2000ms (95th percentile)
```

---

## 3. Spending Limit Configuration Sequence
**Mapped to**: SCIB-28 - Spending Limit API

```mermaid
sequenceDiagram
    participant Parent as Parent/Guardian
    participant WebApp as Web Application
    participant APIGateway as API Gateway
    participant LimitService as Spending Limit Service
    participant ConfigDB as Configuration DB
    participant YouthService as Youth Account Service
    participant Cache as Redis Cache
    participant MessageQueue as Message Queue
    participant NotificationService as Notification Service
    
    Parent->>WebApp: Configure Spending Limits
    WebApp->>APIGateway: PUT /api/youth-accounts/{id}/spending-limit
    APIGateway->>APIGateway: Validate OAuth 2.0 Token
    APIGateway->>APIGateway: Authorize Parent Access
    APIGateway->>LimitService: Forward Limit Request
    
    LimitService->>LimitService: Validate Limit Values
    LimitService->>LimitService: Check Business Rules (daily ≤ weekly/7)
    
    alt Valid Limits
        LimitService->>YouthService: Validate Youth Account
        YouthService-->>LimitService: Account Exists
        
        LimitService->>ConfigDB: Update Spending Limits
        ConfigDB-->>LimitService: Update Confirmation
        
        LimitService->>Cache: Invalidate Cached Limits
        Cache-->>LimitService: Cache Cleared
        
        LimitService->>MessageQueue: Publish Limit Change Event
        MessageQueue->>NotificationService: Limit Update Notification
        NotificationService->>NotificationService: Notify Parent & Youth
        
        LimitService-->>APIGateway: Limit Updated (200 OK)
        
    else Invalid Limits
        LimitService-->>APIGateway: Validation Error (422)
        
    else Unauthorized
        LimitService-->>APIGateway: Forbidden (403)
    end
    
    APIGateway-->>WebApp: Limit Response
    WebApp-->>Parent: Configuration Result
    
    Note over Parent, NotificationService: Target Response Time: <300ms (95th percentile)
```

---

## 4. Transaction History Retrieval Sequence
**Mapped to**: SCIB-29 - Transaction History API

```mermaid
sequenceDiagram
    participant Parent as Parent/Guardian
    participant WebApp as Web Application
    participant APIGateway as API Gateway
    participant TransactionService as Transaction Service
    participant Cache as Redis Cache
    participant TransactionDB as Transaction DB
    participant YouthService as Youth Account Service
    
    Parent->>WebApp: Request Transaction History
    WebApp->>APIGateway: GET /api/youth-accounts/{id}/transactions?page=1&pageSize=20
    APIGateway->>APIGateway: Validate OAuth 2.0 Token
    APIGateway->>APIGateway: Authorize Parent Access
    APIGateway->>TransactionService: Forward Request
    
    TransactionService->>TransactionService: Validate Query Parameters
    TransactionService->>TransactionService: Build Query with Filters
    
    TransactionService->>YouthService: Validate Youth Account Access
    YouthService-->>TransactionService: Access Authorized
    
    TransactionService->>Cache: Check Cache for Query Result
    alt Cache Hit (Recent Queries)
        Cache-->>TransactionService: Return Cached Results
    else Cache Miss
        TransactionService->>TransactionDB: Execute Paginated Query
        Note over TransactionDB: Indexed query on youth_account_id, date
        TransactionDB-->>TransactionService: Transaction Results
        TransactionService->>Cache: Cache Results (TTL: 2min)
    end
    
    TransactionService->>TransactionService: Calculate Summary Statistics
    TransactionService->>TransactionService: Build Pagination Metadata
    
    TransactionService-->>APIGateway: Transaction History (200 OK)
    APIGateway-->>WebApp: JSON Response
    WebApp-->>Parent: Transaction List View
    
    Note over Parent, TransactionDB: Target Response Time: <800ms (95th percentile)
```

---

## 5. Account Status Management Sequence

```mermaid
sequenceDiagram
    participant Parent as Parent/Guardian
    participant WebApp as Web Application
    participant APIGateway as API Gateway
    participant YouthService as Youth Account Service
    participant Database as PostgreSQL DB
    participant MessageQueue as Message Queue
    participant NotificationService as Notification Service
    participant AuditSystem as Audit System
    
    Parent->>WebApp: Suspend/Reactivate Account
    WebApp->>APIGateway: PATCH /api/youth-accounts/{id}/status
    APIGateway->>APIGateway: Validate OAuth 2.0 Token
    APIGateway->>APIGateway: Authorize Parent Access
    APIGateway->>YouthService: Forward Status Change
    
    YouthService->>YouthService: Validate Status Transition
    YouthService->>Database: Update Account Status
    Database-->>YouthService: Status Updated
    
    YouthService->>MessageQueue: Publish Status Change Event
    
    par Async Processing
        MessageQueue->>NotificationService: Status Change Notification
        NotificationService->>NotificationService: Send Alerts
    and Audit Logging
        MessageQueue->>AuditSystem: Log Status Change
        AuditSystem->>AuditSystem: Store Audit Record
    end
    
    YouthService-->>APIGateway: Status Updated (200 OK)
    APIGateway-->>WebApp: Status Response
    WebApp-->>Parent: Status Change Confirmation
```

---

## 6. Bulk Fund Transfer Sequence

```mermaid
sequenceDiagram
    participant Parent as Parent/Guardian
    participant WebApp as Web Application
    participant APIGateway as API Gateway
    participant BulkService as Bulk Operations Service
    participant TransferService as Fund Transfer Service
    participant MessageQueue as Message Queue
    participant Database as PostgreSQL DB
    
    Parent->>WebApp: Initiate Bulk Transfer
    WebApp->>APIGateway: POST /api/youth-accounts/bulk/fund-transfer
    APIGateway->>APIGateway: Validate OAuth 2.0 Token
    APIGateway->>BulkService: Forward Bulk Request
    
    BulkService->>BulkService: Validate Batch Request
    BulkService->>BulkService: Create Batch ID
    BulkService->>Database: Store Batch Record
    
    BulkService->>MessageQueue: Queue Individual Transfers
    BulkService-->>APIGateway: Batch Accepted (202)
    APIGateway-->>WebApp: Batch ID & Status URL
    WebApp-->>Parent: Batch Processing Started
    
    loop For Each Transfer
        MessageQueue->>TransferService: Process Individual Transfer
        TransferService->>TransferService: Execute Transfer Logic
        TransferService->>Database: Update Batch Status
    end
    
    Note over Parent, Database: Async processing with status tracking
```

---

## 7. Error Handling Sequence

```mermaid
sequenceDiagram
    participant Client as Client Application
    participant APIGateway as API Gateway
    participant Service as Microservice
    participant CircuitBreaker as Circuit Breaker
    participant ExternalSystem as External System
    participant Logger as Centralized Logger
    
    Client->>APIGateway: API Request
    APIGateway->>Service: Forward Request
    Service->>CircuitBreaker: Call External System
    
    alt Circuit Breaker Open
        CircuitBreaker-->>Service: Fail Fast
        Service->>Logger: Log Circuit Breaker Event
        Service-->>APIGateway: Service Unavailable (503)
        
    else Circuit Breaker Closed
        CircuitBreaker->>ExternalSystem: Forward Call
        
        alt External System Failure
            ExternalSystem-->>CircuitBreaker: Timeout/Error
            CircuitBreaker->>CircuitBreaker: Record Failure
            CircuitBreaker-->>Service: External Error
            Service->>Service: Retry with Exponential Backoff
            Service->>Logger: Log Retry Attempt
            
            alt Retry Successful
                Service-->>APIGateway: Success Response
            else Max Retries Exceeded
                Service->>Logger: Log Max Retries Exceeded
                Service-->>APIGateway: External Service Error (502)
            end
            
        else External System Success
            ExternalSystem-->>CircuitBreaker: Success Response
            CircuitBreaker-->>Service: Success Response
            Service-->>APIGateway: Success Response
        end
    end
    
    APIGateway->>APIGateway: Add Correlation ID
    APIGateway-->>Client: Response with Error Details
```

---

## 8. Authentication & Authorization Sequence

```mermaid
sequenceDiagram
    participant Parent as Parent/Guardian
    participant WebApp as Web Application
    participant AuthService as OAuth 2.0 Provider
    participant APIGateway as API Gateway
    participant YouthService as Youth Account Service
    participant MFA as MFA Service
    
    Parent->>WebApp: Login Request
    WebApp->>AuthService: OAuth 2.0 Authorization Request
    AuthService->>MFA: Trigger MFA Challenge
    MFA-->>Parent: Send MFA Code (SMS/Email)
    Parent->>AuthService: Provide MFA Code
    AuthService->>AuthService: Validate MFA
    AuthService-->>WebApp: Authorization Code
    
    WebApp->>AuthService: Exchange Code for Token
    AuthService-->>WebApp: JWT Access Token + Refresh Token
    
    WebApp->>APIGateway: API Request with Bearer Token
    APIGateway->>APIGateway: Validate JWT Token
    APIGateway->>APIGateway: Check Token Expiry (1 hour)
    APIGateway->>APIGateway: Verify Scopes & Permissions
    
    alt Valid Token
        APIGateway->>YouthService: Forward Authorized Request
        YouthService->>YouthService: Check Resource Access (RBAC)
        YouthService-->>APIGateway: Service Response
        APIGateway-->>WebApp: API Response
        
    else Invalid/Expired Token
        APIGateway-->>WebApp: Unauthorized (401)
        WebApp->>AuthService: Refresh Token Request
        AuthService-->>WebApp: New Access Token
    end
```

---

## Sequence Diagram Standards & Compliance

### Design Principles
- **Security First**: All sequences include OAuth 2.0 authentication validation
- **Performance Optimization**: Caching strategies clearly depicted
- **Error Handling**: Comprehensive error scenarios included
- **Audit Compliance**: Audit logging shown in all financial transactions
- **Async Processing**: Message queue patterns for non-blocking operations

### Performance Targets
- **Dashboard API**: <500ms (95th percentile) - SCIB-26
- **Fund Transfer API**: <2000ms (95th percentile) - SCIB-27
- **Spending Limit API**: <300ms (95th percentile) - SCIB-28
- **Transaction History API**: <800ms (95th percentile) - SCIB-29

### Security Features
- **OAuth 2.0 + JWT**: Industry standard authentication
- **Multi-Factor Authentication**: Required for parent accounts
- **Rate Limiting**: 1000 requests/hour per user
- **RBAC**: Role-based access control for resource protection
- **Audit Logging**: Complete audit trail for compliance

### Reliability Patterns
- **Circuit Breaker**: Prevents cascade failures
- **Retry Logic**: Exponential backoff for transient failures
- **Graceful Degradation**: Non-critical features fail gracefully
- **Correlation IDs**: Request tracing across distributed services

---

**Document Approval**
- **Solution Architect**: [Generated from HLD Document]
- **API Architect**: [Based on API Contract Outline]
- **Security Architect**: [Security patterns included]
- **Performance Engineer**: [Performance targets specified]

**Traceability**
- **Source**: HLD Document (API Development/Requirement Documents/HLDDocument.txt)
- **API Contract**: API Contract Outline (API Development/Requirement Documents/APIContractOutline.txt)
- **JIRA Mappings**: SCIB-25 through SCIB-30
- **Generated**: 2024 via Enterprise Architecture Automation