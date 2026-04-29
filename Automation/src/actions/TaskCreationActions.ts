import { Page } from '@playwright/test';
import { TaskFlowPage } from '../pages/TaskFlowPage';

export class TaskCreationActions {
  private taskFlowPage: TaskFlowPage;

  constructor(private readonly page: Page) {
    this.taskFlowPage = new TaskFlowPage(page);
  }

  /**
   * Navigate to the application and open task creation modal
   */
  async navigateToTaskCreation(): Promise<void> {
    await this.taskFlowPage.navigateToApp();
    await this.taskFlowPage.openAddTaskModal();
  }

  /**
   * Fill the title field
   */
  async fillTitle(title: string): Promise<void> {
    await this.taskFlowPage.taskTitleInputLocator.fill(title);
  }

  /**
   * Fill the description field
   */
  async fillDescription(description: string): Promise<void> {
    await this.taskFlowPage.descriptionTextareaLocator.fill(description);
  }

  /**
   * Select priority from dropdown
   */
  async selectPriority(priority: string): Promise<void> {
    await this.taskFlowPage.prioritySelectLocator.selectOption(priority);
  }

  /**
   * Fill the assignee field
   */
  async fillAssignee(assignee: string): Promise<void> {
    await this.taskFlowPage.assigneeSelectLocator.fill(assignee);
  }

  /**
   * Clear all form fields
   */
  async clearAllFields(): Promise<void> {
    await this.taskFlowPage.taskTitleInputLocator.clear();
    await this.taskFlowPage.descriptionTextareaLocator.clear();
    await this.taskFlowPage.prioritySelectLocator.selectOption('');
    await this.taskFlowPage.assigneeSelectLocator.clear();
  }

  /**
   * Submit the task creation form
   */
  async submitTaskCreation(): Promise<void> {
    await this.taskFlowPage.submitButtonLocator.click();
  }

  /**
   * Create a task with all required fields
   */
  async createTaskWithAllFields(taskData: {
    title: string;
    description: string;
    priority: string;
    assignee: string;
  }): Promise<void> {
    await this.fillTitle(taskData.title);
    await this.fillDescription(taskData.description);
    await this.selectPriority(taskData.priority);
    await this.fillAssignee(taskData.assignee);
    await this.submitTaskCreation();
  }

  /**
   * Create a task with missing title
   */
  async createTaskWithMissingTitle(taskData: {
    description: string;
    priority: string;
    assignee: string;
  }): Promise<void> {
    await this.fillDescription(taskData.description);
    await this.selectPriority(taskData.priority);
    await this.fillAssignee(taskData.assignee);
    await this.submitTaskCreation();
  }

  /**
   * Create a task with missing description
   */
  async createTaskWithMissingDescription(taskData: {
    title: string;
    priority: string;
    assignee: string;
  }): Promise<void> {
    await this.fillTitle(taskData.title);
    await this.selectPriority(taskData.priority);
    await this.fillAssignee(taskData.assignee);
    await this.submitTaskCreation();
  }

  /**
   * Create a task with missing priority
   */
  async createTaskWithMissingPriority(taskData: {
    title: string;
    description: string;
    assignee: string;
  }): Promise<void> {
    await this.fillTitle(taskData.title);
    await this.fillDescription(taskData.description);
    await this.fillAssignee(taskData.assignee);
    await this.submitTaskCreation();
  }

  /**
   * Create a task with missing assignee
   */
  async createTaskWithMissingAssignee(taskData: {
    title: string;
    description: string;
    priority: string;
  }): Promise<void> {
    await this.fillTitle(taskData.title);
    await this.fillDescription(taskData.description);
    await this.selectPriority(taskData.priority);
    await this.submitTaskCreation();
  }

  /**
   * Create a task with all fields missing
   */
  async createTaskWithAllFieldsMissing(): Promise<void> {
    await this.submitTaskCreation();
  }

  /**
   * Attempt to create task with invalid priority
   */
  async createTaskWithInvalidPriority(taskData: {
    title: string;
    description: string;
    assignee: string;
    invalidPriority: string;
  }): Promise<void> {
    await this.fillTitle(taskData.title);
    await this.fillDescription(taskData.description);
    await this.fillAssignee(taskData.assignee);
    
    // Try to set invalid priority through JavaScript if dropdown doesn't allow it
    await this.page.evaluate((priority) => {
      const priorityField = document.querySelector('#task-priority') as HTMLSelectElement;
      if (priorityField) {
        priorityField.value = priority;
      }
    }, taskData.invalidPriority);
    
    await this.submitTaskCreation();
  }
}