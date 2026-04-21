# OpenAPI Specification - SCIB Task Management API

## Overview

This directory contains the OpenAPI 3.0.3 specification for the SCIB Task Management System's Task Creation Synchronization API.

## Files

- `openapi.yaml` - Complete OpenAPI specification for the Task Creation API

## API Features

### Core Functionality
- **Real-time Task Creation**: Tasks appear on all connected users' boards within 500ms
- **Batch Task Processing**: Create up to 1000 tasks in a single request
- **Comprehensive Validation**: Server-side validation with detailed error responses
- **WebSocket Integration**: Real-time synchronization via WebSocket events

### Authentication & Security
- JWT-based authentication with 24-hour token expiration
- Role-based access control (RBAC) at board level
- TLS 1.3 encryption for all communications
- Comprehensive input validation and sanitization
- Rate limiting and DDoS protection

### Performance Targets
- Task creation API: < 200ms response time (95th percentile)
- Batch processing: < 2000ms for 100 tasks
- WebSocket broadcast: < 50ms from creation to publish
- Real-time synchronization: < 500ms end-to-end

## API Endpoints

### Task Management
- `POST /api/boards/{boardId}/tasks` - Create a single task
- `POST /api/boards/{boardId}/tasks/batch` - Create multiple tasks
- `POST /api/boards/{boardId}/tasks/validate` - Validate task data

### System
- `GET /health` - Health check endpoint

### WebSocket
- `wss://api.scib.com/ws/boards/{boardId}` - Real-time task synchronization

## Error Handling

The API provides comprehensive error handling with standardized error codes:

- `400` - Validation errors with field-specific details
- `401` - Authentication required
- `403` - Insufficient permissions
- `404` - Resource not found (board, user, etc.)
- `409` - Conflict (duplicate title)
- `422` - Business rule violation
- `429` - Rate limit exceeded
- `500` - Internal server error

## Rate Limits

| Endpoint | Limit | Window | Scope |
|----------|-------|--------|---------|
| Single task creation | 100 requests | 1 minute | Per user |
| Batch task creation | 10 requests | 1 minute | Per user |
| Task validation | 200 requests | 1 minute | Per user |
| WebSocket connections | 50 connections | N/A | Per user |

## WebSocket Events

### Event Types
- `TASK_CREATED` - Single task creation event
- `TASKS_BATCH_CREATED` - Batch task creation event
- `CONNECTION_ESTABLISHED` - WebSocket connection established
- `CONNECTION_ERROR` - Connection error event

### Connection
```
wss://api.scib.com/ws/boards/{boardId}?token={jwt_token}
```

## Compliance & Standards

- **OpenAPI 3.0.3** specification compliance
- **GDPR** compliant data handling
- **SOC 2 Type II** certified security controls
- **ISO 27001** aligned security framework
- **OWASP Top 10** protection

## Related Documentation

- **High-Level Design**: `API Development/Requirement Documents/HLDDocument.txt`
- **API Contract**: `API Development/Requirement Documents/APIContractOutline.txt`
- **Non-Functional Requirements**: `API Development/Requirement Documents/NFR.txt`
- **Story Reference**: DEMO-1841

## Implementation Notes

### Architecture Decisions
- **Real-time Synchronization** (DEMO-1889): WebSocket + Redis Pub/Sub
- **Optimistic UI Pattern** (DEMO-1887): Immediate local updates
- **Comprehensive Validation** (DEMO-1888): Server-side validation
- **Batch Processing** (DEMO-1891): Efficient bulk operations
- **Database Optimization** (DEMO-1890): Constraints and indexing

### Performance Considerations
- Stateless service design for horizontal scaling
- Database connection pooling and query optimization
- Redis pub/sub for low-latency event broadcasting
- Batch processing with transaction management
- Comprehensive caching strategy

### Security Implementation
- JWT token validation on all endpoints
- Board-level access control enforcement
- Input validation and sanitization
- SQL injection prevention via parameterized queries
- XSS protection through output encoding

## Development

### Validation
The OpenAPI specification can be validated using:
```bash
swagger-codegen validate -i openapi.yaml
```

### Code Generation
Client SDKs can be generated using:
```bash
swagger-codegen generate -i openapi.yaml -l typescript-angular -o ./client
```

### Testing
API testing can be performed using the specification with tools like:
- Postman (import OpenAPI spec)
- Insomnia (import OpenAPI spec)
- Newman (automated testing)
- Dredd (API testing framework)

## Version History

- **v1.0.0** - Initial release with task creation and synchronization features

## Support

For questions or issues regarding this API specification:
- Development Team: dev-team@scib.com
- Documentation: API Development/Requirement Documents/
- Issue Tracking: DEMO-1841