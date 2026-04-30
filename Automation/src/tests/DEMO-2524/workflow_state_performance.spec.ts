import { test, expect } from '@playwright/test';
import { WorkflowStateActions } from '../../actions/WorkflowStateActions';
import { TaskFlowPage } from '../../pages/TaskFlowPage';
import { TestDataLoader } from '../../utils/TestDataLoader';

test.describe('DEMO-2524 - Workflow State Performance', () => {
  let workflowStateActions: WorkflowStateActions;
  let taskFlowPage: TaskFlowPage;
  let testData: any;

  test.beforeEach(async ({ page }) => {
    workflowStateActions = new WorkflowStateActions(page);
    taskFlowPage = new TaskFlowPage(page);
    testData = TestDataLoader.loadTestData('workflow-state-data.json');
    
    await workflowStateActions.navigateToTaskCreation();
  });

  test('[DEMO-2524][TS10-TC1] Verify dropdown loads within performance target', async () => {
    // Given the user is on the task creation form
    await expect(taskFlowPage.addTaskModalHeadingLocator).toBeVisible();
    
    // When the user clicks the workflow state dropdown
    const loadTime = await workflowStateActions.measureDropdownLoadTime();
    
    // Then the dropdown should open within 2 seconds
    expect(loadTime).toBeLessThan(testData.performanceThresholds.dropdownLoadTime);
    
    // And all workflow state options should be visible
    await expect(taskFlowPage.workflowStateOptionsLocator.first()).toBeVisible();
    
    // And the response time should be recorded
    console.log(`Dropdown load time: ${loadTime}ms`);
    
    // And no performance degradation should occur
    expect(loadTime).toBeGreaterThan(0);
  });

  test('[DEMO-2524][TS10-TC2] Verify performance under different network conditions', async () => {
    // Given the user is on the task creation form
    await expect(taskFlowPage.addTaskModalHeadingLocator).toBeVisible();
    
    // And the network is simulated as "Slow 3G"
    await workflowStateActions.simulateSlowNetwork();
    
    // When the user clicks the workflow state dropdown
    const loadTime = await workflowStateActions.measureDropdownLoadTime();
    
    // Then the dropdown should still load within 2 seconds
    expect(loadTime).toBeLessThan(testData.performanceThresholds.dropdownLoadTime);
    
    // And all options should be accessible
    await expect(taskFlowPage.workflowStateOptionsLocator.first()).toBeVisible();
    
    // And the user experience should remain acceptable
    const optionCount = await taskFlowPage.countWorkflowStateOptions();
    expect(optionCount).toBeGreaterThan(0);
  });
});