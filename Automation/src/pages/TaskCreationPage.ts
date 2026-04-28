import { Locator, Page } from '@playwright/test';

/**
 * Page Object Model for Task Creation Form
 * Contains only locators and element definitions - no business logic
 */
export class TaskCreationPage {
  readonly page: Page;
  
  // Form Elements
  readonly titleField: Locator;
  readonly descriptionField: Locator;
  readonly priorityDropdown: Locator;
  readonly assigneeField: Locator;
  readonly submitButton: Locator;
  readonly cancelButton: Locator;
  
  // Validation Elements
  readonly titleErrorMessage: Locator;
  readonly descriptionErrorMessage: Locator;
  readonly priorityErrorMessage: Locator;
  readonly assigneeErrorMessage: Locator;
  readonly generalErrorMessage: Locator;
  
  // Success Elements
  readonly successMessage: Locator;
  readonly taskIdDisplay: Locator;
  
  // Priority Options
  readonly priorityHigh: Locator;
  readonly priorityMedium: Locator;
  readonly priorityLow: Locator;
  readonly priorityCritical: Locator;
  
  // Character Counter
  readonly descriptionCharacterCounter: Locator;
  
  // Form State Elements
  readonly formContainer: Locator;
  readonly loadingSpinner: Locator;

  constructor(page: Page) {
    this.page = page;
    
    // Form Elements
    this.titleField = page.locator('[data-testid="task-title-input"]');
    this.descriptionField = page.locator('[data-testid="task-description-textarea"]');
    this.priorityDropdown = page.locator('[data-testid="task-priority-select"]');
    this.assigneeField = page.locator('[data-testid="task-assignee-input"]');
    this.submitButton = page.locator('[data-testid="task-submit-button"]');
    this.cancelButton = page.locator('[data-testid="task-cancel-button"]');
    
    // Validation Elements
    this.titleErrorMessage = page.locator('[data-testid="title-error-message"]');
    this.descriptionErrorMessage = page.locator('[data-testid="description-error-message"]');
    this.priorityErrorMessage = page.locator('[data-testid="priority-error-message"]');
    this.assigneeErrorMessage = page.locator('[data-testid="assignee-error-message"]');
    this.generalErrorMessage = page.locator('[data-testid="general-error-message"]');
    
    // Success Elements
    this.successMessage = page.locator('[data-testid="success-message"]');
    this.taskIdDisplay = page.locator('[data-testid="task-id-display"]');
    
    // Priority Options
    this.priorityHigh = page.locator('[data-testid="priority-option-high"]');
    this.priorityMedium = page.locator('[data-testid="priority-option-medium"]');
    this.priorityLow = page.locator('[data-testid="priority-option-low"]');
    this.priorityCritical = page.locator('[data-testid="priority-option-critical"]');
    
    // Character Counter
    this.descriptionCharacterCounter = page.locator('[data-testid="description-char-counter"]');
    
    // Form State Elements
    this.formContainer = page.locator('[data-testid="task-creation-form"]');
    this.loadingSpinner = page.locator('[data-testid="loading-spinner"]');
  }

  /**
   * Navigate to task creation page
   */
  async navigateToTaskCreation(): Promise<void> {
    await this.page.goto('/tasks/create');
    await this.formContainer.waitFor({ state: 'visible' });
  }

  /**
   * Get priority option by value
   */
  getPriorityOption(priority: string): Locator {
    return this.page.locator(`[data-testid="priority-option-${priority.toLowerCase()}"]`);
  }

  /**
   * Check if form is in loading state
   */
  async isFormLoading(): Promise<boolean> {
    return await this.loadingSpinner.isVisible();
  }

  /**
   * Wait for form to be ready
   */
  async waitForFormReady(): Promise<void> {
    await this.formContainer.waitFor({ state: 'visible' });
    await this.page.waitForLoadState('networkidle');
  }
}