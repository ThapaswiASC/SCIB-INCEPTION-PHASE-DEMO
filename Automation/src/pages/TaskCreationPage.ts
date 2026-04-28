import { Page, Locator } from '@playwright/test';

/**
 * Page Object Model for Task Creation Form
 * Contains all locators for the task creation page elements
 */
export class TaskCreationPage {
  readonly page: Page;
  
  // Form Fields
  readonly titleField: Locator;
  readonly descriptionField: Locator;
  readonly priorityDropdown: Locator;
  readonly assigneeField: Locator;
  readonly submitButton: Locator;
  readonly cancelButton: Locator;
  
  // Validation Error Messages
  readonly titleErrorMessage: Locator;
  readonly descriptionErrorMessage: Locator;
  readonly priorityErrorMessage: Locator;
  readonly assigneeErrorMessage: Locator;
  readonly generalErrorMessage: Locator;
  
  // Success Elements
  readonly successMessage: Locator;
  readonly taskIdDisplay: Locator;
  
  // Form State Elements
  readonly formContainer: Locator;
  readonly loadingSpinner: Locator;
  readonly characterCounter: Locator;
  
  // Priority Options
  readonly priorityHigh: Locator;
  readonly priorityMedium: Locator;
  readonly priorityLow: Locator;
  readonly priorityCritical: Locator;

  constructor(page: Page) {
    this.page = page;
    
    // Form Fields
    this.titleField = page.locator('[data-testid="task-title-input"]');
    this.descriptionField = page.locator('[data-testid="task-description-textarea"]');
    this.priorityDropdown = page.locator('[data-testid="task-priority-select"]');
    this.assigneeField = page.locator('[data-testid="task-assignee-input"]');
    this.submitButton = page.locator('[data-testid="task-submit-button"]');
    this.cancelButton = page.locator('[data-testid="task-cancel-button"]');
    
    // Validation Error Messages
    this.titleErrorMessage = page.locator('[data-testid="title-error-message"]');
    this.descriptionErrorMessage = page.locator('[data-testid="description-error-message"]');
    this.priorityErrorMessage = page.locator('[data-testid="priority-error-message"]');
    this.assigneeErrorMessage = page.locator('[data-testid="assignee-error-message"]');
    this.generalErrorMessage = page.locator('[data-testid="general-error-message"]');
    
    // Success Elements
    this.successMessage = page.locator('[data-testid="success-message"]');
    this.taskIdDisplay = page.locator('[data-testid="task-id-display"]');
    
    // Form State Elements
    this.formContainer = page.locator('[data-testid="task-creation-form"]');
    this.loadingSpinner = page.locator('[data-testid="loading-spinner"]');
    this.characterCounter = page.locator('[data-testid="description-character-counter"]');
    
    // Priority Options
    this.priorityHigh = page.locator('[data-testid="priority-option-high"]');
    this.priorityMedium = page.locator('[data-testid="priority-option-medium"]');
    this.priorityLow = page.locator('[data-testid="priority-option-low"]');
    this.priorityCritical = page.locator('[data-testid="priority-option-critical"]');
  }

  /**
   * Navigate to the task creation page
   */
  async navigateToTaskCreation(): Promise<void> {
    await this.page.goto('/tasks/create');
    await this.formContainer.waitFor({ state: 'visible' });
  }

  /**
   * Check if the form is visible and ready for interaction
   */
  async isFormReady(): Promise<boolean> {
    return await this.formContainer.isVisible() && 
           await this.submitButton.isEnabled();
  }

  /**
   * Get all visible error messages
   */
  async getVisibleErrorMessages(): Promise<string[]> {
    const errorMessages: string[] = [];
    const errorLocators = [
      this.titleErrorMessage,
      this.descriptionErrorMessage,
      this.priorityErrorMessage,
      this.assigneeErrorMessage,
      this.generalErrorMessage
    ];

    for (const locator of errorLocators) {
      if (await locator.isVisible()) {
        errorMessages.push(await locator.textContent() || '');
      }
    }

    return errorMessages;
  }

  /**
   * Check if success message is displayed
   */
  async isSuccessMessageVisible(): Promise<boolean> {
    return await this.successMessage.isVisible();
  }

  /**
   * Get the generated task ID from success message
   */
  async getGeneratedTaskId(): Promise<string | null> {
    if (await this.taskIdDisplay.isVisible()) {
      return await this.taskIdDisplay.textContent();
    }
    return null;
  }

  /**
   * Get current character count for description field
   */
  async getDescriptionCharacterCount(): Promise<number> {
    if (await this.characterCounter.isVisible()) {
      const text = await this.characterCounter.textContent();
      const match = text?.match(/(\d+)/);
      return match ? parseInt(match[1]) : 0;
    }
    return 0;
  }
}