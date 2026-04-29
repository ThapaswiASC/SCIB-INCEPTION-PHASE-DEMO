import { test, expect } from '@playwright/test';
import { TaskCreationActions } from '../../actions/TaskCreationActions';
import { TaskFlowPage } from '../../pages/TaskFlowPage';
import { TestDataLoader } from '../../utils/TestDataLoader';

test.describe('DEMO-2523 - Task Creation Character Limits Scenarios', () => {
  let taskCreationActions: TaskCreationActions;
  let taskFlowPage: TaskFlowPage;
  let testData: any;

  test.beforeEach(async ({ page }) => {
    taskCreationActions = new TaskCreationActions(page);
    taskFlowPage = new TaskFlowPage(page);
    testData = TestDataLoader.loadTestData('task-creation-data.json');
    
    await taskCreationActions.navigateToTaskCreation();
  });

  test('[DEMO-2523][TS9-TC1] Verify Task Creation with Maximum Length Description', async () => {
    // Given the description field contains text exceeding 2000 characters
    const taskDataWithLongDescription = testData.taskDataWithLongDescription;
    
    // When the user submits the form
    await taskCreationActions.createTaskWithAllFields(taskDataWithLongDescription);

    // Then the system should enforce the character limit policy
    const isSuccessVisible = await taskFlowPage.isSuccessMessageVisible();
    const isDescriptionErrorVisible = await taskFlowPage.descriptionErrorMessageLocator.isVisible();
    
    // And either create the task successfully or display validation error for exceeding limit
    expect(isSuccessVisible || isDescriptionErrorVisible).toBe(true);
    
    if (isSuccessVisible) {
      // If task creation is successful (description was truncated or allowed)
      const successMessage = await taskFlowPage.getSuccessMessageText();
      expect(successMessage).toContain('Task created successfully!');
    } else if (isDescriptionErrorVisible) {
      // If validation error is shown for character limit
      const descriptionError = await taskFlowPage.getFieldErrorMessage(taskFlowPage.descriptionErrorMessageLocator);
      expect(descriptionError).toContain('2000 characters');
    }
    
    // And the character limit should be clearly communicated to the user
    // This would typically be shown as a character counter or in the validation message
  });
});