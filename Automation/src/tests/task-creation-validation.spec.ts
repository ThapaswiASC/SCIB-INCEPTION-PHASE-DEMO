import { test, expect } from '@playwright/test';
import { TaskCreationActions } from '../actions/TaskCreationActions';
import { TestDataLoader } from '../utils/TestDataLoader';

test.describe('Task Creation - Validation Scenarios', () => {
  let taskActions: TaskCreationActions;
  let testDataLoader: TestDataLoader;

  test.beforeEach(async ({ page }) => {
    taskActions = new TaskCreationActions(page);
    testDataLoader = TestDataLoader.getInstance();
  });

  test('DEMO-2648: Task creation fails with empty title', async () => {
    // Given the user is on the task creation form
    await taskActions.navigateToTaskCreationForm();
    
    const validTaskData = testDataLoader.getValidTaskData();
    
    // And the title field is empty
    // And other fields have valid data
    await taskActions.fillTaskDescription(validTaskData.description);
    await taskActions.selectPriority(validTaskData.priority);
    await taskActions.fillAssignee(validTaskData.assignee);
    
    // When the user clicks the Submit button
    await taskActions.submitTaskForm();
    await taskActions.waitForFormSubmission();
    
    // Then a validation error should be displayed for the title field
    await expect(taskActions.taskCreationPage.titleErrorMessage).toBeVisible();
    await expect(taskActions.taskCreationPage.titleErrorMessage).toContainText(
      testDataLoader.getErrorMessage('titleRequired')
    );
    
    // And the task should not be created
    await expect(taskActions.taskCreationPage.successMessage).not.toBeVisible();
    
    // And the user should remain on the creation form
    await expect(taskActions.taskCreationPage.formContainer).toBeVisible();
    
    // And other field values should be preserved
    const currentValues = await taskActions.getCurrentFormValues();
    expect(currentValues.description).toBe(validTaskData.description);
    expect(currentValues.assignee).toBe(validTaskData.assignee);
  });

  test('DEMO-2650: Task creation fails with empty description', async () => {
    // Given the user is on the task creation form
    await taskActions.navigateToTaskCreationForm();
    
    const validTaskData = testDataLoader.getValidTaskData();
    
    // And the description field is empty
    await taskActions.fillTaskTitle(validTaskData.title);
    await taskActions.selectPriority(validTaskData.priority);
    await taskActions.fillAssignee(validTaskData.assignee);
    
    // When the user clicks the Submit button
    await taskActions.submitTaskForm();
    await taskActions.waitForFormSubmission();
    
    // Then a validation error should be displayed for the description field
    await expect(taskActions.taskCreationPage.descriptionErrorMessage).toBeVisible();
    await expect(taskActions.taskCreationPage.descriptionErrorMessage).toContainText(
      testDataLoader.getErrorMessage('descriptionRequired')
    );
    
    // And the task should not be created
    await expect(taskActions.taskCreationPage.successMessage).not.toBeVisible();
    
    // And the user should remain on the creation form
    await expect(taskActions.taskCreationPage.formContainer).toBeVisible();
  });

  test('DEMO-2652: Task creation fails with missing priority', async () => {
    // Given the user is on the task creation form
    await taskActions.navigateToTaskCreationForm();
    
    const validTaskData = testDataLoader.getValidTaskData();
    
    // And no priority is selected
    await taskActions.fillTaskTitle(validTaskData.title);
    await taskActions.fillTaskDescription(validTaskData.description);
    await taskActions.fillAssignee(validTaskData.assignee);
    
    // When the user clicks the Submit button
    await taskActions.submitTaskForm();
    await taskActions.waitForFormSubmission();
    
    // Then a validation error should be displayed for the priority field
    await expect(taskActions.taskCreationPage.priorityErrorMessage).toBeVisible();
    await expect(taskActions.taskCreationPage.priorityErrorMessage).toContainText(
      testDataLoader.getErrorMessage('priorityRequired')
    );
    
    // And the task should not be created
    await expect(taskActions.taskCreationPage.successMessage).not.toBeVisible();
    
    // And the user should remain on the creation form
    await expect(taskActions.taskCreationPage.formContainer).toBeVisible();
  });

  test('DEMO-2654: Task creation fails with empty assignee', async () => {
    // Given the user is on the task creation form
    await taskActions.navigateToTaskCreationForm();
    
    const validTaskData = testDataLoader.getValidTaskData();
    
    // And the assignee field is empty
    await taskActions.fillTaskTitle(validTaskData.title);
    await taskActions.fillTaskDescription(validTaskData.description);
    await taskActions.selectPriority(validTaskData.priority);
    
    // When the user clicks the Submit button
    await taskActions.submitTaskForm();
    await taskActions.waitForFormSubmission();
    
    // Then a validation error should be displayed for the assignee field
    await expect(taskActions.taskCreationPage.assigneeErrorMessage).toBeVisible();
    await expect(taskActions.taskCreationPage.assigneeErrorMessage).toContainText(
      testDataLoader.getErrorMessage('assigneeRequired')
    );
    
    // And the task should not be created
    await expect(taskActions.taskCreationPage.successMessage).not.toBeVisible();
    
    // And the user should remain on the creation form
    await expect(taskActions.taskCreationPage.formContainer).toBeVisible();
  });

  test('DEMO-2656: Task creation fails with non-SCIB domain assignee', async () => {
    // Given the user is on the task creation form
    await taskActions.navigateToTaskCreationForm();
    
    const invalidAssigneeTaskData = testDataLoader.getInvalidAssigneeTaskData();
    
    // And the assignee is set to external domain
    await taskActions.fillTaskTitle(invalidAssigneeTaskData.title);
    await taskActions.fillTaskDescription(invalidAssigneeTaskData.description);
    await taskActions.selectPriority(invalidAssigneeTaskData.priority);
    await taskActions.fillAssignee(invalidAssigneeTaskData.assignee);
    
    // When the user clicks the Submit button
    await taskActions.submitTaskForm();
    await taskActions.waitForFormSubmission();
    
    // Then a validation error should be displayed for the assignee field
    await expect(taskActions.taskCreationPage.assigneeErrorMessage).toBeVisible();
    await expect(taskActions.taskCreationPage.assigneeErrorMessage).toContainText(
      testDataLoader.getErrorMessage('invalidAssignee')
    );
    
    // And the task should not be created
    await expect(taskActions.taskCreationPage.successMessage).not.toBeVisible();
    
    // And the user should remain on the creation form
    await expect(taskActions.taskCreationPage.formContainer).toBeVisible();
  });
});