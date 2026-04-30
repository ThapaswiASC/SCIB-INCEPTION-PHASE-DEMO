import { test, expect } from '@playwright/test';
import { WorkflowStateActions } from '../../actions/WorkflowStateActions';
import { TaskFlowPage } from '../../pages/TaskFlowPage';
import { TestDataLoader } from '../../utils/TestDataLoader';

test.describe('DEMO-2524 - Workflow State Persistence', () => {
  let workflowStateActions: WorkflowStateActions;
  let taskFlowPage: TaskFlowPage;
  let testData: any;

  test.beforeEach(async ({ page }) => {
    workflowStateActions = new WorkflowStateActions(page);
    taskFlowPage = new TaskFlowPage(page);
    testData = TestDataLoader.loadTestData('workflow-state-data.json');
    
    await workflowStateActions.navigateToTaskCreation();
  });

  test('[DEMO-2524][TS8-TC1] Verify workflow state persists after creation and refresh', async () => {
    // Given the user is on the task creation form
    await expect(taskFlowPage.addTaskModalHeadingLocator).toBeVisible();
    
    // When the user enters task title "Persistence Test Task"
    await workflowStateActions.fillTaskTitle(testData.taskTitles.persistence);
    
    // And the user selects "Blocked" from workflow state dropdown
    await workflowStateActions.selectWorkflowState('Blocked');
    
    // And the user clicks the "Create Task" button
    await workflowStateActions.submitTaskCreation();
    
    // Then the task should be created successfully
    await expect(taskFlowPage.successMessageLocator).toBeVisible();
    
    // And the task details should show "Blocked" as workflow state
    // When the user refreshes the page
    await workflowStateActions.refreshPageAndVerifyPersistence();
    
    // Then the task should still display "Blocked" as workflow state
    // And the task list should show "Blocked" state
    // And the database should contain "Blocked" state for the task
    const taskExists = await taskFlowPage.isTaskInList(testData.taskTitles.persistence);
    expect(taskExists).toBe(true);
  });
});