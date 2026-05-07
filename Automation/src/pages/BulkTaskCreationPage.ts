import { Page, Locator } from '@playwright/test';

export class BulkTaskCreationPage {
  constructor(private readonly page: Page) {}

  // Navigation
  readonly bulkTaskCreationLinkLocator: Locator = this.page.getByRole('link', { name: 'Bulk Task Creation' });
  readonly backToProjectLinkLocator: Locator = this.page.getByRole('link', { name: 'Back to Project' });

  // Header / Top Bar
  readonly pageHeadingLocator: Locator = this.page.getByRole('heading', { name: 'Bulk Task Creation' });
  readonly instructionsHeadingLocator: Locator = this.page.getByRole('heading', { name: 'Instructions' });

  // Main Content - File Upload Section
  readonly fileUploadSectionHeadingLocator: Locator = this.page.getByRole('heading', { name: 'Upload CSV File' });
  readonly fileInputLocator: Locator = this.page.getByLabel('Choose CSV file');
  readonly fileInputAlternativeLocator: Locator = this.page.locator('input[type="file"]');
  readonly dragDropAreaLocator: Locator = this.page.getByText('Drag and drop your CSV file here or click to browse');
  readonly selectedFileNameLocator: Locator = this.page.locator('[data-testid="selected-file-name"]');
  readonly fileSizeDisplayLocator: Locator = this.page.locator('[data-testid="file-size-display"]');

  // File Upload Actions
  readonly uploadButtonLocator: Locator = this.page.getByRole('button', { name: 'Upload File' });
  readonly submitButtonLocator: Locator = this.page.getByRole('button', { name: 'Submit' });
  readonly cancelButtonLocator: Locator = this.page.getByRole('button', { name: 'Cancel' });
  readonly clearFileButtonLocator: Locator = this.page.getByRole('button', { name: 'Clear File' });

  // Progress and Status
  readonly progressBarLocator: Locator = this.page.getByRole('progressbar');
  readonly progressTextLocator: Locator = this.page.locator('[data-testid="progress-text"]');
  readonly processingStatusLocator: Locator = this.page.getByText('Processing your file');

  // Messages and Alerts
  readonly successMessageLocator: Locator = this.page.getByRole('alert').filter({ hasText: 'successfully' });
  readonly errorMessageLocator: Locator = this.page.getByRole('alert').filter({ hasText: 'error' });
  readonly warningMessageLocator: Locator = this.page.getByRole('alert').filter({ hasText: 'warning' });
  readonly validationMessageLocator: Locator = this.page.locator('[data-testid="validation-message"]');

  // File Format Information
  readonly formatRequirementsHeadingLocator: Locator = this.page.getByRole('heading', { name: 'File Format Requirements' });
  readonly downloadTemplateButtonLocator: Locator = this.page.getByRole('button', { name: 'Download Template' });
  readonly exampleFormatLinkLocator: Locator = this.page.getByRole('link', { name: 'View Example Format' });

  // Results Section
  readonly resultsHeadingLocator: Locator = this.page.getByRole('heading', { name: 'Results' });
  readonly successCountLocator: Locator = this.page.locator('[data-testid="success-count"]');
  readonly errorCountLocator: Locator = this.page.locator('[data-testid="error-count"]');
  readonly totalTasksCountLocator: Locator = this.page.locator('[data-testid="total-tasks-count"]');

  // Error Report
  readonly errorReportHeadingLocator: Locator = this.page.getByRole('heading', { name: 'Error Report' });
  readonly errorReportTableLocator: Locator = this.page.getByRole('table');
  readonly errorRowsLocator: Locator = this.page.getByRole('row').filter({ hasText: 'error' });
  readonly downloadErrorReportButtonLocator: Locator = this.page.getByRole('button', { name: 'Download Error Report' });

  // Task Preview
  readonly taskPreviewHeadingLocator: Locator = this.page.getByRole('heading', { name: 'Task Preview' });
  readonly previewTableLocator: Locator = this.page.locator('[data-testid="task-preview-table"]');
  readonly taskRowsLocator: Locator = this.page.getByRole('row');

  // File Validation
  readonly fileSizeLimitTextLocator: Locator = this.page.getByText('Maximum file size');
  readonly supportedFormatsTextLocator: Locator = this.page.getByText('Supported formats: CSV');
  readonly requiredHeadersTextLocator: Locator = this.page.getByText('Required headers');

  // Modals
  readonly confirmationModalLocator: Locator = this.page.getByRole('dialog');
  readonly confirmationHeadingLocator: Locator = this.page.getByRole('heading', { name: 'Confirm Bulk Creation' });
  readonly confirmModalButtonLocator: Locator = this.page.getByRole('button', { name: 'Confirm' });
  readonly cancelModalButtonLocator: Locator = this.page.getByRole('button', { name: 'Cancel' });

  // Loading States
  readonly loadingSpinnerLocator: Locator = this.page.locator('[data-testid="loading-spinner"]');
  readonly processingIndicatorLocator: Locator = this.page.getByText('Processing');

  // Footer Actions
  readonly viewCreatedTasksButtonLocator: Locator = this.page.getByRole('button', { name: 'View Created Tasks' });
  readonly createMoreTasksButtonLocator: Locator = this.page.getByRole('button', { name: 'Create More Tasks' });

  /**
   * Navigate to the bulk task creation page
   */
  async navigateToBulkTaskCreation(): Promise<void> {
    await this.page.goto('/bulk-task-creation');
    await this.page.waitForLoadState('networkidle');
  }

  /**
   * Check if the bulk task creation page is loaded
   */
  async isBulkTaskCreationPageLoaded(): Promise<boolean> {
    return await this.pageHeadingLocator.isVisible();
  }

  /**
   * Upload a CSV file
   */
  async uploadCSVFile(filePath: string): Promise<void> {
    await this.fileInputLocator.setInputFiles(filePath);
  }

  /**
   * Get selected file name
   */
  async getSelectedFileName(): Promise<string> {
    return await this.selectedFileNameLocator.textContent() || '';
  }

  /**
   * Get file size display
   */
  async getFileSizeDisplay(): Promise<string> {
    return await this.fileSizeDisplayLocator.textContent() || '';
  }

  /**
   * Submit the uploaded file for processing
   */
  async submitFile(): Promise<void> {
    await this.submitButtonLocator.click();
  }

  /**
   * Clear the selected file
   */
  async clearFile(): Promise<void> {
    await this.clearFileButtonLocator.click();
  }

  /**
   * Check if success message is visible
   */
  async isSuccessMessageVisible(): Promise<boolean> {
    return await this.successMessageLocator.isVisible();
  }

  /**
   * Get success message text
   */
  async getSuccessMessageText(): Promise<string> {
    return await this.successMessageLocator.textContent() || '';
  }

  /**
   * Check if error message is visible
   */
  async isErrorMessageVisible(): Promise<boolean> {
    return await this.errorMessageLocator.isVisible();
  }

  /**
   * Get error message text
   */
  async getErrorMessageText(): Promise<string> {
    return await this.errorMessageLocator.textContent() || '';
  }

  /**
   * Get success count from results
   */
  async getSuccessCount(): Promise<number> {
    const text = await this.successCountLocator.textContent() || '0';
    return parseInt(text.match(/\d+/)?.[0] || '0');
  }

  /**
   * Get error count from results
   */
  async getErrorCount(): Promise<number> {
    const text = await this.errorCountLocator.textContent() || '0';
    return parseInt(text.match(/\d+/)?.[0] || '0');
  }

  /**
   * Get total tasks count from results
   */
  async getTotalTasksCount(): Promise<number> {
    const text = await this.totalTasksCountLocator.textContent() || '0';
    return parseInt(text.match(/\d+/)?.[0] || '0');
  }

  /**
   * Check if progress bar is visible
   */
  async isProgressBarVisible(): Promise<boolean> {
    return await this.progressBarLocator.isVisible();
  }

  /**
   * Wait for processing to complete
   */
  async waitForProcessingComplete(): Promise<void> {
    await this.processingIndicatorLocator.waitFor({ state: 'hidden', timeout: 60000 });
  }

  /**
   * Check if error report is available
   */
  async isErrorReportAvailable(): Promise<boolean> {
    return await this.errorReportTableLocator.isVisible();
  }

  /**
   * Download error report
   */
  async downloadErrorReport(): Promise<void> {
    await this.downloadErrorReportButtonLocator.click();
  }

  /**
   * View created tasks
   */
  async viewCreatedTasks(): Promise<void> {
    await this.viewCreatedTasksButtonLocator.click();
  }
}