# Playwright TypeScript Automation Framework

Enterprise-grade test automation framework built with Playwright and TypeScript, following industry best practices and three-tier architecture pattern.

## 🏗️ Architecture

This framework follows a **three-tier architecture** pattern:

- **Pages (`src/pages/`)**: Locators and element definitions only
- **Actions (`src/actions/`)**: Reusable business logic flows (no assertions)
- **Tests (`src/tests/`)**: BDD-style test scenarios with assertions

## 🚀 Features

- ✅ **Multi-browser support** (Chromium, Firefox, WebKit)
- ✅ **Parallel test execution**
- ✅ **CI/CD integration** with GitHub Actions
- ✅ **Environment-specific test data management**
- ✅ **Comprehensive reporting** (HTML, JUnit, JSON)
- ✅ **TypeScript with strict typing**
- ✅ **Page Object Model implementation**
- ✅ **Retry mechanisms and error handling**
- ✅ **Screenshot and video capture on failures**
- ✅ **Mobile device testing support**

## 📁 Project Structure

```
Automation/
├── .github/workflows/          # CI/CD pipeline configuration
├── src/
│   ├── pages/                 # Page Object classes (locators only)
│   ├── actions/               # Business logic flows (no assertions)
│   ├── tests/                 # Test specifications (BDD style)
│   ├── utils/                 # Utility classes and helpers
│   └── test-data/             # Test data in JSON format
├── playwright.config.ts       # Playwright configuration
├── package.json              # Dependencies and scripts
└── tsconfig.json             # TypeScript configuration
```

## 🛠️ Setup

### Prerequisites
- Node.js 18 or higher
- npm or yarn

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd Automation
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Install Playwright browsers**
   ```bash
   npx playwright install
   ```

## 🧪 Running Tests

### Local Execution

```bash
# Run all tests
npm test

# Run tests in headed mode
npm run test:headed

# Run tests with UI mode
npm run test:ui

# Run tests in debug mode
npm run test:debug

# Run specific browser tests
npm run test:chromium
npm run test:firefox
npm run test:webkit

# Run mobile tests
npm run test:mobile
```

### Environment Configuration

Set environment variables for different test environments:

```bash
# Development
NODE_ENV=development npm test

# Staging
NODE_ENV=staging npm test

# Production
NODE_ENV=production npm test
```

## 📊 Test Reports

After test execution, reports are available in:

- **HTML Report**: `playwright-report/index.html`
- **JUnit XML**: `test-results/junit.xml`
- **JSON Results**: `test-results/results.json`

View HTML report:
```bash
npm run report
```

## 🏗️ Framework Components

### Page Objects

Page classes contain only locators and element definitions:

```typescript
export class LoginPage extends BasePage {
  readonly usernameInput: Locator;
  readonly passwordInput: Locator;
  readonly loginButton: Locator;

  constructor(page: Page) {
    super(page);
    this.usernameInput = page.locator('[data-testid="username"]');
    this.passwordInput = page.locator('[data-testid="password"]');
    this.loginButton = page.locator('[data-testid="login-button"]');
  }
}
```

### Actions

Action classes implement reusable business flows:

```typescript
export class LoginActions extends BaseActions {
  private loginPage: LoginPage;

  constructor(page: Page) {
    super(page);
    this.loginPage = new LoginPage(page);
  }

  async loginUser(username: string, password: string): Promise<void> {
    await this.loginPage.usernameInput.fill(username);
    await this.loginPage.passwordInput.fill(password);
    await this.loginPage.loginButton.click();
    await this.waitForNavigation();
  }
}
```

### Tests

Test files follow BDD pattern with Given/When/Then structure:

```typescript
test('should login successfully with valid credentials', async ({ page }) => {
  // Given: User is on login page
  const loginActions = new LoginActions(page);
  await page.goto('/login');

  // When: User enters valid credentials and clicks login
  const userData = testDataLoader.getTestDataByKey('users', 'validUser');
  await loginActions.loginUser(userData.username, userData.password);

  // Then: User should be redirected to dashboard
  await expect(page).toHaveURL('/dashboard');
  await expect(page.locator('[data-testid="welcome-message"]')).toBeVisible();
});
```

## 📋 Test Data Management

Test data is managed through JSON files in `src/test-data/`:

```json
{
  "common": {
    "baseUrl": "https://example.com"
  },
  "development": {
    "users": {
      "validUser": {
        "username": "dev_user@example.com",
        "password": "DevPassword123!"
      }
    }
  }
}
```

Load test data in tests:

```typescript
const testDataLoader = TestDataLoader.getInstance();
const userData = testDataLoader.getTestDataByKey('example-data', 'users', 'development');
```

## 🔧 CI/CD Integration

The framework includes GitHub Actions workflow for:

- Multi-browser parallel execution
- Automatic test report generation
- Artifact storage for reports and screenshots
- Integration with pull requests

## 📝 Best Practices

1. **Separation of Concerns**: Keep locators, actions, and assertions separate
2. **No Hard Waits**: Use Playwright's auto-waiting mechanisms
3. **Data-Driven Tests**: Use external JSON files for test data
4. **Meaningful Names**: Use descriptive names for tests and methods
5. **Error Handling**: Implement proper retry mechanisms
6. **Documentation**: Add inline documentation for complex logic

## 🤝 Contributing

1. Follow the established architecture patterns
2. Add tests for new features
3. Update documentation for changes
4. Ensure all tests pass before submitting PR

## 📞 Support

For questions or issues:
- Create an issue in the repository
- Contact the QA Automation team
- Review the documentation and examples

---

**Framework Version**: 1.0.0  
**Last Updated**: 2024  
**Maintained by**: QA Automation Team