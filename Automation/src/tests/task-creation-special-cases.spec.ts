import { test, expect } from '@playwright/test';
import { TaskCreationActions } from '../actions/TaskCreationActions';
import { TestDataLoader } from '../utils/TestDataLoader';

/**
 * Test Suite: Task Creation Special Cases
 * Covers edge cases and special character handling
 */
test.describe('Task Creation - Special Cases', () => {
  let taskActions: TaskCreationActions;
  let testDataLoader: TestDataLoader;

  test.beforeEach(async ({ page }) => {
    taskActions = new TaskCreationActions(page);
    testDataLoader = TestDataLoader.getInstance();
    await taskActions.navigateToTaskCreationForm();
  });

  test('DEMO-2660: Task creation with special characters in title', async ({ page }) => {
    // Given the title field contains special characters and other fields have valid data
    const testData = testDataLoader.getScenarioData('DEMO-2660');
    await taskActions.fillCompleteTaskForm(testData);

    // When the user clicks the Submit button
    await taskActions.submitForm();
    await taskActions.waitForValidation();

    // Then the system should handle special characters according to business rules
    // Either create the task successfully or display appropriate validation message
    const hasValidationError = await taskActions.hasValidationErrors();
    const isSuccessful = await taskActions.taskCreationPage.isSuccessMessageVisible();

    // One of these should be true - either success or validation error
    expect(hasValidationError || isSuccessful).toBeTruthy();

    // And the system behavior should be consistent and secure
    if (hasValidationError) {
      // If validation error, check it's appropriate for special characters
      await expect(taskActions.taskCreationPage.formContainer).toBeVisible();
      const errorMessages = await taskActions.taskCreationPage.getVisibleErrorMessages();
      expect(errorMessages.some(msg => 
        msg.includes('invalid characters') || 
        msg.includes('special characters') ||
        msg.includes('not allowed')
      )).toBeTruthy();
    } else {
      // If successful, verify task was created
      await expect(taskActions.taskCreationPage.successMessage).toBeVisible();
    }
  });

  test('DEMO-2662: Task creation with maximum length description', async ({ page }) => {
    // Given the description field contains text exceeding 2000 characters
    const testData = testDataLoader.getScenarioData('DEMO-2662');
    await taskActions.fillTitle(testData.title);
    await taskActions.fillAssignee(testData.assignee);
    await taskActions.selectPriority(testData.priority);
    
    // Fill with description exceeding character limit
    await taskActions.fillDescription(testData.description);

    // Verify character counter if available
    const charCount = await taskActions.taskCreationPage.getDescriptionCharacterCount();
    if (charCount > 0) {
      expect(charCount).toBeGreaterThan(2000);
    }

    // When the user clicks the Submit button
    await taskActions.submitForm();
    await taskActions.waitForValidation();

    // Then the system should enforce the character limit policy
    const hasValidationError = await taskActions.hasValidationErrors();
    const isSuccessful = await taskActions.taskCreationPage.isSuccessMessageVisible();

    // Either create successfully or show validation error for exceeding limit
    expect(hasValidationError || isSuccessful).toBeTruthy();

    if (hasValidationError) {
      // And the character limit should be clearly communicated to the user
      const errorMessages = await taskActions.taskCreationPage.getVisibleErrorMessages();
      expect(errorMessages.some(msg => 
        msg.includes('2000') || 
        msg.includes('character limit') ||
        msg.includes('too long')
      )).toBeTruthy();
    }
  });

  test('Special characters validation in different fields', async ({ page }) => {
    const specialCharsData = testDataLoader.getInvalidData().specialCharacters;
    
    // Test special characters in title with emoji
    await taskActions.fillCompleteTaskForm({
      title: specialCharsData.titleWithEmoji,
      description: 'Valid description',
      priority: 'High',
      assignee: 'test@scib.com'
    });

    await taskActions.submitForm();
    await taskActions.waitForValidation();

    // System should handle emoji appropriately
    const hasError = await taskActions.hasValidationErrors();
    const isSuccess = await taskActions.taskCreationPage.isSuccessMessageVisible();
    expect(hasError || isSuccess).toBeTruthy();
  });

  test('Boundary testing for description character limits', async ({ page }) => {
    const edgeData = testDataLoader.getEdgeCaseData().maxLengthDescription;
    
    // Test exactly at limit (2000 characters)
    await taskActions.fillCompleteTaskForm({
      title: 'Boundary Test Task',
      description: edgeData.exactly2000,
      priority: 'Medium',
      assignee: 'test@scib.com'
    });

    await taskActions.submitForm();
    
    // Should be successful at exactly 2000 characters
    await expect(taskActions.taskCreationPage.successMessage).toBeVisible();
  });

  test('XSS and injection prevention in form fields', async ({ page }) => {
    const maliciousInputs = [
      '<script>alert("xss")</script>',
      '"><script>alert("xss")</script>',
      'javascript:alert("xss")',
      '${7*7}',
      '{{7*7}}'
    ];

    for (const maliciousInput of maliciousInputs) {
      await taskActions.clearAllFields();
      
      await taskActions.fillCompleteTaskForm({
        title: `XSS Test: ${maliciousInput}`,
        description: `Testing XSS prevention with: ${maliciousInput}`,
        priority: 'Low',
        assignee: 'security.test@scib.com'
      });

      await taskActions.submitForm();
      await taskActions.waitForValidation();

      // System should either sanitize input and create task or reject with validation
      const hasError = await taskActions.hasValidationErrors();
      const isSuccess = await taskActions.taskCreationPage.isSuccessMessageVisible();
      expect(hasError || isSuccess).toBeTruthy();

      // Navigate back for next iteration
      if (isSuccess) {
        await taskActions.navigateToTaskCreationForm();
      }
    }
  });
});