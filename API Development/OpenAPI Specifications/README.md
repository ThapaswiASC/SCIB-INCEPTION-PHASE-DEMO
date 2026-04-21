# OpenAPI Specification - Real-Time Task Creation Synchronization API

## Overview

This directory contains the complete OpenAPI 3.0.3 specification for the Real-Time Task Creation Synchronization API, automatically generated from the High-Level Design (HLD) document and API Contract Outline.

## Files

### `openapi.yaml`
Complete OpenAPI specification including:
- **REST API Endpoints**: Single task creation, bulk creation, validation, and batch status
- **Request/Response Schemas**: Comprehensive data models with validation rules
- **Error Handling**: Detailed error responses and status codes
- **Security Definitions**: JWT authentication and authorization
- **Rate Limiting**: API rate limits and headers
- **WebSocket Protocol**: Documentation of real-time messaging (as comments)

## API Features

### Core Functionality
- **Real-time Task Synchronization**: Tasks appear across all connected clients within 500ms
- **Comprehensive Validation**: Server-side validation with detailed error feedback
- **Batch Processing**: Support for bulk task creation with async processing
- **Performance Optimization**: Response time guarantees and rate limiting

### Security & Compliance
- **JWT Authentication**: Secure token-based authentication
- **Role-Based Access Control**: Granular permissions based on user roles
- **Data Protection**: AES-256 encryption and TLS 1.3 transport security
- **Audit Logging**: Complete audit trail for compliance

### Performance Guarantees
- **Single Task Creation**: < 200ms (95th percentile)
- **Bulk Task Creation**: < 2s for batches ≤100 tasks
- **Real-time Sync**: < 500ms across all clients
- **WebSocket Delivery**: < 50ms message latency

## Architecture Integration

### Technology Stack
- **Frontend**: Angular with TypeScript and reactive forms
- **Backend**: Spring Boot with comprehensive validation service
- **Database**: PostgreSQL with unique constraints and performance indexes
- **Real-time**: WebSocket with Redis pub/sub messaging
- **Caching**: Redis for validation results and session management

### ADR Mappings
This specification implements decisions from the following Architectural Decision Records:

| ADR | Component | Implementation |
|-----|-----------|----------------|
| DEMO-1886 | UI/UX | Task creation form with real-time validation |
| DEMO-1887 | Frontend | Angular reactive forms integration |
| DEMO-1888 | Backend | Comprehensive validation service |
| DEMO-1889 | Backend | Task creation and WebSocket broadcasting |
| DEMO-1890 | Database | Unique constraints and performance optimization |
| DEMO-1891 | Backend | Batch processing with async support |
| DEMO-1892 | Testing | E2E test strategy for multi-user scenarios |
| DEMO-1893 | Documentation | Complete API specification |

## Usage Examples

### Single Task Creation
```bash
curl -X POST "https://api.scib.platform.com/v1/api/boards/{boardId}/tasks" \
  -H "Authorization: Bearer {jwt_token}" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Implement user authentication",
    "description": "Add JWT-based authentication",
    "priority": "HIGH",
    "columnId": "column-uuid",
    "assigneeId": "user-uuid",
    "tags": ["authentication", "security"]
  }'
```

### Batch Task Creation
```bash
curl -X POST "https://api.scib.platform.com/v1/api/boards/{boardId}/tasks/batch" \
  -H "Authorization: Bearer {jwt_token}" \
  -H "Content-Type: application/json" \
  -d '{
    "tasks": [
      {
        "title": "Setup CI/CD pipeline",
        "priority": "HIGH",
        "columnId": "column-uuid"
      },
      {
        "title": "Write unit tests",
        "priority": "MEDIUM",
        "columnId": "column-uuid"
      }
    ],
    "options": {
      "async": false
    }
  }'
```

### Task Validation
```bash
curl -X POST "https://api.scib.platform.com/v1/api/boards/{boardId}/tasks/validate" \
  -H "Authorization: Bearer {jwt_token}" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Test task",
    "priority": "LOW",
    "columnId": "column-uuid"
  }'
```

## WebSocket Protocol

### Connection
```javascript
const ws = new WebSocket('wss://api.scib.platform.com/ws/boards/{boardId}?token={jwt_token}');

ws.onmessage = (event) => {
  const message = JSON.parse(event.data);
  if (message.type === 'TASK_CREATED') {
    // Handle new task creation
    updateUI(message.data.task);
  }
};
```

### Message Types
- **TASK_CREATED**: Single task creation event
- **TASKS_BATCH_CREATED**: Batch task creation event
- **CONNECTION_ESTABLISHED**: Connection confirmation
- **HEARTBEAT**: Keep-alive message

## Rate Limiting

| Endpoint | Rate Limit | Window |
|----------|------------|--------|
| Single Task Creation | 100 requests | per minute |
| Bulk Task Creation | 10 requests | per minute |
| Task Validation | 200 requests | per minute |
| Batch Status Check | 60 requests | per minute |
| WebSocket Connections | 10 concurrent | per user |

## Error Handling

### Common Error Codes
- **400 Bad Request**: Validation errors
- **401 Unauthorized**: Authentication required
- **403 Forbidden**: Insufficient permissions
- **404 Not Found**: Resource not found
- **409 Conflict**: Duplicate task title
- **429 Too Many Requests**: Rate limit exceeded
- **500 Internal Server Error**: System error

### Error Response Format
```json
{
  "error": {
    "code": "VALIDATION_FAILED",
    "message": "Request validation failed",
    "details": [
      {
        "field": "title",
        "code": "REQUIRED",
        "message": "Title is required"
      }
    ],
    "timestamp": "2024-01-15T10:30:00Z",
    "path": "/api/boards/123/tasks",
    "correlationId": "abc123-def456-ghi789"
  }
}
```

## Validation Rules

### Task Creation Validation
- **Title**: 1-200 characters, unique per board
- **Description**: Max 2000 characters
- **Priority**: Must be LOW, MEDIUM, HIGH, or CRITICAL
- **Due Date**: Must be future date if provided
- **Assignee**: Must have board access
- **Column**: Must exist in the board
- **Tags**: Max 10 tags, 50 characters each

### Batch Validation
- **Batch Size**: 1-1000 tasks per batch
- **Async Threshold**: >100 tasks automatically processed async
- **Validation**: All tasks validated before any are created
- **Ordering**: Task order maintained across all clients

## Monitoring & Observability

### Performance Metrics
- API response times with percentile tracking
- Real-time synchronization latency
- WebSocket connection health
- Database query performance
- Error rates and patterns

### Audit Logging
- All task creation events logged
- User action attribution
- IP address and device tracking
- Correlation IDs for request tracing
- Compliance audit trails

## Compliance & Security

### Data Protection
- **GDPR Compliance**: Data subject rights support
- **ISO 27001**: Information security management
- **SOC 2 Type II**: Service organization controls
- **Data Encryption**: AES-256 at rest, TLS 1.3 in transit

### Access Control
- **JWT Authentication**: Token-based security
- **RBAC**: Role-based access control
- **Board Permissions**: Resource-level security
- **Session Management**: Secure session handling

## Development & Testing

### API Testing
- Interactive documentation available at `/swagger-ui`
- Postman collection available for download
- SDK support for popular programming languages
- Comprehensive test suite with E2E scenarios

### Environment URLs
- **Production**: `https://api.scib.platform.com/v1`
- **Staging**: `https://staging-api.scib.platform.com/v1`
- **Development**: `https://dev-api.scib.platform.com/v1`

---

**Generated From**: HLD Document and API Contract Outline  
**Generation Date**: 2024-01-15  
**API Version**: 1.0.0  
**OpenAPI Version**: 3.0.3  
**Compliance**: Enterprise standards validated