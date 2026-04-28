import { test, expect } from '@playwright/test';
import { TaskCreationActions } from '../actions/TaskCreationActions';
import { TestDataLoader } from '../utils/TestDataLoader';

/**
 * Test Suite: Task Creation Validation Failures
 * Covers negative test cases for missing mandatory fields
 */
test.describe('Task Creation - Validation Failures', () => {
  let taskActions: TaskCreationActions;
  let testDataLoader: TestDataLoader;

  test.beforeEach(async ({ page }) => {
    taskActions = new TaskCreationActions(page);
    testDataLoader = TestDataLoader.getInstance();
    await taskActions.navigateToTaskCreationForm();
  });

  test('DEMO-2648: Task creation fails with empty title', async ({ page }) => {
    // Given the title field is empty and other fields have valid data
    const testData = testDataLoader.getScenarioData('DEMO-2648');
    await taskActions.fillCompleteTaskForm(testData);

    // When the user clicks the Submit button
    await taskActions.submitForm();
    await taskActions.waitForValidation();

    // Then a validation error should be displayed for the title field
    await expect(taskActions.taskCreationPage.titleErrorMessage).toBeVisible();
    
    // And the error message should state "Title is required"
    const titleError = await taskActions.taskCreationPage.titleErrorMessage.textContent();
    expect(titleError).toBe(testDataLoader.getValidationMessages().required.title);

    // And the task should not be created
    await expect(taskActions.taskCreationPage.successMessage).not.toBeVisible();

    // And the user should remain on the creation form
    await expect(taskActions.taskCreationPage.formContainer).toBeVisible();
    
    // Verify other field values are preserved
    const formValues = await taskActions.getCurrentFormValues();
    expect(formValues.description).toBe(testData.description);
    expect(formValues.assignee).toBe(testData.assignee);
  });

  test('DEMO-2650: Task creation fails with empty description', async ({ page }) => {
    // Given the description field is empty and other fields have valid data
    const testData = testDataLoader.getScenarioData('DEMO-2650');
    await taskActions.fillCompleteTaskForm(testData);

    // When the user clicks the Submit button
    await taskActions.submitForm();
    await taskActions.waitForValidation();

    // Then a validation error should be displayed for the description field
    await expect(taskActions.taskCreationPage.descriptionErrorMessage).toBeVisible();
    
    // And the error message should state "Description is required"
    const descriptionError = await taskActions.taskCreationPage.descriptionErrorMessage.textContent();
    expect(descriptionError).toBe(testDataLoader.getValidationMessages().required.description);

    // And the task should not be created
    await expect(taskActions.taskCreationPage.successMessage).not.toBeVisible();

    // And the user should remain on the creation form
    await expect(taskActions.taskCreationPage.formContainer).toBeVisible();
    
    // Verify other field values are preserved
    const formValues = await taskActions.getCurrentFormValues();
    expect(formValues.title).toBe(testData.title);
    expect(formValues.assignee).toBe(testData.assignee);
  });

  test('DEMO-2652: Task creation fails with missing priority', async ({ page }) => {
    // Given no priority is selected and other fields have valid data
    const testData = testDataLoader.getScenarioData('DEMO-2652');
    await taskActions.fillCompleteTaskForm(testData);

    // When the user clicks the Submit button
    await taskActions.submitForm();
    await taskActions.waitForValidation();

    // Then a validation error should be displayed for the priority field
    await expect(taskActions.taskCreationPage.priorityErrorMessage).toBeVisible();
    
    // And the error message should state "Priority is required"
    const priorityError = await taskActions.taskCreationPage.priorityErrorMessage.textContent();
    expect(priorityError).toBe(testDataLoader.getValidationMessages().required.priority);

    // And the task should not be created
    await expect(taskActions.taskCreationPage.successMessage).not.toBeVisible();

    // And the user should remain on the creation form
    await expect(taskActions.taskCreationPage.formContainer).toBeVisible();
  });

  test('DEMO-2654: Task creation fails with empty assignee', async ({ page }) => {
    // Given the assignee field is empty and other fields have valid data
    const testData = testDataLoader.getScenarioData('DEMO-2654');
    await taskActions.fillCompleteTaskForm(testData);

    // When the user clicks the Submit button
    await taskActions.submitForm();
    await taskActions.waitForValidation();

    // Then a validation error should be displayed for the assignee field
    await expect(taskActions.taskCreationPage.assigneeErrorMessage).toBeVisible();
    
    // And the error message should state "Assignee is required"
    const assigneeError = await taskActions.taskCreationPage.assigneeErrorMessage.textContent();
    expect(assigneeError).toBe(testDataLoader.getValidationMessages().required.assignee);

    // And the task should not be created
    await expect(taskActions.taskCreationPage.successMessage).not.toBeVisible();

    // And the user should remain on the creation form
    await expect(taskActions.taskCreationPage.formContainer).toBeVisible();
  });

  test('DEMO-2656: Task creation fails with non-SCIB domain assignee', async ({ page }) => {
    // Given the assignee is set to external email and other fields have valid data
    const testData = testDataLoader.getScenarioData('DEMO-2656');
    await taskActions.fillCompleteTaskForm(testData);

    // When the user clicks the Submit button
    await taskActions.submitForm();
    await taskActions.waitForValidation();

    // Then a validation error should be displayed for the assignee field
    await expect(taskActions.taskCreationPage.assigneeErrorMessage).toBeVisible();
    
    // And the error message should state "Assignee must be from SCIB Active Directory"
    const assigneeError = await taskActions.taskCreationPage.assigneeErrorMessage.textContent();
    expect(assigneeError).toBe(testDataLoader.getValidationMessages().validation.invalidAssignee);

    // And the task should not be created
    await expect(taskActions.taskCreationPage.successMessage).not.toBeVisible();

    // And the user should remain on the creation form
    await expect(taskActions.taskCreationPage.formContainer).toBeVisible();
  });

  test('DEMO-2664: Task creation fails with invalid priority value', async ({ page }) => {
    // Given an attempt is made to select invalid priority and other fields have valid data
    const testData = testDataLoader.getScenarioData('DEMO-2664');
    await taskActions.fillTitle(testData.title);
    await taskActions.fillDescription(testData.description);
    await taskActions.fillAssignee(testData.assignee);
    
    // Try to select invalid priority
    await taskActions.selectPriority(testData.priority);

    // When the user clicks the Submit button
    await taskActions.submitForm();
    await taskActions.waitForValidation();

    // Then the system should enforce valid priority values
    // Either prevent selection of invalid priority or display validation error
    const hasValidationError = await taskActions.hasValidationErrors();
    expect(hasValidationError).toBeTruthy();

    // And the task should not be created with invalid priority
    await expect(taskActions.taskCreationPage.successMessage).not.toBeVisible();

    // And the user should be guided to select a valid priority value
    await expect(taskActions.taskCreationPage.formContainer).toBeVisible();
  });
});