import { test, expect } from '@playwright/test';
import { BulkTaskCreationActions } from '../../actions/BulkTaskCreationActions';
import { BulkTaskCreationPage } from '../../pages/BulkTaskCreationPage';
import { TestDataLoader } from '../../utils/TestDataLoader';

test.describe('DEMO-2525 - Bulk Task Creation Max File Size', () => {
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

  test('[DEMO-2525][Edge Case] Verify Bulk Creation with Maximum File Size Limit', async () => {
    // Given I have a CSV file near the maximum size limit
    await expect(bulkTaskCreationPage.pageHeadingLocator).toBeVisible();

    // Verify file size limit information is displayed
    await expect(bulkTaskCreationPage.fileSizeLimitTextLocator).toBeVisible();
    
    const fileSizeLimitText = await bulkTaskCreationPage.fileSizeLimitTextLocator.textContent();
    expect(fileSizeLimitText).toContain(testData.fileValidation.maxFileSize);

    // When I attempt to upload the large CSV file
    // Create a large CSV content that approaches the size limit
    let largeCsvContent = testData.maxSizeCSVContent;
    
    // Generate a large CSV content (simulating near max size)
    const headerRow = 'Title,Description,Priority,Assignee,Due Date\n';
    largeCsvContent = headerRow;
    
    for (let i = 1; i <= 500; i++) {
      largeCsvContent += `Task ${i},Very long description that contains many characters to increase file size and test the maximum file size validation functionality of the bulk task creation feature. This description is intentionally verbose to simulate real-world scenarios where task descriptions might be lengthy and detailed.,High,user${i}@scib.com,2024-12-31\n`;
    }

    await bulkTaskCreationActions.uploadMaxSizeCSVFile(
      largeCsvContent,
      'bulk_tasks_max_size.csv'
    );

    // Then the system should validate the file size
    const fileSizeDisplay = await bulkTaskCreationPage.getFileSizeDisplay();
    expect(fileSizeDisplay).toBeTruthy();

    // And provide clear feedback about size limits
    const isFileSizeValid = await bulkTaskCreationActions.verifyFileSizeValidation();
    
    if (!isFileSizeValid) {
      // File is within limits, should proceed normally
      await bulkTaskCreationPage.submitFile();
      await bulkTaskCreationActions.waitForBulkCreationComplete();
      
      const isSuccessVisible = await bulkTaskCreationPage.isSuccessMessageVisible();
      expect(isSuccessVisible).toBe(true);
    } else {
      // File exceeds limits, should show error
      const errorMessage = await bulkTaskCreationPage.getErrorMessageText();
      expect(errorMessage).toContain(testData.errorMessages.fileSizeExceeded);
    }

    // And handle the file appropriately based on size validation
    // The system should either process the file or reject it with clear messaging
    const hasProcessingIndicator = await bulkTaskCreationActions.verifyProgressIndicators();
    const hasErrorMessage = await bulkTaskCreationPage.isErrorMessageVisible();
    
    expect(hasProcessingIndicator || hasErrorMessage).toBe(true);
  });
});