import { test, expect } from '@playwright/test';
import { WorkflowStateActions } from '../../actions/WorkflowStateActions';
import { TaskFlowPage } from '../../pages/TaskFlowPage';
import { TestDataLoader } from '../../utils/TestDataLoader';

test.describe('DEMO-2524 - Multiple State Changes', () => {
  let workflowStateActions: WorkflowStateActions;
  let taskFlowPage: TaskFlowPage;
  let testData: any;

  test.beforeEach(async ({ page }) => {
    workflowStateActions = new WorkflowStateActions(page);
    taskFlowPage = new TaskFlowPage(page);
    testData = TestDataLoader.loadTestData('workflow-state-data.json');
    
    await workflowStateActions.navigateToTaskCreation();
  });

  test('[DEMO-2524][TS6-TC2] Make multiple state changes with UI feedback', async () => {
    // Given the user is on the task creation form
    await expect(taskFlowPage.addTaskModalHeadingLocator).toBeVisible();
    
    // When the user enters task title "Multi-Change Test Task"
    await workflowStateActions.fillTaskTitle(testData.taskTitles.multiChange);
    
    // And makes sequential state changes
    const stateSequence = ['To Do', 'In Progress', 'Testing', 'Done', 'Blocked'];
    
    for (const state of stateSequence) {
      await workflowStateActions.selectWorkflowState(state);
      
      // Verify UI reflects the change
      const currentState = await taskFlowPage.getSelectedWorkflowState();
      expect(currentState).toContain(state);
      
      // Brief pause between selections
      await workflowStateActions.page.waitForTimeout(500);
    }
    
    // Then the final selection should be "Blocked"
    const finalSelection = await taskFlowPage.getSelectedWorkflowState();
    expect(finalSelection).toContain('Blocked');
    
    // When the user clicks the "Create Task" button
    await workflowStateActions.submitTaskCreation();
    
    // Then the task should be created with "Blocked" state
    await expect(taskFlowPage.successMessageLocator).toBeVisible();
    
    // And the UI should provide consistent feedback throughout
    const successMessage = await taskFlowPage.getSuccessMessageText();
    expect(successMessage).toContain('Task created successfully');
  });
});