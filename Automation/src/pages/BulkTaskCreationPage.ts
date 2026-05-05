import { Page, Locator } from '@playwright/test';

export class BulkTaskCreationPage {
  constructor(private readonly page: Page) {}

  // Navigation
  readonly bulkCreationNavLinkLocator: Locator = this.page.getByRole('link', { name: 'Bulk Creation' });
  readonly homeNavLinkLocator: Locator = this.page.getByRole('link', { name: 'Home' });
  readonly projectsNavLinkLocator: Locator = this.page.getByRole('link', { name: 'Projects' });

  // Header / Top Bar
  readonly pageHeadingLocator: Locator = this.page.getByRole('heading', { name: 'Bulk Task Creation' });
  readonly breadcrumbLocator: Locator = this.page.getByRole('navigation', { name: 'breadcrumb' });

  // Main Content - File Upload Section
  readonly uploadSectionHeadingLocator: Locator = this.page.getByRole('heading', { name: 'Upload CSV File' });
  readonly fileInputLocator: Locator = this.page.getByLabel('Select CSV file');
  readonly uploadButtonLocator: Locator = this.page.getByRole('button', { name: 'Upload File' });
  readonly dragDropAreaLocator: Locator = this.page.getByTestId('file-drop-zone');
  readonly fileFormatHelpTextLocator: Locator = this.page.getByText('Accepted formats: CSV files only');
  readonly maxFileSizeTextLocator: Locator = this.page.getByText('Maximum file size');

  // File Validation Section
  readonly validationSectionHeadingLocator: Locator = this.page.getByRole('heading', { name: 'File Validation' });
  readonly validationStatusLocator: Locator = this.page.getByTestId('validation-status');
  readonly progressBarLocator: Locator = this.page.getByRole('progressbar');
  readonly progressPercentageLocator: Locator = this.page.getByTestId('progress-percentage');

  // Preview Section
  readonly previewSectionHeadingLocator: Locator = this.page.getByRole('heading', { name: 'Preview Tasks' });
  readonly previewTableLocator: Locator = this.page.getByRole('table');
  readonly taskRowsLocator: Locator = this.page.getByRole('row');
  readonly taskNameColumnLocator: Locator = this.page.getByRole('columnheader', { name: 'Task Name' });
  readonly descriptionColumnLocator: Locator = this.page.getByRole('columnheader', { name: 'Description' });
  readonly priorityColumnLocator: Locator = this.page.getByRole('columnheader', { name: 'Priority' });
  readonly statusColumnLocator: Locator = this.page.getByRole('columnheader', { name: 'Status' });

  // Error Reporting Section
  readonly errorSectionHeadingLocator: Locator = this.page.getByRole('heading', { name: 'Validation Errors' });
  readonly errorListLocator: Locator = this.page.getByTestId('error-list');
  readonly errorItemsLocator: Locator = this.page.getByTestId('error-item');
  readonly downloadErrorReportButtonLocator: Locator = this.page.getByRole('button', { name: 'Download Error Report' });

  // Action Buttons
  readonly submitButtonLocator: Locator = this.page.getByRole('button', { name: 'Create Tasks' });
  readonly cancelButtonLocator: Locator = this.page.getByRole('button', { name: 'Cancel' });
  readonly resetButtonLocator: Locator = this.page.getByRole('button', { name: 'Reset Form' });
  readonly downloadTemplateButtonLocator: Locator = this.page.getByRole('button', { name: 'Download Template' });

  // Success/Confirmation Section
  readonly successMessageLocator: Locator = this.page.getByRole('alert');
  readonly successHeadingLocator: Locator = this.page.getByRole('heading', { name: 'Tasks Created Successfully' });
  readonly createdTasksCountLocator: Locator = this.page.getByTestId('created-tasks-count');
  readonly viewTasksButtonLocator: Locator = this.page.getByRole('button', { name: 'View Created Tasks' });

  // Help and Guidance Section
  readonly helpSectionHeadingLocator: Locator = this.page.getByRole('heading', { name: 'CSV Format Guidelines' });
  readonly formatExampleLocator: Locator = this.page.getByTestId('format-example');
  readonly requiredFieldsListLocator: Locator = this.page.getByTestId('required-fields');
  readonly formatGuidelinesTextLocator: Locator = this.page.getByText('CSV format requirements');

  // Modals
  readonly confirmationModalLocator: Locator = this.page.getByRole('dialog', { name: 'Confirm Bulk Creation' });
  readonly modalConfirmButtonLocator: Locator = this.page.getByRole('button', { name: 'Confirm' });
  readonly modalCancelButtonLocator: Locator = this.page.getByRole('button', { name: 'Cancel' });
  readonly modalCloseButtonLocator: Locator = this.page.getByRole('button', { name: 'Close' });

  // Loading States
  readonly loadingSpinnerLocator: Locator = this.page.getByTestId('loading-spinner');
  readonly processingMessageLocator: Locator = this.page.getByText('Processing file');
  readonly uploadingMessageLocator: Locator = this.page.getByText('Uploading file');

  // Footer
  readonly footerLocator: Locator = this.page.getByRole('contentinfo');
  readonly supportLinkLocator: Locator = this.page.getByRole('link', { name: 'Support' });
  readonly documentationLinkLocator: Locator = this.page.getByRole('link', { name: 'Documentation' });
}