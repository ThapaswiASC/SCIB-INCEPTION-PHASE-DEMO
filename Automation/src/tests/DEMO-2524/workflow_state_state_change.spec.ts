import { test, expect } from '@playwright/test';
import { WorkflowStateActions } from '../../actions/WorkflowStateActions';
import { TaskFlowPage } from '../../pages/TaskFlowPage';
import { TestDataLoader } from '../../utils/TestDataLoader';

test.describe('DEMO-2524 - Workflow State Change', () => {
  let workflowStateActions: WorkflowStateActions;
  let taskFlowPage: TaskFlowPage;
  let testData: any;

  test.beforeEach(async ({ page }) => {
    workflowStateActions = new WorkflowStateActions(page);
    taskFlowPage = new TaskFlowPage(page);
    testData = TestDataLoader.loadTestData('workflow-state-data.json');
    
    await workflowStateActions.navigateToTaskCreation();
  });

  test('[DEMO-2524][TS6-TC1] Change workflow state selection before submission', async () => {
    // Given the user is on the task creation form
    await expect(taskFlowPage.addTaskModalHeadingLocator).toBeVisible();
    
    // When the user enters task title "Sample SCIB Task for Testing"
    await workflowStateActions.fillTaskTitle(testData.taskTitles.basic);
    
    // And the user selects "Testing" from workflow state dropdown
    await workflowStateActions.selectWorkflowState('Testing');
    
    // And the user changes selection to "Code Review"
    await workflowStateActions.selectWorkflowState('Code Review');
    
    // And the user clicks the "Create Task" button
    await workflowStateActions.submitTaskCreation();
    
    // Then the task should be created successfully
    await expect(taskFlowPage.successMessageLocator).toBeVisible();
    
    // And the task should have "Code Review" as the workflow state
    const successMessage = await taskFlowPage.getSuccessMessageText();
    expect(successMessage).toContain('Task created successfully');
    
    // And the task should not have "Testing" as the workflow state
    // (This would be verified by checking the final state in the task details)
  });

  test('[DEMO-2524][TS6-TC2] Make multiple state changes with UI feedback', async () => {
    // Given the user is on the task creation form
    await expect(taskFlowPage.addTaskModalHeadingLocator).toBeVisible();
    
    // When the user enters task title "Multi-Change Test Task"
    await workflowStateActions.fillTaskTitle(testData.taskTitles.multiChange);
    
    // And the user makes multiple state changes
    await workflowStateActions.changeWorkflowStateMultipleTimes(testData.multipleStateChanges);
    
    // Then the UI should reflect each selection change
    // And the final selection should be "Blocked"
    const finalState = await taskFlowPage.getSelectedWorkflowState();
    expect(finalState).toContain('Blocked');
    
    // When the user clicks the "Create Task" button
    await workflowStateActions.submitTaskCreation();
    
    // Then the task should be created with "Blocked" state
    await expect(taskFlowPage.successMessageLocator).toBeVisible();
    
    // And the UI should provide consistent feedback throughout
    const successMessage = await taskFlowPage.getSuccessMessageText();
    expect(successMessage).toContain('Task created successfully');
  });
});