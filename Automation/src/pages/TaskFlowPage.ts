import { Page, Locator } from '@playwright/test';

export class TaskFlowPage {
  constructor(private readonly page: Page) {}

  // Navigation
  readonly skipToMainContentLocator: Locator = this.page.getByRole('link', { name: 'Skip to main content' });
  readonly logoLocator: Locator = this.page.getByRole('link', { name: 'TF TaskFlow' });
  readonly taskManagementNavLocator: Locator = this.page.getByRole('button', { name: 'Task Management' });
  readonly kanbanBoardNavLocator: Locator = this.page.getByRole('link', { name: 'Kanban Board' });

  // Main Content - Kanban Board
  readonly kanbanBoardHeadingLocator: Locator = this.page.getByRole('heading', { name: 'Kanban Board Dashboard' });
  readonly addTaskButtonLocator: Locator = this.page.getByRole('button', { name: 'Add Task' });

  // Task Creation Modal
  readonly addTaskModalHeadingLocator: Locator = this.page.getByRole('heading', { name: 'Add New Task' });
  readonly addTaskModalCloseButtonLocator: Locator = this.page.getByRole('button', { name: 'Close' });
  readonly taskTitleInputLocator: Locator = this.page.locator('#task-title');
  readonly descriptionTextareaLocator: Locator = this.page.locator('#task-description');
  readonly prioritySelectLocator: Locator = this.page.locator('#task-priority');
  readonly assigneeSelectLocator: Locator = this.page.locator('#task-assignee');
  readonly dueDateInputLocator: Locator = this.page.getByRole('textbox', { name: 'Due Date' });
  readonly cancelTaskButtonLocator: Locator = this.page.getByRole('button', { name: 'Cancel' });
  readonly createTaskButtonLocator: Locator = this.page.getByRole('button', { name: 'Create Task' });
  readonly submitButtonLocator: Locator = this.page.getByRole('button', { name: 'Submit' });

  // Validation Error Messages
  readonly titleErrorMessageLocator: Locator = this.page.locator('#task-title').locator('..').locator('.error-message');
  readonly descriptionErrorMessageLocator: Locator = this.page.locator('#task-description').locator('..').locator('.error-message');
  readonly priorityErrorMessageLocator: Locator = this.page.locator('#task-priority').locator('..').locator('.error-message');
  readonly assigneeErrorMessageLocator: Locator = this.page.locator('#task-assignee').locator('..').locator('.error-message');

  // Success Messages
  readonly successMessageLocator: Locator = this.page.locator('.success-message');

  // Task List
  readonly taskListLocator: Locator = this.page.locator('.task-card');
  readonly todoColumnLocator: Locator = this.page.getByRole('heading', { name: 'To Do' }).locator('..');

  /**
   * Navigate to the application
   */
  async navigateToApp(): Promise<void> {
    await this.page.goto('/');
    await this.page.waitForLoadState('networkidle');
  }

  /**
   * Open the Add Task modal
   */
  async openAddTaskModal(): Promise<void> {
    await this.addTaskButtonLocator.click();
    await this.addTaskModalHeadingLocator.waitFor({ state: 'visible' });
  }

  /**
   * Check if the Add Task modal is visible
   */
  async isAddTaskModalVisible(): Promise<boolean> {
    return await this.addTaskModalHeadingLocator.isVisible();
  }

  /**
   * Close the Add Task modal
   */
  async closeAddTaskModal(): Promise<void> {
    await this.addTaskModalCloseButtonLocator.click();
    await this.addTaskModalHeadingLocator.waitFor({ state: 'hidden' });
  }

  /**
   * Get the current value of the title field
   */
  async getTitleValue(): Promise<string> {
    return await this.taskTitleInputLocator.inputValue();
  }

  /**
   * Get the current value of the description field
   */
  async getDescriptionValue(): Promise<string> {
    return await this.descriptionTextareaLocator.inputValue();
  }

  /**
   * Get the current value of the priority field
   */
  async getPriorityValue(): Promise<string> {
    return await this.prioritySelectLocator.inputValue();
  }

  /**
   * Get the current value of the assignee field
   */
  async getAssigneeValue(): Promise<string> {
    return await this.assigneeSelectLocator.inputValue();
  }

  /**
   * Check if a field has validation error styling
   */
  async hasFieldError(fieldLocator: Locator): Promise<boolean> {
    return await fieldLocator.evaluate(el => el.classList.contains('error'));
  }

  /**
   * Get validation error message for a specific field
   */
  async getFieldErrorMessage(errorLocator: Locator): Promise<string> {
    await errorLocator.waitFor({ state: 'visible' });
    return await errorLocator.textContent() || '';
  }

  /**
   * Check if success message is displayed
   */
  async isSuccessMessageVisible(): Promise<boolean> {
    return await this.successMessageLocator.isVisible();
  }

  /**
   * Get success message text
   */
  async getSuccessMessageText(): Promise<string> {
    await this.successMessageLocator.waitFor({ state: 'visible' });
    return await this.successMessageLocator.textContent() || '';
  }

  /**
   * Count the number of tasks in the task list
   */
  async getTaskCount(): Promise<number> {
    return await this.taskListLocator.count();
  }

  /**
   * Check if a task with specific title exists in the task list
   */
  async isTaskInList(taskTitle: string): Promise<boolean> {
    const taskLocator = this.page.locator('.task-card').filter({ hasText: taskTitle });
    return await taskLocator.count() > 0;
  }
}