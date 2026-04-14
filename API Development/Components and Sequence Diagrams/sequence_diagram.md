# Sequence Diagrams
## Youth Account Management System

### Version: 1.0
### Date: 2024
### Classification: Internal

---

## 1. Youth Account Dashboard Retrieval Sequence

**Mapped to ADR**: SCIB-26 - Create API to retrieve youth account dashboard details

```mermaid
sequenceDiagram
    participant Parent as Parent/Guardian
    participant WebApp as Web Application
    participant APIGateway as API Gateway
    participant AuthSvc as Authentication Service
    participant DashboardSvc as Dashboard Service
    participant Cache as Redis Cache
    participant CoreBanking as Core Banking System
    participant Database as PostgreSQL DB
    participant AuditSvc as Audit Service

    Parent->>WebApp: Request dashboard view
    WebApp->>APIGateway: GET /youth-accounts/{id}/dashboard
    APIGateway->>AuthSvc: Validate OAuth token
    AuthSvc-->>APIGateway: Token valid + permissions
    APIGateway->>DashboardSvc: Forward request with user context
    
    DashboardSvc->>Cache: Check cached dashboard data
    alt Cache Hit
        Cache-->>DashboardSvc: Return cached data
    else Cache Miss
        DashboardSvc->>Database: Get account settings
        Database-->>DashboardSvc: Account settings
        DashboardSvc->>CoreBanking: Get current balance
        CoreBanking-->>DashboardSvc: Balance data
        DashboardSvc->>CoreBanking: Get recent transactions (5)
        CoreBanking-->>DashboardSvc: Transaction data
        DashboardSvc->>Cache: Store aggregated data
    end
    
    DashboardSvc->>Database: Calculate spending summary
    Database-->>DashboardSvc: Spending analytics
    DashboardSvc->>DashboardSvc: Aggregate dashboard response
    DashboardSvc-->>APIGateway: Dashboard data (JSON)
    APIGateway->>AuditSvc: Log dashboard access
    APIGateway-->>WebApp: HTTP 200 + Dashboard JSON
    WebApp->>WebApp: Render dashboard UI
    WebApp-->>Parent: Display dashboard

    Note over Parent,AuditSvc: Response time < 200ms
    Note over DashboardSvc,Cache: Cache TTL: 5 minutes
```

---

## 2. Fund Transfer Sequence

**Mapped to ADR**: SCIB-27 - Create API for parent to transfer funds to youth account

```mermaid
sequenceDiagram
    participant Parent as Parent/Guardian
    participant WebApp as Web Application
    participant APIGateway as API Gateway
    participant AuthSvc as Authentication Service
    participant TransferSvc as Transfer Service
    participant PaymentSvc as Payment Service
    participant CoreBanking as Core Banking System
    participant Database as PostgreSQL DB
    participant AuditSvc as Audit Service
    participant NotificationSvc as Notification Service

    Parent->>WebApp: Initiate fund transfer
    WebApp->>WebApp: Validate transfer form
    WebApp->>APIGateway: POST /youth-accounts/{id}/fund-transfer
    APIGateway->>AuthSvc: Validate OAuth token + permissions
    AuthSvc-->>APIGateway: Authorization confirmed
    APIGateway->>TransferSvc: Transfer request + user context
    
    TransferSvc->>Database: Validate account ownership
    Database-->>TransferSvc: Ownership confirmed
    TransferSvc->>CoreBanking: Check source account balance
    CoreBanking-->>TransferSvc: Balance sufficient
    TransferSvc->>Database: Check spending limits
    Database-->>TransferSvc: Limits validated
    
    TransferSvc->>Database: Create transfer record (PENDING)
    Database-->>TransferSvc: Transfer ID generated
    TransferSvc->>PaymentSvc: Execute internal transfer
    PaymentSvc->>CoreBanking: Debit source account
    CoreBanking-->>PaymentSvc: Debit successful
    PaymentSvc->>CoreBanking: Credit youth account
    CoreBanking-->>PaymentSvc: Credit successful
    PaymentSvc-->>TransferSvc: Transfer completed
    
    TransferSvc->>Database: Update transfer status (COMPLETED)
    TransferSvc->>AuditSvc: Log transfer completion
    TransferSvc->>NotificationSvc: Send transfer notification
    TransferSvc-->>APIGateway: Transfer confirmation
    APIGateway-->>WebApp: HTTP 201 + Transfer details
    WebApp-->>Parent: Transfer success confirmation
    
    NotificationSvc->>Parent: Email/SMS notification
    
    Note over TransferSvc,PaymentSvc: Idempotency key prevents duplicates
    Note over TransferSvc,Database: ACID transaction ensures consistency
```

---

## 3. Spending Limit Configuration Sequence

**Mapped to ADR**: SCIB-28 - Create API for configuring youth spending limit

```mermaid
sequenceDiagram
    participant Parent as Parent/Guardian
    participant WebApp as Web Application
    participant APIGateway as API Gateway
    participant AuthSvc as Authentication Service
    participant LimitSvc as Limit Service
    participant Database as PostgreSQL DB
    participant Cache as Redis Cache
    participant AuditSvc as Audit Service
    participant NotificationSvc as Notification Service

    Parent->>WebApp: Access spending limit settings
    WebApp->>APIGateway: GET /youth-accounts/{id}/spending-limit
    APIGateway->>AuthSvc: Validate token
    AuthSvc-->>APIGateway: Authorized
    APIGateway->>LimitSvc: Get current limits
    LimitSvc->>Database: Query spending limits
    Database-->>LimitSvc: Current limit configuration
    LimitSvc-->>APIGateway: Limit data
    APIGateway-->>WebApp: HTTP 200 + Limits
    WebApp-->>Parent: Display current limits
    
    Parent->>WebApp: Modify spending limits
    WebApp->>WebApp: Validate limit values
    WebApp->>APIGateway: PUT /youth-accounts/{id}/spending-limit
    APIGateway->>AuthSvc: Validate authorization
    AuthSvc-->>APIGateway: Authorized for limit changes
    APIGateway->>LimitSvc: Update limit request
    
    LimitSvc->>LimitSvc: Validate business rules
    LimitSvc->>Database: Check account status
    Database-->>LimitSvc: Account active
    LimitSvc->>Database: Update spending limits
    Database-->>LimitSvc: Limits updated
    LimitSvc->>Cache: Invalidate cached limits
    LimitSvc->>AuditSvc: Log limit change
    LimitSvc->>NotificationSvc: Send limit change notification
    LimitSvc-->>APIGateway: Update confirmation
    APIGateway-->>WebApp: HTTP 200 + Updated limits
    WebApp-->>Parent: Confirmation message
    
    NotificationSvc->>Parent: Email confirmation
    
    Note over LimitSvc,Database: Effective date handling
    Note over LimitSvc,Cache: Real-time limit enforcement
```

---

## 4. Transaction History Retrieval Sequence

**Mapped to ADR**: SCIB-29 - Create API to retrieve youth account transaction history

```mermaid
sequenceDiagram
    participant Parent as Parent/Guardian
    participant WebApp as Web Application
    participant APIGateway as API Gateway
    participant AuthSvc as Authentication Service
    participant TransactionSvc as Transaction Service
    participant CoreBanking as Core Banking System
    participant Cache as Redis Cache
    participant Database as PostgreSQL DB
    participant AuditSvc as Audit Service

    Parent->>WebApp: Request transaction history
    WebApp->>WebApp: Build query parameters (pagination, filters)
    WebApp->>APIGateway: GET /youth-accounts/{id}/transactions?page=1&limit=20
    APIGateway->>AuthSvc: Validate access token
    AuthSvc-->>APIGateway: Access granted
    APIGateway->>TransactionSvc: Transaction query + filters
    
    TransactionSvc->>TransactionSvc: Validate query parameters
    TransactionSvc->>Cache: Check cached results
    alt Cache Hit (for same query)
        Cache-->>TransactionSvc: Cached transaction page
    else Cache Miss
        TransactionSvc->>CoreBanking: Query transaction ledger
        CoreBanking-->>TransactionSvc: Raw transaction data
        TransactionSvc->>Database: Get merchant details
        Database-->>TransactionSvc: Merchant information
        TransactionSvc->>TransactionSvc: Format and paginate results
        TransactionSvc->>Cache: Cache formatted results (5 min TTL)
    end
    
    TransactionSvc->>TransactionSvc: Calculate pagination metadata
    TransactionSvc->>TransactionSvc: Generate transaction summary
    TransactionSvc-->>APIGateway: Paginated transaction response
    APIGateway->>AuditSvc: Log transaction history access
    APIGateway-->>WebApp: HTTP 200 + Transaction data
    WebApp->>WebApp: Render transaction list
    WebApp-->>Parent: Display transaction history
    
    Note over TransactionSvc,Cache: Cache key includes filters
    Note over TransactionSvc,CoreBanking: Real-time transaction data
```

---

## 5. Account Validation Sequence

**Mapped to**: Account validation and security checks

```mermaid
sequenceDiagram
    participant Client as Client Application
    participant APIGateway as API Gateway
    participant AuthSvc as Authentication Service
    participant ValidationSvc as Validation Service
    participant Database as PostgreSQL DB
    participant CoreBanking as Core Banking System
    participant ComplianceSvc as Compliance Service
    participant AuditSvc as Audit Service

    Client->>APIGateway: GET /youth-accounts/{id}/validate
    APIGateway->>AuthSvc: Validate request token
    AuthSvc-->>APIGateway: Token validated
    APIGateway->>ValidationSvc: Account validation request
    
    ValidationSvc->>Database: Check account existence
    Database-->>ValidationSvc: Account found
    ValidationSvc->>Database: Verify account ownership
    Database-->>ValidationSvc: Ownership confirmed
    ValidationSvc->>CoreBanking: Check account status
    CoreBanking-->>ValidationSvc: Account status: ACTIVE
    
    ValidationSvc->>ComplianceSvc: Run compliance checks
    ComplianceSvc->>ComplianceSvc: KYC verification
    ComplianceSvc->>ComplianceSvc: AML screening
    ComplianceSvc->>ComplianceSvc: PCI compliance check
    ComplianceSvc-->>ValidationSvc: Compliance status: PASSED
    
    ValidationSvc->>ValidationSvc: Determine access permissions
    ValidationSvc->>ValidationSvc: Check active restrictions
    ValidationSvc-->>APIGateway: Validation response
    APIGateway->>AuditSvc: Log validation request
    APIGateway-->>Client: HTTP 200 + Validation result
    
    Note over ValidationSvc,ComplianceSvc: Real-time compliance validation
    Note over ValidationSvc,Database: Cached permission matrix
```

---

## 6. Error Handling Sequence

**Generic error handling across all APIs**

```mermaid
sequenceDiagram
    participant Client as Client Application
    participant APIGateway as API Gateway
    participant Service as Microservice
    participant Database as Database
    participant AuditSvc as Audit Service
    participant NotificationSvc as Notification Service

    Client->>APIGateway: API Request
    APIGateway->>Service: Forward request
    Service->>Database: Database operation
    Database-->>Service: Database error
    
    Service->>Service: Classify error type
    Service->>Service: Generate error response
    Service->>AuditSvc: Log error event
    
    alt Critical Error
        Service->>NotificationSvc: Alert operations team
        NotificationSvc->>NotificationSvc: Send critical alert
    else Business Rule Violation
        Service->>Service: Return business error
    else Transient Error
        Service->>Service: Implement retry logic
    end
    
    Service-->>APIGateway: Error response
    APIGateway->>APIGateway: Add correlation ID
    APIGateway-->>Client: HTTP error + details
    
    Note over Service,AuditSvc: All errors logged for analysis
    Note over APIGateway,Client: Consistent error format
```

---

## 7. Authentication and Authorization Sequence

**OAuth 2.0 flow for API access**

```mermaid
sequenceDiagram
    participant User as Parent/Guardian
    participant WebApp as Web Application
    participant AuthServer as OAuth Authorization Server
    participant APIGateway as API Gateway
    participant Service as Youth Account Service
    participant Database as User Database

    User->>WebApp: Login request
    WebApp->>AuthServer: Authorization request (OAuth 2.0)
    AuthServer->>Database: Validate credentials
    Database-->>AuthServer: User authenticated
    AuthServer->>AuthServer: Generate access token + refresh token
    AuthServer-->>WebApp: Authorization code
    WebApp->>AuthServer: Exchange code for tokens
    AuthServer-->>WebApp: Access token + Refresh token
    
    WebApp->>APIGateway: API request + Bearer token
    APIGateway->>AuthServer: Validate access token
    AuthServer-->>APIGateway: Token valid + user permissions
    APIGateway->>Service: Authorized request + user context
    Service-->>APIGateway: Service response
    APIGateway-->>WebApp: API response
    
    Note over AuthServer,Database: MFA validation included
    Note over APIGateway,Service: JWT token with claims
```

---

## Sequence Diagram Summary

### Key Architectural Patterns Demonstrated:

1. **API Gateway Pattern**: Centralized entry point for all API requests
2. **Authentication/Authorization**: OAuth 2.0 with JWT tokens
3. **Caching Strategy**: Redis for performance optimization
4. **Audit Logging**: Comprehensive audit trail for all operations
5. **Error Handling**: Consistent error handling across all services
6. **Microservices**: Loosely coupled service architecture
7. **Database Transactions**: ACID compliance for financial operations
8. **Real-time Notifications**: Event-driven notification system

### Performance Considerations:

- **Caching**: Strategic caching to achieve <200ms response times
- **Pagination**: Efficient pagination for large data sets
- **Connection Pooling**: Database connection optimization
- **Async Processing**: Non-blocking operations where possible

### Security Features:

- **Token Validation**: Every request validates OAuth tokens
- **Permission Checks**: Granular permission validation
- **Audit Logging**: Complete audit trail for compliance
- **Input Validation**: Comprehensive input validation
- **Encryption**: End-to-end encryption for sensitive data

### Compliance and Auditability:

- **Transaction Logging**: All financial transactions logged
- **Access Logging**: User access patterns tracked
- **Error Logging**: System errors captured for analysis
- **Compliance Checks**: Real-time compliance validation

---

**Document Version**: 1.0
**Last Updated**: 2024
**Review Cycle**: Monthly
**Approval**: Pending Architecture Review Board
**Classification**: Internal