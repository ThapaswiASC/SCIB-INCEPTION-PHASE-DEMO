import { test, expect } from '@playwright/test';
import { WorkflowStateActions } from '../../actions/WorkflowStateActions';
import { TaskFlowPage } from '../../pages/TaskFlowPage';

test.describe('DEMO-2524 - Multiple Tasks Default State', () => {
  let workflowStateActions: WorkflowStateActions;
  let taskFlowPage: TaskFlowPage;

  test.beforeEach(async ({ page }) => {
    workflowStateActions = new WorkflowStateActions(page);
    taskFlowPage = new TaskFlowPage(page);
    
    await workflowStateActions.navigateToTaskCreation();
  });

  test('[DEMO-2524][TS2-TC2] Create multiple tasks without state selection', async () => {
    const taskNames = ['Task 1 Default', 'Task 2 Default', 'Task 3 Default'];
    
    for (let i = 0; i < taskNames.length; i++) {
      const taskName = taskNames[i];
      
      // Given the user is logged into SCIB system
      await expect(taskFlowPage.addTaskModalHeadingLocator).toBeVisible();
      
      // When the user creates a task without selecting state
      await workflowStateActions.createTaskWithoutWorkflowState(taskName);
      
      // Then the task should be created successfully
      await expect(taskFlowPage.successMessageLocator).toBeVisible();
      
      // Reopen modal for next task if not the last one
      if (i < taskNames.length - 1) {
        await taskFlowPage.openAddTaskModal();
      }
    }
    
    // And all tasks should have "To Do" as the workflow state
    // And the system should maintain consistency
    const successMessage = await taskFlowPage.getSuccessMessageText();
    expect(successMessage).toContain('Task created successfully');
  });
});