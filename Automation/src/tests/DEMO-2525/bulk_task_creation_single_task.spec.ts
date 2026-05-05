import { test, expect } from '@playwright/test';
import { BulkTaskCreationActions } from '../../actions/BulkTaskCreationActions';
import { BulkTaskCreationPage } from '../../pages/BulkTaskCreationPage';
import { TestDataLoader } from '../../utils/TestDataLoader';

test.describe('DEMO-2525 - Bulk Task Creation Single Task', () => {
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

  test('[DEMO-2525][Boundary] Verify Bulk Creation with Single Task CSV', async () => {
    // Given I have a CSV file with one valid task
    await expect(bulkTaskCreationPage.pageHeadingLocator).toBeVisible();

    // When I upload the single-task CSV file
    await bulkTaskCreationActions.uploadSingleTaskCSVFile(
      testData.singleTaskCSVContent,
      'bulk_tasks_single.csv'
    );

    // And I submit for bulk creation
    // (File submission is handled in uploadSingleTaskCSVFile method)

    // Then the single task should be created successfully
    await bulkTaskCreationActions.waitForBulkCreationComplete();

    const isSuccessful = await bulkTaskCreationActions.verifySuccessfulBulkCreation(
      testData.expectedResults.singleTaskCSV.successCount
    );
    expect(isSuccessful).toBe(true);

    // And I should receive appropriate success confirmation
    const isSuccessMessageVisible = await bulkTaskCreationPage.isSuccessMessageVisible();
    expect(isSuccessMessageVisible).toBe(true);

    const successMessage = await bulkTaskCreationPage.getSuccessMessageText();
    expect(successMessage).toContain(testData.successMessages.singleTaskCreated);

    // And the task should contain all data from the CSV file
    const successCount = await bulkTaskCreationPage.getSuccessCount();
    expect(successCount).toBe(1);

    const totalCount = await bulkTaskCreationPage.getTotalTasksCount();
    expect(totalCount).toBe(1);

    const errorCount = await bulkTaskCreationPage.getErrorCount();
    expect(errorCount).toBe(0);
  });
});