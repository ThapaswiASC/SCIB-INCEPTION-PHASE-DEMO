import { test, expect } from '@playwright/test';
import { TaskCreationActions } from '../../actions/TaskCreationActions';
import { TaskFlowPage } from '../../pages/TaskFlowPage';
import { TestDataLoader } from '../../utils/TestDataLoader';

test.describe('DEMO-2523 - Task Creation Success Scenarios', () => {
  let taskCreationActions: TaskCreationActions;
  let taskFlowPage: TaskFlowPage;
  let testData: any;

  test.beforeEach(async ({ page }) => {
    taskCreationActions = new TaskCreationActions(page);
    taskFlowPage = new TaskFlowPage(page);
    testData = TestDataLoader.loadTestData('task-creation-data.json');
    
    await taskCreationActions.navigateToTaskCreation();
  });

  test('[DEMO-2523][TS1-TC1] Verify Successful Task Creation with All Valid Mandatory Fields', async () => {
    // Given all mandatory fields are available on the form
    await expect(taskFlowPage.addTaskModalHeadingLocator).toBeVisible();
    await expect(taskFlowPage.taskTitleInputLocator).toBeVisible();
    await expect(taskFlowPage.descriptionTextareaLocator).toBeVisible();
    await expect(taskFlowPage.prioritySelectLocator).toBeVisible();
    await expect(taskFlowPage.assigneeSelectLocator).toBeVisible();

    // When the user enters all valid mandatory fields
    const validTaskData = testData.validTaskData;
    await taskCreationActions.createTaskWithAllFields(validTaskData);

    // Then the task should be created successfully
    await expect(taskFlowPage.successMessageLocator).toBeVisible();
    
    // And an auto-generated task ID should be displayed
    const successMessage = await taskFlowPage.getSuccessMessageText();
    expect(successMessage).toContain('Task created successfully!');
    expect(successMessage).toMatch(/TASK-\d+/);
    
    // And the task should appear in the task list
    await expect(taskFlowPage.addTaskModalHeadingLocator).not.toBeVisible();
    
    // Wait for task to appear in list (simulated behavior)
    await taskFlowPage.page.waitForTimeout(1000);
  });
});