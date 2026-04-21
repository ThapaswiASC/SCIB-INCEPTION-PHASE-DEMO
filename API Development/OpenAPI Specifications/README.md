# OpenAPI Specifications

## Task Management API - OpenAPI 3.0.3 Specification

This directory contains the complete OpenAPI specification for the Task Management API, automatically generated from the High-Level Design (HLD) document and API Contract Outline.

### Files

- **`openapi.yaml`** - Complete OpenAPI 3.0.3 specification for the Task Creation API endpoint

### API Overview

The Task Management API provides enterprise-grade task creation functionality with comprehensive validation, security, and compliance features.

#### Key Features

- **Real-time Synchronization**: Tasks appear across all connected clients within 500ms
- **Enterprise Security**: JWT authentication with RBAC authorization
- **Comprehensive Validation**: Multi-layer input validation and business rule enforcement
- **Audit Compliance**: Complete audit trails for GDPR, SOX, and ISO 27001 compliance
- **Performance Optimized**: <200ms response times with 1000+ RPS throughput
- **Rate Limited**: DDoS protection with 100 requests/minute per user

#### Endpoints

| Method | Endpoint | Description | Authentication |
|--------|----------|-------------|----------------|
| POST | `/api/tasks` | Create a new task | Required (JWT Bearer) |

#### Request/Response Format

- **Content Type**: `application/json`
- **Character Encoding**: UTF-8
- **Date Format**: ISO 8601 (RFC 3339)
- **UUID Format**: UUID v4

#### Error Handling

Standardized error responses with:
- HTTP status codes (400, 401, 403, 409, 422, 429, 500, 503)
- Structured error messages with correlation IDs
- Detailed validation error information
- Actionable error remediation guidance

#### Security Features

- **Authentication**: JWT Bearer tokens (24-hour expiry)
- **Authorization**: Role-based access control (RBAC)
- **Input Validation**: XSS and SQL injection prevention
- **Rate Limiting**: Per-user and per-IP rate limits
- **Encryption**: TLS 1.3 in transit, AES-256 at rest

#### Compliance

- **GDPR**: Data protection and privacy controls
- **SOX**: Financial data handling compliance
- **ISO 27001**: Information security management
- **OWASP Top 10**: Security vulnerability protection

### Usage Examples

#### Basic Task Creation

```bash
curl -X POST https://api.taskmanagement.company.com/v1/api/tasks \
  -H "Authorization: Bearer <jwt_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Implement user authentication",
    "priority": "HIGH"
  }'
```

#### Detailed Task Creation

```bash
curl -X POST https://api.taskmanagement.company.com/v1/api/tasks \
  -H "Authorization: Bearer <jwt_token>" \
  -H "Content-Type: application/json" \
  -H "X-Correlation-ID: abc12345-def6-7890-ghij-klmnopqrstuv" \
  -d '{
    "title": "Implement JWT-based authentication system",
    "description": "Implement comprehensive JWT authentication with RBAC",
    "priority": "HIGH",
    "status": "TODO",
    "dueDate": "2024-12-31T23:59:59Z",
    "assignedTo": "550e8400-e29b-41d4-a716-446655440000",
    "tags": ["authentication", "security", "backend"]
  }'
```

### Validation Rules

#### Required Fields
- `title` (1-200 characters)
- `priority` (LOW, MEDIUM, HIGH, CRITICAL)

#### Optional Fields
- `description` (max 2000 characters)
- `status` (defaults to TODO)
- `dueDate` (must be future date)
- `assignedTo` (valid user UUID)
- `tags` (max 10 tags, 50 chars each)

#### Business Rules
- Due date must be at least 1 hour in the future
- HIGH/CRITICAL priority tasks require manager approval
- Assigned user must be active with appropriate permissions
- Tags are automatically normalized (lowercased, deduplicated)
- Title similarity check warns of potential duplicates (>90% match)

### Rate Limiting

- **Per User**: 100 requests per minute
- **Per IP**: 1000 requests per minute
- **Burst Allowance**: 10 requests
- **Headers**: Rate limit information included in all responses

### Monitoring and Observability

#### Request Tracing
- Correlation IDs for distributed tracing
- Request/response logging with structured JSON
- Performance metrics and alerting

#### Health Monitoring
- Real-time performance dashboards
- Error rate monitoring and alerting
- Business metrics tracking

### Development Tools

#### Interactive Documentation
- Swagger UI: `https://api.taskmanagement.company.com/docs`
- ReDoc: `https://api.taskmanagement.company.com/redoc`

#### SDK and Tools
- Postman Collection: Available in documentation
- Client SDKs: JavaScript, Python, Java, C#
- Code generators: OpenAPI Generator compatible

### Environments

| Environment | Base URL | Purpose |
|-------------|----------|----------|
| Production | `https://api.taskmanagement.company.com/v1` | Live production system |
| Staging | `https://api-staging.taskmanagement.company.com/v1` | Pre-production testing |
| Development | `https://api-dev.taskmanagement.company.com/v1` | Development and testing |

### Support and Documentation

- **API Documentation**: https://docs.taskmanagement.company.com
- **Support Email**: api-support@company.com
- **Developer Portal**: https://developer.taskmanagement.company.com
- **Status Page**: https://status.taskmanagement.company.com

### Version Information

- **OpenAPI Version**: 3.0.3
- **API Version**: 1.0.0
- **Generated From**: HLD Document and API Contract Outline
- **ADR Reference**: DEMO-2350
- **Last Updated**: 2024

### Compliance and Security

This API specification has been validated for:
- ✅ OpenAPI 3.0.3 compliance
- ✅ Security best practices (OWASP)
- ✅ Enterprise architecture standards
- ✅ GDPR data protection requirements
- ✅ SOX financial compliance
- ✅ ISO 27001 security management

---

**Note**: This specification is automatically generated and maintained. Manual modifications may be overwritten during regeneration. For changes, update the source HLD document or API Contract Outline.

**Classification**: Internal Use Only
**Distribution**: Authorized Personnel Only