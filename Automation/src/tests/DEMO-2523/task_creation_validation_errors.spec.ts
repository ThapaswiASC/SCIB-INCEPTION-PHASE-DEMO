import { test, expect } from '@playwright/test';
import { TaskCreationActions } from '../../actions/TaskCreationActions';
import { TaskFlowPage } from '../../pages/TaskFlowPage';
import { TestDataLoader } from '../../utils/TestDataLoader';

test.describe('DEMO-2523 - Task Creation Validation Error Scenarios', () => {
  let taskCreationActions: TaskCreationActions;
  let taskFlowPage: TaskFlowPage;
  let testData: any;

  test.beforeEach(async ({ page }) => {
    taskCreationActions = new TaskCreationActions(page);
    taskFlowPage = new TaskFlowPage(page);
    testData = TestDataLoader.loadTestData('task-creation-data.json');
    
    await taskCreationActions.navigateToTaskCreation();
  });

  test('[DEMO-2523][TS1-TC2] Verify Task Creation Fails with Empty Title Field', async () => {
    // Given the title field is empty and other fields are filled
    const taskDataWithoutTitle = testData.taskDataWithoutTitle;
    
    // When the user submits without title
    await taskCreationActions.createTaskWithMissingTitle(taskDataWithoutTitle);

    // Then a validation error should be displayed for the title field
    await expect(taskFlowPage.titleErrorMessageLocator).toBeVisible();
    
    // And the error message should state "Title is required"
    const titleError = await taskFlowPage.getFieldErrorMessage(taskFlowPage.titleErrorMessageLocator);
    expect(titleError).toBe('Title is required');
    
    // And the task should not be created
    await expect(taskFlowPage.successMessageLocator).not.toBeVisible();
    
    // And the user should remain on the creation form
    await expect(taskFlowPage.addTaskModalHeadingLocator).toBeVisible();
  });

  test('[DEMO-2523][TS2-TC1] Verify Task Creation Fails with Missing Description Field', async () => {
    // Given the description field is empty and other fields are filled
    const taskDataWithoutDescription = testData.taskDataWithoutDescription;
    
    // When the user submits without description
    await taskCreationActions.createTaskWithMissingDescription(taskDataWithoutDescription);

    // Then a validation error should be displayed for the description field
    await expect(taskFlowPage.descriptionErrorMessageLocator).toBeVisible();
    
    // And the error message should state "Description is required"
    const descriptionError = await taskFlowPage.getFieldErrorMessage(taskFlowPage.descriptionErrorMessageLocator);
    expect(descriptionError).toBe('Description is required');
    
    // And the task should not be created
    await expect(taskFlowPage.successMessageLocator).not.toBeVisible();
    
    // And the user should remain on the creation form
    await expect(taskFlowPage.addTaskModalHeadingLocator).toBeVisible();
  });

  test('[DEMO-2523][TS4-TC1] Verify Task Creation Fails with Missing Priority Field', async () => {
    // Given no priority is selected and other fields are filled
    const taskDataWithoutPriority = testData.taskDataWithoutPriority;
    
    // When the user submits without priority
    await taskCreationActions.createTaskWithMissingPriority(taskDataWithoutPriority);

    // Then a validation error should be displayed for the priority field
    await expect(taskFlowPage.priorityErrorMessageLocator).toBeVisible();
    
    // And the error message should state "Priority is required"
    const priorityError = await taskFlowPage.getFieldErrorMessage(taskFlowPage.priorityErrorMessageLocator);
    expect(priorityError).toBe('Priority is required');
    
    // And the task should not be created
    await expect(taskFlowPage.successMessageLocator).not.toBeVisible();
    
    // And the user should remain on the creation form
    await expect(taskFlowPage.addTaskModalHeadingLocator).toBeVisible();
  });

  test('[DEMO-2523][TS5-TC1] Verify Task Creation Fails with Missing Assignee Field', async () => {
    // Given the assignee field is empty and other fields are filled
    const taskDataWithoutAssignee = testData.taskDataWithoutAssignee;
    
    // When the user submits without assignee
    await taskCreationActions.createTaskWithMissingAssignee(taskDataWithoutAssignee);

    // Then a validation error should be displayed for the assignee field
    await expect(taskFlowPage.assigneeErrorMessageLocator).toBeVisible();
    
    // And the error message should state "Assignee is required"
    const assigneeError = await taskFlowPage.getFieldErrorMessage(taskFlowPage.assigneeErrorMessageLocator);
    expect(assigneeError).toBe('Assignee is required');
    
    // And the task should not be created
    await expect(taskFlowPage.successMessageLocator).not.toBeVisible();
    
    // And the user should remain on the creation form
    await expect(taskFlowPage.addTaskModalHeadingLocator).toBeVisible();
  });

  test('[DEMO-2523][TS6-TC1] Verify Task Creation Fails with Invalid Assignee Domain', async () => {
    // Given assignee with non-SCIB domain
    const taskDataWithInvalidAssignee = testData.taskDataWithInvalidAssignee;
    
    // When the user submits with external domain assignee
    await taskCreationActions.createTaskWithAllFields(taskDataWithInvalidAssignee);

    // Then a validation error should be displayed for the assignee field
    await expect(taskFlowPage.assigneeErrorMessageLocator).toBeVisible();
    
    // And the error message should state "Assignee must be from SCIB Active Directory"
    const assigneeError = await taskFlowPage.getFieldErrorMessage(taskFlowPage.assigneeErrorMessageLocator);
    expect(assigneeError).toBe('Assignee must be from SCIB Active Directory');
    
    // And the task should not be created
    await expect(taskFlowPage.successMessageLocator).not.toBeVisible();
    
    // And the user should remain on the creation form
    await expect(taskFlowPage.addTaskModalHeadingLocator).toBeVisible();
  });

  test('[DEMO-2523][TS7-TC1] Verify Multiple Validation Errors with All Fields Missing', async () => {
    // Given all mandatory fields are empty
    // When the user clicks the Submit button
    await taskCreationActions.createTaskWithAllFieldsMissing();

    // Then validation errors should be displayed for all mandatory fields
    await expect(taskFlowPage.titleErrorMessageLocator).toBeVisible();
    await expect(taskFlowPage.descriptionErrorMessageLocator).toBeVisible();
    await expect(taskFlowPage.priorityErrorMessageLocator).toBeVisible();
    await expect(taskFlowPage.assigneeErrorMessageLocator).toBeVisible();
    
    // And the title error should state "Title is required"
    const titleError = await taskFlowPage.getFieldErrorMessage(taskFlowPage.titleErrorMessageLocator);
    expect(titleError).toBe('Title is required');
    
    // And the description error should state "Description is required"
    const descriptionError = await taskFlowPage.getFieldErrorMessage(taskFlowPage.descriptionErrorMessageLocator);
    expect(descriptionError).toBe('Description is required');
    
    // And the priority error should state "Priority is required"
    const priorityError = await taskFlowPage.getFieldErrorMessage(taskFlowPage.priorityErrorMessageLocator);
    expect(priorityError).toBe('Priority is required');
    
    // And the assignee error should state "Assignee is required"
    const assigneeError = await taskFlowPage.getFieldErrorMessage(taskFlowPage.assigneeErrorMessageLocator);
    expect(assigneeError).toBe('Assignee is required');
    
    // And the task should not be created
    await expect(taskFlowPage.successMessageLocator).not.toBeVisible();
    
    // And the user should remain on the creation form
    await expect(taskFlowPage.addTaskModalHeadingLocator).toBeVisible();
  });
});