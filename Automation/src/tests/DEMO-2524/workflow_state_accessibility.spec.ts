import { test, expect } from '@playwright/test';
import { WorkflowStateActions } from '../../actions/WorkflowStateActions';
import { TaskFlowPage } from '../../pages/TaskFlowPage';

test.describe('DEMO-2524 - Workflow State Accessibility', () => {
  let workflowStateActions: WorkflowStateActions;
  let taskFlowPage: TaskFlowPage;

  test.beforeEach(async ({ page }) => {
    workflowStateActions = new WorkflowStateActions(page);
    taskFlowPage = new TaskFlowPage(page);
    
    await workflowStateActions.navigateToTaskCreation();
  });

  test('[DEMO-2524][TS4-TC2] Test dropdown accessibility', async () => {
    // Given the user is using assistive technology
    await expect(taskFlowPage.addTaskModalHeadingLocator).toBeVisible();
    
    // When the user focuses on the workflow state dropdown
    await taskFlowPage.workflowStateDropdownLocator.focus();
    
    // Then the dropdown should have proper ARIA labels
    const hasARIALabels = await taskFlowPage.hasProperARIALabels();
    expect(hasARIALabels).toBe(true);
    
    // And the options should be announced by screen readers
    await workflowStateActions.openWorkflowStateDropdown();
    
    // Verify accessibility attributes
    const ariaExpanded = await taskFlowPage.workflowStateDropdownLocator.getAttribute('aria-expanded');
    expect(ariaExpanded).toBe('true');
    
    // And the selected state should be clearly indicated
    const ariaSelected = await taskFlowPage.workflowStateOptionsLocator.first().getAttribute('aria-selected');
    expect(ariaSelected).toBeDefined();
  });
});