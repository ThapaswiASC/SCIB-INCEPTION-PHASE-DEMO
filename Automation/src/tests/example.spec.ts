import { test, expect } from '@playwright/test';
import { TestDataLoader } from '@utils/TestDataLoader';
import { TestHelpers } from '@utils/TestHelpers';

/**
 * Example Test Specification
 * Demonstrates BDD-style test structure and framework usage
 * Following Given/When/Then pattern with proper assertions
 */

test.describe('Example Test Suite', () => {
  let testDataLoader: TestDataLoader;
  
  test.beforeEach(async ({ page }) => {
    testDataLoader = TestDataLoader.getInstance();
    
    // Given: Navigate to the application
    const testData = testDataLoader.loadTestData('example-data', process.env.NODE_ENV || 'development');
    await page.goto(testData.common.baseUrl);
  });

  test('should demonstrate framework capabilities', async ({ page }) => {
    // Given: User is on the homepage
    await expect(page).toHaveTitle(/Example/);
    
    // When: User interacts with the page
    const uniqueId = TestHelpers.generateUniqueId();
    console.log(`Test execution ID: ${uniqueId}`);
    
    // Then: Verify expected behavior
    const currentDate = TestHelpers.getCurrentDate();
    console.log(`Test executed on: ${currentDate}`);
    
    // Take screenshot for documentation
    await TestHelpers.takeTimestampedScreenshot(page, 'example-test');
    
    // Validate URL
    await TestHelpers.validateCurrentUrl(page, 'example.com');
  });

  test('should handle data-driven scenarios', async ({ page }) => {
    // Given: Test data is loaded for current environment
    const environment = process.env.NODE_ENV || 'development';
    const userData = testDataLoader.getTestDataByKey('example-data', 'users', environment);
    
    // When: Using test data in test execution
    console.log(`Testing with user: ${userData.validUser.username}`);
    
    // Then: Verify data is properly loaded
    expect(userData.validUser.username).toBeTruthy();
    expect(TestHelpers.isValidEmail(userData.validUser.username)).toBe(true);
  });

  test('should demonstrate retry mechanism', async ({ page }) => {
    // Given: A potentially flaky operation
    const operation = async () => {
      // Simulate operation that might fail
      const randomSuccess = Math.random() > 0.3;
      if (!randomSuccess) {
        throw new Error('Simulated failure');
      }
      return 'Success';
    };
    
    // When: Using retry helper
    const result = await TestHelpers.retryOperation(operation, 3, 500);
    
    // Then: Operation should eventually succeed
    expect(result).toBe('Success');
  });

  test.afterEach(async ({ page }) => {
    // Cleanup actions after each test
    await page.close();
  });
});

test.describe('Framework Validation Tests', () => {
  
  test('should validate helper functions', async () => {
    // Given: Helper utility functions
    const email = 'test@example.com';
    const invalidEmail = 'invalid-email';
    const amount = 1234.56;
    
    // When: Using helper functions
    const isValidEmail = TestHelpers.isValidEmail(email);
    const isInvalidEmail = TestHelpers.isValidEmail(invalidEmail);
    const formattedCurrency = TestHelpers.formatCurrency(amount);
    const randomString = TestHelpers.generateRandomString(10);
    
    // Then: Verify helper function results
    expect(isValidEmail).toBe(true);
    expect(isInvalidEmail).toBe(false);
    expect(formattedCurrency).toBe('$1,234.56');
    expect(randomString).toHaveLength(10);
  });

  test('should validate test data loading', async () => {
    // Given: Test data loader instance
    const loader = TestDataLoader.getInstance();
    
    // When: Loading test data
    const data = loader.loadTestData('example-data');
    
    // Then: Verify data structure
    expect(data.common).toBeDefined();
    expect(data.common.baseUrl).toBeTruthy();
    expect(data.development).toBeDefined();
    expect(data.staging).toBeDefined();
  });
});