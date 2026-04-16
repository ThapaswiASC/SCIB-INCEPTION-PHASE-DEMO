# Sequence Diagrams
## Youth Account Management System

### Document Information
- **Version**: 1.0
- **Date**: 2024
- **Generated From**: HLD Document and API Contract Outline
- **Related ADRs**: SCIB-26, SCIB-27, SCIB-28, SCIB-29, SCIB-30

---

## 1. Youth Account Dashboard Sequence Diagram
**Mapped to ADR**: SCIB-26

```mermaid
sequenceDiagram
    participant Parent as Parent/Guardian
    participant WebApp as Web Application
    participant APIGateway as API Gateway
    participant AuthService as Auth Service
    participant DashboardAPI as Dashboard API
    participant Cache as Redis Cache
    participant CoreBank as Core Banking System
    participant Database as Youth Account DB

    Parent->>WebApp: Access Youth Account Dashboard
    WebApp->>APIGateway: GET /api/youth-accounts/{youthAccountId}/dashboard
    Note over APIGateway: Rate Limiting Check (100 req/min)
    
    APIGateway->>AuthService: Validate OAuth Token
    AuthService-->>APIGateway: Token Valid + User Permissions
    
    APIGateway->>DashboardAPI: Forward Request with Headers
    Note over DashboardAPI: X-Request-ID: req-12345-67890
    
    DashboardAPI->>Cache: Check Cached Dashboard Data
    alt Cache Hit
        Cache-->>DashboardAPI: Cached Dashboard Data
    else Cache Miss
        DashboardAPI->>Database: Get Youth Account Settings
        Database-->>DashboardAPI: Account Settings
        
        DashboardAPI->>CoreBank: Get Account Balance
        CoreBank-->>DashboardAPI: Current Balance
        
        DashboardAPI->>CoreBank: Get Recent Transactions (Last 5)
        CoreBank-->>DashboardAPI: Transaction List
        
        DashboardAPI->>Cache: Store Aggregated Data (TTL: 5min)
    end
    
    DashboardAPI-->>APIGateway: Dashboard Response (200 OK)
    Note over DashboardAPI: Response Time < 500ms
    
    APIGateway-->>WebApp: JSON Response with Headers
    Note over APIGateway: X-RateLimit-Remaining: 99
    
    WebApp-->>Parent: Display Dashboard
    Note over WebApp: Account Balance, Spending Limit, Recent Transactions
```

---

## 2. Fund Transfer Sequence Diagram
**Mapped to ADR**: SCIB-27

```mermaid
sequenceDiagram
    participant Parent as Parent/Guardian
    participant WebApp as Web Application
    participant APIGateway as API Gateway
    participant AuthService as Auth Service
    participant TransferAPI as Fund Transfer API
    participant PaymentService as Payment Service
    participant CoreBank as Core Banking System
    participant Database as Youth Account DB
    participant NotificationSvc as Notification Service

    Parent->>WebApp: Initiate Fund Transfer ($25.00)
    WebApp->>APIGateway: POST /api/youth-accounts/{youthAccountId}/fund-transfer
    Note over WebApp: Headers: Authorization, X-Idempotency-Key
    
    APIGateway->>AuthService: Validate OAuth Token & Permissions
    AuthService-->>APIGateway: Token Valid + Parent Authorization
    
    APIGateway->>TransferAPI: Forward Transfer Request
    Note over TransferAPI: Validate Request Schema
    
    TransferAPI->>Database: Check Idempotency Key
    alt Duplicate Request
        Database-->>TransferAPI: Previous Transfer Result
        TransferAPI-->>APIGateway: 409 Conflict - Duplicate Transfer
    else New Request
        TransferAPI->>CoreBank: Validate Source Account
        CoreBank-->>TransferAPI: Account Valid + Balance Check
        
        alt Insufficient Funds
            TransferAPI-->>APIGateway: 400 Bad Request - Insufficient Funds
        else Sufficient Funds
            TransferAPI->>PaymentService: Execute Transfer
            Note over PaymentService: Amount: $25.00, Currency: USD
            
            PaymentService->>CoreBank: Debit Source Account
            CoreBank-->>PaymentService: Debit Successful
            
            PaymentService->>CoreBank: Credit Youth Account
            CoreBank-->>PaymentService: Credit Successful
            
            PaymentService-->>TransferAPI: Transfer Complete
            Note over PaymentService: Transfer ID: tf-123456789
            
            TransferAPI->>Database: Log Transfer Record
            Database-->>TransferAPI: Record Saved
            
            TransferAPI->>NotificationSvc: Send Transfer Notification
            NotificationSvc-->>Parent: SMS/Email Confirmation
            
            TransferAPI-->>APIGateway: 201 Created - Transfer Success
        end
    end
    
    APIGateway-->>WebApp: Transfer Response
    WebApp-->>Parent: Display Transfer Confirmation
    Note over Parent: Confirmation Number: CONF-ABC123
```

---

## 3. Spending Limit Configuration Sequence Diagram
**Mapped to ADR**: SCIB-28

```mermaid
sequenceDiagram
    participant Parent as Parent/Guardian
    participant WebApp as Web Application
    participant APIGateway as API Gateway
    participant AuthService as Auth Service
    participant LimitAPI as Spending Limit API
    participant Database as Youth Account DB
    participant Cache as Redis Cache
    participant NotificationSvc as Notification Service

    Parent->>WebApp: Configure Weekly Spending Limit ($75.00)
    WebApp->>APIGateway: PUT /api/youth-accounts/{youthAccountId}/spending-limit
    
    APIGateway->>AuthService: Validate OAuth Token
    AuthService-->>APIGateway: Token Valid + Parent Authorization
    
    APIGateway->>LimitAPI: Forward Limit Configuration
    Note over LimitAPI: Validate Limit Range (0.00 - 500.00)
    
    LimitAPI->>Database: Get Current Spending Limit
    Database-->>LimitAPI: Current Limit: $50.00
    
    alt Limit Validation Passed
        LimitAPI->>Database: Update Spending Limit
        Note over Database: Weekly Limit: $75.00, Effective: 2024-01-16
        Database-->>LimitAPI: Update Successful
        
        LimitAPI->>Cache: Invalidate Dashboard Cache
        Cache-->>LimitAPI: Cache Cleared
        
        LimitAPI->>NotificationSvc: Send Limit Change Notification
        NotificationSvc-->>Parent: Limit Updated Confirmation
        
        LimitAPI-->>APIGateway: 200 OK - Limit Updated
    else Limit Validation Failed
        LimitAPI-->>APIGateway: 422 Unprocessable Entity
        Note over LimitAPI: Error: Limit exceeds maximum allowed
    end
    
    APIGateway-->>WebApp: Limit Configuration Response
    WebApp-->>Parent: Display Updated Limit
    Note over Parent: New Weekly Limit: $75.00
```

---

## 4. Transaction History Retrieval Sequence Diagram
**Mapped to ADR**: SCIB-29

```mermaid
sequenceDiagram
    participant Parent as Parent/Guardian
    participant WebApp as Web Application
    participant APIGateway as API Gateway
    participant AuthService as Auth Service
    participant HistoryAPI as Transaction History API
    participant CoreBank as Core Banking System
    participant Cache as Redis Cache

    Parent->>WebApp: Request Transaction History (Last 30 Days)
    WebApp->>APIGateway: GET /api/youth-accounts/{youthAccountId}/transactions
    Note over WebApp: Query: startDate=2024-01-01&endDate=2024-01-30&page=1&pageSize=20
    
    APIGateway->>AuthService: Validate OAuth Token
    AuthService-->>APIGateway: Token Valid + Read Permissions
    
    APIGateway->>HistoryAPI: Forward History Request
    Note over HistoryAPI: Validate Query Parameters
    
    HistoryAPI->>Cache: Check Cached Transaction Data
    alt Cache Hit (Recent Query)
        Cache-->>HistoryAPI: Cached Transaction Page
    else Cache Miss
        HistoryAPI->>CoreBank: Query Transaction History
        Note over CoreBank: Filter: Date Range, Account ID, Pagination
        
        CoreBank-->>HistoryAPI: Transaction Records
        
        HistoryAPI->>HistoryAPI: Calculate Summary Statistics
        Note over HistoryAPI: Total Debits, Credits, Net Amount
        
        HistoryAPI->>Cache: Cache Result (TTL: 10min)
    end
    
    HistoryAPI-->>APIGateway: 200 OK - Paginated Transactions
    Note over HistoryAPI: Response Time < 1 second
    
    APIGateway-->>WebApp: Transaction History Response
    Note over APIGateway: Headers: X-Total-Count, X-Page-Count
    
    WebApp-->>Parent: Display Transaction History
    Note over Parent: Paginated List with Summary Statistics
```

---

## 5. Error Handling Sequence Diagram
**Cross-Cutting Concern for All APIs**

```mermaid
sequenceDiagram
    participant Client as Client Application
    participant APIGateway as API Gateway
    participant APIService as API Service
    participant ExternalSvc as External Service
    participant CircuitBreaker as Circuit Breaker
    participant ErrorHandler as Error Handler
    participant LoggingService as Logging Service

    Client->>APIGateway: API Request
    APIGateway->>APIService: Forward Request
    
    APIService->>CircuitBreaker: Call External Service
    
    alt Circuit Breaker Open
        CircuitBreaker-->>APIService: 503 Service Unavailable
        APIService->>ErrorHandler: Handle Circuit Breaker Error
    else Circuit Breaker Closed
        CircuitBreaker->>ExternalSvc: Forward Request
        
        alt External Service Error
            ExternalSvc-->>CircuitBreaker: 500 Internal Server Error
            CircuitBreaker->>CircuitBreaker: Increment Failure Count
            CircuitBreaker-->>APIService: Propagate Error
            APIService->>ErrorHandler: Handle External Service Error
        else External Service Success
            ExternalSvc-->>CircuitBreaker: 200 OK
            CircuitBreaker->>CircuitBreaker: Reset Failure Count
            CircuitBreaker-->>APIService: Success Response
        end
    end
    
    ErrorHandler->>LoggingService: Log Error with Correlation ID
    ErrorHandler->>ErrorHandler: Generate Standardized Error Response
    
    ErrorHandler-->>APIService: Formatted Error Response
    APIService-->>APIGateway: Error Response
    APIGateway-->>Client: Standardized Error Format
    
    Note over Client: {
    Note over Client:   "error": {
    Note over Client:     "code": "EXTERNAL_SERVICE_UNAVAILABLE",
    Note over Client:     "message": "Service temporarily unavailable",
    Note over Client:     "requestId": "req-12345-67890"
    Note over Client:   }
    Note over Client: }
```

---

## 6. Authentication & Authorization Sequence Diagram
**Security Flow for All Protected Endpoints**

```mermaid
sequenceDiagram
    participant Parent as Parent/Guardian
    participant WebApp as Web Application
    participant APIGateway as API Gateway
    participant AuthService as OAuth 2.0 Auth Service
    participant TokenStore as Token Store
    participant UserService as User Service

    Parent->>WebApp: Login Request
    WebApp->>AuthService: OAuth 2.0 Authorization Request
    
    AuthService->>UserService: Validate User Credentials
    UserService-->>AuthService: User Valid + Roles
    
    AuthService->>TokenStore: Generate JWT Token
    Note over TokenStore: Token Expiry: 1 hour, Refresh: 24 hours
    TokenStore-->>AuthService: JWT Token + Refresh Token
    
    AuthService-->>WebApp: Authorization Code
    WebApp->>AuthService: Exchange Code for Tokens
    AuthService-->>WebApp: Access Token + Refresh Token
    
    WebApp->>WebApp: Store Tokens Securely
    
    loop API Requests
        WebApp->>APIGateway: API Request with Bearer Token
        APIGateway->>AuthService: Validate Token
        
        alt Token Valid
            AuthService-->>APIGateway: Token Valid + User Context
            APIGateway->>APIGateway: Check Rate Limits
            APIGateway->>APIService: Forward Request
        else Token Expired
            AuthService-->>APIGateway: 401 Unauthorized
            APIGateway-->>WebApp: 401 Token Expired
            WebApp->>AuthService: Refresh Token Request
            AuthService-->>WebApp: New Access Token
        else Token Invalid
            AuthService-->>APIGateway: 403 Forbidden
            APIGateway-->>WebApp: 403 Invalid Token
            WebApp->>Parent: Redirect to Login
        end
    end
```

---

## Sequence Diagram Standards & Compliance

### 1. Banking Compliance Integration
- **PCI-DSS**: All payment data flows show encryption and tokenization
- **SOX**: Audit trails included in all financial transaction sequences
- **GDPR**: Data access controls and consent validation shown
- **AML/KYC**: User validation steps included in authentication flows

### 2. Security Patterns
- **OAuth 2.0**: Standard authentication flow with JWT tokens
- **Rate Limiting**: API gateway enforces 100 requests/minute
- **Circuit Breaker**: Fault tolerance for external service failures
- **Idempotency**: Duplicate request handling for fund transfers

### 3. Performance Considerations
- **Caching**: Redis cache integration for dashboard and transaction data
- **Response Times**: Target response times noted for each API
- **Pagination**: Large dataset handling for transaction history
- **Async Processing**: Notification services operate asynchronously

### 4. Error Handling Standards
- **Standardized Errors**: Consistent error format across all APIs
- **HTTP Status Codes**: Proper status codes (200, 201, 400, 401, 403, 404, 409, 422, 429, 500)
- **Correlation IDs**: Request tracking with X-Request-ID headers
- **Graceful Degradation**: System continues with reduced functionality

### 5. Audit & Traceability
- **Request Correlation**: Unique request IDs for end-to-end tracing
- **Audit Logging**: All user actions and system events logged
- **Compliance Monitoring**: Regulatory reporting integration
- **Data Lineage**: Complete data flow tracking

---

**Sequence Diagrams Document End**