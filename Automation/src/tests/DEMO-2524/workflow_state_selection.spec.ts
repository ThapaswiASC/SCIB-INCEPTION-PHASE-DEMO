import { test, expect } from '@playwright/test';
import { WorkflowStateActions } from '../../actions/WorkflowStateActions';
import { TaskFlowPage } from '../../pages/TaskFlowPage';
import { TestDataLoader } from '../../utils/TestDataLoader';

test.describe('DEMO-2524 - Workflow State Selection', () => {
  let workflowStateActions: WorkflowStateActions;
  let taskFlowPage: TaskFlowPage;
  let testData: any;

  test.beforeEach(async ({ page }) => {
    workflowStateActions = new WorkflowStateActions(page);
    taskFlowPage = new TaskFlowPage(page);
    testData = TestDataLoader.loadTestData('workflow-state-data.json');
    
    await workflowStateActions.navigateToTaskCreation();
  });

  testData.validWorkflowStates.forEach((state: string) => {
    test(`[DEMO-2524][TS1-TC2] Create task with valid workflow state: ${state}`, async () => {
      // Given the user is on the task creation form
      await expect(taskFlowPage.addTaskModalHeadingLocator).toBeVisible();
      
      // When the user enters task title and selects state
      await workflowStateActions.createTaskWithWorkflowState(`Test Task - ${state}`, state);
      
      // Then the task should be created successfully
      await expect(taskFlowPage.successMessageLocator).toBeVisible();
      
      // And the task should display the correct workflow state
      const successMessage = await taskFlowPage.getSuccessMessageText();
      expect(successMessage).toContain('Task created successfully');
    });
  });
});