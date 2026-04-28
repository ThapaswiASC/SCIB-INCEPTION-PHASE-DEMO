import { test, expect } from '@playwright/test';
import { TaskCreationActions } from '../actions/TaskCreationActions';
import { TestDataLoader } from '../utils/TestDataLoader';

/**
 * Test Suite: Task Creation Multiple Validations
 * Covers scenarios with multiple validation errors
 */
test.describe('Task Creation - Multiple Validations', () => {
  let taskActions: TaskCreationActions;
  let testDataLoader: TestDataLoader;

  test.beforeEach(async ({ page }) => {
    taskActions = new TaskCreationActions(page);
    testDataLoader = TestDataLoader.getInstance();
    await taskActions.navigateToTaskCreationForm();
  });

  test('DEMO-2658: Multiple validation errors with all fields missing', async ({ page }) => {
    // Given all mandatory fields are empty
    const testData = testDataLoader.getScenarioData('DEMO-2658');
    await taskActions.fillCompleteTaskForm(testData);

    // When the user clicks the Submit button
    await taskActions.submitForm();
    await taskActions.waitForValidation();

    // Then validation errors should be displayed for all mandatory fields
    const errorMessages = await taskActions.taskCreationPage.getVisibleErrorMessages();
    expect(errorMessages.length).toBeGreaterThanOrEqual(4); // At least 4 validation errors

    // And the title error should state "Title is required"
    await expect(taskActions.taskCreationPage.titleErrorMessage).toBeVisible();
    const titleError = await taskActions.taskCreationPage.titleErrorMessage.textContent();
    expect(titleError).toBe(testDataLoader.getValidationMessages().required.title);

    // And the description error should state "Description is required"
    await expect(taskActions.taskCreationPage.descriptionErrorMessage).toBeVisible();
    const descriptionError = await taskActions.taskCreationPage.descriptionErrorMessage.textContent();
    expect(descriptionError).toBe(testDataLoader.getValidationMessages().required.description);

    // And the priority error should state "Priority is required"
    await expect(taskActions.taskCreationPage.priorityErrorMessage).toBeVisible();
    const priorityError = await taskActions.taskCreationPage.priorityErrorMessage.textContent();
    expect(priorityError).toBe(testDataLoader.getValidationMessages().required.priority);

    // And the assignee error should state "Assignee is required"
    await expect(taskActions.taskCreationPage.assigneeErrorMessage).toBeVisible();
    const assigneeError = await taskActions.taskCreationPage.assigneeErrorMessage.textContent();
    expect(assigneeError).toBe(testDataLoader.getValidationMessages().required.assignee);

    // And the task should not be created
    await expect(taskActions.taskCreationPage.successMessage).not.toBeVisible();

    // And the user should remain on the creation form
    await expect(taskActions.taskCreationPage.formContainer).toBeVisible();
  });

  test('Multiple field validation with mixed valid and invalid data', async ({ page }) => {
    // Test scenario with some valid and some invalid fields
    await taskActions.fillCompleteTaskForm({
      title: '', // Invalid - empty
      description: 'Valid description content',
      priority: 'High', // Valid
      assignee: 'invalid-email' // Invalid - not proper email format
    });

    await taskActions.submitForm();
    await taskActions.waitForValidation();

    // Should show multiple validation errors
    const errorMessages = await taskActions.taskCreationPage.getVisibleErrorMessages();
    expect(errorMessages.length).toBeGreaterThanOrEqual(2);

    // Title should show required error
    await expect(taskActions.taskCreationPage.titleErrorMessage).toBeVisible();
    
    // Assignee should show validation error
    await expect(taskActions.taskCreationPage.assigneeErrorMessage).toBeVisible();

    // Task should not be created
    await expect(taskActions.taskCreationPage.successMessage).not.toBeVisible();
  });

  test('Validation error recovery - fixing errors one by one', async ({ page }) => {
    // Start with all fields empty
    await taskActions.clearAllFields();
    await taskActions.submitForm();
    await taskActions.waitForValidation();

    // Verify all validation errors are shown
    let errorCount = (await taskActions.taskCreationPage.getVisibleErrorMessages()).length;
    expect(errorCount).toBeGreaterThanOrEqual(4);

    // Fix title field
    await taskActions.fillTitle('Valid Title');
    await taskActions.submitForm();
    await taskActions.waitForValidation();

    // Title error should be gone, others should remain
    await expect(taskActions.taskCreationPage.titleErrorMessage).not.toBeVisible();
    await expect(taskActions.taskCreationPage.descriptionErrorMessage).toBeVisible();

    // Fix description field
    await taskActions.fillDescription('Valid description');
    await taskActions.submitForm();
    await taskActions.waitForValidation();

    // Description error should be gone
    await expect(taskActions.taskCreationPage.descriptionErrorMessage).not.toBeVisible();
    await expect(taskActions.taskCreationPage.priorityErrorMessage).toBeVisible();

    // Fix priority field
    await taskActions.selectPriority('High');
    await taskActions.submitForm();
    await taskActions.waitForValidation();

    // Priority error should be gone
    await expect(taskActions.taskCreationPage.priorityErrorMessage).not.toBeVisible();
    await expect(taskActions.taskCreationPage.assigneeErrorMessage).toBeVisible();

    // Fix assignee field
    await taskActions.fillAssignee('valid@scib.com');
    await taskActions.submitForm();

    // All errors should be gone and task should be created
    await expect(taskActions.taskCreationPage.successMessage).toBeVisible();
  });

  test('Form state preservation during validation failures', async ({ page }) => {
    const partialData = {
      title: 'Partial Task Title',
      description: 'This is a partial description',
      priority: 'Medium',
      assignee: '' // Missing assignee to trigger validation
    };

    await taskActions.fillCompleteTaskForm(partialData);
    await taskActions.submitForm();
    await taskActions.waitForValidation();

    // Verify validation error for assignee
    await expect(taskActions.taskCreationPage.assigneeErrorMessage).toBeVisible();

    // Verify other field values are preserved
    const formValues = await taskActions.getCurrentFormValues();
    expect(formValues.title).toBe(partialData.title);
    expect(formValues.description).toBe(partialData.description);
    expect(formValues.assignee).toBe(''); // Should remain empty
  });

  test('Simultaneous validation of multiple invalid formats', async ({ page }) => {
    await taskActions.fillCompleteTaskForm({
      title: 'A'.repeat(300), // Exceeds title length limit
      description: 'B'.repeat(2500), // Exceeds description limit
      priority: 'InvalidPriority', // Invalid priority
      assignee: 'not-an-email-format' // Invalid email format
    });

    await taskActions.submitForm();
    await taskActions.waitForValidation();

    // Should show multiple format validation errors
    const errorMessages = await taskActions.taskCreationPage.getVisibleErrorMessages();
    expect(errorMessages.length).toBeGreaterThan(0);

    // Task should not be created
    await expect(taskActions.taskCreationPage.successMessage).not.toBeVisible();

    // User should remain on form
    await expect(taskActions.taskCreationPage.formContainer).toBeVisible();
  });
});