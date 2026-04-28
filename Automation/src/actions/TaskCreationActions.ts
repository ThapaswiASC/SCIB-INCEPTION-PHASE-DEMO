import { Page, expect } from '@playwright/test';
import { TaskCreationPage } from '../pages/TaskCreationPage';

/**
 * Action class for Task Creation workflows
 * Contains reusable business flows - no assertions
 */
export class TaskCreationActions {
  readonly page: Page;
  readonly taskCreationPage: TaskCreationPage;

  constructor(page: Page) {
    this.page = page;
    this.taskCreationPage = new TaskCreationPage(page);
  }

  /**
   * Navigate to task creation form and wait for it to be ready
   */
  async navigateToTaskCreationForm(): Promise<void> {
    await this.taskCreationPage.navigateToTaskCreation();
    await this.taskCreationPage.waitForFormReady();
  }

  /**
   * Fill task title field
   */
  async fillTaskTitle(title: string): Promise<void> {
    await this.taskCreationPage.titleField.clear();
    await this.taskCreationPage.titleField.fill(title);
  }

  /**
   * Fill task description field
   */
  async fillTaskDescription(description: string): Promise<void> {
    await this.taskCreationPage.descriptionField.clear();
    await this.taskCreationPage.descriptionField.fill(description);
  }

  /**
   * Select priority from dropdown
   */
  async selectPriority(priority: string): Promise<void> {
    await this.taskCreationPage.priorityDropdown.click();
    await this.taskCreationPage.getPriorityOption(priority).click();
  }

  /**
   * Fill assignee field
   */
  async fillAssignee(assignee: string): Promise<void> {
    await this.taskCreationPage.assigneeField.clear();
    await this.taskCreationPage.assigneeField.fill(assignee);
  }

  /**
   * Submit the task creation form
   */
  async submitTaskForm(): Promise<void> {
    await this.taskCreationPage.submitButton.click();
  }

  /**
   * Cancel task creation
   */
  async cancelTaskCreation(): Promise<void> {
    await this.taskCreationPage.cancelButton.click();
  }

  /**
   * Fill complete task form with all mandatory fields
   */
  async fillCompleteTaskForm(taskData: {
    title: string;
    description: string;
    priority: string;
    assignee: string;
  }): Promise<void> {
    await this.fillTaskTitle(taskData.title);
    await this.fillTaskDescription(taskData.description);
    await this.selectPriority(taskData.priority);
    await this.fillAssignee(taskData.assignee);
  }

  /**
   * Create task with complete workflow
   */
  async createTask(taskData: {
    title: string;
    description: string;
    priority: string;
    assignee: string;
  }): Promise<void> {
    await this.navigateToTaskCreationForm();
    await this.fillCompleteTaskForm(taskData);
    await this.submitTaskForm();
  }

  /**
   * Clear all form fields
   */
  async clearAllFields(): Promise<void> {
    await this.taskCreationPage.titleField.clear();
    await this.taskCreationPage.descriptionField.clear();
    await this.taskCreationPage.assigneeField.clear();
    // Priority dropdown will reset to default when clicked elsewhere
    await this.taskCreationPage.formContainer.click();
  }

  /**
   * Get current form field values
   */
  async getCurrentFormValues(): Promise<{
    title: string;
    description: string;
    assignee: string;
  }> {
    return {
      title: await this.taskCreationPage.titleField.inputValue(),
      description: await this.taskCreationPage.descriptionField.inputValue(),
      assignee: await this.taskCreationPage.assigneeField.inputValue()
    };
  }

  /**
   * Generate long description text exceeding character limit
   */
  generateLongDescription(length: number = 2500): string {
    const baseText = "This is a comprehensive task description that includes detailed requirements, implementation guidelines, testing criteria, and acceptance conditions. ";
    return baseText.repeat(Math.ceil(length / baseText.length)).substring(0, length);
  }

  /**
   * Attempt to select invalid priority (for testing validation)
   */
  async attemptInvalidPrioritySelection(invalidPriority: string): Promise<void> {
    await this.taskCreationPage.priorityDropdown.click();
    // Try to find the invalid option, if it doesn't exist, the test will handle it
    const invalidOption = this.page.locator(`[data-testid="priority-option-${invalidPriority.toLowerCase()}"]`);
    if (await invalidOption.isVisible()) {
      await invalidOption.click();
    }
  }

  /**
   * Wait for form submission to complete
   */
  async waitForFormSubmission(): Promise<void> {
    // Wait for either success message or error messages to appear
    await Promise.race([
      this.taskCreationPage.successMessage.waitFor({ state: 'visible', timeout: 10000 }),
      this.taskCreationPage.generalErrorMessage.waitFor({ state: 'visible', timeout: 10000 }),
      this.taskCreationPage.titleErrorMessage.waitFor({ state: 'visible', timeout: 10000 })
    ]);
  }
}