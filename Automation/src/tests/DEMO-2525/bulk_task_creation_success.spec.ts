import { test, expect } from '@playwright/test';
import { BulkTaskCreationActions } from '../../actions/BulkTaskCreationActions';
import { BulkTaskCreationPage } from '../../pages/BulkTaskCreationPage';
import { TestDataLoader } from '../../utils/TestDataLoader';

test.describe('DEMO-2525 - Bulk Task Creation Success Scenarios', () => {
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

  test('[DEMO-2525][AC1] Verify Successful Bulk Task Creation with Valid CSV File', async () => {
    // Given I have a valid CSV file with task data
    await expect(bulkTaskCreationPage.pageHeadingLocator).toBeVisible();
    await expect(bulkTaskCreationPage.fileInputLocator).toBeVisible();

    // When I navigate to the bulk task creation page
    const isPageLoaded = await bulkTaskCreationPage.isBulkTaskCreationPageLoaded();
    expect(isPageLoaded).toBe(true);

    // And I upload the CSV file "bulk_tasks_valid.csv"
    await bulkTaskCreationActions.uploadValidCSVFile(
      testData.validCSVContent,
      'bulk_tasks_valid.csv'
    );

    // And I submit the file
    // (File submission is handled in uploadValidCSVFile method)

    // Then all tasks should be created successfully
    await bulkTaskCreationActions.waitForBulkCreationComplete();
    
    const isSuccessful = await bulkTaskCreationActions.verifySuccessfulBulkCreation(
      testData.expectedResults.validCSV.successCount
    );
    expect(isSuccessful).toBe(true);

    // And I should see a success confirmation message
    const isSuccessMessageVisible = await bulkTaskCreationPage.isSuccessMessageVisible();
    expect(isSuccessMessageVisible).toBe(true);

    const successMessage = await bulkTaskCreationPage.getSuccessMessageText();
    expect(successMessage).toContain(testData.successMessages.allTasksCreated);

    // And all tasks should be accessible in the project
    const successCount = await bulkTaskCreationPage.getSuccessCount();
    expect(successCount).toBe(testData.expectedResults.validCSV.successCount);

    const totalCount = await bulkTaskCreationPage.getTotalTasksCount();
    expect(totalCount).toBe(testData.expectedResults.validCSV.totalCount);
  });
});