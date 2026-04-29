import { test, expect } from '@playwright/test';
import { TaskCreationActions } from '../../actions/TaskCreationActions';
import { TaskFlowPage } from '../../pages/TaskFlowPage';
import { TestDataLoader } from '../../utils/TestDataLoader';

test.describe('DEMO-2523 - Task Creation Special Characters Scenarios', () => {
  let taskCreationActions: TaskCreationActions;
  let taskFlowPage: TaskFlowPage;
  let testData: any;

  test.beforeEach(async ({ page }) => {
    taskCreationActions = new TaskCreationActions(page);
    taskFlowPage = new TaskFlowPage(page);
    testData = TestDataLoader.loadTestData('task-creation-data.json');
    
    await taskCreationActions.navigateToTaskCreation();
  });

  test('[DEMO-2523][TS8-TC1] Verify Task Creation with Special Characters in Title', async () => {
    // Given the title field contains special characters
    const taskDataWithSpecialChars = testData.taskDataWithSpecialCharacters;
    
    // When the user submits the form
    await taskCreationActions.createTaskWithAllFields(taskDataWithSpecialChars);

    // Then the system should handle special characters according to business rules
    // And either create the task successfully or display appropriate validation message
    const isSuccessVisible = await taskFlowPage.isSuccessMessageVisible();
    const isTitleErrorVisible = await taskFlowPage.titleErrorMessageLocator.isVisible();
    
    // The system behavior should be consistent and secure
    expect(isSuccessVisible || isTitleErrorVisible).toBe(true);
    
    if (isSuccessVisible) {
      // If task creation is successful
      const successMessage = await taskFlowPage.getSuccessMessageText();
      expect(successMessage).toContain('Task created successfully!');
    } else if (isTitleErrorVisible) {
      // If validation error is shown
      const titleError = await taskFlowPage.getFieldErrorMessage(taskFlowPage.titleErrorMessageLocator);
      expect(titleError).toBeTruthy();
      expect(titleError.length).toBeGreaterThan(0);
    }
  });
});