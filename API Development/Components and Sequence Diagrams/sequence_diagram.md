# Sequence Diagram - Task Creation Synchronization System

## Document Information
- **Version**: 1.0
- **Date**: 2024-12-19
- **System**: SCIB Task Management Platform
- **Story Reference**: DEMO-1841
- **Generated From**: HLD Document v1.0

---

## Overview
This sequence diagram illustrates the real-time task creation synchronization flow across all connected users in the SCIB Task Management System. The diagram shows the complete end-to-end process from task creation to real-time appearance on all user boards within 500ms.

---

## Single Task Creation Sequence

```mermaid
sequenceDiagram
    participant U1 as User 1 (Creator)
    participant UI1 as Angular SPA (User 1)
    participant U2 as User 2 (Observer)
    participant UI2 as Angular SPA (User 2)
    participant LB as Load Balancer
    participant API as Spring Boot API
    participant VS as TaskValidationService
    participant TCS as TaskCreationService
    participant DB as PostgreSQL
    participant Redis as Redis Pub/Sub
    participant WS as WebSocket Handler
    
    Note over U1, WS: Single Task Creation Flow
    
    %% Task Creation Request
    U1->>UI1: Fill task creation form
    UI1->>UI1: Client-side validation
    UI1->>UI1: Optimistic UI update (show task locally)
    
    UI1->>+LB: POST /api/boards/{boardId}/tasks
    Note right of UI1: Request includes:<br/>- title, description<br/>- assigneeId, priority<br/>- dueDate, tags, columnId
    
    LB->>+API: Route request to API instance
    API->>API: Extract JWT token & validate user
    API->>API: Check board access permissions
    
    %% Validation Phase
    API->>+VS: validateTask(request)
    VS->>VS: Validate title uniqueness
    VS->>VS: Validate assignee permissions
    VS->>VS: Validate business rules
    VS-->>-API: ValidationResult (success)
    
    %% Task Creation Phase
    API->>+TCS: createTask(validatedRequest)
    TCS->>+DB: BEGIN TRANSACTION
    TCS->>DB: INSERT INTO tasks (...)
    TCS->>DB: UPDATE task positions
    TCS->>DB: COMMIT TRANSACTION
    DB-->>-TCS: Task created with ID
    
    %% Event Publishing
    TCS->>+Redis: PUBLISH task_created event
    Note right of TCS: Event payload:<br/>- boardId, taskId<br/>- complete task metadata<br/>- creator information
    Redis-->>-TCS: Event published
    
    TCS-->>-API: Task creation result
    API-->>-LB: 201 Created + Task object
    LB-->>-UI1: Task creation response
    
    UI1->>UI1: Update optimistic UI with server response
    UI1->>U1: Show success notification
    
    %% Real-time Broadcasting
    Redis->>+WS: Notify task_created event
    WS->>WS: Identify connected clients for boardId
    
    %% Broadcast to User 1 (Creator)
    WS->>UI1: WebSocket: TASK_CREATED event
    UI1->>UI1: Confirm optimistic update
    UI1->>U1: Visual confirmation (green border)
    
    %% Broadcast to User 2 (Observer)
    WS->>UI2: WebSocket: TASK_CREATED event
    UI2->>UI2: Add task to board with animation
    UI2->>U2: Task appears with fade-in effect
    
    WS-->>-Redis: Broadcasting complete
    
    Note over U1, WS: End-to-end latency target: < 500ms
```

---

## Batch Task Creation Sequence

```mermaid
sequenceDiagram
    participant U1 as User 1 (Creator)
    participant UI1 as Angular SPA
    participant API as Spring Boot API
    participant VS as TaskValidationService
    participant TCS as TaskCreationService
    participant DB as PostgreSQL
    participant Redis as Redis Pub/Sub
    participant WS as WebSocket Handler
    participant U2 as User 2 (Observer)
    participant UI2 as Angular SPA (User 2)
    
    Note over U1, UI2: Batch Task Creation Flow (up to 1000 tasks)
    
    %% Batch Request
    U1->>UI1: Submit batch task creation
    UI1->>+API: POST /api/boards/{boardId}/tasks/batch
    Note right of UI1: Batch payload:<br/>- tasks[] array<br/>- options (validateOnly, continueOnError)
    
    API->>API: Validate batch size (max 1000)
    API->>API: Check user permissions
    
    %% Batch Validation
    loop For each task in batch
        API->>+VS: validateTask(taskRequest)
        VS->>VS: Validate individual task
        VS-->>-API: ValidationResult
    end
    
    API->>API: Aggregate validation results
    
    alt All tasks valid
        %% Batch Creation
        API->>+TCS: createTasksBatch(validatedTasks)
        TCS->>+DB: BEGIN TRANSACTION
        
        loop For each validated task
            TCS->>DB: INSERT INTO tasks
        end
        
        TCS->>DB: UPDATE positions for all tasks
        TCS->>DB: COMMIT TRANSACTION
        DB-->>-TCS: Batch creation complete
        
        %% Batch Event Publishing
        TCS->>+Redis: PUBLISH tasks_batch_created event
        Note right of TCS: Batch event includes:<br/>- batchId, boardId<br/>- all created tasks<br/>- processing metadata
        Redis-->>-TCS: Batch event published
        
        TCS-->>-API: BatchCreationResult (success)
        API-->>-UI1: 201 Created + Batch result
        
        %% Real-time Broadcasting
        Redis->>+WS: Notify tasks_batch_created event
        WS->>WS: Identify connected clients
        
        %% Broadcast to all connected users
        par Broadcast to Creator
            WS->>UI1: WebSocket: TASKS_BATCH_CREATED
            UI1->>UI1: Update UI with all tasks
            UI1->>U1: Show batch success notification
        and Broadcast to Observers
            WS->>UI2: WebSocket: TASKS_BATCH_CREATED
            UI2->>UI2: Add all tasks with staggered animation
            UI2->>U2: Tasks appear with sequential fade-in
        end
        
        WS-->>-Redis: Broadcasting complete
        
    else Validation failures
        API-->>UI1: 400 Bad Request + Validation errors
        UI1->>UI1: Display validation errors
        UI1->>U1: Show error messages
        
        Note over API, UI1: No WebSocket broadcast on validation failure
    end
    
    Note over U1, UI2: Batch processing target: < 2000ms for 100 tasks
```

---

## Task Validation Sequence

```mermaid
sequenceDiagram
    participant UI as Angular SPA
    participant API as Spring Boot API
    participant VS as TaskValidationService
    participant DB as PostgreSQL
    participant Cache as Redis Cache
    
    Note over UI, Cache: Real-time Task Validation Flow
    
    %% Real-time Validation (as user types)
    UI->>UI: User types in task title field
    UI->>UI: Debounce input (300ms delay)
    
    UI->>+API: POST /api/boards/{boardId}/tasks/validate
    Note right of UI: Validation request:<br/>- partial task data<br/>- field-level validation
    
    API->>API: Validate request format
    API->>+VS: validateTaskData(request)
    
    %% Check cached validation results
    VS->>+Cache: GET validation_cache:{boardId}:{title}
    Cache-->>-VS: Cache miss/hit
    
    alt Cache miss - perform validation
        VS->>+DB: SELECT COUNT(*) FROM tasks WHERE board_id = ? AND title = ?
        DB-->>-VS: Title uniqueness result
        
        VS->>VS: Validate other business rules
        VS->>VS: Check assignee permissions
        VS->>VS: Validate date constraints
        
        VS->>+Cache: SET validation_cache:{boardId}:{title} (TTL: 60s)
        Cache-->>-VS: Cached
        
    else Cache hit
        VS->>VS: Use cached validation result
    end
    
    VS-->>-API: ValidationResult with errors/warnings
    API-->>-UI: 200 OK + Validation response
    
    alt Validation successful
        UI->>UI: Show green checkmark
        UI->>UI: Enable submit button
    else Validation errors
        UI->>UI: Show red error messages
        UI->>UI: Disable submit button
        UI->>UI: Highlight invalid fields
    end
    
    Note over UI, Cache: Validation response target: < 100ms
```

---

## WebSocket Connection and Event Handling

```mermaid
sequenceDiagram
    participant U as User
    participant UI as Angular SPA
    participant LB as Load Balancer
    participant WS as WebSocket Handler
    participant Auth as Auth Service
    participant Redis as Redis Pub/Sub
    
    Note over U, Redis: WebSocket Connection and Event Flow
    
    %% WebSocket Connection Establishment
    U->>UI: Navigate to board page
    UI->>UI: Get JWT token from storage
    
    UI->>+LB: WebSocket connection request
    Note right of UI: WSS connection:<br/>wss://api.scib.com/ws/boards/{boardId}<br/>?token={jwt_token}
    
    LB->>+WS: Route to WebSocket handler
    WS->>+Auth: Validate JWT token
    Auth-->>-WS: Token validation result
    
    alt Token valid
        WS->>WS: Store connection in session map
        WS->>+Redis: SUBSCRIBE to board:{boardId} channel
        Redis-->>-WS: Subscription confirmed
        
        WS-->>-LB: Connection established
        LB-->>-UI: WebSocket connection ready
        
        WS->>UI: CONNECTION_ESTABLISHED event
        Note right of WS: Event includes:<br/>- sessionId<br/>- userId<br/>- boardId
        
        UI->>UI: Update connection status (green indicator)
        UI->>U: Show "Connected" status
        
    else Token invalid
        WS-->>LB: Connection rejected
        LB-->>UI: WebSocket error
        
        WS->>UI: CONNECTION_ERROR event
        UI->>UI: Show reconnection dialog
        UI->>U: Prompt for re-authentication
    end
    
    %% Event Handling Loop
    loop WebSocket Event Processing
        Redis->>WS: Event notification (task_created, etc.)
        WS->>WS: Process event and identify recipients
        
        alt User is connected to target board
            WS->>UI: Broadcast event to client
            UI->>UI: Process event and update UI
            UI->>U: Show real-time update
        else User not connected to board
            WS->>WS: Skip broadcast for this client
        end
    end
    
    %% Connection Cleanup
    U->>UI: Navigate away or close browser
    UI->>WS: WebSocket disconnect
    WS->>WS: Remove from session map
    WS->>Redis: UNSUBSCRIBE from board channel
    WS->>WS: Clean up resources
    
    Note over U, Redis: Connection management with automatic cleanup
```

---

## Error Handling and Recovery Sequence

```mermaid
sequenceDiagram
    participant UI as Angular SPA
    participant API as Spring Boot API
    participant VS as TaskValidationService
    participant TCS as TaskCreationService
    participant DB as PostgreSQL
    participant WS as WebSocket Handler
    
    Note over UI, WS: Error Handling and Recovery Flow
    
    %% Task Creation with Validation Error
    UI->>+API: POST /api/boards/{boardId}/tasks (invalid data)
    API->>+VS: validateTask(request)
    VS->>VS: Detect validation errors
    VS-->>-API: ValidationResult (errors)
    
    API-->>-UI: 400 Bad Request + Detailed errors
    Note right of API: Error response includes:<br/>- error code<br/>- field-specific messages<br/>- timestamp, requestId
    
    UI->>UI: Revert optimistic UI update
    UI->>UI: Display field-specific errors
    UI->>UI: Highlight invalid fields
    
    %% Task Creation with Database Error
    UI->>+API: POST /api/boards/{boardId}/tasks (valid data)
    API->>+VS: validateTask(request)
    VS-->>-API: ValidationResult (success)
    
    API->>+TCS: createTask(request)
    TCS->>+DB: BEGIN TRANSACTION
    TCS->>DB: INSERT INTO tasks
    DB-->>-TCS: Database constraint violation
    
    TCS->>DB: ROLLBACK TRANSACTION
    TCS-->>-API: TaskCreationException
    
    API-->>-UI: 409 Conflict + Error details
    UI->>UI: Revert optimistic update
    UI->>UI: Show error notification
    
    %% WebSocket Connection Failure
    WS->>UI: Connection lost (network issue)
    UI->>UI: Detect connection loss
    UI->>UI: Show "Reconnecting..." status
    
    loop Reconnection attempts (exponential backoff)
        UI->>WS: Attempt reconnection
        alt Reconnection successful
            WS->>UI: CONNECTION_ESTABLISHED
            UI->>UI: Update status to "Connected"
            UI->>API: Sync missed updates
            leave loop
        else Reconnection failed
            UI->>UI: Wait (backoff delay)
        end
    end
    
    %% Service Unavailable Error
    UI->>+API: POST /api/boards/{boardId}/tasks
    API-->>-UI: 503 Service Unavailable
    
    UI->>UI: Show "Service temporarily unavailable"
    UI->>UI: Enable retry button
    UI->>UI: Queue request for retry
    
    Note over UI, WS: Graceful error handling with user feedback
```

---

## Performance and Monitoring Sequence

```mermaid
sequenceDiagram
    participant Mon as Monitoring System
    participant API as Spring Boot API
    participant DB as PostgreSQL
    participant Redis as Redis Pub/Sub
    participant WS as WebSocket Handler
    participant Alert as Alerting System
    
    Note over Mon, Alert: Performance Monitoring and Alerting Flow
    
    %% Performance Metrics Collection
    loop Every 30 seconds
        Mon->>API: Collect metrics (response time, throughput)
        API-->>Mon: Performance metrics
        
        Mon->>DB: Collect database metrics (query time, connections)
        DB-->>Mon: Database performance data
        
        Mon->>Redis: Collect pub/sub metrics (latency, throughput)
        Redis-->>Mon: Redis performance data
        
        Mon->>WS: Collect WebSocket metrics (connections, messages)
        WS-->>Mon: WebSocket metrics
    end
    
    %% Performance Threshold Monitoring
    Mon->>Mon: Analyze metrics against thresholds
    
    alt Performance degradation detected
        Mon->>+Alert: Trigger performance alert
        Note right of Mon: Alert conditions:<br/>- API response time > 500ms<br/>- Task sync latency > 1000ms<br/>- Error rate > 1%
        
        Alert->>Alert: Send notifications to operations team
        Alert->>API: Request detailed diagnostics
        API-->>Alert: Diagnostic information
        Alert-->>-Mon: Alert processed
        
    else Performance within acceptable range
        Mon->>Mon: Continue monitoring
    end
    
    %% SLA Compliance Tracking
    Mon->>Mon: Calculate SLA metrics
    Note right of Mon: SLA targets:<br/>- 99.9% availability<br/>- < 500ms task synchronization<br/>- < 200ms API response time
    
    Mon->>Mon: Generate SLA reports
    
    Note over Mon, Alert: Continuous performance monitoring and alerting
```

---

## Sequence Diagram Summary

### Key Flows Covered
1. **Single Task Creation**: Complete end-to-end flow with real-time synchronization
2. **Batch Task Creation**: Bulk processing with transaction management
3. **Task Validation**: Real-time validation with caching optimization
4. **WebSocket Management**: Connection establishment and event broadcasting
5. **Error Handling**: Comprehensive error scenarios and recovery mechanisms
6. **Performance Monitoring**: Continuous monitoring and alerting

### Performance Targets
- **Task Creation**: < 200ms API response time
- **Real-time Sync**: < 500ms end-to-end latency
- **Batch Processing**: < 2000ms for 100 tasks
- **Validation**: < 100ms response time
- **WebSocket Events**: < 50ms broadcast latency

### Architecture Decision Records (ADRs) Implemented
- **DEMO-1887**: Optimistic UI updates with server confirmation
- **DEMO-1888**: Comprehensive validation service
- **DEMO-1889**: Real-time synchronization architecture
- **DEMO-1890**: Database constraints and transaction management
- **DEMO-1891**: Batch processing capabilities
- **DEMO-1892**: Error handling and recovery mechanisms

### Compliance and Security
- JWT-based authentication for all operations
- Role-based access control enforcement
- Comprehensive audit logging
- Input validation and sanitization
- Secure WebSocket connections (WSS)

---

**Document Status**: Final v1.0  
**Generated From**: HLD Document v1.0  
**Compliance**: TOGAF, C4 Model, Enterprise Standards  
**Last Updated**: 2024-12-19