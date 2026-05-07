import { test, expect } from '@playwright/test';
import { BulkTaskCreationActions } from '../../actions/BulkTaskCreationActions';
import { BulkTaskCreationPage } from '../../pages/BulkTaskCreationPage';
import { TestDataLoader } from '../../utils/TestDataLoader';

test.describe('DEMO-2525 - Bulk Task Creation Invalid Data', () => {
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

  test('[DEMO-2525][AC2] Verify Error Handling for CSV with Invalid Data Formats', async () => {
    // Given I have a CSV file with invalid data formats
    await expect(bulkTaskCreationPage.pageHeadingLocator).toBeVisible();

    // When I upload the CSV file
    await bulkTaskCreationActions.uploadInvalidDataCSVFile(
      testData.invalidDataCSVContent,
      'bulk_tasks_invalid_data.csv'
    );

    // And the system validates the data
    await bulkTaskCreationActions.waitForBulkCreationComplete();

    // Then specific error messages should be displayed for each invalid entry
    const isErrorReportAvailable = await bulkTaskCreationPage.isErrorReportAvailable();
    expect(isErrorReportAvailable).toBe(true);

    const errorReport = await bulkTaskCreationActions.getErrorReport();
    expect(errorReport.length).toBeGreaterThan(0);

    // And no invalid tasks should be created
    const successCount = await bulkTaskCreationPage.getSuccessCount();
    expect(successCount).toBe(testData.expectedResults.invalidDataCSV.successCount);

    const errorCount = await bulkTaskCreationPage.getErrorCount();
    expect(errorCount).toBe(testData.expectedResults.invalidDataCSV.errorCount);

    // And I should receive guidance on how to correct the errors
    const isErrorMessageVisible = await bulkTaskCreationPage.isErrorMessageVisible();
    expect(isErrorMessageVisible).toBe(true);

    const errorMessage = await bulkTaskCreationPage.getErrorMessageText();
    expect(errorMessage).toContain(testData.errorMessages.invalidData);

    // Verify error report contains specific validation errors
    const errorDetails = await bulkTaskCreationActions.getErrorReport();
    expect(errorDetails.some(error => error.includes('invalid'))).toBe(true);

    // Verify download error report functionality is available
    await expect(bulkTaskCreationPage.downloadErrorReportButtonLocator).toBeVisible();
  });
});