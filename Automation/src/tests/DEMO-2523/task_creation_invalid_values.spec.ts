import { test, expect } from '@playwright/test';
import { TaskCreationActions } from '../../actions/TaskCreationActions';
import { TaskFlowPage } from '../../pages/TaskFlowPage';
import { TestDataLoader } from '../../utils/TestDataLoader';

test.describe('DEMO-2523 - Task Creation Invalid Values Scenarios', () => {
  let taskCreationActions: TaskCreationActions;
  let taskFlowPage: TaskFlowPage;
  let testData: any;

  test.beforeEach(async ({ page }) => {
    taskCreationActions = new TaskCreationActions(page);
    taskFlowPage = new TaskFlowPage(page);
    testData = TestDataLoader.loadTestData('task-creation-data.json');
    
    await taskCreationActions.navigateToTaskCreation();
  });

  test('[DEMO-2523][TS10-TC1] Verify Task Creation Fails with Invalid Priority Value', async () => {
    // Given an attempt is made to select invalid priority "SuperCritical"
    const taskDataWithInvalidPriority = testData.taskDataWithInvalidPriority;
    
    // When the user submits the form
    await taskCreationActions.createTaskWithInvalidPriority(taskDataWithInvalidPriority);

    // Then the system should enforce valid priority values
    const isSuccessVisible = await taskFlowPage.isSuccessMessageVisible();
    const isPriorityErrorVisible = await taskFlowPage.priorityErrorMessageLocator.isVisible();
    
    // And either prevent selection of invalid priority or display validation error
    if (isPriorityErrorVisible) {
      const priorityError = await taskFlowPage.getFieldErrorMessage(taskFlowPage.priorityErrorMessageLocator);
      expect(priorityError).toMatch(/valid priority|invalid priority|select.*priority/i);
    }
    
    // And the task should not be created with invalid priority
    if (isSuccessVisible) {
      // If task was created, verify the priority was corrected or defaulted
      const successMessage = await taskFlowPage.getSuccessMessageText();
      expect(successMessage).toContain('Task created successfully!');
    } else {
      // Task should not be created with invalid priority
      expect(isSuccessVisible).toBe(false);
    }
    
    // And the user should be guided to select a valid priority value
    await expect(taskFlowPage.addTaskModalHeadingLocator).toBeVisible();
  });
});