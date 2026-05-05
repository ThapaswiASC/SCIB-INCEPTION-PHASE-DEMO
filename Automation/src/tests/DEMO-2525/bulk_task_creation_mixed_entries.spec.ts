import { test, expect } from '@playwright/test';
import { BulkTaskCreationActions } from '../../actions/BulkTaskCreationActions';
import { BulkTaskCreationPage } from '../../pages/BulkTaskCreationPage';
import { TestDataLoader } from '../../utils/TestDataLoader';

test.describe('DEMO-2525 - Bulk Task Creation Mixed Entries', () => {
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

  test('[DEMO-2525][AC2] Verify Partial Success with Mixed Valid and Invalid CSV Entries', async () => {
    // Given I have a CSV file with both valid and invalid entries
    await expect(bulkTaskCreationPage.pageHeadingLocator).toBeVisible();

    // When I upload the mixed CSV file
    await bulkTaskCreationActions.uploadMixedCSVFile(
      testData.mixedValidInvalidCSVContent,
      'bulk_tasks_mixed.csv'
    );

    // And I submit for bulk creation
    // (File submission is handled in uploadMixedCSVFile method)

    // Then valid tasks should be created successfully
    await bulkTaskCreationActions.waitForBulkCreationComplete();

    const isPartialSuccess = await bulkTaskCreationActions.verifyPartialSuccess(
      testData.expectedResults.mixedCSV.successCount,
      testData.expectedResults.mixedCSV.errorCount
    );
    expect(isPartialSuccess).toBe(true);

    // And invalid entries should be reported with specific errors
    const isErrorReportAvailable = await bulkTaskCreationPage.isErrorReportAvailable();
    expect(isErrorReportAvailable).toBe(true);

    const errorReport = await bulkTaskCreationActions.getErrorReport();
    expect(errorReport.length).toBeGreaterThan(0);

    // And I should receive a detailed error report
    const errorCount = await bulkTaskCreationPage.getErrorCount();
    expect(errorCount).toBe(testData.expectedResults.mixedCSV.errorCount);

    const successCount = await bulkTaskCreationPage.getSuccessCount();
    expect(successCount).toBe(testData.expectedResults.mixedCSV.successCount);

    // Verify partial success message
    const successMessage = await bulkTaskCreationPage.getSuccessMessageText();
    expect(successMessage).toContain(testData.successMessages.partialSuccess);
  });
});