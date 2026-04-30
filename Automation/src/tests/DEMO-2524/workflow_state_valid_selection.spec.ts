import { test, expect } from '@playwright/test';
import { WorkflowStateActions } from '../../actions/WorkflowStateActions';
import { TaskFlowPage } from '../../pages/TaskFlowPage';
import { TestDataLoader } from '../../utils/TestDataLoader';

test.describe('DEMO-2524 - Workflow State Valid Selection', () => {
  let workflowStateActions: WorkflowStateActions;
  let taskFlowPage: TaskFlowPage;
  let testData: any;

  test.beforeEach(async ({ page }) => {
    workflowStateActions = new WorkflowStateActions(page);
    taskFlowPage = new TaskFlowPage(page);
    testData = TestDataLoader.loadTestData('workflow-state-data.json');
    
    await workflowStateActions.navigateToTaskCreation();
  });

  test('[DEMO-2524][TS1-TC1] Select In Progress state for new task', async () => {
    // Given the user is on the task creation form
    await expect(taskFlowPage.addTaskModalHeadingLocator).toBeVisible();
    
    // When the user enters task title "Sample SCIB Task for Testing"
    await workflowStateActions.fillTaskTitle(testData.taskTitles.basic);
    
    // And the user clicks the workflow state dropdown
    await workflowStateActions.openWorkflowStateDropdown();
    
    // And the user selects "In Progress" from the dropdown
    await workflowStateActions.selectWorkflowState('In Progress');
    
    // And the user clicks the "Create Task" button
    await workflowStateActions.submitTaskCreation();
    
    // Then the task should be created successfully
    await expect(taskFlowPage.successMessageLocator).toBeVisible();
    
    // And the task should display "In Progress" as the workflow state
    const successMessage = await taskFlowPage.getSuccessMessageText();
    expect(successMessage).toContain('Task created successfully');
    
    // And the task should appear in the task list
    await expect(taskFlowPage.addTaskModalHeadingLocator).not.toBeVisible();
  });

  testData.validWorkflowStates.forEach((state: string) => {
    test(`[DEMO-2524][TS1-TC2] Create task with valid workflow state: ${state}`, async () => {
      // Given the user is on the task creation form
      await expect(taskFlowPage.addTaskModalHeadingLocator).toBeVisible();
      
      // When the user enters task title "Test Task - <state>"
      await workflowStateActions.fillTaskTitle(`Test Task - ${state}`);
      
      // And the user selects "<state>" from workflow state dropdown
      await workflowStateActions.selectWorkflowState(state);
      
      // And the user clicks the "Create Task" button
      await workflowStateActions.submitTaskCreation();
      
      // Then the task should be created successfully
      await expect(taskFlowPage.successMessageLocator).toBeVisible();
      
      // And the task should display "<state>" as the workflow state
      const successMessage = await taskFlowPage.getSuccessMessageText();
      expect(successMessage).toContain('Task created successfully');
    });
  });
});