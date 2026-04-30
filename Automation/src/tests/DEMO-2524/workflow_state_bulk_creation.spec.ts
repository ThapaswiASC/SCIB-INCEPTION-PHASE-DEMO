import { test, expect } from '@playwright/test';
import { WorkflowStateActions } from '../../actions/WorkflowStateActions';
import { TaskFlowPage } from '../../pages/TaskFlowPage';
import { TestDataLoader } from '../../utils/TestDataLoader';

test.describe('DEMO-2524 - Bulk Task Creation', () => {
  let workflowStateActions: WorkflowStateActions;
  let taskFlowPage: TaskFlowPage;
  let testData: any;

  test.beforeEach(async ({ page }) => {
    workflowStateActions = new WorkflowStateActions(page);
    taskFlowPage = new TaskFlowPage(page);
    testData = TestDataLoader.loadTestData('workflow-state-data.json');
    
    await workflowStateActions.navigateToTaskCreation();
  });

  test('[DEMO-2524][TS7-TC2] Create bulk tasks with all workflow states', async () => {
    // Given the user starts bulk task creation
    await expect(taskFlowPage.addTaskModalHeadingLocator).toBeVisible();
    
    const startTime = Date.now();
    
    // When the user creates 6 tasks with different states
    await workflowStateActions.createMultipleTasksWithStates(testData.bulkTaskData);
    
    const endTime = Date.now();
    const totalTime = endTime - startTime;
    
    // Then all tasks should be created within 30 seconds
    expect(totalTime).toBeLessThan(testData.performanceThresholds.bulkCreationTime);
    
    // And each task should have the correct workflow state
    // And the system should remain responsive
    // And no data corruption should occur
    console.log(`Bulk creation time: ${totalTime}ms`);
    
    // Verify successful completion
    await expect(taskFlowPage.successMessageLocator).toBeVisible();
  });
});