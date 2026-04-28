# SCIB Task Management System - Playwright Automation Framework

## Overview

This repository contains a comprehensive Playwright automation framework for testing the SCIB Task Management System. The framework follows industry best practices with a three-tier architecture pattern, ensuring maintainable, scalable, and robust test automation.

## Architecture

### Three-Tier Architecture Pattern

1. **Pages Layer (`src/pages/`)** - Contains Page Object Models with locators only
2. **Actions Layer (`src/actions/`)** - Contains reusable business flows and workflows
3. **Tests Layer (`src/tests/`)** - Contains BDD-style test specifications with assertions

## Framework Features

- ✅ **Multi-Browser Support** - Chromium, Firefox, WebKit, Mobile browsers
- ✅ **Parallel Execution** - Optimized for CI/CD environments
- ✅ **Data-Driven Testing** - JSON-based test data management
- ✅ **Environment Configuration** - Support for dev, test, staging, prod
- ✅ **Comprehensive Reporting** - HTML, JUnit, JSON reports
- ✅ **CI/CD Integration** - GitHub Actions workflow included
- ✅ **Strong TypeScript Typing** - Full type safety and IntelliSense
- ✅ **Auto-Waiting** - Playwright's built-in smart waiting
- ✅ **Screenshot & Video** - Failure capture and debugging support

## Test Coverage

### Task Creation Scenarios (10 Test Cases)

1. **DEMO-2646** - Successful task creation with all valid mandatory fields
2. **DEMO-2648** - Task creation fails with empty title
3. **DEMO-2650** - Task creation fails with empty description  
4. **DEMO-2652** - Task creation fails with missing priority
5. **DEMO-2654** - Task creation fails with empty assignee
6. **DEMO-2656** - Task creation fails with non-SCIB domain assignee
7. **DEMO-2658** - Multiple validation errors with all fields missing
8. **DEMO-2660** - Task creation with special characters in title
9. **DEMO-2662** - Task creation with maximum length description
10. **DEMO-2664** - Task creation fails with invalid priority value

## Quick Start

### Prerequisites

- Node.js 18+ 
- npm or yarn

### Installation

```bash
# Clone repository
git clone <repository-url>
cd Automation

# Install dependencies
npm install

# Install Playwright browsers
npm run install:browsers
```

### Running Tests

```bash
# Run all tests
npm test

# Run tests in headed mode
npm run test:headed

# Run specific browser
npm run test:chromium
npm run test:firefox
npm run test:webkit

# Run mobile tests
npm run test:mobile

# Debug mode
npm run test:debug

# Interactive UI mode
npm run test:ui
```

### Environment Configuration

Set environment variables:

```bash
# Base URL
export BASE_URL=http://localhost:3000

# Test environment
export TEST_ENV=dev
```

## Project Structure

```
Automation/
├── .github/workflows/          # CI/CD workflows
├── src/
│   ├── pages/                  # Page Object Models
│   │   └── TaskCreationPage.ts
│   ├── actions/                # Business workflows
│   │   └── TaskCreationActions.ts
│   ├── tests/                  # Test specifications
│   │   ├── task-creation-success.spec.ts
│   │   ├── task-creation-validation.spec.ts
│   │   ├── task-creation-special-cases.spec.ts
│   │   └── task-creation-multiple-validation.spec.ts
│   ├── test-data/              # Test data files
│   │   ├── task-data.json
│   │   └── validation-messages.json
│   └── utils/                  # Utility classes
│       └── TestDataLoader.ts
├── playwright.config.ts        # Playwright configuration
├── package.json               # Dependencies and scripts
└── readme.md                  # This file
```

## Test Data Management

### JSON-Based Test Data

Test data is externalized in JSON files for easy maintenance:

- `task-data.json` - Task creation data sets
- `validation-messages.json` - Error messages and validation rules

### TestDataLoader Utility

The `TestDataLoader` class provides:
- Environment-specific configurations
- Dynamic test data generation
- Centralized data access methods

## Page Object Model

### TaskCreationPage.ts

Contains all locators for the task creation form:
- Form input elements
- Validation message elements  
- Success/error state elements
- Navigation elements

**Key Principles:**
- Locators only, no business logic
- Strong TypeScript typing
- Descriptive element names
- Consistent naming conventions

## Action Classes

### TaskCreationActions.ts

Provides reusable business workflows:
- Form navigation and setup
- Field filling operations
- Form submission workflows
- Data validation helpers

**Key Principles:**
- No assertions (test-agnostic)
- Reusable across test scenarios
- Async/await consistency
- Error handling

## Test Specifications

### BDD-Style Test Structure

Tests follow Given/When/Then pattern:

```typescript
test('Scenario description', async () => {
  // Given - Setup conditions
  await taskActions.navigateToTaskCreationForm();
  
  // When - Perform actions
  await taskActions.createTask(validTaskData);
  
  // Then - Verify outcomes
  await expect(page.locator('[data-testid="success-message"]')).toBeVisible();
});
```

## CI/CD Integration

### GitHub Actions Workflow

The framework includes a comprehensive CI/CD pipeline:

- **Multi-browser matrix** - Tests run across all supported browsers
- **Parallel execution** - Optimized for speed
- **Artifact collection** - Reports and screenshots preserved
- **Failure handling** - Retry logic and debugging artifacts

### Pipeline Features

- Automatic browser installation
- Environment variable support
- Test result artifacts
- HTML report generation
- Screenshot/video capture on failures

## Best Practices

### Locator Strategy

1. **Prefer data-testid attributes** for stable element identification
2. **Avoid CSS selectors** that depend on styling
3. **Use semantic locators** when data-testid unavailable
4. **Implement locator fallbacks** for robustness

### Test Design

1. **Independent tests** - Each test can run in isolation
2. **Clear test names** - Descriptive scenario identification
3. **Proper cleanup** - No test pollution
4. **Data-driven approach** - Externalized test data

### Error Handling

1. **Explicit waits** - Use Playwright's auto-waiting
2. **Meaningful assertions** - Clear failure messages
3. **Screenshot capture** - Visual debugging support
4. **Retry mechanisms** - Handle flaky scenarios

## Debugging

### Debug Mode

```bash
# Run in debug mode
npm run test:debug

# Run specific test in debug
npx playwright test task-creation-success.spec.ts --debug
```

### Visual Debugging

- **Screenshots** - Captured on failures
- **Videos** - Recorded for failed tests
- **Trace files** - Step-by-step execution traces
- **HTML reports** - Interactive test results

## Maintenance

### Adding New Tests

1. **Identify test scenario** - Define Given/When/Then
2. **Update Page Objects** - Add new locators if needed
3. **Extend Actions** - Create reusable workflows
4. **Write test specification** - Follow BDD pattern
5. **Add test data** - Update JSON files

### Updating Locators

1. **Centralized updates** - Modify Page Object Models only
2. **Batch testing** - Run full suite after changes
3. **Version control** - Track locator changes

## Reporting

### HTML Reports

```bash
# Generate and view HTML report
npm run report
```

### CI/CD Reports

- **JUnit XML** - For CI/CD integration
- **JSON results** - For custom processing
- **Artifacts** - Screenshots, videos, traces

## Support

### Common Issues

1. **Browser installation** - Run `npm run install:browsers`
2. **Port conflicts** - Check BASE_URL configuration
3. **Timeout issues** - Adjust timeout values in config
4. **Flaky tests** - Enable retries in CI environment

### Troubleshooting

1. **Check browser compatibility** - Ensure supported browser versions
2. **Verify test data** - Validate JSON file structure
3. **Review locators** - Confirm element availability
4. **Examine logs** - Check console output for errors

---

## Contributing

1. Follow the three-tier architecture pattern
2. Maintain TypeScript typing
3. Add comprehensive test coverage
4. Update documentation
5. Test across all supported browsers

## License

MIT License - See LICENSE file for details