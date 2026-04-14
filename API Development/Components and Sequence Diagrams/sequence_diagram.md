# Sequence Diagrams
# Youth Account Management System

## Overview
This document contains sequence diagrams for the Youth Account Management System, illustrating the flow of interactions between different system components for key use cases.

## 1. Youth Account Dashboard Retrieval

```mermaid
sequenceDiagram
    participant Parent as Parent/Guardian
    participant WebApp as Web Application
    participant APIGateway as API Gateway
    participant AuthService as Auth Service
    participant DashboardService as Dashboard Service
    participant YouthService as Youth Account Service
    participant TransactionService as Transaction Service
    participant Cache as Redis Cache
    participant Database as PostgreSQL DB

    Parent->>WebApp: Request dashboard view
    WebApp->>APIGateway: GET /api/youth-accounts/{id}/dashboard
    APIGateway->>AuthService: Validate OAuth token
    AuthService-->>APIGateway: Token valid + permissions
    APIGateway->>DashboardService: Forward request
    
    DashboardService->>Cache: Check cached dashboard data
    alt Cache Hit
        Cache-->>DashboardService: Return cached data
    else Cache Miss
        DashboardService->>YouthService: Get account details
        YouthService->>Database: Query youth_accounts table
        Database-->>YouthService: Account data
        YouthService-->>DashboardService: Account details
        
        DashboardService->>TransactionService: Get recent transactions
        TransactionService->>Database: Query transaction_history
        Database-->>TransactionService: Transaction data
        TransactionService-->>DashboardService: Recent transactions
        
        DashboardService->>Cache: Store aggregated data
    end
    
    DashboardService-->>APIGateway: Dashboard response
    APIGateway-->>WebApp: JSON response
    WebApp-->>Parent: Render dashboard
```

## 2. Fund Transfer Process

```mermaid
sequenceDiagram
    participant Parent as Parent/Guardian
    participant WebApp as Web Application
    participant APIGateway as API Gateway
    participant AuthService as Auth Service
    participant YouthService as Youth Account Service
    participant PaymentGateway as Payment Gateway
    participant CoreBanking as Core Banking System
    participant NotificationService as Notification Service
    participant Database as PostgreSQL DB
    participant AuditLog as Audit Log

    Parent->>WebApp: Initiate fund transfer
    WebApp->>APIGateway: POST /api/youth-accounts/{id}/fund-transfer
    APIGateway->>AuthService: Validate OAuth token & permissions
    AuthService-->>APIGateway: Authorization confirmed
    APIGateway->>YouthService: Process transfer request
    
    YouthService->>Database: Begin transaction
    YouthService->>Database: Validate parent-youth relationship
    Database-->>YouthService: Relationship confirmed
    
    YouthService->>Database: Check source account balance
    Database-->>YouthService: Balance sufficient
    
    YouthService->>PaymentGateway: Initiate transfer
    PaymentGateway->>CoreBanking: Process fund movement
    CoreBanking-->>PaymentGateway: Transfer successful
    PaymentGateway-->>YouthService: Transfer confirmation
    
    YouthService->>Database: Update account balances
    YouthService->>Database: Create transaction records
    YouthService->>Database: Commit transaction
    
    YouthService->>AuditLog: Log transfer activity
    YouthService->>NotificationService: Send transfer notification
    NotificationService-->>Parent: Transfer confirmation
    
    YouthService-->>APIGateway: Transfer response
    APIGateway-->>WebApp: JSON response
    WebApp-->>Parent: Success confirmation
```

## 3. Spending Limit Configuration

```mermaid
sequenceDiagram
    participant Parent as Parent/Guardian
    participant WebApp as Web Application
    participant APIGateway as API Gateway
    participant AuthService as Auth Service
    participant LimitService as Spending Limit Service
    participant YouthService as Youth Account Service
    participant Database as PostgreSQL DB
    participant Cache as Redis Cache
    participant NotificationService as Notification Service
    participant AuditLog as Audit Log

    Parent->>WebApp: Configure spending limit
    WebApp->>APIGateway: PUT /api/youth-accounts/{id}/spending-limit
    APIGateway->>AuthService: Validate token & admin permissions
    AuthService-->>APIGateway: Admin access confirmed
    APIGateway->>LimitService: Process limit update
    
    LimitService->>YouthService: Validate account ownership
    YouthService->>Database: Check parent-youth relationship
    Database-->>YouthService: Ownership confirmed
    YouthService-->>LimitService: Account validation passed
    
    LimitService->>Database: Begin transaction
    LimitService->>Database: Get current spending limits
    Database-->>LimitService: Current limit data
    
    LimitService->>Database: Update youth_account_settings
    LimitService->>Database: Create limit change audit record
    LimitService->>Database: Commit transaction
    
    LimitService->>Cache: Invalidate cached limit data
    LimitService->>AuditLog: Log limit change
    LimitService->>NotificationService: Send limit update notification
    NotificationService-->>Parent: Limit update confirmation
    
    LimitService-->>APIGateway: Update response
    APIGateway-->>WebApp: JSON response
    WebApp-->>Parent: Success confirmation
```

## 4. Transaction History Retrieval

```mermaid
sequenceDiagram
    participant Parent as Parent/Guardian
    participant WebApp as Web Application
    participant APIGateway as API Gateway
    participant AuthService as Auth Service
    participant TransactionService as Transaction Service
    participant Database as PostgreSQL DB
    participant Cache as Redis Cache

    Parent->>WebApp: Request transaction history
    WebApp->>APIGateway: GET /api/youth-accounts/{id}/transactions?page=1&limit=20
    APIGateway->>AuthService: Validate OAuth token
    AuthService-->>APIGateway: Token valid
    APIGateway->>TransactionService: Forward request with filters
    
    TransactionService->>Cache: Check cached transaction data
    alt Cache Hit
        Cache-->>TransactionService: Return cached results
    else Cache Miss
        TransactionService->>Database: Query transaction_history with filters
        Note over Database: SELECT * FROM transaction_history<br/>WHERE youth_account_id = ?<br/>ORDER BY transaction_date DESC<br/>LIMIT 20 OFFSET 0
        Database-->>TransactionService: Transaction records
        
        TransactionService->>Database: Get total count for pagination
        Database-->>TransactionService: Total record count
        
        TransactionService->>Cache: Cache results (5 min TTL)
    end
    
    TransactionService-->>APIGateway: Paginated transaction response
    APIGateway-->>WebApp: JSON response with pagination
    WebApp-->>Parent: Render transaction list
```

## 5. Account Status Management

```mermaid
sequenceDiagram
    participant Parent as Parent/Guardian
    participant WebApp as Web Application
    participant APIGateway as API Gateway
    participant AuthService as Auth Service
    participant YouthService as Youth Account Service
    participant Database as PostgreSQL DB
    participant NotificationService as Notification Service
    participant AuditLog as Audit Log
    participant ComplianceService as Compliance Service

    Parent->>WebApp: Change account status (suspend/activate)
    WebApp->>APIGateway: PATCH /api/youth-accounts/{id}/status
    APIGateway->>AuthService: Validate admin permissions
    AuthService-->>APIGateway: Admin access confirmed
    APIGateway->>YouthService: Process status change
    
    YouthService->>ComplianceService: Validate status change
    ComplianceService-->>YouthService: Change approved
    
    YouthService->>Database: Begin transaction
    YouthService->>Database: Get current account status
    Database-->>YouthService: Current status data
    
    YouthService->>Database: Update account status
    YouthService->>Database: Create status change audit record
    YouthService->>Database: Commit transaction
    
    YouthService->>AuditLog: Log status change
    YouthService->>NotificationService: Send status change notification
    NotificationService-->>Parent: Status change confirmation
    
    YouthService-->>APIGateway: Status update response
    APIGateway-->>WebApp: JSON response
    WebApp-->>Parent: Success confirmation
```

## 6. Real-time Spending Alert Flow

```mermaid
sequenceDiagram
    participant Youth as Youth Account Holder
    participant Merchant as Merchant System
    participant PaymentProcessor as Payment Processor
    participant TransactionService as Transaction Service
    participant LimitService as Spending Limit Service
    participant NotificationService as Notification Service
    participant Parent as Parent/Guardian
    participant Database as PostgreSQL DB

    Youth->>Merchant: Make purchase
    Merchant->>PaymentProcessor: Process payment
    PaymentProcessor->>TransactionService: Transaction notification
    
    TransactionService->>Database: Record transaction
    TransactionService->>LimitService: Check spending limits
    
    LimitService->>Database: Get current spending and limits
    Database-->>LimitService: Spending data
    
    alt Spending threshold reached (50%, 75%, 90%)
        LimitService->>NotificationService: Send spending alert
        NotificationService->>Parent: Spending threshold alert
    end
    
    alt Spending limit exceeded
        LimitService->>PaymentProcessor: Decline transaction
        PaymentProcessor-->>Merchant: Transaction declined
        Merchant-->>Youth: Payment declined
        LimitService->>NotificationService: Send limit exceeded alert
        NotificationService->>Parent: Limit exceeded notification
    else Within limits
        PaymentProcessor-->>Merchant: Transaction approved
        Merchant-->>Youth: Purchase successful
    end
```

## 7. Error Handling and Recovery Flow

```mermaid
sequenceDiagram
    participant Client as Client Application
    participant APIGateway as API Gateway
    participant YouthService as Youth Account Service
    participant Database as PostgreSQL DB
    participant CircuitBreaker as Circuit Breaker
    participant ErrorHandler as Error Handler
    participant MonitoringService as Monitoring Service

    Client->>APIGateway: API Request
    APIGateway->>CircuitBreaker: Check service health
    
    alt Circuit Open (Service Down)
        CircuitBreaker-->>APIGateway: Service unavailable
        APIGateway-->>Client: 503 Service Unavailable
    else Circuit Closed (Service Healthy)
        CircuitBreaker->>YouthService: Forward request
        YouthService->>Database: Database operation
        
        alt Database Error
            Database-->>YouthService: Connection timeout/error
            YouthService->>ErrorHandler: Handle database error
            ErrorHandler->>MonitoringService: Log error metrics
            ErrorHandler->>YouthService: Retry with backoff
            
            alt Retry Successful
                YouthService->>Database: Retry operation
                Database-->>YouthService: Success response
                YouthService-->>APIGateway: Success response
            else Retry Failed
                ErrorHandler-->>YouthService: Max retries exceeded
                YouthService-->>APIGateway: 500 Internal Server Error
            end
        else Database Success
            Database-->>YouthService: Success response
            YouthService-->>APIGateway: Success response
        end
        
        APIGateway-->>Client: Response
    end
```

## 8. Authentication and Authorization Flow

```mermaid
sequenceDiagram
    participant Parent as Parent/Guardian
    participant WebApp as Web Application
    participant AuthService as Auth Service
    participant IdentityProvider as Identity Provider
    participant APIGateway as API Gateway
    participant YouthService as Youth Account Service
    participant Database as PostgreSQL DB

    Parent->>WebApp: Login request
    WebApp->>AuthService: Authenticate user
    AuthService->>IdentityProvider: OAuth 2.0 authorization
    IdentityProvider-->>AuthService: Authorization code
    AuthService->>IdentityProvider: Exchange code for token
    IdentityProvider-->>AuthService: Access token + ID token
    
    AuthService->>Database: Validate user permissions
    Database-->>AuthService: User roles and permissions
    AuthService-->>WebApp: JWT token with scopes
    WebApp-->>Parent: Login successful
    
    Note over Parent,Database: Subsequent API calls
    
    Parent->>WebApp: Access youth account
    WebApp->>APIGateway: API call with JWT token
    APIGateway->>AuthService: Validate JWT token
    AuthService-->>APIGateway: Token valid + user context
    
    APIGateway->>YouthService: Forward request with user context
    YouthService->>Database: Verify parent-youth relationship
    Database-->>YouthService: Relationship authorized
    YouthService-->>APIGateway: Authorized response
    APIGateway-->>WebApp: API response
    WebApp-->>Parent: Display data
```

## Sequence Diagram Standards and Conventions

### Naming Conventions
- **Participants**: Use clear, descriptive names
- **Messages**: Use verb-noun format (e.g., "Get account details")
- **Return Messages**: Use dashed arrows for responses

### Error Handling
- All diagrams include error scenarios using `alt/else` blocks
- Timeout and retry mechanisms are explicitly shown
- Circuit breaker patterns are documented

### Security Considerations
- Authentication flows are detailed in every user-initiated sequence
- Authorization checks are explicitly shown
- Audit logging is included in all financial operations

### Performance Optimizations
- Caching strategies are illustrated
- Database transaction boundaries are marked
- Asynchronous operations are clearly indicated

### Compliance Requirements
- All financial transactions include audit trails
- Data validation steps are shown
- Regulatory compliance checks are included where applicable

---

**Document Version**: 1.0  
**Last Updated**: 2024-01-15  
**Author**: Senior Solution Architect  
**Review Status**: Approved  
**Compliance**: PCI-DSS, GDPR, SOX, Basel III