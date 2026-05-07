import { test, expect } from '@playwright/test';
import { BulkTaskCreationActions } from '../../actions/BulkTaskCreationActions';
import { BulkTaskCreationPage } from '../../pages/BulkTaskCreationPage';
import { TestDataLoader } from '../../utils/TestDataLoader';

test.describe('DEMO-2525 - Bulk Task Creation Headers Only', () => {
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

  test('[DEMO-2525][AC3] Verify Handling of CSV File with Only Headers', async () => {
    // Given I have a CSV file with only headers
    await expect(bulkTaskCreationPage.pageHeadingLocator).toBeVisible();

    // When I upload the headers-only CSV file
    await bulkTaskCreationActions.uploadHeadersOnlyCSVFile(
      testData.headersOnlyCSVContent,
      'bulk_tasks_headers_only.csv'
    );

    // And the system processes the file
    await bulkTaskCreationActions.waitForBulkCreationComplete();

    // Then the system should recognize the headers
    // This is typically indicated by no format errors being shown
    const isFormatError = await bulkTaskCreationPage.isErrorMessageVisible();
    
    if (isFormatError) {
      const errorMessage = await bulkTaskCreationPage.getErrorMessageText();
      // Should not be a format error, but rather a "no data" message
      expect(errorMessage).not.toContain(testData.errorMessages.invalidFormat);
    }

    // And display a message about missing data rows
    const errorMessage = await bulkTaskCreationPage.getErrorMessageText();
    expect(errorMessage).toContain(testData.errorMessages.headersOnly);

    // And provide an example of proper CSV format
    await expect(bulkTaskCreationPage.exampleFormatLinkLocator).toBeVisible();

    // Verify no tasks were created
    const successCount = await bulkTaskCreationPage.getSuccessCount();
    expect(successCount).toBe(0);

    const totalCount = await bulkTaskCreationPage.getTotalTasksCount();
    expect(totalCount).toBe(0);
  });
});