# Sequence Diagrams
# Youth Account Management System

## Document Information
- **Document Version**: 1.0
- **Date**: 2024
- **System**: Youth Account Management System
- **Traceability**: SCIB-25, SCIB-26, SCIB-27, SCIB-28, SCIB-29, SCIB-30
- **Compliance**: PCI-DSS Level 1, GDPR, SOX, BSA

---

## 1. Authentication & Authorization Flow

### Description
This sequence diagram shows the OAuth 2.0 + JWT authentication flow for parent users accessing youth account management features.

```mermaid
sequenceDiagram
    participant Client as Parent Client App
    participant Gateway as API Gateway
    participant Auth as Auth Service
    participant JWT as JWT Service
    participant Account as Youth Account Service
    participant DB as Database
    
    Client->>Gateway: POST /auth/login (credentials)
    Gateway->>Auth: Validate credentials
    Auth->>DB: Check user credentials
    DB-->>Auth: User validated
    Auth->>JWT: Generate JWT token
    JWT-->>Auth: JWT token (15 min expiry)
    Auth-->>Gateway: Authentication response
    Gateway-->>Client: JWT token + refresh token
    
    Note over Client,DB: Subsequent API calls
    Client->>Gateway: GET /youth-accounts/{id}/dashboard (Bearer token)
    Gateway->>JWT: Validate JWT token
    JWT-->>Gateway: Token valid
    Gateway->>Account: Get dashboard data
    Account->>DB: Query account data
    DB-->>Account: Account information
    Account-->>Gateway: Dashboard response
    Gateway-->>Client: Dashboard data (200 OK)
```

**Key Security Controls:**
- JWT tokens expire in 15 minutes
- Rate limiting: 1000 requests/hour per user
- MFA required for parent authentication
- RBAC enforcement at gateway level

---

## 2. Youth Account Dashboard Retrieval (SCIB-26)

### Description
Sequence diagram for retrieving consolidated youth account dashboard information including balance, spending limits, and recent transactions.

```mermaid
sequenceDiagram
    participant Parent as Parent Client
    participant Gateway as API Gateway
    participant YouthSvc as Youth Account Service
    participant LimitSvc as Spending Limit Service
    participant TxnSvc as Transaction Service
    participant Cache as Redis Cache
    participant DB as PostgreSQL DB
    participant Audit as Audit Service
    
    Parent->>Gateway: GET /youth-accounts/{youthAccountId}/dashboard
    Gateway->>Gateway: Validate JWT & RBAC
    Gateway->>YouthSvc: Get dashboard data
    
    par Parallel Data Retrieval
        YouthSvc->>Cache: Check account cache
        Cache-->>YouthSvc: Cache miss
        YouthSvc->>DB: Query account balance & status
        DB-->>YouthSvc: Account data
        YouthSvc->>Cache: Update cache (TTL: 5 min)
    and
        YouthSvc->>LimitSvc: Get spending limits
        LimitSvc->>DB: Query current week spending
        DB-->>LimitSvc: Spending data
        LimitSvc-->>YouthSvc: Limit information
    and
        YouthSvc->>TxnSvc: Get recent transactions (last 10)
        TxnSvc->>DB: Query recent transactions
        DB-->>TxnSvc: Transaction list
        TxnSvc-->>YouthSvc: Recent transactions
    end
    
    YouthSvc->>YouthSvc: Aggregate dashboard data
    YouthSvc->>Audit: Log dashboard access
    YouthSvc-->>Gateway: Dashboard response
    Gateway-->>Parent: 200 OK + Dashboard JSON
    
    Note over Parent,Audit: Response includes: balance, spending limit,<br/>current week spent, remaining limit,<br/>recent transactions, account status
```

**Performance Requirements:**
- API response time < 200ms (95th percentile)
- Dashboard loading < 2 seconds
- Parallel data retrieval for optimization
- Redis caching with 5-minute TTL

---

## 3. Fund Transfer Process (SCIB-27)

### Description
Sequence diagram for transferring funds from parent account to youth account with real-time processing and validation.

```mermaid
sequenceDiagram
    participant Parent as Parent Client
    participant Gateway as API Gateway
    participant TransferSvc as Fund Transfer Service
    participant YouthSvc as Youth Account Service
    participant CoreBank as Core Banking System
    participant PayGateway as Payment Gateway
    participant NotifSvc as Notification Service
    participant Audit as Audit Service
    participant DB as Database
    
    Parent->>Gateway: POST /youth-accounts/{id}/fund-transfer
    Gateway->>Gateway: Validate JWT, rate limit (10/hour)
    Gateway->>TransferSvc: Process fund transfer
    
    TransferSvc->>TransferSvc: Validate request parameters
    TransferSvc->>DB: Begin transaction
    
    par Account Validation
        TransferSvc->>YouthSvc: Validate youth account
        YouthSvc->>DB: Check account status
        DB-->>YouthSvc: Account active
        YouthSvc-->>TransferSvc: Account valid
    and
        TransferSvc->>CoreBank: Validate parent account
        CoreBank-->>TransferSvc: Account valid + balance
    end
    
    alt Sufficient Funds
        TransferSvc->>CoreBank: Debit parent account
        CoreBank-->>TransferSvc: Debit successful
        
        TransferSvc->>PayGateway: Credit youth account
        PayGateway-->>TransferSvc: Credit successful
        
        TransferSvc->>DB: Update account balances
        TransferSvc->>DB: Record transfer transaction
        TransferSvc->>DB: Commit transaction
        
        TransferSvc->>NotifSvc: Send transfer notifications
        NotifSvc->>Parent: Email/SMS notification
        NotifSvc->>YouthSvc: In-app notification
        
        TransferSvc->>Audit: Log successful transfer
        TransferSvc-->>Gateway: 200 OK + Transfer details
        
    else Insufficient Funds
        TransferSvc->>DB: Rollback transaction
        TransferSvc->>Audit: Log failed transfer attempt
        TransferSvc-->>Gateway: 400 Bad Request + Error
    end
    
    Gateway-->>Parent: Transfer response
    
    Note over Parent,Audit: Processing time < 5 seconds<br/>Automatic rollback on any failure<br/>Complete audit trail maintained
```

**Business Rules:**
- Minimum transfer: $1.00
- Maximum transfer: $1,000.00
- Rate limit: 10 transfers per hour
- ACID transaction properties enforced
- Automatic rollback on failure

---

## 4. Spending Limit Configuration (SCIB-28)

### Description
Sequence diagram for configuring weekly spending limits for youth accounts with notification preferences.

```mermaid
sequenceDiagram
    participant Parent as Parent Client
    participant Gateway as API Gateway
    participant LimitSvc as Spending Limit Service
    participant YouthSvc as Youth Account Service
    participant NotifSvc as Notification Service
    participant Audit as Audit Service
    participant DB as Database
    
    Parent->>Gateway: PUT /youth-accounts/{id}/spending-limit
    Gateway->>Gateway: Validate JWT & parent ownership
    Gateway->>LimitSvc: Update spending limit
    
    LimitSvc->>LimitSvc: Validate limit parameters
    Note over LimitSvc: Min: $1.00, Max: $10,000.00
    
    LimitSvc->>YouthSvc: Verify account ownership
    YouthSvc->>DB: Check parent-youth relationship
    DB-->>YouthSvc: Ownership confirmed
    YouthSvc-->>LimitSvc: Account accessible
    
    LimitSvc->>DB: Begin transaction
    LimitSvc->>DB: Get current limit settings
    DB-->>LimitSvc: Current limit: $75.00
    
    LimitSvc->>DB: Update spending limit configuration
    LimitSvc->>DB: Update notification preferences
    LimitSvc->>DB: Set effective date (next Monday)
    LimitSvc->>DB: Commit transaction
    
    LimitSvc->>NotifSvc: Send limit change notification
    NotifSvc->>Parent: Email confirmation
    NotifSvc->>YouthSvc: Youth app notification
    
    LimitSvc->>Audit: Log limit configuration change
    Note over Audit: Previous: $75.00 → New: $100.00<br/>Effective: 2024-01-22
    
    LimitSvc-->>Gateway: 200 OK + Updated configuration
    Gateway-->>Parent: Limit update confirmation
    
    Note over Parent,Audit: Notification thresholds:<br/>75% spent, 90% spent, limit exceeded
```

**Configuration Options:**
- Weekly limit: $1.00 - $10,000.00
- Effective date: Next Monday (default)
- Notification preferences: 75%, 90%, exceed
- Automatic reset every Monday

---

## 5. Transaction History Retrieval (SCIB-29)

### Description
Sequence diagram for retrieving transaction history with filtering, pagination, and analytics.

```mermaid
sequenceDiagram
    participant Parent as Parent Client
    participant Gateway as API Gateway
    participant TxnSvc as Transaction Service
    participant Cache as Redis Cache
    participant DB as PostgreSQL DB
    participant Analytics as Analytics Service
    participant Audit as Audit Service
    
    Parent->>Gateway: GET /youth-accounts/{id}/transactions?startDate=2024-01-01&endDate=2024-01-31&page=1&pageSize=20
    Gateway->>Gateway: Validate JWT & query parameters
    Gateway->>TxnSvc: Get transaction history
    
    TxnSvc->>TxnSvc: Validate date range & pagination
    TxnSvc->>Cache: Check cache key (account_id:filters:page)
    Cache-->>TxnSvc: Cache miss
    
    par Transaction Query
        TxnSvc->>DB: Query transactions with filters
        Note over DB: SELECT * FROM transactions<br/>WHERE youth_account_id = ?<br/>AND date BETWEEN ? AND ?<br/>ORDER BY date DESC<br/>LIMIT 20 OFFSET 0
        DB-->>TxnSvc: Transaction records (20 items)
    and
        TxnSvc->>DB: Count total matching records
        DB-->>TxnSvc: Total count: 45 records
    and
        TxnSvc->>Analytics: Calculate spending summary
        Analytics->>DB: Aggregate spending data
        DB-->>Analytics: Totals by category
        Analytics-->>TxnSvc: Summary statistics
    end
    
    TxnSvc->>TxnSvc: Build paginated response
    TxnSvc->>Cache: Cache response (TTL: 10 min)
    TxnSvc->>Audit: Log transaction history access
    
    TxnSvc-->>Gateway: 200 OK + Transaction list + Pagination + Summary
    Gateway-->>Parent: Transaction history response
    
    Note over Parent,Audit: Response includes:<br/>- Filtered transactions (page 1 of 3)<br/>- Pagination metadata<br/>- Spending summary by category<br/>- Total debits/credits
```

**Query Capabilities:**
- Date range filtering
- Transaction type filtering (debit/credit)
- Status filtering (completed/pending/failed)
- Pagination (max 100 items per page)
- Sorting by date, amount, merchant
- Spending analytics and summaries

---

## 6. Real-time Spending Limit Enforcement

### Description
Sequence diagram for real-time transaction validation against spending limits during purchase attempts.

```mermaid
sequenceDiagram
    participant Merchant as Merchant Terminal
    participant PayGateway as Payment Gateway
    participant Gateway as API Gateway
    participant LimitSvc as Spending Limit Service
    participant TxnSvc as Transaction Service
    participant NotifSvc as Notification Service
    participant DB as Database
    participant Parent as Parent App
    
    Merchant->>PayGateway: Process youth card transaction ($25.00)
    PayGateway->>Gateway: POST /youth-accounts/{id}/validate-transaction
    Gateway->>Gateway: Validate merchant credentials
    Gateway->>LimitSvc: Validate transaction against limits
    
    LimitSvc->>DB: Get current week spending
    DB-->>LimitSvc: Current spent: $75.00
    LimitSvc->>DB: Get weekly limit
    DB-->>LimitSvc: Weekly limit: $100.00
    
    LimitSvc->>LimitSvc: Calculate: $75.00 + $25.00 = $100.00
    
    alt Within Limit
        LimitSvc->>TxnSvc: Pre-authorize transaction
        TxnSvc->>DB: Reserve spending amount
        LimitSvc-->>Gateway: APPROVED + remaining limit
        Gateway-->>PayGateway: Transaction approved
        PayGateway-->>Merchant: Payment authorized
        
        Note over Merchant,Parent: Transaction completes normally
        
        PayGateway->>Gateway: Transaction completed
        Gateway->>TxnSvc: Record final transaction
        TxnSvc->>DB: Update spending totals
        
    else Limit Exceeded
        LimitSvc->>LimitSvc: Calculate excess: $25.00 - $25.00 = $0.00 (at limit)
        LimitSvc->>NotifSvc: Send limit reached notification
        NotifSvc->>Parent: Push notification: "Spending limit reached"
        
        LimitSvc-->>Gateway: APPROVED_WITH_WARNING
        Gateway-->>PayGateway: Transaction approved (at limit)
        PayGateway-->>Merchant: Payment authorized
        
    else Over Limit
        Note over LimitSvc: If transaction would exceed limit by >$0
        LimitSvc->>NotifSvc: Send limit exceeded notification
        NotifSvc->>Parent: Push notification: "Transaction blocked - limit exceeded"
        
        LimitSvc-->>Gateway: BLOCKED + excess amount
        Gateway-->>PayGateway: Transaction declined
        PayGateway-->>Merchant: Payment declined
    end
    
    Note over Merchant,Parent: Real-time enforcement<br/>Parental notifications<br/>Audit trail maintained
```

**Enforcement Rules:**
- Real-time spending calculation
- Immediate parent notification
- Transaction blocking when over limit
- Weekly limit reset every Monday
- Grace period considerations

---

## 7. Error Handling & Recovery Flow

### Description
Sequence diagram showing error handling and recovery mechanisms for system failures.

```mermaid
sequenceDiagram
    participant Client as Parent Client
    participant Gateway as API Gateway
    participant Service as Youth Account Service
    participant DB as Database
    participant CircuitBreaker as Circuit Breaker
    participant Fallback as Fallback Service
    participant Monitor as Monitoring
    
    Client->>Gateway: API Request
    Gateway->>CircuitBreaker: Route to service
    CircuitBreaker->>Service: Forward request
    
    alt Normal Operation
        Service->>DB: Database query
        DB-->>Service: Success response
        Service-->>CircuitBreaker: Success
        CircuitBreaker-->>Gateway: Success
        Gateway-->>Client: 200 OK
        
    else Database Timeout
        Service->>DB: Database query
        DB-->>Service: Timeout (no response)
        Service->>Service: Retry with exponential backoff
        Service->>DB: Retry query
        DB-->>Service: Timeout again
        Service->>Service: Max retries exceeded
        
        Service->>Monitor: Log error + alert
        Service-->>CircuitBreaker: 500 Internal Server Error
        CircuitBreaker->>CircuitBreaker: Increment failure count
        
        alt Circuit Breaker Open
            CircuitBreaker->>Fallback: Route to fallback
            Fallback-->>CircuitBreaker: Cached/degraded response
            CircuitBreaker-->>Gateway: 206 Partial Content
            Gateway-->>Client: Degraded response + warning
            
        else Circuit Breaker Closed
            CircuitBreaker-->>Gateway: 500 Internal Server Error
            Gateway-->>Client: Standardized error response
        end
        
    else Service Unavailable
        CircuitBreaker->>Service: Forward request
        Service-->>CircuitBreaker: Service unavailable
        CircuitBreaker->>CircuitBreaker: Open circuit
        CircuitBreaker->>Fallback: Immediate fallback
        Fallback-->>CircuitBreaker: Cached data
        CircuitBreaker-->>Gateway: 503 Service Unavailable + cached data
        Gateway-->>Client: Graceful degradation response
    end
    
    Note over Client,Monitor: Error Recovery Features:<br/>- Exponential backoff retry<br/>- Circuit breaker pattern<br/>- Graceful degradation<br/>- Real-time monitoring & alerting
```

**Error Handling Strategy:**
- Circuit breaker pattern for external services
- Exponential backoff retry (max 3 attempts)
- Graceful degradation with cached data
- Standardized error response format
- Real-time monitoring and alerting

---

## Compliance & Audit Considerations

### Security Controls
- **Authentication**: OAuth 2.0 + JWT with 15-minute expiry
- **Authorization**: RBAC with minimum privilege principle
- **Rate Limiting**: Configurable per endpoint (1000/hour general, 10/hour transfers)
- **Input Validation**: All parameters validated at gateway level
- **Audit Logging**: Complete audit trail for all operations

### Performance Requirements
- **API Response**: < 200ms (95th percentile)
- **Dashboard Load**: < 2 seconds
- **Fund Transfer**: < 5 seconds processing
- **Concurrent Users**: 10,000 during peak hours
- **Throughput**: 50,000 API requests per minute

### Compliance Standards
- **PCI-DSS Level 1**: Payment processing compliance
- **GDPR**: Data privacy and protection
- **SOX**: Financial reporting controls
- **BSA**: Bank Secrecy Act compliance
- **WCAG 2.1 AA**: Accessibility standards

---

**Document Control:**
- Version: 1.0
- Last Updated: 2024
- Next Review: Monthly
- Approval: Enterprise Architecture Review Board
- Implementation Status: Design Phase Complete