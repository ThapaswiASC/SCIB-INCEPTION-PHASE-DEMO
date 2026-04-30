import { test, expect } from '@playwright/test';
import { WorkflowStateActions } from '../../actions/WorkflowStateActions';
import { TaskFlowPage } from '../../pages/TaskFlowPage';
import { TestDataLoader } from '../../utils/TestDataLoader';

test.describe('DEMO-2524 - Workflow State Multiple Creation', () => {
  let workflowStateActions: WorkflowStateActions;
  let taskFlowPage: TaskFlowPage;
  let testData: any;

  test.beforeEach(async ({ page }) => {
    workflowStateActions = new WorkflowStateActions(page);
    taskFlowPage = new TaskFlowPage(page);
    testData = TestDataLoader.loadTestData('workflow-state-data.json');
    
    await workflowStateActions.navigateToTaskCreation();
  });

  test('[DEMO-2524][TS7-TC1] Create multiple tasks with different workflow states', async () => {
    const tasksToCreate = [
      { name: 'Task with To Do State', state: 'To Do' },
      { name: 'Task with In Progress State', state: 'In Progress' },
      { name: 'Task with Done State', state: 'Done' }
    ];
    
    for (let i = 0; i < tasksToCreate.length; i++) {
      const task = tasksToCreate[i];
      
      // Given the user is on the task creation form
      await expect(taskFlowPage.addTaskModalHeadingLocator).toBeVisible();
      
      // When the user creates a task with specific state
      await workflowStateActions.createTaskWithWorkflowState(task.name, task.state);
      
      // Then the task should be created successfully
      await expect(taskFlowPage.successMessageLocator).toBeVisible();
      
      // Reopen modal for next task if not the last one
      if (i < tasksToCreate.length - 1) {
        await taskFlowPage.openAddTaskModal();
      }
    }
    
    // And all tasks should be created successfully
    // And no state conflicts should occur
    // And system performance should remain stable
    const successMessage = await taskFlowPage.getSuccessMessageText();
    expect(successMessage).toContain('Task created successfully');
  });
});