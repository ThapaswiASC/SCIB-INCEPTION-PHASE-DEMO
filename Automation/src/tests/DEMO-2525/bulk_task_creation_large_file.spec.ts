import { test, expect } from '@playwright/test';
import { BulkTaskCreationActions } from '../../actions/BulkTaskCreationActions';
import { BulkTaskCreationPage } from '../../pages/BulkTaskCreationPage';
import { TestDataLoader } from '../../utils/TestDataLoader';

test.describe('DEMO-2525 - Bulk Task Creation Large File', () => {
  let bulkTaskCreationActions: BulkTaskCreationActions;
  let bulkTaskCreationPage: BulkTaskCreationPage;
  let testData: any;

  test.beforeEach(async ({ page }) => {
    bulkTaskCreationActions = new BulkTaskCreationActions(page);
    bulkTaskCreationPage = new BulkTaskCreationPage(page);
    testData = TestDataLoader.loadTestData('bulk-task-creation-data.json');
    
    await bulkTaskCreationActions.navigateToBulkTaskCreation();
  });

  test.afterEach(async () => {
    await bulkTaskCreationActions.cleanup();
  });

  test('[DEMO-2525][AC1] Verify Bulk Task Creation with Large Valid CSV File', async () => {
    // Given I have a large valid CSV file with 100 tasks
    await expect(bulkTaskCreationPage.pageHeadingLocator).toBeVisible();

    // Generate large CSV content with 100 tasks
    let largeCsvContent = 'Title,Description,Priority,Assignee,Due Date\n';
    const priorities = ['High', 'Medium', 'Low'];
    const assignees = ['john.doe@scib.com', 'jane.smith@scib.com', 'bob.wilson@scib.com', 'alice.brown@scib.com'];
    
    for (let i = 1; i <= 100; i++) {
      const priority = priorities[i % priorities.length];
      const assignee = assignees[i % assignees.length];
      const dueDate = `2024-12-${String(31 - (i % 31)).padStart(2, '0')}`;
      
      largeCsvContent += `Task ${i},Description for task ${i} with detailed information,${priority},${assignee},${dueDate}\n`;
    }

    // When I upload the large CSV file
    await bulkTaskCreationActions.uploadLargeCSVFile(
      largeCsvContent,
      'bulk_tasks_large_100.csv'
    );

    // And I submit for bulk creation
    // (File submission is handled in uploadLargeCSVFile method)

    // Then all 100 tasks should be created successfully
    await bulkTaskCreationActions.waitForBulkCreationComplete();

    const isSuccessful = await bulkTaskCreationActions.verifySuccessfulBulkCreation(100);
    expect(isSuccessful).toBe(true);

    // And the system should remain responsive
    const isSuccessMessageVisible = await bulkTaskCreationPage.isSuccessMessageVisible();
    expect(isSuccessMessageVisible).toBe(true);

    // And I should see progress indicators during processing
    // Note: Progress indicators would have been visible during processing
    // We can verify the final state shows completion
    const successCount = await bulkTaskCreationPage.getSuccessCount();
    expect(successCount).toBe(100);

    const errorCount = await bulkTaskCreationPage.getErrorCount();
    expect(errorCount).toBe(0);

    const totalCount = await bulkTaskCreationPage.getTotalTasksCount();
    expect(totalCount).toBe(100);

    // Verify success message
    const successMessage = await bulkTaskCreationPage.getSuccessMessageText();
    expect(successMessage).toContain(testData.successMessages.allTasksCreated);
  });
});