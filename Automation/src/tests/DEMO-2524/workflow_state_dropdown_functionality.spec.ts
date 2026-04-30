import { test, expect } from '@playwright/test';
import { WorkflowStateActions } from '../../actions/WorkflowStateActions';
import { TaskFlowPage } from '../../pages/TaskFlowPage';
import { TestDataLoader } from '../../utils/TestDataLoader';

test.describe('DEMO-2524 - Workflow State Dropdown Functionality', () => {
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
    
    // And the dropdown should contain all required workflow states
    const availableOptions = await taskFlowPage.getWorkflowStateOptions();
    
    for (const expectedState of testData.validWorkflowStates) {
      expect(availableOptions).toContain(expectedState);
    }
    
    // And the dropdown should contain exactly 6 workflow states
    const optionCount = await taskFlowPage.countWorkflowStateOptions();
    expect(optionCount).toBe(6);
  });

  test('[DEMO-2524][TS4-TC2] Test dropdown keyboard navigation', async () => {
    // Given the user is on the task creation form
    await expect(taskFlowPage.addTaskModalHeadingLocator).toBeVisible();
    
    // And the workflow state dropdown is present
    await expect(taskFlowPage.workflowStateDropdownLocator).toBeVisible();
    
    // When the user navigates to the dropdown using Tab key
    await workflowStateActions.navigateDropdownWithKeyboard();
    
    // And the user presses Arrow Down key
    // Then the dropdown should open
    await expect(taskFlowPage.workflowStateOptionsLocator.first()).toBeVisible();
    
    // And the user should be able to navigate options with arrow keys
    await workflowStateActions.navigateDropdownOptions();
    
    // And the user should be able to select option with Enter key
    await workflowStateActions.selectDropdownOptionWithEnter();
    
    // And the user should be able to close dropdown with Escape key
    await workflowStateActions.closeDropdownWithEscape();
  });
});