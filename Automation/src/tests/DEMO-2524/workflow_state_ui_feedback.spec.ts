import { test, expect } from '@playwright/test';
import { WorkflowStateActions } from '../../actions/WorkflowStateActions';
import { TaskFlowPage } from '../../pages/TaskFlowPage';
import { TestDataLoader } from '../../utils/TestDataLoader';

test.describe('DEMO-2524 - UI Feedback for State Changes', () => {
  let workflowStateActions: WorkflowStateActions;
  let taskFlowPage: TaskFlowPage;
  let testData: any;

  test.beforeEach(async ({ page }) => {
    workflowStateActions = new WorkflowStateActions(page);
    taskFlowPage = new TaskFlowPage(page);
    testData = TestDataLoader.loadTestData('workflow-state-data.json');
    
    await workflowStateActions.navigateToTaskCreation();
  });

  test('[DEMO-2524][TS6-TC2] UI feedback for multiple state changes', async () => {
    // Given the user is on the task creation form
    await expect(taskFlowPage.addTaskModalHeadingLocator).toBeVisible();
    
    // When the user enters task title
    await workflowStateActions.fillTaskTitle(testData.taskTitles.multiChange);
    
    // And makes multiple state changes with UI feedback verification
    for (const state of testData.multipleStateChanges) {
      await workflowStateActions.selectWorkflowState(state);
      
      // Then the UI should reflect each selection change
      const currentSelection = await taskFlowPage.getSelectedWorkflowState();
      expect(currentSelection).toContain(state);
      
      // Brief pause to observe UI feedback
      await workflowStateActions.page.waitForTimeout(300);
    }
    
    // And the final selection should be "Blocked"
    const finalState = await taskFlowPage.getSelectedWorkflowState();
    expect(finalState).toContain('Blocked');
    
    // When the user submits the task
    await workflowStateActions.submitTaskCreation();
    
    // Then the task should be created with "Blocked" state
    await expect(taskFlowPage.successMessageLocator).toBeVisible();
    
    // And the UI should provide consistent feedback throughout
    const successMessage = await taskFlowPage.getSuccessMessageText();
    expect(successMessage).toContain('Task created successfully');
  });
});