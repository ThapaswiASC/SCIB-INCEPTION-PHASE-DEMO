# OpenAPI Specifications

## Overview
This directory contains the complete OpenAPI 3.0.3 specification for the Task Management API System, automatically generated from the High-Level Design Document and API Contract Outline.

## Files

### openapi.yaml
Complete OpenAPI specification including:
- **API Information**: Title, description, version, contact details
- **Server Configuration**: Production, staging, and development endpoints
- **Security Schemes**: OAuth 2.0 Bearer token authentication
- **Endpoints**: POST /api/tasks with comprehensive request/response schemas
- **Error Handling**: Detailed error responses for all HTTP status codes
- **Health Checks**: Basic and detailed health monitoring endpoints
- **Data Models**: Complete schemas with validation rules and examples
- **Enterprise Extensions**: Compliance, monitoring, and deployment metadata

## API Features

### Core Functionality
- **Task Creation**: POST /api/tasks endpoint with comprehensive validation
- **Input Validation**: Multi-layer validation with detailed error responses
- **Business Rules**: Due date validation, user assignment checks
- **Audit Logging**: Complete audit trail for all operations

### Security
- **Authentication**: OAuth 2.0 Bearer tokens with JWT format
- **Authorization**: Role-based access control (RBAC)
- **Rate Limiting**: 100 requests/hour per user, burst protection
- **Input Security**: XSS prevention, SQL injection protection
- **Data Encryption**: TLS 1.3 in transit, AES-256 at rest

### Compliance
- **GDPR**: Data protection, right to erasure, data portability
- **SOX**: Audit trails, change management controls
- **ISO 27001**: Security controls, risk management
- **PCI-DSS**: Data encryption, access control (if applicable)

### Monitoring & Observability
- **Health Endpoints**: Basic and detailed system health checks
- **Request Tracing**: Correlation IDs for distributed tracing
- **Performance Metrics**: Response time, error rate, throughput
- **Alerting**: Automated alerts for SLA violations

## Usage

### Development
```bash
# Validate OpenAPI specification
swagger-codegen validate -i openapi.yaml

# Generate client SDKs
swagger-codegen generate -i openapi.yaml -l javascript -o ./client-js
swagger-codegen generate -i openapi.yaml -l python -o ./client-python
swagger-codegen generate -i openapi.yaml -l java -o ./client-java

# Generate server stubs
swagger-codegen generate -i openapi.yaml -l nodejs-server -o ./server-nodejs
swagger-codegen generate -i openapi.yaml -l spring -o ./server-spring
```

### Documentation
```bash
# Serve interactive documentation
swagger-ui-serve openapi.yaml

# Generate static documentation
redoc-cli build openapi.yaml --output api-docs.html
```

### Testing
```bash
# API contract testing
dredd openapi.yaml https://api.taskmanagement.company.com/v1

# Load testing with generated scenarios
k6 run --summary-export=results.json openapi-load-test.js
```

## Validation Rules

### Request Validation
- **Title**: Required, 1-100 characters, no HTML special characters
- **Description**: Optional, max 500 characters, no HTML special characters
- **Status**: Enum [TODO, IN_PROGRESS, DONE], defaults to TODO
- **Priority**: Enum [LOW, MEDIUM, HIGH, CRITICAL], defaults to MEDIUM
- **Due Date**: ISO 8601 date format, must be future date
- **Assigned To**: Valid UUID, user must exist and be active

### Response Validation
- All responses include correlation ID for tracing
- Error responses follow consistent structure
- Success responses include all required fields
- Timestamps in ISO 8601 format with timezone

## Error Handling

### HTTP Status Codes
- **201 Created**: Task successfully created
- **400 Bad Request**: Validation errors with detailed field-level messages
- **401 Unauthorized**: Invalid or missing authentication token
- **403 Forbidden**: Insufficient permissions for operation
- **409 Conflict**: Business rule violations
- **429 Too Many Requests**: Rate limit exceeded
- **500 Internal Server Error**: System errors

### Error Response Format
All error responses follow a consistent structure:
```json
{
  "error": {
    "code": "ERROR_CODE",
    "message": "Human-readable message",
    "details": { /* Additional context */ },
    "timestamp": "2024-12-19T10:30:00.000Z",
    "correlationId": "abc123-def456-ghi789"
  }
}
```

## Rate Limiting

### Limits
- **Per User**: 100 requests per hour
- **Per API Key**: 1000 requests per hour
- **Burst Protection**: 10 requests per minute

### Headers
- `X-Rate-Limit-Remaining`: Remaining requests in current window
- `X-Rate-Limit-Reset`: Unix timestamp when limit resets
- `Retry-After`: Seconds to wait when rate limited

## Authentication

### OAuth 2.0 Flow
1. **Authorization**: Redirect to authorization server
2. **Token Exchange**: Exchange authorization code for access token
3. **API Access**: Include Bearer token in Authorization header
4. **Token Refresh**: Use refresh token when access token expires

### Required Scopes
- `tasks:create`: Create new tasks
- `tasks:read`: Read task information
- `tasks:update`: Update existing tasks
- `tasks:delete`: Delete tasks

## Performance Requirements

### Response Times
- **95th Percentile**: < 200ms
- **99th Percentile**: < 500ms
- **Maximum**: < 2 seconds

### Throughput
- **Normal Load**: 1,000 RPS
- **Peak Load**: 5,000 RPS
- **Sustained Load**: 2,000 RPS for 1 hour

### Availability
- **Uptime**: 99.99% (52.6 minutes downtime/year)
- **Failover**: < 30 seconds detection, < 2 minutes recovery
- **Error Rate**: < 0.1% for all requests

## Deployment

### Kubernetes Configuration
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: task-management-api
spec:
  replicas: 3
  selector:
    matchLabels:
      app: task-management-api
  template:
    spec:
      containers:
      - name: api
        image: task-management-api:latest
        ports:
        - containerPort: 3000
        resources:
          requests:
            cpu: 100m
            memory: 256Mi
          limits:
            cpu: 500m
            memory: 512Mi
        livenessProbe:
          httpGet:
            path: /health
            port: 3000
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /health
            port: 3000
          initialDelaySeconds: 5
          periodSeconds: 5
```

### Auto-scaling
```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: task-management-api-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: task-management-api
  minReplicas: 2
  maxReplicas: 20
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
```

## Monitoring

### Metrics
- **Application**: Response time, error rate, throughput
- **Infrastructure**: CPU, memory, disk, network
- **Business**: Task creation rate, user activity

### Alerting
- **Critical**: Response time > 500ms, Error rate > 1%
- **Warning**: Response time > 200ms, CPU > 70%
- **Info**: Unusual traffic patterns, capacity planning

### Dashboards
- **Executive**: Business metrics and KPIs
- **Operations**: System health and performance
- **Development**: Application metrics and errors

## Compliance & Audit

### Audit Logging
- All API calls logged with user attribution
- Complete request/response data (excluding sensitive fields)
- Immutable audit trail with cryptographic integrity
- Real-time audit event streaming

### Data Protection
- Personal data identification and classification
- Automated data retention and deletion
- Data subject rights implementation (GDPR)
- Cross-border data transfer controls

### Security Controls
- Regular security assessments and penetration testing
- Vulnerability scanning and dependency management
- Security incident response procedures
- Compliance reporting and certification

---

**Generated By**: Lead API Integration Architect  
**Source Documents**: HLDDocument.txt, APIContractOutline.txt  
**ADR Reference**: DEMO-2350  
**Last Updated**: 2024-12-19  
**Specification Version**: 1.0.0  
**OpenAPI Version**: 3.0.3