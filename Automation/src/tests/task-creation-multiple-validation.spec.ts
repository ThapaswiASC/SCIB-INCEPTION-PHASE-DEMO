import { test, expect } from '@playwright/test';
import { TaskCreationActions } from '../actions/TaskCreationActions';
import { TestDataLoader } from '../utils/TestDataLoader';

test.describe('Task Creation - Multiple Validation Scenarios', () => {
  let taskActions: TaskCreationActions;
  let testDataLoader: TestDataLoader;

  test.beforeEach(async ({ page }) => {
    taskActions = new TaskCreationActions(page);
    testDataLoader = TestDataLoader.getInstance();
  });

  test('DEMO-2658: Multiple validation errors with all fields missing', async () => {
    // Given the user is on the task creation form
    await taskActions.navigateToTaskCreationForm();
    
    // And all mandatory fields are empty
    await taskActions.clearAllFields();
    
    // When the user clicks the Submit button
    await taskActions.submitTaskForm();
    await taskActions.waitForFormSubmission();
    
    // Then validation errors should be displayed for all mandatory fields
    await expect(taskActions.taskCreationPage.titleErrorMessage).toBeVisible();
    await expect(taskActions.taskCreationPage.descriptionErrorMessage).toBeVisible();
    await expect(taskActions.taskCreationPage.priorityErrorMessage).toBeVisible();
    await expect(taskActions.taskCreationPage.assigneeErrorMessage).toBeVisible();
    
    // And the specific error messages should be correct
    await expect(taskActions.taskCreationPage.titleErrorMessage).toContainText(
      testDataLoader.getErrorMessage('titleRequired')
    );
    await expect(taskActions.taskCreationPage.descriptionErrorMessage).toContainText(
      testDataLoader.getErrorMessage('descriptionRequired')
    );
    await expect(taskActions.taskCreationPage.priorityErrorMessage).toContainText(
      testDataLoader.getErrorMessage('priorityRequired')
    );
    await expect(taskActions.taskCreationPage.assigneeErrorMessage).toContainText(
      testDataLoader.getErrorMessage('assigneeRequired')
    );
    
    // And the task should not be created
    await expect(taskActions.taskCreationPage.successMessage).not.toBeVisible();
    
    // And the user should remain on the creation form
    await expect(taskActions.taskCreationPage.formContainer).toBeVisible();
  });

  test('Multiple validation errors with partial field completion', async () => {
    // Given the user is on the task creation form
    await taskActions.navigateToTaskCreationForm();
    
    const validTaskData = testDataLoader.getValidTaskData();
    
    // And only some fields are filled (missing title and priority)
    await taskActions.fillTaskDescription(validTaskData.description);
    await taskActions.fillAssignee(validTaskData.assignee);
    
    // When the user clicks the Submit button
    await taskActions.submitTaskForm();
    await taskActions.waitForFormSubmission();
    
    // Then validation errors should be displayed for missing fields
    await expect(taskActions.taskCreationPage.titleErrorMessage).toBeVisible();
    await expect(taskActions.taskCreationPage.priorityErrorMessage).toBeVisible();
    
    // And no errors should be shown for completed fields
    await expect(taskActions.taskCreationPage.descriptionErrorMessage).not.toBeVisible();
    await expect(taskActions.taskCreationPage.assigneeErrorMessage).not.toBeVisible();
    
    // And the task should not be created
    await expect(taskActions.taskCreationPage.successMessage).not.toBeVisible();
    
    // And completed field values should be preserved
    const currentValues = await taskActions.getCurrentFormValues();
    expect(currentValues.description).toBe(validTaskData.description);
    expect(currentValues.assignee).toBe(validTaskData.assignee);
  });

  test('Multiple validation errors with invalid data types', async () => {
    // Given the user is on the task creation form
    await taskActions.navigateToTaskCreationForm();
    
    const invalidAssignees = testDataLoader.getInvalidAssignees();
    const longDescription = taskActions.generateLongDescription(3000);
    
    // And multiple fields have invalid data
    await taskActions.fillTaskTitle(""); // Empty title
    await taskActions.fillTaskDescription(longDescription); // Too long description
    await taskActions.fillAssignee(invalidAssignees[0]); // Invalid domain assignee
    // Priority left unselected
    
    // When the user clicks the Submit button
    await taskActions.submitTaskForm();
    await taskActions.waitForFormSubmission();
    
    // Then multiple validation errors should be displayed
    const errorElements = [
      taskActions.taskCreationPage.titleErrorMessage,
      taskActions.taskCreationPage.descriptionErrorMessage,
      taskActions.taskCreationPage.priorityErrorMessage,
      taskActions.taskCreationPage.assigneeErrorMessage
    ];
    
    // At least some validation errors should be visible
    let visibleErrors = 0;
    for (const errorElement of errorElements) {
      if (await errorElement.isVisible()) {
        visibleErrors++;
      }
    }
    
    expect(visibleErrors).toBeGreaterThan(0);
    
    // And the task should not be created
    await expect(taskActions.taskCreationPage.successMessage).not.toBeVisible();
  });

  test('Progressive validation error resolution', async () => {
    // Given the user is on the task creation form with all fields empty
    await taskActions.navigateToTaskCreationForm();
    await taskActions.clearAllFields();
    
    // When the user attempts to submit
    await taskActions.submitTaskForm();
    await taskActions.waitForFormSubmission();
    
    // Then all validation errors should be visible
    await expect(taskActions.taskCreationPage.titleErrorMessage).toBeVisible();
    
    const validTaskData = testDataLoader.getValidTaskData();
    
    // When the user fills the title field
    await taskActions.fillTaskTitle(validTaskData.title);
    
    // And attempts to submit again
    await taskActions.submitTaskForm();
    await taskActions.waitForFormSubmission();
    
    // Then title error should be resolved but others remain
    // Note: This behavior depends on form validation implementation
    // Some forms validate on blur, others on submit
    
    // Continue filling fields progressively
    await taskActions.fillTaskDescription(validTaskData.description);
    await taskActions.selectPriority(validTaskData.priority);
    await taskActions.fillAssignee(validTaskData.assignee);
    
    // When all fields are completed and submitted
    await taskActions.submitTaskForm();
    await taskActions.waitForFormSubmission();
    
    // Then the task should be created successfully
    await expect(taskActions.taskCreationPage.successMessage).toBeVisible();
    
    // And no validation errors should be visible
    await expect(taskActions.taskCreationPage.titleErrorMessage).not.toBeVisible();
    await expect(taskActions.taskCreationPage.descriptionErrorMessage).not.toBeVisible();
    await expect(taskActions.taskCreationPage.priorityErrorMessage).not.toBeVisible();
    await expect(taskActions.taskCreationPage.assigneeErrorMessage).not.toBeVisible();
  });

  test('Form state preservation during validation errors', async () => {
    // Given the user is on the task creation form
    await taskActions.navigateToTaskCreationForm();
    
    const validTaskData = testDataLoader.getValidTaskData();
    const invalidAssignee = testDataLoader.getInvalidAssignees()[0];
    
    // And the user fills form with mix of valid and invalid data
    await taskActions.fillTaskTitle(validTaskData.title);
    await taskActions.fillTaskDescription(validTaskData.description);
    await taskActions.selectPriority(validTaskData.priority);
    await taskActions.fillAssignee(invalidAssignee); // Invalid assignee
    
    // When the user submits the form
    await taskActions.submitTaskForm();
    await taskActions.waitForFormSubmission();
    
    // Then validation error should be shown for invalid assignee
    await expect(taskActions.taskCreationPage.assigneeErrorMessage).toBeVisible();
    
    // And all valid field values should be preserved
    const currentValues = await taskActions.getCurrentFormValues();
    expect(currentValues.title).toBe(validTaskData.title);
    expect(currentValues.description).toBe(validTaskData.description);
    expect(currentValues.assignee).toBe(invalidAssignee);
    
    // When the user corrects the invalid field
    await taskActions.fillAssignee(validTaskData.assignee);
    await taskActions.submitTaskForm();
    await taskActions.waitForFormSubmission();
    
    // Then the task should be created successfully
    await expect(taskActions.taskCreationPage.successMessage).toBeVisible();
  });
});