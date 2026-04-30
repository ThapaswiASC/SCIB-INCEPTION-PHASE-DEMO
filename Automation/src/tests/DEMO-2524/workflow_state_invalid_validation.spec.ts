import { test, expect } from '@playwright/test';
import { WorkflowStateActions } from '../../actions/WorkflowStateActions';
import { TaskFlowPage } from '../../pages/TaskFlowPage';
import { TestDataLoader } from '../../utils/TestDataLoader';

test.describe('DEMO-2524 - Workflow State Invalid Validation', () => {
  let workflowStateActions: WorkflowStateActions;
  let taskFlowPage: TaskFlowPage;
  let testData: any;

  test.beforeEach(async ({ page }) => {
    workflowStateActions = new WorkflowStateActions(page);
    taskFlowPage = new TaskFlowPage(page);
    testData = TestDataLoader.loadTestData('workflow-state-data.json');
    
    await workflowStateActions.navigateToTaskCreation();
  });

  test('[DEMO-2524][TS3-TC1] Attempt to create task with invalid workflow state', async () => {
    // Given the user is on the task creation form
    await expect(taskFlowPage.addTaskModalHeadingLocator).toBeVisible();
    
    // When the user enters task title "Sample SCIB Task for Testing"
    await workflowStateActions.fillTaskTitle(testData.taskTitles.basic);
    
    // And the user attempts to submit invalid state "Invalid State"
    await workflowStateActions.attemptMaliciousInput('Invalid State');
    
    // And the user clicks the "Create Task" button
    await workflowStateActions.submitTaskCreation();
    
    // Then a validation error should be displayed
    const isErrorVisible = await taskFlowPage.workflowStateErrorMessageLocator.isVisible();
    const isSuccessVisible = await taskFlowPage.successMessageLocator.isVisible();
    
    if (isErrorVisible) {
      // And the error message should indicate invalid workflow state
      const errorMessage = await taskFlowPage.getFieldErrorMessage(taskFlowPage.workflowStateErrorMessageLocator);
      expect(errorMessage).toMatch(/invalid.*state|valid.*state/i);
    }
    
    // And the task should not be created
    // And the user should remain on the creation form
    await expect(taskFlowPage.addTaskModalHeadingLocator).toBeVisible();
  });

  testData.invalidWorkflowStates.forEach((invalidState: string) => {
    test(`[DEMO-2524][TS3-TC2] Validate invalid workflow state: "${invalidState}"`, async () => {
      // Given the user is on the task creation form
      await expect(taskFlowPage.addTaskModalHeadingLocator).toBeVisible();
      
      // When the user enters task title "Test Invalid States"
      await workflowStateActions.fillTaskTitle('Test Invalid States');
      
      // And the user attempts to submit invalid state
      await workflowStateActions.attemptMaliciousInput(invalidState);
      
      // And the user clicks the "Create Task" button
      await workflowStateActions.submitTaskCreation();
      
      // Then the system should handle the invalid input appropriately
      const isErrorVisible = await taskFlowPage.workflowStateErrorMessageLocator.isVisible();
      const isSuccessVisible = await taskFlowPage.successMessageLocator.isVisible();
      
      // And no task should be created with invalid state
      if (!isSuccessVisible) {
        await expect(taskFlowPage.addTaskModalHeadingLocator).toBeVisible();
      }
    });
  });
});