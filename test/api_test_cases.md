# API Test Cases - Task Management System

## Overview
This document describes all test cases for the Task Management System API.

## Test Environment
- **Base URL**: http://localhost:8080/api
- **API Version**: 1.0.0
- **Test Framework**: JUnit 5, Postman

---

## 1. Create Task Tests

### TC-001: Create Task with Valid Request
**Endpoint**: POST /tasks  
**Scenario**: Create a new task with valid title and description  
**Preconditions**: None  
**Request Body**:
```json
{
  "title": "Complete project documentation",
  "description": "Write comprehensive documentation for the task management system"
}
```
**Expected Result**:
- Status Code: 201 Created
- Response contains: id, title, description, status (PENDING), createdAt
- Task is stored in the system

---

### TC-002: Create Task with Missing Title
**Endpoint**: POST /tasks  
**Scenario**: Attempt to create task without title  
**Preconditions**: None  
**Request Body**:
```json
{
  "description": "Valid description"
}
```
**Expected Result**:
- Status Code: 400 Bad Request
- Error code: VALIDATION_ERROR
- Error message indicates title is required

---

### TC-003: Create Task with Blank Title
**Endpoint**: POST /tasks  
**Scenario**: Attempt to create task with empty title  
**Preconditions**: None  
**Request Body**:
```json
{
  "title": "",
  "description": "Valid description"
}
```
**Expected Result**:
- Status Code: 400 Bad Request
- Error code: VALIDATION_ERROR
- Error message indicates title is required

---

### TC-004: Create Task with Title Too Long
**Endpoint**: POST /tasks  
**Scenario**: Attempt to create task with title exceeding 200 characters  
**Preconditions**: None  
**Request Body**:
```json
{
  "title": "[201 character string]",
  "description": "Valid description"
}
```
**Expected Result**:
- Status Code: 400 Bad Request
- Error code: VALIDATION_ERROR
- Error message indicates title must not exceed 200 characters

---

### TC-005: Create Task with Missing Description
**Endpoint**: POST /tasks  
**Scenario**: Attempt to create task without description  
**Preconditions**: None  
**Request Body**:
```json
{
  "title": "Valid title"
}
```
**Expected Result**:
- Status Code: 400 Bad Request
- Error code: VALIDATION_ERROR
- Error message indicates description is required

---

### TC-006: Create Task with Description Too Long
**Endpoint**: POST /tasks  
**Scenario**: Attempt to create task with description exceeding 1000 characters  
**Preconditions**: None  
**Request Body**:
```json
{
  "title": "Valid title",
  "description": "[1001 character string]"
}
```
**Expected Result**:
- Status Code: 400 Bad Request
- Error code: VALIDATION_ERROR
- Error message indicates description must not exceed 1000 characters

---

## 2. Get Task Tests

### TC-007: Get Task with Valid ID
**Endpoint**: GET /tasks/{id}  
**Scenario**: Retrieve an existing task  
**Preconditions**: Task with given ID exists  
**Path Parameters**: id = 1  
**Expected Result**:
- Status Code: 200 OK
- Response contains complete task details
- All fields match the stored task

---

### TC-008: Get Task with Non-Existent ID
**Endpoint**: GET /tasks/{id}  
**Scenario**: Attempt to retrieve a task that doesn't exist  
**Preconditions**: None  
**Path Parameters**: id = 999  
**Expected Result**:
- Status Code: 404 Not Found
- Error code: NOT_FOUND
- Error message: "Task not found with id: 999"

---

## 3. Update Task Tests

### TC-009: Update Task with Valid Request
**Endpoint**: PUT /tasks/{id}  
**Scenario**: Update an existing task with valid data  
**Preconditions**: Task with given ID exists  
**Path Parameters**: id = 1  
**Request Body**:
```json
{
  "title": "Updated title",
  "description": "Updated description",
  "status": "IN_PROGRESS"
}
```
**Expected Result**:
- Status Code: 200 OK
- Response contains updated task details
- updatedAt timestamp is set

---

### TC-010: Update Task with Non-Existent ID
**Endpoint**: PUT /tasks/{id}  
**Scenario**: Attempt to update a task that doesn't exist  
**Preconditions**: None  
**Path Parameters**: id = 999  
**Request Body**:
```json
{
  "title": "Valid title",
  "description": "Valid description",
  "status": "IN_PROGRESS"
}
```
**Expected Result**:
- Status Code: 404 Not Found
- Error code: NOT_FOUND
- Error message: "Task not found with id: 999"

---

### TC-011: Update Task with Invalid Status
**Endpoint**: PUT /tasks/{id}  
**Scenario**: Attempt to update task with null status  
**Preconditions**: Task with given ID exists  
**Path Parameters**: id = 1  
**Request Body**:
```json
{
  "title": "Valid title",
  "description": "Valid description",
  "status": null
}
```
**Expected Result**:
- Status Code: 400 Bad Request
- Error code: VALIDATION_ERROR
- Error message indicates status is required

---

### TC-012: Update Task with Blank Title
**Endpoint**: PUT /tasks/{id}  
**Scenario**: Attempt to update task with empty title  
**Preconditions**: Task with given ID exists  
**Path Parameters**: id = 1  
**Request Body**:
```json
{
  "title": "",
  "description": "Valid description",
  "status": "IN_PROGRESS"
}
```
**Expected Result**:
- Status Code: 400 Bad Request
- Error code: VALIDATION_ERROR
- Error message indicates title is required

---

## 4. Delete Task Tests

### TC-013: Delete Task with Valid ID
**Endpoint**: DELETE /tasks/{id}  
**Scenario**: Delete an existing task  
**Preconditions**: Task with given ID exists  
**Path Parameters**: id = 1  
**Expected Result**:
- Status Code: 204 No Content
- Task is removed from the system
- Subsequent GET returns 404

---

### TC-014: Delete Task with Non-Existent ID
**Endpoint**: DELETE /tasks/{id}  
**Scenario**: Attempt to delete a task that doesn't exist  
**Preconditions**: None  
**Path Parameters**: id = 999  
**Expected Result**:
- Status Code: 404 Not Found
- Error code: NOT_FOUND
- Error message: "Task not found with id: 999"

---

## 5. Get All Tasks Tests

### TC-015: Get All Tasks without Filter
**Endpoint**: GET /tasks  
**Scenario**: Retrieve all tasks  
**Preconditions**: Multiple tasks exist in the system  
**Query Parameters**: None  
**Expected Result**:
- Status Code: 200 OK
- Response is an array of tasks
- All tasks are returned regardless of status

---

### TC-016: Get All Tasks with Status Filter
**Endpoint**: GET /tasks?status=PENDING  
**Scenario**: Retrieve only tasks with PENDING status  
**Preconditions**: Tasks with various statuses exist  
**Query Parameters**: status = PENDING  
**Expected Result**:
- Status Code: 200 OK
- Response contains only tasks with PENDING status
- Other status tasks are not included

---

### TC-017: Get All Tasks when None Exist
**Endpoint**: GET /tasks  
**Scenario**: Retrieve tasks when database is empty  
**Preconditions**: No tasks in the system  
**Query Parameters**: None  
**Expected Result**:
- Status Code: 200 OK
- Response is an empty array

---

## Test Coverage Summary

| Category | Test Cases | Positive | Negative |
|----------|------------|----------|----------|
| Create Task | 6 | 1 | 5 |
| Get Task | 2 | 1 | 1 |
| Update Task | 4 | 1 | 3 |
| Delete Task | 2 | 1 | 1 |
| Get All Tasks | 3 | 3 | 0 |
| **Total** | **17** | **7** | **10** |

---

## Test Execution Notes

1. **Test Order**: Tests should be executed in the order listed to ensure proper setup
2. **Data Cleanup**: Each test should clean up its data after execution
3. **Environment**: Tests can run against local, dev, or staging environments
4. **Automation**: All tests are automated using JUnit 5 and Postman
5. **CI/CD Integration**: Tests are integrated into the GitHub Actions pipeline

---

## Edge Cases Covered

- Empty strings vs null values
- Boundary values for string lengths (200 for title, 1000 for description)
- All valid status enum values
- Non-existent resource IDs
- Concurrent operations (handled by in-memory store)

---

## Known Limitations

1. In-memory storage means data is lost on application restart
2. No authentication/authorization tests (not implemented in current version)
3. No performance/load testing included
4. No database transaction rollback tests

---

## Future Test Enhancements

- Add performance tests for high-volume scenarios
- Add security tests when authentication is implemented
- Add integration tests with external systems
- Add contract tests for API versioning
- Add chaos engineering tests for resilience
