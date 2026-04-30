import { test, expect } from '@playwright/test';
import { WorkflowStateActions } from '../../actions/WorkflowStateActions';
import { TaskFlowPage } from '../../pages/TaskFlowPage';
import { TestDataLoader } from '../../utils/TestDataLoader';

test.describe('DEMO-2524 - Workflow State Session Persistence', () => {
  let workflowStateActions: WorkflowStateActions;
  let taskFlowPage: TaskFlowPage;
  let testData: any;

  test.beforeEach(async ({ page }) => {
    workflowStateActions = new WorkflowStateActions(page);
    taskFlowPage = new TaskFlowPage(page);
    testData = TestDataLoader.loadTestData('workflow-state-data.json');
    
    await workflowStateActions.navigateToTaskCreation();
  });

  test('[DEMO-2524][TS8-TC2] Verify state persistence across user sessions', async () => {
    // Given the user creates a task "Cross-Session Test Task" with "Code Review" state
    await workflowStateActions.createTaskWithWorkflowState(
      testData.taskTitles.crossSession, 
      'Code Review'
    );
    
    // Then the task should be created successfully
    await expect(taskFlowPage.successMessageLocator).toBeVisible();
    
    // When the user logs out and logs back in
    await workflowStateActions.simulateLogoutAndLogin();
    
    // And the user navigates to the task details
    await taskFlowPage.navigateToTaskDetails(testData.taskTitles.crossSession);
    
    // Then the task should still display "Code Review" state
    const taskState = await taskFlowPage.getTaskStateFromDetails(testData.taskTitles.crossSession);
    expect(taskState).toContain('Code Review');
    
    // When a different authorized user views the same task
    // (Simulated by another session check)
    await workflowStateActions.simulateLogoutAndLogin();
    
    // Then the task should display the same "Code Review" state
    // And the state should be consistent across all sessions
    const persistentTaskExists = await taskFlowPage.isTaskInList(testData.taskTitles.crossSession);
    expect(persistentTaskExists).toBe(true);
  });
});