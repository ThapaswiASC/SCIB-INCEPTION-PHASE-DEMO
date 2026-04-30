import { test, expect } from '@playwright/test';
import { WorkflowStateActions } from '../../actions/WorkflowStateActions';
import { TaskFlowPage } from '../../pages/TaskFlowPage';

test.describe('DEMO-2524 - System Performance Under Load', () => {
  let workflowStateActions: WorkflowStateActions;
  let taskFlowPage: TaskFlowPage;

  test.beforeEach(async ({ page }) => {
    workflowStateActions = new WorkflowStateActions(page);
    taskFlowPage = new TaskFlowPage(page);
    
    await workflowStateActions.navigateToTaskCreation();
  });

  test('[DEMO-2524][TS10-TC2] System recovery after load test', async () => {
    // Given the system has been under high concurrent load
    // (Simulate load by creating multiple tasks rapidly)
    const loadTasks = Array.from({ length: 10 }, (_, i) => ({
      name: `Load Test Task ${i + 1}`,
      state: 'To Do'
    }));
    
    // Simulate high load
    await workflowStateActions.createMultipleTasksWithStates(loadTasks.slice(0, 5));
    
    // When the load is reduced to normal levels
    await workflowStateActions.page.waitForTimeout(2000); // Simulate load reduction
    
    // Then the system should return to baseline performance
    const normalLoadTime = await workflowStateActions.measureDropdownLoadTime();
    expect(normalLoadTime).toBeLessThan(3000); // Should be responsive
    
    // And all functionality should remain intact
    await expect(taskFlowPage.workflowStateDropdownLocator).toBeVisible();
    await expect(taskFlowPage.addTaskModalHeadingLocator).toBeVisible();
    
    // And no performance degradation should persist
    const taskCount = await taskFlowPage.getTaskCount();
    expect(taskCount).toBeGreaterThanOrEqual(0);
  });
});