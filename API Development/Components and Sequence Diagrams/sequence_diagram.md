# Sequence Diagrams
## Youth Account Management System

### Version: 1.0
### Date: 2024
### Generated from: HLD Document and API Contract Outline

---

## 1. Youth Account Dashboard Sequence Diagram

```mermaid
sequenceDiagram
    participant U as User (Parent/Guardian)
    participant WA as Web Application
    participant AG as API Gateway
    participant DS as Dashboard Service
    participant YS as Youth Account Service
    participant C as Cache (Redis)
    participant DB as Database (PostgreSQL)
    participant CBS as Core Banking System

    U->>WA: Request Dashboard
    WA->>AG: GET /youth-accounts/{id}/dashboard
    Note over AG: Authentication & Authorization
    AG->>DS: Route to Dashboard Service
    
    DS->>C: Check Cache for Account Data
    alt Cache Hit
        C-->>DS: Return Cached Data
    else Cache Miss
        DS->>YS: Get Account Details
        YS->>DB: Query Account Information
        DB-->>YS: Account Data
        YS->>CBS: Sync Balance Information
        CBS-->>YS: Current Balance
        YS-->>DS: Complete Account Data
        DS->>C: Cache Account Data (TTL: 5min)
    end
    
    DS->>DB: Get Recent Transactions (Last 5)
    DB-->>DS: Transaction History
    DS->>YS: Get Spending Limit Status
    YS->>DB: Query Spending Limits
    DB-->>YS: Limit Configuration
    YS-->>DS: Spending Status
    
    DS-->>AG: Dashboard Response
    AG-->>WA: JSON Response
    WA-->>U: Render Dashboard
    
    Note over U,CBS: Response Time: <200ms (95th percentile)
```

---

## 2. Fund Transfer Sequence Diagram

```mermaid
sequenceDiagram
    participant U as User (Parent)
    participant WA as Web Application
    participant AG as API Gateway
    participant TS as Transfer Service
    participant YS as Youth Account Service
    participant PS as Payment Service
    participant CBS as Core Banking System
    participant DB as Database
    participant NS as Notification Service
    participant AL as Audit Logger

    U->>WA: Initiate Fund Transfer
    WA->>AG: POST /youth-accounts/{id}/fund-transfer
    Note over AG: OAuth 2.0 Authentication
    Note over AG: Rate Limiting Check
    AG->>TS: Route Transfer Request
    
    TS->>AL: Log Transfer Initiation
    TS->>YS: Validate Youth Account
    YS->>DB: Check Account Status
    DB-->>YS: Account Valid
    YS-->>TS: Account Validation Success
    
    TS->>CBS: Validate Source Account
    CBS-->>TS: Source Account Valid
    TS->>CBS: Check Available Balance
    CBS-->>TS: Sufficient Funds
    
    TS->>PS: Initiate Payment Transaction
    Note over PS: PCI-DSS Compliant Processing
    PS->>CBS: Execute Fund Transfer
    CBS-->>PS: Transfer Successful
    PS-->>TS: Payment Confirmation
    
    TS->>DB: Record Transfer Transaction
    TS->>YS: Update Youth Account Balance
    YS->>DB: Update Account Data
    TS->>AL: Log Transfer Completion
    
    TS->>NS: Send Transfer Notification
    NS->>U: Email/SMS Notification
    
    TS-->>AG: Transfer Response
    AG-->>WA: JSON Response
    WA-->>U: Transfer Confirmation
    
    Note over U,AL: End-to-End Traceability
    Note over TS,CBS: Idempotent Operations
```

---

## 3. Spending Limit Configuration Sequence Diagram

```mermaid
sequenceDiagram
    participant P as Parent User
    participant WA as Web Application
    participant AG as API Gateway
    participant YS as Youth Account Service
    participant DB as Database
    participant C as Cache (Redis)
    participant NS as Notification Service
    participant AL as Audit Logger

    P->>WA: Configure Spending Limit
    WA->>AG: PUT /youth-accounts/{id}/spending-limit
    Note over AG: Parent Authorization Check
    AG->>YS: Route Limit Update Request
    
    YS->>AL: Log Limit Change Request
    YS->>DB: Validate Current Limits
    DB-->>YS: Current Configuration
    
    alt Limit Increase
        Note over YS: Business Rule: Requires Additional Validation
        YS->>YS: Validate Increase Rules
    else Limit Decrease
        Note over YS: Immediate Processing
    end
    
    YS->>DB: Update Spending Limits
    DB-->>YS: Update Confirmation
    YS->>C: Invalidate Cache
    C-->>YS: Cache Cleared
    
    YS->>AL: Log Successful Update
    YS->>NS: Send Limit Change Notification
    NS->>P: Confirmation Notification
    
    YS-->>AG: Update Response
    AG-->>WA: JSON Response
    WA-->>P: Limit Updated Confirmation
    
    Note over P,AL: Audit Trail for Compliance
```

---

## 4. Transaction History Retrieval Sequence Diagram

```mermaid
sequenceDiagram
    participant U as User
    participant WA as Web Application
    participant AG as API Gateway
    participant YS as Youth Account Service
    participant DB as Database (Partitioned)
    participant C as Cache (Redis)
    participant CBS as Core Banking System

    U->>WA: Request Transaction History
    WA->>AG: GET /youth-accounts/{id}/transactions?page=1&limit=20
    Note over AG: Authorization & Rate Limiting
    AG->>YS: Route History Request
    
    YS->>YS: Parse Query Parameters
    Note over YS: Validate Date Range, Pagination
    
    YS->>C: Check Cache for Query
    alt Cache Hit
        C-->>YS: Cached Results
    else Cache Miss
        YS->>DB: Query Transaction History
        Note over DB: Partitioned Query by Date
        DB-->>YS: Transaction Records
        YS->>C: Cache Results (TTL: 10min)
    end
    
    YS->>YS: Apply Filters & Pagination
    YS->>YS: Calculate Summary Statistics
    
    alt Real-time Balance Sync Required
        YS->>CBS: Get Current Balance
        CBS-->>YS: Latest Balance
    end
    
    YS-->>AG: Paginated Response
    AG-->>WA: JSON Response with Metadata
    WA-->>U: Render Transaction History
    
    Note over U,CBS: Optimized for Large Datasets
```

---

## 5. Account Status Check Sequence Diagram

```mermaid
sequenceDiagram
    participant U as User
    participant WA as Web Application
    participant AG as API Gateway
    participant YS as Youth Account Service
    participant DB as Database
    participant CBS as Core Banking System
    participant CS as Compliance Service

    U->>WA: Check Account Status
    WA->>AG: GET /youth-accounts/{id}/status
    AG->>YS: Route Status Request
    
    YS->>DB: Get Account Information
    DB-->>YS: Account Details
    
    YS->>CBS: Sync Account Status
    CBS-->>YS: Current Status
    
    YS->>CS: Check Compliance Status
    CS-->>YS: Compliance Information
    
    YS->>YS: Aggregate Status Information
    YS-->>AG: Status Response
    AG-->>WA: JSON Response
    WA-->>U: Display Account Status
```

---

## 6. Notification Management Sequence Diagram

```mermaid
sequenceDiagram
    participant U as User
    participant WA as Web Application
    participant AG as API Gateway
    participant NS as Notification Service
    participant DB as Database
    participant ES as Email Service
    participant SMS as SMS Service
    participant PN as Push Notification Service

    U->>WA: Get Notifications
    WA->>AG: GET /youth-accounts/{id}/notifications
    AG->>NS: Route Notification Request
    
    NS->>DB: Query User Notifications
    DB-->>NS: Notification List
    NS-->>AG: Notifications Response
    AG-->>WA: JSON Response
    WA-->>U: Display Notifications
    
    U->>WA: Mark Notifications Read
    WA->>AG: PATCH /notifications
    AG->>NS: Update Read Status
    NS->>DB: Update Notification Status
    DB-->>NS: Update Confirmation
    NS-->>AG: Success Response
    AG-->>WA: Confirmation
    WA-->>U: Updated Status
    
    Note over NS: Async Notification Delivery
    NS->>ES: Send Email Notifications
    NS->>SMS: Send SMS Notifications
    NS->>PN: Send Push Notifications
```

---

## 7. Error Handling and Circuit Breaker Sequence Diagram

```mermaid
sequenceDiagram
    participant U as User
    participant WA as Web Application
    participant AG as API Gateway
    participant YS as Youth Account Service
    participant CB as Circuit Breaker
    participant CBS as Core Banking System
    participant DB as Database
    participant AL as Audit Logger

    U->>WA: Request Dashboard
    WA->>AG: API Request
    AG->>YS: Route Request
    
    YS->>CB: Check Circuit State
    alt Circuit Open
        CB-->>YS: Circuit Open - Fallback
        YS->>DB: Get Cached Data Only
        DB-->>YS: Cached Account Data
        Note over YS: Degraded Mode - No Real-time Balance
    else Circuit Closed
        CB->>CBS: Forward Request
        CBS-->>CB: Timeout/Error
        CB->>CB: Increment Failure Count
        CB-->>YS: Service Unavailable
        YS->>AL: Log Service Failure
        YS->>DB: Fallback to Cached Data
    end
    
    YS-->>AG: Response (with degraded flag)
    AG-->>WA: JSON Response
    WA-->>U: Display with Warning Message
    
    Note over CB: Circuit Breaker Pattern Prevents Cascade Failures
```

---

## 8. Security and Audit Sequence Diagram

```mermaid
sequenceDiagram
    participant U as User
    participant WA as Web Application
    participant AG as API Gateway
    participant AS as Auth Service
    participant YS as Youth Account Service
    participant AL as Audit Logger
    participant SM as Security Monitor
    participant DB as Database

    U->>WA: Login Request
    WA->>AS: OAuth 2.0 Authentication
    AS->>AS: Validate Credentials
    AS->>AL: Log Authentication Attempt
    AS-->>WA: JWT Token
    
    U->>WA: Sensitive Operation Request
    WA->>AG: API Request with JWT
    AG->>AS: Validate Token
    AS-->>AG: Token Valid
    
    AG->>SM: Security Policy Check
    SM->>SM: Rate Limit Check
    SM->>SM: Anomaly Detection
    SM-->>AG: Security Approved
    
    AG->>YS: Authorized Request
    YS->>AL: Log Operation Start
    YS->>DB: Execute Operation
    DB-->>YS: Operation Result
    YS->>AL: Log Operation Complete
    
    YS-->>AG: Response
    AG->>AL: Log API Response
    AG-->>WA: JSON Response
    WA-->>U: Operation Result
    
    Note over AL: Immutable Audit Trail
    Note over SM: Real-time Security Monitoring
```

---

## Sequence Diagram Standards and Conventions

### Naming Conventions
- **Participants**: Abbreviated service names (YS = Youth Service)
- **Messages**: RESTful API patterns with HTTP methods
- **Notes**: Performance targets and business rules

### Performance Annotations
- Response time targets included in notes
- Cache TTL values specified
- Timeout configurations documented

### Security Considerations
- Authentication flows clearly marked
- Authorization checkpoints identified
- Audit logging points highlighted

### Error Handling Patterns
- Circuit breaker implementations
- Fallback mechanisms
- Graceful degradation scenarios

### Compliance Markers
- PCI-DSS compliance points
- Audit trail requirements
- Data protection measures

---

## Integration with Architecture

These sequence diagrams directly map to:
- **HLD Document**: Section 4 (Component Design)
- **API Contract**: All 6 major endpoints
- **ADR References**: SCIB-26 through SCIB-197
- **NFR Requirements**: Performance and security targets

---

*Generated from Youth Account Management System HLD Document v1.0*
*Compliant with OpenAPI 3.0 and enterprise architecture standards*