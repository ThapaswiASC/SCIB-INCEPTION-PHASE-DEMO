import { test, expect } from '@playwright/test';
import { TaskCreationActions } from '../actions/TaskCreationActions';
import { TestDataLoader } from '../utils/TestDataLoader';

test.describe('Task Creation - Success Scenarios', () => {
  let taskActions: TaskCreationActions;
  let testDataLoader: TestDataLoader;

  test.beforeEach(async ({ page }) => {
    taskActions = new TaskCreationActions(page);
    testDataLoader = TestDataLoader.getInstance();
  });

  test('DEMO-2646: Successful task creation with all valid mandatory fields', async () => {
    // Given all mandatory fields are available on the form
    await taskActions.navigateToTaskCreationForm();
    
    const validTaskData = testDataLoader.getValidTaskData();
    
    // When the user enters all required fields and submits
    await taskActions.fillTaskTitle(validTaskData.title);
    await taskActions.fillTaskDescription(validTaskData.description);
    await taskActions.selectPriority(validTaskData.priority);
    await taskActions.fillAssignee(validTaskData.assignee);
    await taskActions.submitTaskForm();
    
    // Then the task should be created successfully
    await taskActions.waitForFormSubmission();
    
    await expect(taskActions.taskCreationPage.successMessage).toBeVisible();
    await expect(taskActions.taskCreationPage.successMessage).toContainText(
      testDataLoader.getSuccessMessage('taskCreated')
    );
    
    // And an auto-generated task ID should be displayed
    await expect(taskActions.taskCreationPage.taskIdDisplay).toBeVisible();
    
    // And the task should appear in the task list (assuming navigation occurs)
    // This would typically involve navigating to task list and verifying the task exists
  });

  test('Task creation with alternate valid data set', async () => {
    // Given the user is on the task creation form
    await taskActions.navigateToTaskCreationForm();
    
    const alternateTaskData = testDataLoader.getValidTaskDataAlternate();
    
    // When the user creates a task with alternate valid data
    await taskActions.createTask(alternateTaskData);
    
    // Then the task should be created successfully
    await taskActions.waitForFormSubmission();
    await expect(taskActions.taskCreationPage.successMessage).toBeVisible();
  });

  test('Task creation with different valid priority levels', async () => {
    const validPriorities = testDataLoader.getValidPriorities();
    const baseTaskData = testDataLoader.getValidTaskData();
    
    for (const priority of validPriorities) {
      await test.step(`Create task with ${priority} priority`, async () => {
        await taskActions.navigateToTaskCreationForm();
        
        const taskData = testDataLoader.generateTaskData({ 
          ...baseTaskData,
          priority,
          title: `${baseTaskData.title} - ${priority} Priority`
        });
        
        await taskActions.createTask(taskData);
        await taskActions.waitForFormSubmission();
        
        await expect(taskActions.taskCreationPage.successMessage).toBeVisible();
      });
    }
  });

  test('Task creation with different valid assignees', async () => {
    const validAssignees = testDataLoader.getValidAssignees();
    const baseTaskData = testDataLoader.getValidTaskData();
    
    for (const assignee of validAssignees) {
      await test.step(`Create task with assignee: ${assignee}`, async () => {
        await taskActions.navigateToTaskCreationForm();
        
        const taskData = testDataLoader.generateTaskData({ 
          ...baseTaskData,
          assignee,
          title: `${baseTaskData.title} - ${assignee}`
        });
        
        await taskActions.createTask(taskData);
        await taskActions.waitForFormSubmission();
        
        await expect(taskActions.taskCreationPage.successMessage).toBeVisible();
      });
    }
  });
});