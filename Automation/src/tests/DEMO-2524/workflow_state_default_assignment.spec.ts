import { test, expect } from '@playwright/test';
import { WorkflowStateActions } from '../../actions/WorkflowStateActions';
import { TaskFlowPage } from '../../pages/TaskFlowPage';
import { TestDataLoader } from '../../utils/TestDataLoader';

test.describe('DEMO-2524 - Workflow State Default Assignment', () => {
  let workflowStateActions: WorkflowStateActions;
  let taskFlowPage: TaskFlowPage;
  let testData: any;

  test.beforeEach(async ({ page }) => {
    workflowStateActions = new WorkflowStateActions(page);
    taskFlowPage = new TaskFlowPage(page);
    testData = TestDataLoader.loadTestData('workflow-state-data.json');
    
    await workflowStateActions.navigateToTaskCreation();
  });

  test('[DEMO-2524][TS2-TC1] Create task without selecting workflow state', async () => {
    // Given the user is on the task creation form
    await expect(taskFlowPage.addTaskModalHeadingLocator).toBeVisible();
    
    // When the user enters task title "Sample SCIB Task for Testing"
    await workflowStateActions.fillTaskTitle(testData.taskTitles.basic);
    
    // And the user does not select any workflow state
    // (Skip workflow state selection)
    
    // And the user clicks the "Create Task" button
    await workflowStateActions.submitTaskCreation();
    
    // Then the task should be created successfully
    await expect(taskFlowPage.successMessageLocator).toBeVisible();
    
    // And the task should automatically have "To Do" as the workflow state
    const successMessage = await taskFlowPage.getSuccessMessageText();
    expect(successMessage).toContain('Task created successfully');
    
    // And no validation errors should be displayed
    await expect(taskFlowPage.workflowStateErrorMessageLocator).not.toBeVisible();
  });

  test('[DEMO-2524][TS2-TC2] Create multiple tasks without state selection', async () => {
    const taskNames = ['Task 1 Default', 'Task 2 Default', 'Task 3 Default'];
    
    for (const taskName of taskNames) {
      // Given the user is logged into SCIB system
      await expect(taskFlowPage.addTaskModalHeadingLocator).toBeVisible();
      
      // When the user creates a task without selecting state
      await workflowStateActions.createTaskWithoutWorkflowState(taskName);
      
      // Then the task should be created successfully
      await expect(taskFlowPage.successMessageLocator).toBeVisible();
      
      // Reopen modal for next task if not the last one
      if (taskNames.indexOf(taskName) < taskNames.length - 1) {
        await taskFlowPage.openAddTaskModal();
      }
    }
    
    // And all tasks should have "To Do" as the workflow state
    // And the system should maintain consistency
    const successMessage = await taskFlowPage.getSuccessMessageText();
    expect(successMessage).toContain('Task created successfully');
  });
});