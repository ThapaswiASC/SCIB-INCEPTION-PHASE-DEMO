# OpenAPI Specifications

## Overview

This directory contains the OpenAPI specifications for the Task Management API, automatically generated from the High-Level Design (HLD) document and API Contract Outline.

## Files

### openapi.yaml
Complete OpenAPI 3.0.3 specification for the Task Management API, including:

- **POST /api/tasks**: Task creation endpoint with comprehensive validation
- **GET /health**: Service health check endpoint
- Complete request/response schemas with validation rules
- Detailed error handling for all HTTP status codes
- Security specifications (JWT authentication, RBAC)
- Rate limiting and compliance documentation

## Key Features

### Security
- JWT Bearer token authentication
- Role-based access control (RBAC)
- Input validation and sanitization
- Rate limiting: 100 requests per minute per user
- TLS 1.3 encryption in transit
- AES-256 encryption at rest

### Compliance
- GDPR compliant data handling
- PCI-DSS security standards
- ISO 27001 information security management
- SOC 2 Type II controls
- Comprehensive audit logging

### Performance
- Response time target: < 200ms for 95% of requests
- Throughput: 1000+ requests per second
- Auto-scaling capabilities
- Caching and optimization

### Error Handling
- Standardized error response format
- Detailed validation error messages
- Proper HTTP status codes
- Request correlation IDs for tracing

## API Endpoints

### POST /api/tasks
Creates a new task with the following features:
- Comprehensive input validation
- Real-time synchronization via WebSocket
- Audit logging for compliance
- Business rule validation
- Automatic timestamp generation

**Required Fields:**
- `title`: Task title (1-255 characters)

**Optional Fields:**
- `description`: Task description (max 1000 characters)
- `status`: Task status (TODO, IN_PROGRESS, DONE, CANCELLED)
- `priority`: Task priority (LOW, MEDIUM, HIGH, CRITICAL)
- `dueDate`: Due date (ISO 8601 format, must be future date)
- `assignedTo`: UUID of assigned user
- `tags`: Array of tags (max 10, alphanumeric only)

### GET /health
Health check endpoint for monitoring and load balancer configuration.

## Response Codes

- **201 Created**: Task successfully created
- **400 Bad Request**: Validation errors
- **401 Unauthorized**: Missing or invalid authentication
- **403 Forbidden**: Insufficient permissions
- **409 Conflict**: Business rule violation
- **422 Unprocessable Entity**: Semantic validation errors
- **429 Too Many Requests**: Rate limit exceeded
- **500 Internal Server Error**: Unexpected server error
- **503 Service Unavailable**: Service temporarily unavailable

## Authentication

All API endpoints (except /health) require JWT authentication:

```
Authorization: Bearer <jwt_token>
```

**Token Properties:**
- Expiration: 1 hour
- Algorithm: HS256 or RS256
- Required claims: sub, iat, exp, permissions

## Rate Limiting

- **User-based**: 100 requests per minute per authenticated user
- **IP-based**: 1000 requests per minute per IP address
- **Burst allowance**: 20 requests
- **Algorithm**: Sliding window

## Request Headers

### Required
- `Authorization`: Bearer token
- `Content-Type`: application/json
- `Accept`: application/json

### Optional
- `X-Request-ID`: Unique request identifier for tracing
- `X-Client-Version`: Client application version
- `User-Agent`: Client application identifier

## Response Headers

- `X-Request-ID`: Request correlation ID
- `X-Response-Time`: Processing time in milliseconds
- `X-Rate-Limit-Remaining`: Remaining requests in window
- `X-Rate-Limit-Reset`: Rate limit reset timestamp
- `Location`: URI of created resource (for 201 responses)

## Data Validation

### Input Validation
- Server-side validation for all inputs
- XSS prevention through input sanitization
- SQL injection prevention through parameterized queries
- Maximum payload size: 1MB

### Business Rules
- Task title is required and cannot be empty
- Due date must be in the future if specified
- Assigned user must exist and be active
- Tags must be alphanumeric with underscores and hyphens only

## Error Format

All errors follow a standardized format:

```json
{
  "error": {
    "code": "ERROR_CODE",
    "message": "Human-readable error message",
    "details": "Additional error details or array of validation errors",
    "timestamp": "2024-01-15T10:30:00Z",
    "requestId": "req_123456789"
  }
}
```

## Code Examples

### cURL
```bash
curl -X POST https://api.taskmanagement.com/api/tasks \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "title": "Complete project documentation",
    "description": "Create comprehensive documentation",
    "priority": "HIGH",
    "tags": ["documentation", "api"]
  }'
```

### JavaScript
```javascript
const response = await fetch('https://api.taskmanagement.com/api/tasks', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    title: 'Complete project documentation',
    priority: 'HIGH'
  })
});

const task = await response.json();
```

### Python
```python
import requests

response = requests.post(
    'https://api.taskmanagement.com/api/tasks',
    headers={
        'Authorization': f'Bearer {token}',
        'Content-Type': 'application/json'
    },
    json={
        'title': 'Complete project documentation',
        'priority': 'HIGH'
    }
)

task = response.json()
```

## Compliance and Audit

### Audit Logging
All API operations are logged with:
- User identification
- Timestamp
- Request/response data
- IP address and user agent
- Correlation ID for tracing

### Data Retention
- Audit logs: 7 years
- Application logs: 90 days
- Performance metrics: 1 year

### GDPR Compliance
- Data minimization principles
- Right to erasure support
- Consent management
- Data portability
- Breach notification procedures

## Monitoring and Observability

### Metrics
- Request count and rate
- Response time percentiles
- Error rate by status code
- Authentication success/failure rate
- Business metrics (tasks created per hour)

### Health Checks
- Application health: GET /health
- Database connectivity
- External service dependencies
- Resource utilization

### Alerting
- Response time > 500ms
- Error rate > 1%
- Authentication failure rate > 5%
- Rate limit exceeded threshold

## Development and Testing

### Environment URLs
- **Production**: https://api.taskmanagement.com
- **Staging**: https://staging-api.taskmanagement.com
- **Development**: https://dev-api.taskmanagement.com

### Testing
- Unit tests with 80% coverage minimum
- Integration tests for API endpoints
- Contract tests using OpenAPI specification
- Performance tests with load simulation
- Security tests including penetration testing

## Support and Documentation

- **API Documentation**: https://docs.taskmanagement.com/api
- **Support Email**: api-support@taskmanagement.com
- **Status Page**: https://status.taskmanagement.com
- **Change Log**: https://docs.taskmanagement.com/changelog

---

**Generated**: Automatically from HLD and API Contract Outline documents
**Version**: 1.0.0
**Last Updated**: 2024
**Specification Standard**: OpenAPI 3.0.3