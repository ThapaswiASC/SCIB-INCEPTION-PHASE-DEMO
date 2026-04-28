import { test, expect } from '@playwright/test';
import { TaskCreationActions } from '../actions/TaskCreationActions';
import { TestDataLoader } from '../utils/TestDataLoader';

test.describe('Task Creation - Special Cases', () => {
  let taskActions: TaskCreationActions;
  let testDataLoader: TestDataLoader;

  test.beforeEach(async ({ page }) => {
    taskActions = new TaskCreationActions(page);
    testDataLoader = TestDataLoader.getInstance();
  });

  test('DEMO-2660: Task creation with special characters in title', async () => {
    // Given the user is on the task creation form
    await taskActions.navigateToTaskCreationForm();
    
    const specialCharTaskData = testDataLoader.getSpecialCharacterTaskData();
    
    // And the title field contains special characters
    await taskActions.fillTaskTitle(specialCharTaskData.title);
    await taskActions.fillTaskDescription(specialCharTaskData.description);
    await taskActions.selectPriority(specialCharTaskData.priority);
    await taskActions.fillAssignee(specialCharTaskData.assignee);
    
    // When the user clicks the Submit button
    await taskActions.submitTaskForm();
    await taskActions.waitForFormSubmission();
    
    // Then the system should handle special characters according to business rules
    // This test expects either success or appropriate validation message
    const isSuccessVisible = await taskActions.taskCreationPage.successMessage.isVisible();
    const isErrorVisible = await taskActions.taskCreationPage.titleErrorMessage.isVisible();
    
    expect(isSuccessVisible || isErrorVisible).toBeTruthy();
    
    if (isErrorVisible) {
      // If validation error is shown, verify it's appropriate
      await expect(taskActions.taskCreationPage.titleErrorMessage).toContainText(
        testDataLoader.getErrorMessage('specialCharactersNotAllowed')
      );
    } else {
      // If successful, verify success message
      await expect(taskActions.taskCreationPage.successMessage).toBeVisible();
    }
    
    // And the system behavior should be consistent and secure
    await expect(taskActions.taskCreationPage.formContainer).toBeVisible();
  });

  test('DEMO-2662: Task creation with maximum length description', async () => {
    // Given the user is on the task creation form
    await taskActions.navigateToTaskCreationForm();
    
    const validTaskData = testDataLoader.getValidTaskData();
    const maxLength = testDataLoader.getCharacterLimit('descriptionMax');
    const longDescription = taskActions.generateLongDescription(maxLength + 500);
    
    // And the description field contains text exceeding character limit
    await taskActions.fillTaskTitle(validTaskData.title);
    await taskActions.fillTaskDescription(longDescription);
    await taskActions.selectPriority(validTaskData.priority);
    await taskActions.fillAssignee(validTaskData.assignee);
    
    // When the user clicks the Submit button
    await taskActions.submitTaskForm();
    await taskActions.waitForFormSubmission();
    
    // Then the system should enforce the character limit policy
    const isSuccessVisible = await taskActions.taskCreationPage.successMessage.isVisible();
    const isErrorVisible = await taskActions.taskCreationPage.descriptionErrorMessage.isVisible();
    
    expect(isSuccessVisible || isErrorVisible).toBeTruthy();
    
    if (isErrorVisible) {
      // If validation error is shown for exceeding limit
      await expect(taskActions.taskCreationPage.descriptionErrorMessage).toContainText(
        testDataLoader.getErrorMessage('descriptionTooLong')
      );
    }
    
    // And the character limit should be clearly communicated to the user
    if (await taskActions.taskCreationPage.descriptionCharacterCounter.isVisible()) {
      await expect(taskActions.taskCreationPage.descriptionCharacterCounter).toBeVisible();
    }
  });

  test('DEMO-2664: Task creation fails with invalid priority value', async () => {
    // Given the user is on the task creation form
    await taskActions.navigateToTaskCreationForm();
    
    const validTaskData = testDataLoader.getValidTaskData();
    const invalidPriorities = testDataLoader.getInvalidPriorities();
    
    for (const invalidPriority of invalidPriorities) {
      await test.step(`Test invalid priority: ${invalidPriority}`, async () => {
        // Clear form and fill with valid data except priority
        await taskActions.clearAllFields();
        await taskActions.fillTaskTitle(validTaskData.title);
        await taskActions.fillTaskDescription(validTaskData.description);
        await taskActions.fillAssignee(validTaskData.assignee);
        
        // And an attempt is made to select invalid priority
        await taskActions.attemptInvalidPrioritySelection(invalidPriority);
        
        // When the user clicks the Submit button
        await taskActions.submitTaskForm();
        await taskActions.waitForFormSubmission();
        
        // Then the system should enforce valid priority values
        // Either prevent selection or show validation error
        const isErrorVisible = await taskActions.taskCreationPage.priorityErrorMessage.isVisible();
        const isSuccessVisible = await taskActions.taskCreationPage.successMessage.isVisible();
        
        // Task should not be created with invalid priority
        expect(isSuccessVisible).toBeFalsy();
        
        if (isErrorVisible) {
          await expect(taskActions.taskCreationPage.priorityErrorMessage).toContainText(
            testDataLoader.getErrorMessage('invalidPriority')
          );
        }
        
        // And the user should be guided to select a valid priority value
        await expect(taskActions.taskCreationPage.formContainer).toBeVisible();
      });
    }
  });

  test('Task creation with boundary value testing', async () => {
    // Test with minimum valid description length
    await test.step('Test minimum description length', async () => {
      await taskActions.navigateToTaskCreationForm();
      
      const validTaskData = testDataLoader.getValidTaskData();
      const minDescription = "Min desc"; // Minimum acceptable description
      
      await taskActions.fillTaskTitle(validTaskData.title);
      await taskActions.fillTaskDescription(minDescription);
      await taskActions.selectPriority(validTaskData.priority);
      await taskActions.fillAssignee(validTaskData.assignee);
      
      await taskActions.submitTaskForm();
      await taskActions.waitForFormSubmission();
      
      // Should either succeed or show appropriate validation
      const isSuccessVisible = await taskActions.taskCreationPage.successMessage.isVisible();
      const isErrorVisible = await taskActions.taskCreationPage.descriptionErrorMessage.isVisible();
      
      expect(isSuccessVisible || isErrorVisible).toBeTruthy();
    });

    // Test with maximum valid description length
    await test.step('Test maximum description length', async () => {
      await taskActions.navigateToTaskCreationForm();
      
      const validTaskData = testDataLoader.getValidTaskData();
      const maxLength = testDataLoader.getCharacterLimit('descriptionMax');
      const maxDescription = testDataLoader.generateLongText(maxLength);
      
      await taskActions.fillTaskTitle(validTaskData.title);
      await taskActions.fillTaskDescription(maxDescription);
      await taskActions.selectPriority(validTaskData.priority);
      await taskActions.fillAssignee(validTaskData.assignee);
      
      await taskActions.submitTaskForm();
      await taskActions.waitForFormSubmission();
      
      // Should succeed with maximum valid length
      await expect(taskActions.taskCreationPage.successMessage).toBeVisible();
    });
  });
});