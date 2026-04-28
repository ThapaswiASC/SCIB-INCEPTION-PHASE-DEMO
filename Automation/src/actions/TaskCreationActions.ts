import { Page, expect } from '@playwright/test';
import { TaskCreationPage } from '../pages/TaskCreationPage';

/**
 * Action class for Task Creation workflows
 * Contains reusable business logic flows without assertions
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
    await this.taskCreationPage.formContainer.waitFor({ state: 'visible' });
  }

  /**
   * Fill the title field with provided text
   */
  async fillTitle(title: string): Promise<void> {
    await this.taskCreationPage.titleField.clear();
    await this.taskCreationPage.titleField.fill(title);
  }

  /**
   * Fill the description field with provided text
   */
  async fillDescription(description: string): Promise<void> {
    await this.taskCreationPage.descriptionField.clear();
    await this.taskCreationPage.descriptionField.fill(description);
  }

  /**
   * Select priority from dropdown
   */
  async selectPriority(priority: string): Promise<void> {
    await this.taskCreationPage.priorityDropdown.click();
    
    switch (priority.toLowerCase()) {
      case 'high':
        await this.taskCreationPage.priorityHigh.click();
        break;
      case 'medium':
        await this.taskCreationPage.priorityMedium.click();
        break;
      case 'low':
        await this.taskCreationPage.priorityLow.click();
        break;
      case 'critical':
        await this.taskCreationPage.priorityCritical.click();
        break;
      default:
        // For invalid priority testing, try to type the value
        await this.taskCreationPage.priorityDropdown.fill(priority);
    }
  }

  /**
   * Fill the assignee field with provided email
   */
  async fillAssignee(assignee: string): Promise<void> {
    await this.taskCreationPage.assigneeField.clear();
    await this.taskCreationPage.assigneeField.fill(assignee);
  }

  /**
   * Submit the task creation form
   */
  async submitForm(): Promise<void> {
    await this.taskCreationPage.submitButton.click();
  }

  /**
   * Fill complete task form with all provided data
   */
  async fillCompleteTaskForm(taskData: {
    title?: string;
    description?: string;
    priority?: string;
    assignee?: string;
  }): Promise<void> {
    if (taskData.title !== undefined) {
      await this.fillTitle(taskData.title);
    }
    
    if (taskData.description !== undefined) {
      await this.fillDescription(taskData.description);
    }
    
    if (taskData.priority !== undefined) {
      await this.selectPriority(taskData.priority);
    }
    
    if (taskData.assignee !== undefined) {
      await this.fillAssignee(taskData.assignee);
    }
  }

  /**
   * Clear all form fields
   */
  async clearAllFields(): Promise<void> {
    await this.taskCreationPage.titleField.clear();
    await this.taskCreationPage.descriptionField.clear();
    await this.taskCreationPage.assigneeField.clear();
    // Reset priority dropdown to default
    await this.taskCreationPage.priorityDropdown.selectOption('');
  }

  /**
   * Fill form with valid data for successful task creation
   */
  async fillValidTaskData(): Promise<void> {
    await this.fillCompleteTaskForm({
      title: 'SCIB-2024-001: Implement User Authentication Module',
      description: 'Develop secure user authentication system with multi-factor authentication support for SCIB platform',
      priority: 'High',
      assignee: 'john.smith@scib.com'
    });
  }

  /**
   * Generate long description text for testing character limits
   */
  generateLongDescription(length: number): string {
    const baseText = 'This is a comprehensive task description that needs to be very detailed to test the character limit functionality of the system. ';
    return baseText.repeat(Math.ceil(length / baseText.length)).substring(0, length);
  }

  /**
   * Fill form with maximum length description
   */
  async fillMaxLengthDescription(): Promise<void> {
    const longDescription = this.generateLongDescription(2001); // Exceeds 2000 character limit
    await this.fillDescription(longDescription);
  }

  /**
   * Wait for form validation to complete
   */
  async waitForValidation(): Promise<void> {
    // Wait a brief moment for validation messages to appear
    await this.page.waitForTimeout(500);
  }

  /**
   * Check if any validation errors are visible
   */
  async hasValidationErrors(): Promise<boolean> {
    const errorMessages = await this.taskCreationPage.getVisibleErrorMessages();
    return errorMessages.length > 0;
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
}