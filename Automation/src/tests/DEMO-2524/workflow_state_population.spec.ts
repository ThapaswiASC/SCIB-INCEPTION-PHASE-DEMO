import { test, expect } from '@playwright/test';
import { WorkflowStateActions } from '../../actions/WorkflowStateActions';
import { TaskFlowPage } from '../../pages/TaskFlowPage';
import { TestDataLoader } from '../../utils/TestDataLoader';

test.describe('DEMO-2524 - Workflow State Population', () => {
  let workflowStateActions: WorkflowStateActions;
  let taskFlowPage: TaskFlowPage;
  let testData: any;

  test.beforeEach(async ({ page }) => {
    workflowStateActions = new WorkflowStateActions(page);
    taskFlowPage = new TaskFlowPage(page);
    testData = TestDataLoader.loadTestData('workflow-state-data.json');
    
    await workflowStateActions.navigateToTaskCreation();
  });

  test('[DEMO-2524][TS4-TC1] Verify all SCIB workflow states in dropdown', async () => {
    // Given the user is on the task creation form
    await expect(taskFlowPage.addTaskModalHeadingLocator).toBeVisible();
    
    // When the user clicks the workflow state dropdown
    await workflowStateActions.openWorkflowStateDropdown();
    
    // Then the dropdown should open
    await expect(taskFlowPage.workflowStateOptionsLocator.first()).toBeVisible();
    
    // And verify each required state is present
    for (const state of testData.validWorkflowStates) {
      const stateOption = taskFlowPage.page.getByRole('option', { name: state });
      await expect(stateOption).toBeVisible();
    }
    
    // And the dropdown should contain exactly 6 workflow states
    const optionCount = await taskFlowPage.countWorkflowStateOptions();
    expect(optionCount).toBe(6);
  });
});