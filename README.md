# SCIB Task Creation Automation Framework

A comprehensive Playwright TypeScript automation framework for testing task creation functionality in the SCIB platform.

## 🏗️ Architecture

This framework follows a three-tier architecture pattern:

- **Pages (`src/pages/`)**: Contains Page Object Models with locators only
- **Actions (`src/actions/`)**: Reusable business logic flows without assertions
- **Tests (`src/tests/`)**: BDD-style test scenarios with all assertions

## 📁 Project Structure

```
Automation/
├── .github/workflows/          # CI/CD pipeline configuration
├── src/
│   ├── pages/                  # Page Object Models
│   ├── actions/                # Business logic actions
│   ├── tests/                  # Test specifications
│   ├── test-data/             # JSON test data files
│   └── utils/                 # Utility classes
├── playwright.config.ts        # Playwright configuration
├── package.json               # Dependencies and scripts
└── README.md                  # This file
```

## 🚀 Getting Started

### Prerequisites

- Node.js 18+ 
- npm or yarn

### Installation

1. Clone the repository
2. Navigate to the Automation folder
3. Install dependencies:
   ```bash
   npm install
   ```
4. Install Playwright browsers:
   ```bash
   npx playwright install
   ```

### Running Tests

```bash
# Run all tests
npm test

# Run tests in headed mode
npm run test:headed

# Run tests with UI mode
npm run test:ui

# Run specific browser tests
npm run test:chromium
npm run test:firefox
npm run test:webkit

# Run mobile tests
npm run test:mobile

# Debug tests
npm run test:debug
```

## 📋 Test Coverage

### Test Scenarios Covered

1. **DEMO-2646**: Successful task creation with valid data
2. **DEMO-2648**: Validation failure - empty title
3. **DEMO-2650**: Validation failure - empty description  
4. **DEMO-2652**: Validation failure - missing priority
5. **DEMO-2654**: Validation failure - empty assignee
6. **DEMO-2656**: Validation failure - invalid assignee domain
7. **DEMO-2658**: Multiple validation errors - all fields empty
8. **DEMO-2660**: Special character handling in title
9. **DEMO-2662**: Maximum length description validation
10. **DEMO-2664**: Invalid priority value handling

### Test Categories

- ✅ **Success Scenarios**: Valid data combinations
- ❌ **Validation Failures**: Missing mandatory fields
- 🔍 **Special Cases**: Edge cases and character limits
- 🔄 **Multiple Validations**: Complex validation scenarios

## 🛠️ Configuration

### Environment Variables

- `BASE_URL`: Application base URL (default: https://scib-task-management.com)
- `TEST_ENV`: Environment (development/staging/production)
- `CI`: Set to true in CI environment

### Browser Support

- ✅ Chromium
- ✅ Firefox  
- ✅ WebKit (Safari)
- ✅ Mobile Chrome
- ✅ Mobile Safari

## 📊 Reporting

The framework generates multiple report formats:

- **HTML Report**: Interactive test results
- **JSON Report**: Machine-readable results
- **JUnit XML**: CI/CD integration
- **Line Reporter**: Console output

View reports:
```bash
npm run report
```

## 🔧 CI/CD Integration

The framework includes GitHub Actions workflow for:

- Multi-browser parallel execution
- Automatic report generation
- Artifact storage
- Scheduled test runs

## 📝 Test Data Management

Test data is organized in JSON files:

- `task-creation-data.json`: Test scenarios and edge cases
- `validation-messages.json`: Expected error messages

Dynamic test data generation is supported through `TestDataLoader` utility.

## 🎯 Best Practices

- **Page Objects**: Locators only, no logic
- **Actions**: Reusable flows, no assertions
- **Tests**: BDD-style, all assertions here
- **Async/Await**: Consistent asynchronous handling
- **Auto-waiting**: Leverage Playwright's built-in waits
- **Strong Typing**: Full TypeScript support

## 🐛 Debugging

```bash
# Debug mode with browser DevTools
npm run test:debug

# Headed mode to see browser
npm run test:headed

# Trace viewer for failed tests
npx playwright show-trace trace.zip
```

## 📈 Maintenance

### Adding New Tests

1. Add test data to JSON files
2. Update Page Objects if new elements
3. Create/update Action methods
4. Write test scenarios with assertions

### Updating Locators

Locators are centralized in Page Object Models for easy maintenance.

## 🤝 Contributing

1. Follow the three-tier architecture
2. Maintain TypeScript typing
3. Add appropriate test data
4. Update documentation

## 📞 Support

For issues and questions:
- Check existing test reports
- Review Playwright documentation
- Contact SCIB QA Team

---

**Framework Version**: 1.0.0  
**Playwright Version**: 1.40.0+  
**Last Updated**: 2024