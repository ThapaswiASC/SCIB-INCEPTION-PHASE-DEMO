import { test, expect } from '@playwright/test';
import { TaskCreationActions } from '../actions/TaskCreationActions';
import { TestDataLoader } from '../utils/TestDataLoader';

/**
 * Test Suite: Successful Task Creation Scenarios
 * Covers positive test cases for task creation functionality
 */
test.describe('Task Creation - Success Scenarios', () => {
  let taskActions: TaskCreationActions;
  let testDataLoader: TestDataLoader;

  test.beforeEach(async ({ page }) => {
    taskActions = new TaskCreationActions(page);
    testDataLoader = TestDataLoader.getInstance();
    await taskActions.navigateToTaskCreationForm();
  });

  test('DEMO-2646: Successful task creation with all valid mandatory fields', async ({ page }) => {
    // Given all mandatory fields are available on the form
    await expect(taskActions.taskCreationPage.formContainer).toBeVisible();
    await expect(taskActions.taskCreationPage.titleField).toBeVisible();
    await expect(taskActions.taskCreationPage.descriptionField).toBeVisible();
    await expect(taskActions.taskCreationPage.priorityDropdown).toBeVisible();
    await expect(taskActions.taskCreationPage.assigneeField).toBeVisible();

    // When the user enters all valid data
    const testData = testDataLoader.getScenarioData('DEMO-2646');
    await taskActions.fillTitle(testData.title);
    await taskActions.fillDescription(testData.description);
    await taskActions.selectPriority(testData.priority);
    await taskActions.fillAssignee(testData.assignee);

    // And clicks the Submit button
    await taskActions.submitForm();

    // Then the task should be created successfully
    await expect(taskActions.taskCreationPage.successMessage).toBeVisible();
    
    // And an auto-generated task ID should be displayed
    await expect(taskActions.taskCreationPage.taskIdDisplay).toBeVisible();
    const taskId = await taskActions.taskCreationPage.getGeneratedTaskId();
    expect(taskId).toBeTruthy();
    expect(taskId).toMatch(/^TASK-\d+$/); // Assuming format TASK-{number}

    // And the task should appear in the task list (navigation verification)
    // Note: This would typically involve navigating to task list and verifying presence
    const successText = await taskActions.taskCreationPage.successMessage.textContent();
    expect(successText).toContain('successfully');
  });

  test('Dynamic task creation with generated data', async ({ page }) => {
    // Generate dynamic test data
    const dynamicData = testDataLoader.generateDynamicTaskData({
      priority: testDataLoader.getRandomValidPriority(),
      assignee: testDataLoader.getRandomValidEmail()
    });

    // Fill form with dynamic data
    await taskActions.fillCompleteTaskForm(dynamicData);
    await taskActions.submitForm();

    // Verify successful creation
    await expect(taskActions.taskCreationPage.successMessage).toBeVisible();
    await expect(taskActions.taskCreationPage.taskIdDisplay).toBeVisible();
  });

  test('Task creation with all priority levels', async ({ page }) => {
    const validPriorities = testDataLoader.getEdgeCaseData().validPriorities;
    const baseData = testDataLoader.getValidTaskData();

    for (const priority of validPriorities) {
      // Clear form and fill with current priority
      await taskActions.clearAllFields();
      await taskActions.fillCompleteTaskForm({
        ...baseData,
        priority: priority,
        title: `${baseData.title} - ${priority} Priority`
      });

      await taskActions.submitForm();

      // Verify successful creation
      await expect(taskActions.taskCreationPage.successMessage).toBeVisible();
      
      // Navigate back to form for next iteration
      await taskActions.navigateToTaskCreationForm();
    }
  });

  test('Task creation with different valid SCIB emails', async ({ page }) => {
    const validEmails = testDataLoader.getEdgeCaseData().validScibEmails;
    const baseData = testDataLoader.getValidTaskData();

    for (const email of validEmails) {
      // Clear form and fill with current email
      await taskActions.clearAllFields();
      await taskActions.fillCompleteTaskData({
        ...baseData,
        assignee: email,
        title: `${baseData.title} - ${email}`
      });

      await taskActions.submitForm();

      // Verify successful creation
      await expect(taskActions.taskCreationPage.successMessage).toBeVisible();
      
      // Navigate back to form for next iteration
      await taskActions.navigateToTaskCreationForm();
    }
  });
});