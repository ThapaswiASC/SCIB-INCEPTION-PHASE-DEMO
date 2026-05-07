import { test, expect } from '@playwright/test';
import { BulkTaskCreationActions } from '../../actions/BulkTaskCreationActions';
import { BulkTaskCreationPage } from '../../pages/BulkTaskCreationPage';
import { TestDataLoader } from '../../utils/TestDataLoader';

test.describe('DEMO-2525 - Bulk Task Creation Empty File', () => {
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

  test('[DEMO-2525][AC3] Verify Error Message for Empty CSV File Upload', async () => {
    // Given I have an empty CSV file
    await expect(bulkTaskCreationPage.pageHeadingLocator).toBeVisible();

    // When I upload the empty CSV file
    await bulkTaskCreationActions.uploadEmptyCSVFile('bulk_tasks_empty.csv');

    // And I attempt to submit the file
    // (File submission is handled in uploadEmptyCSVFile method)

    // Then an appropriate error message should be displayed
    const isErrorMessageVisible = await bulkTaskCreationPage.isErrorMessageVisible();
    expect(isErrorMessageVisible).toBe(true);

    const errorMessage = await bulkTaskCreationPage.getErrorMessageText();
    expect(errorMessage).toContain(testData.errorMessages.emptyFile);

    // And no tasks should be created
    const successCount = await bulkTaskCreationPage.getSuccessCount();
    expect(successCount).toBe(testData.expectedResults.emptyFile.successCount);

    const totalCount = await bulkTaskCreationPage.getTotalTasksCount();
    expect(totalCount).toBe(testData.expectedResults.emptyFile.totalCount);

    // And I should receive guidance on the required file format
    await expect(bulkTaskCreationPage.formatRequirementsHeadingLocator).toBeVisible();
    await expect(bulkTaskCreationPage.downloadTemplateButtonLocator).toBeVisible();
    await expect(bulkTaskCreationPage.exampleFormatLinkLocator).toBeVisible();

    // Verify supported formats information is displayed
    await expect(bulkTaskCreationPage.supportedFormatsTextLocator).toBeVisible();
    
    const supportedFormatsText = await bulkTaskCreationPage.supportedFormatsTextLocator.textContent();
    expect(supportedFormatsText).toContain('CSV');

    // Verify required headers information is displayed
    await expect(bulkTaskCreationPage.requiredHeadersTextLocator).toBeVisible();
  });
});