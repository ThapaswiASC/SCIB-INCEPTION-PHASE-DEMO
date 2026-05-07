import { Page } from '@playwright/test';
import { BulkTaskCreationPage } from '../pages/BulkTaskCreationPage';
import * as path from 'path';
import * as fs from 'fs';

export class BulkTaskCreationActions {
  private bulkTaskCreationPage: BulkTaskCreationPage;

  constructor(private readonly page: Page) {
    this.bulkTaskCreationPage = new BulkTaskCreationPage(page);
  }

  /**
   * Navigate to bulk task creation page
   */
  async navigateToBulkTaskCreation(): Promise<void> {
    await this.bulkTaskCreationPage.navigateToBulkTaskCreation();
  }

  /**
   * Create a temporary CSV file with provided data
   */
  private async createTempCSVFile(fileName: string, content: string): Promise<string> {
    const tempDir = path.join(__dirname, '../temp');
    if (!fs.existsSync(tempDir)) {
      fs.mkdirSync(tempDir, { recursive: true });
    }
    
    const filePath = path.join(tempDir, fileName);
    fs.writeFileSync(filePath, content);
    return filePath;
  }

  /**
   * Upload and submit a valid CSV file
   */
  async uploadValidCSVFile(csvContent: string, fileName: string = 'bulk_tasks_valid.csv'): Promise<void> {
    const filePath = await this.createTempCSVFile(fileName, csvContent);
    await this.bulkTaskCreationPage.uploadCSVFile(filePath);
    await this.bulkTaskCreationPage.submitFile();
  }

  /**
   * Upload and submit a mixed valid/invalid CSV file
   */
  async uploadMixedCSVFile(csvContent: string, fileName: string = 'bulk_tasks_mixed.csv'): Promise<void> {
    const filePath = await this.createTempCSVFile(fileName, csvContent);
    await this.bulkTaskCreationPage.uploadCSVFile(filePath);
    await this.bulkTaskCreationPage.submitFile();
  }

  /**
   * Upload and submit a single task CSV file
   */
  async uploadSingleTaskCSVFile(csvContent: string, fileName: string = 'bulk_tasks_single.csv'): Promise<void> {
    const filePath = await this.createTempCSVFile(fileName, csvContent);
    await this.bulkTaskCreationPage.uploadCSVFile(filePath);
    await this.bulkTaskCreationPage.submitFile();
  }

  /**
   * Upload and submit a headers-only CSV file
   */
  async uploadHeadersOnlyCSVFile(csvContent: string, fileName: string = 'bulk_tasks_headers_only.csv'): Promise<void> {
    const filePath = await this.createTempCSVFile(fileName, csvContent);
    await this.bulkTaskCreationPage.uploadCSVFile(filePath);
    await this.bulkTaskCreationPage.submitFile();
  }

  /**
   * Upload and submit an empty CSV file
   */
  async uploadEmptyCSVFile(fileName: string = 'bulk_tasks_empty.csv'): Promise<void> {
    const filePath = await this.createTempCSVFile(fileName, '');
    await this.bulkTaskCreationPage.uploadCSVFile(filePath);
    await this.bulkTaskCreationPage.submitFile();
  }

  /**
   * Upload and submit a large CSV file
   */
  async uploadLargeCSVFile(csvContent: string, fileName: string = 'bulk_tasks_large.csv'): Promise<void> {
    const filePath = await this.createTempCSVFile(fileName, csvContent);
    await this.bulkTaskCreationPage.uploadCSVFile(filePath);
    await this.bulkTaskCreationPage.submitFile();
  }

  /**
   * Upload and submit a CSV file with invalid data formats
   */
  async uploadInvalidDataCSVFile(csvContent: string, fileName: string = 'bulk_tasks_invalid.csv'): Promise<void> {
    const filePath = await this.createTempCSVFile(fileName, csvContent);
    await this.bulkTaskCreationPage.uploadCSVFile(filePath);
    await this.bulkTaskCreationPage.submitFile();
  }

  /**
   * Attempt to upload a file near maximum size limit
   */
  async uploadMaxSizeCSVFile(csvContent: string, fileName: string = 'bulk_tasks_max_size.csv'): Promise<void> {
    const filePath = await this.createTempCSVFile(fileName, csvContent);
    await this.bulkTaskCreationPage.uploadCSVFile(filePath);
  }

  /**
   * Wait for bulk creation process to complete
   */
  async waitForBulkCreationComplete(): Promise<void> {
    await this.bulkTaskCreationPage.waitForProcessingComplete();
  }

  /**
   * Verify successful bulk creation
   */
  async verifySuccessfulBulkCreation(expectedCount: number): Promise<boolean> {
    const isSuccessVisible = await this.bulkTaskCreationPage.isSuccessMessageVisible();
    if (!isSuccessVisible) return false;

    const successCount = await this.bulkTaskCreationPage.getSuccessCount();
    return successCount === expectedCount;
  }

  /**
   * Verify partial success with errors
   */
  async verifyPartialSuccess(expectedSuccessCount: number, expectedErrorCount: number): Promise<boolean> {
    const successCount = await this.bulkTaskCreationPage.getSuccessCount();
    const errorCount = await this.bulkTaskCreationPage.getErrorCount();
    
    return successCount === expectedSuccessCount && errorCount === expectedErrorCount;
  }

  /**
   * Get detailed error report
   */
  async getErrorReport(): Promise<string[]> {
    if (!await this.bulkTaskCreationPage.isErrorReportAvailable()) {
      return [];
    }

    // Extract error details from error report table
    const errorRows = await this.bulkTaskCreationPage.errorRowsLocator.allTextContents();
    return errorRows;
  }

  /**
   * Download error report file
   */
  async downloadErrorReport(): Promise<void> {
    await this.bulkTaskCreationPage.downloadErrorReport();
  }

  /**
   * Navigate to view created tasks
   */
  async viewCreatedTasks(): Promise<void> {
    await this.bulkTaskCreationPage.viewCreatedTasks();
  }

  /**
   * Clear uploaded file and reset form
   */
  async clearFileAndReset(): Promise<void> {
    await this.bulkTaskCreationPage.clearFile();
  }

  /**
   * Verify file size validation
   */
  async verifyFileSizeValidation(): Promise<boolean> {
    return await this.bulkTaskCreationPage.isErrorMessageVisible();
  }

  /**
   * Verify progress indicators during processing
   */
  async verifyProgressIndicators(): Promise<boolean> {
    return await this.bulkTaskCreationPage.isProgressBarVisible();
  }

  /**
   * Cleanup temporary files
   */
  async cleanup(): Promise<void> {
    const tempDir = path.join(__dirname, '../temp');
    if (fs.existsSync(tempDir)) {
      fs.rmSync(tempDir, { recursive: true, force: true });
    }
  }
}