# Component Diagrams
# Youth Account Management System

## Document Information
- **Document Version**: 1.0
- **Date**: 2024
- **System**: Youth Account Management System
- **Traceability**: SCIB-25, SCIB-26, SCIB-27, SCIB-28, SCIB-29, SCIB-30
- **Architecture Pattern**: Microservices with API Gateway

---

## 1. System Context Diagram (C4 Level 1)

### Description
High-level view of the Youth Account Management System and its external dependencies.

```mermaid
graph TB
    %% External Users
    Parent["👤 Parent/Guardian<br/>Primary User"]
    Youth["👤 Youth Account Holder<br/>Secondary User"]
    Admin["👤 System Administrator<br/>Operations"]
    Auditor["👤 Compliance Auditor<br/>Regulatory"]
    
    %% Main System
    YouthSystem["🏦 Youth Account Management System<br/>Core Banking Platform<br/>Manages youth accounts, transfers, limits"]
    
    %% External Systems
    CoreBanking["🏛️ Core Banking System<br/>Legacy System<br/>Account management & transactions"]
    PaymentGateway["💳 Payment Gateway<br/>External Service<br/>Payment processing & validation"]
    NotificationService["📧 Notification Service<br/>External Service<br/>Email, SMS, Push notifications"]
    AuditSystem["📊 Audit & Compliance System<br/>External Service<br/>Regulatory reporting & audit trails"]
    IdentityProvider["🔐 Identity Provider<br/>External Service<br/>OAuth 2.0 authentication"]
    
    %% User Interactions
    Parent -.->|"Manages youth accounts<br/>Configures limits<br/>Transfers funds"| YouthSystem
    Youth -.->|"Views account balance<br/>Checks spending history"| YouthSystem
    Admin -.->|"System administration<br/>User management"| YouthSystem
    Auditor -.->|"Compliance monitoring<br/>Audit trail access"| YouthSystem
    
    %% System Integrations
    YouthSystem -->|"Account queries<br/>Balance updates<br/>Transaction processing"| CoreBanking
    YouthSystem -->|"Payment authorization<br/>Fund transfers<br/>Transaction validation"| PaymentGateway
    YouthSystem -->|"Email notifications<br/>SMS alerts<br/>Push messages"| NotificationService
    YouthSystem -->|"Audit events<br/>Compliance data<br/>Transaction logs"| AuditSystem
    YouthSystem -->|"User authentication<br/>Token validation<br/>Authorization"| IdentityProvider
    
    %% Styling
    classDef userClass fill:#e1f5fe,stroke:#01579b,stroke-width:2px
    classDef systemClass fill:#f3e5f5,stroke:#4a148c,stroke-width:3px
    classDef externalClass fill:#fff3e0,stroke:#e65100,stroke-width:2px
    
    class Parent,Youth,Admin,Auditor userClass
    class YouthSystem systemClass
    class CoreBanking,PaymentGateway,NotificationService,AuditSystem,IdentityProvider externalClass
```

**Key Relationships:**
- **Primary Users**: Parents manage youth accounts with full control
- **Secondary Users**: Youth have limited read-only access
- **External Dependencies**: Core banking, payments, notifications, audit, identity
- **Compliance**: All interactions logged for regulatory requirements

---

## 2. Container Diagram (C4 Level 2)

### Description
Detailed view of the system's containers, showing the microservices architecture and data flow.

```mermaid
graph TB
    %% Users
    Parent["👤 Parent User"]
    Youth["👤 Youth User"]
    
    %% Frontend Layer
    subgraph "Frontend Layer"
        WebApp["📱 Web Application<br/>React.js + TypeScript<br/>Responsive PWA<br/>Port: 3000"]
        MobileApp["📱 Mobile App<br/>React Native<br/>iOS/Android<br/>Push notifications"]
    end
    
    %% API Gateway Layer
    subgraph "API Gateway Layer"
        APIGateway["🚪 API Gateway<br/>Kong/AWS API Gateway<br/>Authentication, Rate limiting<br/>Port: 8080"]
        LoadBalancer["⚖️ Load Balancer<br/>AWS ALB<br/>SSL termination<br/>Health checks"]
    end
    
    %% Microservices Layer
    subgraph "Microservices Layer"
        YouthService["🏦 Youth Account Service<br/>Java 17 + Spring Boot<br/>Account management<br/>Port: 8081"]
        TransferService["💸 Fund Transfer Service<br/>Java 17 + Spring Boot<br/>Payment processing<br/>Port: 8082"]
        LimitService["🎯 Spending Limit Service<br/>Java 17 + Spring Boot<br/>Limit enforcement<br/>Port: 8083"]
        TransactionService["📊 Transaction Service<br/>Java 17 + Spring Boot<br/>History & analytics<br/>Port: 8084"]
        NotificationService["📧 Notification Service<br/>Node.js + Express<br/>Multi-channel messaging<br/>Port: 8085"]
        AuthService["🔐 Auth Service<br/>Java 17 + Spring Security<br/>JWT token management<br/>Port: 8086"]
    end
    
    %% Data Layer
    subgraph "Data Layer"
        PrimaryDB[("🗄️ Primary Database<br/>PostgreSQL 14<br/>ACID transactions<br/>Port: 5432")]
        ReadReplica[("📖 Read Replica<br/>PostgreSQL 14<br/>Read-only queries<br/>Port: 5433")]
        Cache[("⚡ Redis Cache<br/>Session & data caching<br/>TTL management<br/>Port: 6379")]
        MessageQueue["📬 Message Queue<br/>RabbitMQ/AWS SQS<br/>Async processing<br/>Event streaming"]
    end
    
    %% External Systems
    subgraph "External Systems"
        CoreBanking["🏛️ Core Banking<br/>Legacy SOAP/REST<br/>Account operations"]
        PaymentGateway["💳 Payment Gateway<br/>Stripe/PayPal<br/>PCI-DSS compliant"]
        ExternalNotif["📨 External Notifications<br/>SendGrid/Twilio<br/>Email/SMS delivery"]
        AuditSystem["📊 Audit System<br/>Compliance logging<br/>Regulatory reporting"]
    end
    
    %% User Connections
    Parent --> WebApp
    Youth --> MobileApp
    WebApp --> LoadBalancer
    MobileApp --> LoadBalancer
    
    %% Gateway Routing
    LoadBalancer --> APIGateway
    APIGateway --> AuthService
    APIGateway --> YouthService
    APIGateway --> TransferService
    APIGateway --> LimitService
    APIGateway --> TransactionService
    
    %% Service Dependencies
    YouthService --> PrimaryDB
    YouthService --> Cache
    YouthService --> MessageQueue
    
    TransferService --> PrimaryDB
    TransferService --> Cache
    TransferService --> CoreBanking
    TransferService --> PaymentGateway
    TransferService --> MessageQueue
    
    LimitService --> PrimaryDB
    LimitService --> Cache
    LimitService --> MessageQueue
    
    TransactionService --> ReadReplica
    TransactionService --> Cache
    
    NotificationService --> MessageQueue
    NotificationService --> ExternalNotif
    
    AuthService --> PrimaryDB
    AuthService --> Cache
    
    %% Database Replication
    PrimaryDB -.->|"Async replication"| ReadReplica
    
    %% Audit Integration
    YouthService --> AuditSystem
    TransferService --> AuditSystem
    LimitService --> AuditSystem
    TransactionService --> AuditSystem
    
    %% Styling
    classDef frontendClass fill:#e8f5e8,stroke:#2e7d32,stroke-width:2px
    classDef gatewayClass fill:#fff3e0,stroke:#f57c00,stroke-width:2px
    classDef serviceClass fill:#e3f2fd,stroke:#1976d2,stroke-width:2px
    classDef dataClass fill:#fce4ec,stroke:#c2185b,stroke-width:2px
    classDef externalClass fill:#f3e5f5,stroke:#7b1fa2,stroke-width:2px
    
    class WebApp,MobileApp frontendClass
    class APIGateway,LoadBalancer gatewayClass
    class YouthService,TransferService,LimitService,TransactionService,NotificationService,AuthService serviceClass
    class PrimaryDB,ReadReplica,Cache,MessageQueue dataClass
    class CoreBanking,PaymentGateway,ExternalNotif,AuditSystem externalClass
```

**Architecture Patterns:**
- **Microservices**: Independently deployable services
- **API Gateway**: Centralized entry point with cross-cutting concerns
- **CQRS**: Read replicas for query optimization
- **Event-Driven**: Asynchronous processing with message queues
- **Caching**: Redis for performance optimization

---

## 3. Component Diagram - Youth Account Service (SCIB-26)

### Description
Detailed internal structure of the Youth Account Service responsible for dashboard and account management.

```mermaid
graph TB
    subgraph "Youth Account Service (Port: 8081)"
        %% API Layer
        subgraph "API Layer"
            Controller["🎮 Account Controller<br/>@RestController<br/>Dashboard endpoints<br/>Input validation"]
            ErrorHandler["❌ Error Handler<br/>@ControllerAdvice<br/>Exception mapping<br/>Standard responses"]
        end
        
        %% Business Layer
        subgraph "Business Layer"
            AccountManager["🏦 Account Manager<br/>@Service<br/>Business logic<br/>Account operations"]
            DashboardAggregator["📊 Dashboard Aggregator<br/>@Component<br/>Data consolidation<br/>Response building"]
            ValidationService["✅ Validation Service<br/>@Component<br/>Business rules<br/>Data validation"]
        end
        
        %% Integration Layer
        subgraph "Integration Layer"
            LimitClient["🎯 Limit Service Client<br/>@FeignClient<br/>Spending limits API<br/>Circuit breaker"]
            TransactionClient["📊 Transaction Client<br/>@FeignClient<br/>Recent transactions<br/>Timeout handling"]
            CacheManager["⚡ Cache Manager<br/>@Component<br/>Redis operations<br/>TTL management"]
        end
        
        %% Data Layer
        subgraph "Data Access Layer"
            AccountRepository["🗄️ Account Repository<br/>@Repository<br/>JPA/Hibernate<br/>CRUD operations"]
            AccountEntity["📋 Account Entity<br/>@Entity<br/>Database mapping<br/>Audit fields"]
        end
        
        %% Security Layer
        subgraph "Security Layer"
            SecurityConfig["🔐 Security Config<br/>@Configuration<br/>JWT validation<br/>RBAC enforcement"]
            AuditLogger["📝 Audit Logger<br/>@Component<br/>Action logging<br/>Compliance tracking"]
        end
    end
    
    %% External Dependencies
    APIGateway["🚪 API Gateway"]
    Database[("🗄️ PostgreSQL")]
    Cache[("⚡ Redis Cache")]
    LimitService["🎯 Limit Service"]
    TransactionService["📊 Transaction Service"]
    AuditSystem["📊 Audit System"]
    
    %% Request Flow
    APIGateway --> Controller
    Controller --> AccountManager
    AccountManager --> DashboardAggregator
    DashboardAggregator --> ValidationService
    
    %% Data Access
    AccountManager --> AccountRepository
    AccountRepository --> AccountEntity
    AccountEntity --> Database
    
    %% Caching
    AccountManager --> CacheManager
    CacheManager --> Cache
    
    %% External Service Calls
    DashboardAggregator --> LimitClient
    DashboardAggregator --> TransactionClient
    LimitClient --> LimitService
    TransactionClient --> TransactionService
    
    %% Security & Audit
    Controller --> SecurityConfig
    AccountManager --> AuditLogger
    AuditLogger --> AuditSystem
    
    %% Error Handling
    Controller --> ErrorHandler
    
    %% Styling
    classDef apiClass fill:#e8f5e8,stroke:#2e7d32,stroke-width:2px
    classDef businessClass fill:#e3f2fd,stroke:#1976d2,stroke-width:2px
    classDef integrationClass fill:#fff3e0,stroke:#f57c00,stroke-width:2px
    classDef dataClass fill:#fce4ec,stroke:#c2185b,stroke-width:2px
    classDef securityClass fill:#ffebee,stroke:#d32f2f,stroke-width:2px
    classDef externalClass fill:#f3e5f5,stroke:#7b1fa2,stroke-width:2px
    
    class Controller,ErrorHandler apiClass
    class AccountManager,DashboardAggregator,ValidationService businessClass
    class LimitClient,TransactionClient,CacheManager integrationClass
    class AccountRepository,AccountEntity dataClass
    class SecurityConfig,AuditLogger securityClass
    class APIGateway,Database,Cache,LimitService,TransactionService,AuditSystem externalClass
```

**Key Responsibilities:**
- **Dashboard API**: GET /youth-accounts/{id}/dashboard (SCIB-26)
- **Data Aggregation**: Combines account, limit, and transaction data
- **Caching**: Redis caching with 5-minute TTL
- **Security**: JWT validation and RBAC enforcement
- **Audit**: Complete action logging for compliance

---

## 4. Component Diagram - Fund Transfer Service (SCIB-27)

### Description
Detailed structure of the Fund Transfer Service handling secure money transfers between accounts.

```mermaid
graph TB
    subgraph "Fund Transfer Service (Port: 8082)"
        %% API Layer
        subgraph "API Layer"
            TransferController["💸 Transfer Controller<br/>@RestController<br/>Transfer endpoints<br/>Rate limiting"]
            TransferValidator["✅ Transfer Validator<br/>@Component<br/>Input validation<br/>Business rules"]
        end
        
        %% Business Layer
        subgraph "Business Layer"
            TransferOrchestrator["🎭 Transfer Orchestrator<br/>@Service<br/>Transaction coordination<br/>Saga pattern"]
            FundManager["💰 Fund Manager<br/>@Service<br/>Account operations<br/>Balance management"]
            TransactionManager["🔄 Transaction Manager<br/>@Transactional<br/>ACID compliance<br/>Rollback handling"]
        end
        
        %% Integration Layer
        subgraph "Integration Layer"
            CoreBankingClient["🏛️ Core Banking Client<br/>@FeignClient<br/>Account validation<br/>Balance queries"]
            PaymentGatewayClient["💳 Payment Gateway Client<br/>@Component<br/>Payment processing<br/>PCI compliance"]
            NotificationPublisher["📧 Notification Publisher<br/>@Component<br/>Event publishing<br/>Message queuing"]
        end
        
        %% Data Layer
        subgraph "Data Access Layer"
            TransferRepository["🗄️ Transfer Repository<br/>@Repository<br/>Transfer records<br/>Status tracking"]
            TransferEntity["📋 Transfer Entity<br/>@Entity<br/>Transfer details<br/>Audit trail"]
        end
        
        %% Security & Compliance
        subgraph "Security Layer"
            SecurityFilter["🔐 Security Filter<br/>@Component<br/>Authentication<br/>Authorization"]
            ComplianceChecker["📊 Compliance Checker<br/>@Service<br/>AML screening<br/>Fraud detection"]
            AuditTrail["📝 Audit Trail<br/>@Component<br/>Transaction logging<br/>Regulatory compliance"]
        end
    end
    
    %% External Systems
    APIGateway["🚪 API Gateway"]
    Database[("🗄️ PostgreSQL")]
    CoreBanking["🏛️ Core Banking System"]
    PaymentGateway["💳 Payment Gateway"]
    MessageQueue["📬 Message Queue"]
    AuditSystem["📊 Audit System"]
    
    %% Request Flow
    APIGateway --> TransferController
    TransferController --> TransferValidator
    TransferValidator --> SecurityFilter
    SecurityFilter --> TransferOrchestrator
    
    %% Business Logic Flow
    TransferOrchestrator --> FundManager
    TransferOrchestrator --> TransactionManager
    TransferOrchestrator --> ComplianceChecker
    
    %% External Integrations
    FundManager --> CoreBankingClient
    FundManager --> PaymentGatewayClient
    CoreBankingClient --> CoreBanking
    PaymentGatewayClient --> PaymentGateway
    
    %% Data Persistence
    TransactionManager --> TransferRepository
    TransferRepository --> TransferEntity
    TransferEntity --> Database
    
    %% Notifications & Audit
    TransferOrchestrator --> NotificationPublisher
    NotificationPublisher --> MessageQueue
    TransferOrchestrator --> AuditTrail
    AuditTrail --> AuditSystem
    
    %% Styling
    classDef apiClass fill:#e8f5e8,stroke:#2e7d32,stroke-width:2px
    classDef businessClass fill:#e3f2fd,stroke:#1976d2,stroke-width:2px
    classDef integrationClass fill:#fff3e0,stroke:#f57c00,stroke-width:2px
    classDef dataClass fill:#fce4ec,stroke:#c2185b,stroke-width:2px
    classDef securityClass fill:#ffebee,stroke:#d32f2f,stroke-width:2px
    classDef externalClass fill:#f3e5f5,stroke:#7b1fa2,stroke-width:2px
    
    class TransferController,TransferValidator apiClass
    class TransferOrchestrator,FundManager,TransactionManager businessClass
    class CoreBankingClient,PaymentGatewayClient,NotificationPublisher integrationClass
    class TransferRepository,TransferEntity dataClass
    class SecurityFilter,ComplianceChecker,AuditTrail securityClass
    class APIGateway,Database,CoreBanking,PaymentGateway,MessageQueue,AuditSystem externalClass
```

**Key Features:**
- **Transfer API**: POST /youth-accounts/{id}/fund-transfer (SCIB-27)
- **ACID Transactions**: Guaranteed consistency with rollback
- **Saga Pattern**: Distributed transaction coordination
- **PCI Compliance**: Secure payment processing
- **Rate Limiting**: 10 transfers per hour per user

---

## 5. Component Diagram - Spending Limit Service (SCIB-28)

### Description
Internal structure of the Spending Limit Service for configuring and enforcing spending controls.

```mermaid
graph TB
    subgraph "Spending Limit Service (Port: 8083)"
        %% API Layer
        subgraph "API Layer"
            LimitController["🎯 Limit Controller<br/>@RestController<br/>Limit configuration<br/>Real-time validation"]
            LimitValidator["✅ Limit Validator<br/>@Component<br/>Parameter validation<br/>Business constraints"]
        end
        
        %% Business Layer
        subgraph "Business Layer"
            LimitManager["📊 Limit Manager<br/>@Service<br/>Limit operations<br/>Enforcement logic"]
            SpendingCalculator["🧮 Spending Calculator<br/>@Component<br/>Weekly calculations<br/>Real-time tracking"]
            NotificationTrigger["🔔 Notification Trigger<br/>@Service<br/>Threshold alerts<br/>Parent notifications"]
        end
        
        %% Enforcement Engine
        subgraph "Enforcement Engine"
            RealTimeEnforcer["⚡ Real-time Enforcer<br/>@Component<br/>Transaction validation<br/>Instant blocking"]
            ThresholdMonitor["📈 Threshold Monitor<br/>@Component<br/>75%, 90% alerts<br/>Proactive warnings"]
            WeeklyResetter["🔄 Weekly Resetter<br/>@Scheduled<br/>Monday reset<br/>Automated cleanup"]
        end
        
        %% Data Layer
        subgraph "Data Access Layer"
            LimitRepository["🗄️ Limit Repository<br/>@Repository<br/>Limit configuration<br/>Spending history"]
            SpendingRepository["📊 Spending Repository<br/>@Repository<br/>Weekly tracking<br/>Transaction aggregation"]
            LimitEntity["📋 Limit Entity<br/>@Entity<br/>Configuration data<br/>Effective dates"]
        end
        
        %% Cache Layer
        subgraph "Cache Layer"
            LimitCache["⚡ Limit Cache<br/>@Component<br/>Hot limit data<br/>Sub-second access"]
            SpendingCache["📊 Spending Cache<br/>@Component<br/>Current week totals<br/>Real-time updates"]
        end
    end
    
    %% External Dependencies
    APIGateway["🚪 API Gateway"]
    Database[("🗄️ PostgreSQL")]
    Cache[("⚡ Redis Cache")]
    MessageQueue["📬 Message Queue"]
    NotificationService["📧 Notification Service"]
    TransactionService["📊 Transaction Service"]
    
    %% Request Flow
    APIGateway --> LimitController
    LimitController --> LimitValidator
    LimitValidator --> LimitManager
    
    %% Business Logic
    LimitManager --> SpendingCalculator
    LimitManager --> NotificationTrigger
    LimitManager --> RealTimeEnforcer
    
    %% Monitoring & Enforcement
    SpendingCalculator --> ThresholdMonitor
    ThresholdMonitor --> NotificationTrigger
    WeeklyResetter --> SpendingCalculator
    
    %% Data Access
    LimitManager --> LimitRepository
    SpendingCalculator --> SpendingRepository
    LimitRepository --> LimitEntity
    LimitEntity --> Database
    SpendingRepository --> Database
    
    %% Caching
    LimitManager --> LimitCache
    SpendingCalculator --> SpendingCache
    LimitCache --> Cache
    SpendingCache --> Cache
    
    %% External Communication
    NotificationTrigger --> MessageQueue
    MessageQueue --> NotificationService
    RealTimeEnforcer --> TransactionService
    
    %% Styling
    classDef apiClass fill:#e8f5e8,stroke:#2e7d32,stroke-width:2px
    classDef businessClass fill:#e3f2fd,stroke:#1976d2,stroke-width:2px
    classDef enforcementClass fill:#fff8e1,stroke:#ff8f00,stroke-width:2px
    classDef dataClass fill:#fce4ec,stroke:#c2185b,stroke-width:2px
    classDef cacheClass fill:#f1f8e9,stroke:#689f38,stroke-width:2px
    classDef externalClass fill:#f3e5f5,stroke:#7b1fa2,stroke-width:2px
    
    class LimitController,LimitValidator apiClass
    class LimitManager,SpendingCalculator,NotificationTrigger businessClass
    class RealTimeEnforcer,ThresholdMonitor,WeeklyResetter enforcementClass
    class LimitRepository,SpendingRepository,LimitEntity dataClass
    class LimitCache,SpendingCache cacheClass
    class APIGateway,Database,Cache,MessageQueue,NotificationService,TransactionService externalClass
```

**Key Capabilities:**
- **Configuration API**: PUT /youth-accounts/{id}/spending-limit (SCIB-28)
- **Real-time Enforcement**: Transaction validation against limits
- **Threshold Alerts**: 75%, 90%, and exceed notifications
- **Weekly Reset**: Automated Monday reset of spending totals
- **Performance**: Sub-second limit validation with caching

---

## 6. Data Flow Architecture

### Description
Overall data flow and integration patterns across all services.

```mermaid
graph LR
    %% Data Sources
    subgraph "Data Sources"
        UserInput["👤 User Input<br/>Web/Mobile Apps"]
        CoreBankData["🏛️ Core Banking<br/>Account Data"]
        PaymentData["💳 Payment Gateway<br/>Transaction Data"]
    end
    
    %% API Gateway Layer
    subgraph "API Gateway"
        Gateway["🚪 API Gateway<br/>Rate limiting<br/>Authentication<br/>Request routing"]
    end
    
    %% Service Mesh
    subgraph "Microservices"
        YouthSvc["🏦 Youth Account<br/>Dashboard data"]
        TransferSvc["💸 Fund Transfer<br/>Payment processing"]
        LimitSvc["🎯 Spending Limits<br/>Enforcement"]
        TxnSvc["📊 Transactions<br/>History & analytics"]
    end
    
    %% Data Processing
    subgraph "Data Processing"
        EventProcessor["⚡ Event Processor<br/>Real-time streaming"]
        BatchProcessor["📦 Batch Processor<br/>Nightly aggregation"]
        AnalyticsEngine["📈 Analytics Engine<br/>Spending insights"]
    end
    
    %% Data Storage
    subgraph "Data Storage"
        TransactionalDB[("🗄️ Transactional DB<br/>PostgreSQL<br/>ACID compliance")]
        AnalyticsDB[("📊 Analytics DB<br/>Data Warehouse<br/>Historical data")]
        CacheLayer[("⚡ Cache Layer<br/>Redis<br/>Hot data")]
    end
    
    %% External Systems
    subgraph "External Integration"
        NotificationSys["📧 Notifications<br/>Multi-channel"]
        AuditSys["📊 Audit System<br/>Compliance"]
        MonitoringSys["📈 Monitoring<br/>Observability"]
    end
    
    %% Data Flow
    UserInput --> Gateway
    CoreBankData --> Gateway
    PaymentData --> Gateway
    
    Gateway --> YouthSvc
    Gateway --> TransferSvc
    Gateway --> LimitSvc
    Gateway --> TxnSvc
    
    YouthSvc --> TransactionalDB
    TransferSvc --> TransactionalDB
    LimitSvc --> TransactionalDB
    TxnSvc --> TransactionalDB
    
    YouthSvc --> CacheLayer
    TransferSvc --> CacheLayer
    LimitSvc --> CacheLayer
    TxnSvc --> CacheLayer
    
    TransactionalDB --> EventProcessor
    EventProcessor --> AnalyticsEngine
    EventProcessor --> NotificationSys
    
    TransactionalDB --> BatchProcessor
    BatchProcessor --> AnalyticsDB
    
    YouthSvc --> AuditSys
    TransferSvc --> AuditSys
    LimitSvc --> AuditSys
    TxnSvc --> AuditSys
    
    YouthSvc --> MonitoringSys
    TransferSvc --> MonitoringSys
    LimitSvc --> MonitoringSys
    TxnSvc --> MonitoringSys
    
    %% Styling
    classDef sourceClass fill:#e8f5e8,stroke:#2e7d32,stroke-width:2px
    classDef gatewayClass fill:#fff3e0,stroke:#f57c00,stroke-width:2px
    classDef serviceClass fill:#e3f2fd,stroke:#1976d2,stroke-width:2px
    classDef processClass fill:#fff8e1,stroke:#ff8f00,stroke-width:2px
    classDef storageClass fill:#fce4ec,stroke:#c2185b,stroke-width:2px
    classDef externalClass fill:#f3e5f5,stroke:#7b1fa2,stroke-width:2px
    
    class UserInput,CoreBankData,PaymentData sourceClass
    class Gateway gatewayClass
    class YouthSvc,TransferSvc,LimitSvc,TxnSvc serviceClass
    class EventProcessor,BatchProcessor,AnalyticsEngine processClass
    class TransactionalDB,AnalyticsDB,CacheLayer storageClass
    class NotificationSys,AuditSys,MonitoringSys externalClass
```

**Data Flow Patterns:**
- **Real-time Processing**: Event-driven architecture for immediate responses
- **Batch Processing**: Nightly aggregation for analytics and reporting
- **Caching Strategy**: Multi-level caching for performance optimization
- **Audit Trail**: Complete data lineage for compliance
- **Monitoring**: Real-time observability across all components

---

## 7. Deployment Architecture

### Description
Production deployment architecture showing infrastructure components and scaling strategies.

```mermaid
graph TB
    %% Internet & CDN
    Internet["🌐 Internet"]
    CDN["🚀 CloudFront CDN<br/>Static content<br/>Global edge locations"]
    
    %% DNS & Load Balancing
    Route53["🔗 Route 53<br/>DNS routing<br/>Health checks"]
    WAF["🛡️ AWS WAF<br/>DDoS protection<br/>Security rules"]
    
    %% Load Balancer Tier
    subgraph "Load Balancer Tier"
        ALB["⚖️ Application Load Balancer<br/>SSL termination<br/>Path-based routing<br/>Health checks"]
    end
    
    %% Application Tier (Multi-AZ)
    subgraph "Application Tier - AZ-1a"
        subgraph "ECS Cluster 1a"
            WebApp1a["📱 Web App<br/>ECS Fargate<br/>Auto-scaling"]
            API1a["🚪 API Gateway<br/>ECS Fargate<br/>Rate limiting"]
            Youth1a["🏦 Youth Service<br/>ECS Fargate<br/>2 instances"]
            Transfer1a["💸 Transfer Service<br/>ECS Fargate<br/>2 instances"]
        end
    end
    
    subgraph "Application Tier - AZ-1b"
        subgraph "ECS Cluster 1b"
            WebApp1b["📱 Web App<br/>ECS Fargate<br/>Auto-scaling"]
            API1b["🚪 API Gateway<br/>ECS Fargate<br/>Rate limiting"]
            Youth1b["🏦 Youth Service<br/>ECS Fargate<br/>2 instances"]
            Transfer1b["💸 Transfer Service<br/>ECS Fargate<br/>2 instances"]
        end
    end
    
    %% Data Tier (Multi-AZ)
    subgraph "Data Tier - AZ-1a"
        RDSPrimary[("🗄️ RDS PostgreSQL<br/>Primary instance<br/>Multi-AZ failover")]
        ElastiCache1a[("⚡ ElastiCache Redis<br/>Cluster mode<br/>3 shards")]
    end
    
    subgraph "Data Tier - AZ-1b"
        RDSStandby[("🗄️ RDS PostgreSQL<br/>Standby instance<br/>Synchronous replication")]
        ElastiCache1b[("⚡ ElastiCache Redis<br/>Cluster mode<br/>3 replicas")]
    end
    
    subgraph "Data Tier - AZ-1c"
        RDSReadReplica[("📖 RDS Read Replica<br/>Read-only queries<br/>Async replication")]
        ElastiCache1c[("⚡ ElastiCache Redis<br/>Cluster mode<br/>3 replicas")]
    end
    
    %% Security & Monitoring
    subgraph "Security & Monitoring"
        KMS["🔐 AWS KMS<br/>Encryption keys<br/>Key rotation"]
        SecretsManager["🔑 Secrets Manager<br/>Database credentials<br/>API keys"]
        CloudWatch["📊 CloudWatch<br/>Metrics & logs<br/>Alerting"]
        XRay["🔍 X-Ray<br/>Distributed tracing<br/>Performance insights"]
    end
    
    %% External Services
    subgraph "External Integration"
        CoreBankingExt["🏛️ Core Banking<br/>VPN connection<br/>Dedicated line"]
        PaymentGatewayExt["💳 Payment Gateway<br/>HTTPS/TLS 1.3<br/>PCI compliant"]
        NotificationExt["📧 Notification APIs<br/>SendGrid/Twilio<br/>Multi-channel"]
    end
    
    %% Traffic Flow
    Internet --> Route53
    Route53 --> CDN
    CDN --> WAF
    WAF --> ALB
    
    ALB --> WebApp1a
    ALB --> WebApp1b
    ALB --> API1a
    ALB --> API1b
    
    API1a --> Youth1a
    API1a --> Transfer1a
    API1b --> Youth1b
    API1b --> Transfer1b
    
    %% Database Connections
    Youth1a --> RDSPrimary
    Youth1b --> RDSPrimary
    Transfer1a --> RDSPrimary
    Transfer1b --> RDSPrimary
    
    Youth1a --> RDSReadReplica
    Youth1b --> RDSReadReplica
    
    %% Cache Connections
    Youth1a --> ElastiCache1a
    Youth1b --> ElastiCache1b
    Transfer1a --> ElastiCache1a
    Transfer1b --> ElastiCache1b
    
    %% Database Replication
    RDSPrimary -.->|"Sync replication"| RDSStandby
    RDSPrimary -.->|"Async replication"| RDSReadReplica
    
    %% Cache Replication
    ElastiCache1a -.->|"Cross-AZ replication"| ElastiCache1b
    ElastiCache1b -.->|"Cross-AZ replication"| ElastiCache1c
    
    %% Security Integration
    Youth1a --> KMS
    Transfer1a --> KMS
    Youth1a --> SecretsManager
    Transfer1a --> SecretsManager
    
    %% Monitoring Integration
    Youth1a --> CloudWatch
    Transfer1a --> CloudWatch
    Youth1a --> XRay
    Transfer1a --> XRay
    
    %% External Connections
    Transfer1a --> CoreBankingExt
    Transfer1b --> CoreBankingExt
    Transfer1a --> PaymentGatewayExt
    Transfer1b --> PaymentGatewayExt
    Youth1a --> NotificationExt
    Youth1b --> NotificationExt
    
    %% Styling
    classDef internetClass fill:#e8f5e8,stroke:#2e7d32,stroke-width:2px
    classDef lbClass fill:#fff3e0,stroke:#f57c00,stroke-width:2px
    classDef appClass fill:#e3f2fd,stroke:#1976d2,stroke-width:2px
    classDef dataClass fill:#fce4ec,stroke:#c2185b,stroke-width:2px
    classDef securityClass fill:#ffebee,stroke:#d32f2f,stroke-width:2px
    classDef externalClass fill:#f3e5f5,stroke:#7b1fa2,stroke-width:2px
    
    class Internet,CDN,Route53,WAF internetClass
    class ALB lbClass
    class WebApp1a,WebApp1b,API1a,API1b,Youth1a,Youth1b,Transfer1a,Transfer1b appClass
    class RDSPrimary,RDSStandby,RDSReadReplica,ElastiCache1a,ElastiCache1b,ElastiCache1c dataClass
    class KMS,SecretsManager,CloudWatch,XRay securityClass
    class CoreBankingExt,PaymentGatewayExt,NotificationExt externalClass
```

**Deployment Features:**
- **High Availability**: Multi-AZ deployment with automatic failover
- **Auto Scaling**: ECS Fargate with CPU/memory-based scaling
- **Security**: WAF, KMS encryption, Secrets Manager
- **Monitoring**: CloudWatch metrics, X-Ray tracing
- **Performance**: CDN, load balancing, read replicas

---

## Architecture Decisions & Traceability

### ADR Mapping to Components

| ADR | Component | Responsibility | Implementation |
|-----|-----------|----------------|----------------|
| SCIB-25 | System Architecture | Parent fund allocation and management | Overall microservices design |
| SCIB-26 | Youth Account Service | Dashboard API implementation | Account dashboard endpoint |
| SCIB-27 | Fund Transfer Service | Fund transfer API development | Secure payment processing |
| SCIB-28 | Spending Limit Service | Spending limit configuration API | Limit enforcement engine |
| SCIB-29 | Transaction Service | Transaction history API | History retrieval with analytics |
| SCIB-30 | API Gateway | OpenAPI/Swagger specification | Standardized API documentation |

### Technology Stack

**Frontend:**
- React.js 18 + TypeScript
- Material-UI for design system
- Progressive Web App (PWA)
- React Native for mobile

**Backend:**
- Java 17 + Spring Boot 3.x
- Spring Security for authentication
- Spring Data JPA for data access
- Spring Cloud Gateway for API gateway

**Data:**
- PostgreSQL 14 for transactional data
- Redis 6 for caching and sessions
- RabbitMQ for message queuing

**Infrastructure:**
- AWS ECS Fargate for containerization
- AWS RDS for managed databases
- AWS ElastiCache for managed Redis
- AWS CloudWatch for monitoring

### Non-Functional Requirements

**Performance:**
- API response time < 200ms (95th percentile)
- Dashboard loading < 2 seconds
- 10,000 concurrent users support
- Auto-scaling based on CPU/memory

**Security:**
- OAuth 2.0 + JWT authentication
- PCI-DSS Level 1 compliance
- End-to-end encryption (TLS 1.3)
- Role-based access control (RBAC)

**Availability:**
- 99.99% uptime SLA
- Multi-AZ deployment
- Automated failover < 30 seconds
- Circuit breaker pattern

**Compliance:**
- GDPR data protection
- SOX financial controls
- BSA regulatory compliance
- Complete audit trail

---

**Document Control:**
- Version: 1.0
- Last Updated: 2024
- Next Review: Monthly
- Approval: Enterprise Architecture Review Board
- Implementation Status: Design Phase Complete
- GitHub Repository: ThapaswiASC/SCIB-INCEPTION-PHASE-DEMO