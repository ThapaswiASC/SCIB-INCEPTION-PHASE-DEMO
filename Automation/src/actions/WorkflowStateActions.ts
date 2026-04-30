import { Page } from '@playwright/test';
import { TaskFlowPage } from '../pages/TaskFlowPage';

export class WorkflowStateActions {
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
   * Fill task title
   */
  async fillTaskTitle(title: string): Promise<void> {
    await this.taskFlowPage.taskTitleInputLocator.fill(title);
  }

  /**
   * Select workflow state from dropdown
   */
  async selectWorkflowState(state: string): Promise<void> {
    await this.taskFlowPage.workflowStateDropdownLocator.click();
    await this.taskFlowPage.page.getByRole('option', { name: state }).click();
  }

  /**
   * Open workflow state dropdown
   */
  async openWorkflowStateDropdown(): Promise<void> {
    await this.taskFlowPage.workflowStateDropdownLocator.click();
  }

  /**
   * Submit task creation form
   */
  async submitTaskCreation(): Promise<void> {
    await this.taskFlowPage.createTaskButtonLocator.click();
  }

  /**
   * Create task with specific workflow state
   */
  async createTaskWithWorkflowState(title: string, state?: string): Promise<void> {
    await this.fillTaskTitle(title);
    if (state) {
      await this.selectWorkflowState(state);
    }
    await this.submitTaskCreation();
  }

  /**
   * Create task without selecting workflow state
   */
  async createTaskWithoutWorkflowState(title: string): Promise<void> {
    await this.fillTaskTitle(title);
    await this.submitTaskCreation();
  }

  /**
   * Navigate dropdown with keyboard
   */
  async navigateDropdownWithKeyboard(): Promise<void> {
    await this.taskFlowPage.workflowStateDropdownLocator.focus();
    await this.page.keyboard.press('Tab');
    await this.page.keyboard.press('ArrowDown');
  }

  /**
   * Select dropdown option with Enter key
   */
  async selectDropdownOptionWithEnter(): Promise<void> {
    await this.page.keyboard.press('Enter');
  }

  /**
   * Close dropdown with Escape key
   */
  async closeDropdownWithEscape(): Promise<void> {
    await this.page.keyboard.press('Escape');
  }

  /**
   * Navigate dropdown options with arrow keys
   */
  async navigateDropdownOptions(): Promise<void> {
    await this.page.keyboard.press('ArrowDown');
    await this.page.keyboard.press('ArrowUp');
  }

  /**
   * Attempt to inject malicious input in workflow state
   */
  async attemptMaliciousInput(maliciousInput: string): Promise<void> {
    // Try to inject malicious input through various methods
    await this.page.evaluate((input) => {
      const stateField = document.querySelector('[data-testid="workflow-state-dropdown"]') as HTMLElement;
      if (stateField) {
        stateField.setAttribute('value', input);
      }
    }, maliciousInput);
  }

  /**
   * Attempt SQL injection in workflow state field
   */
  async attemptSQLInjection(sqlInjection: string): Promise<void> {
    await this.page.evaluate((sql) => {
      const stateField = document.querySelector('[data-testid="workflow-state-dropdown"]') as HTMLElement;
      if (stateField) {
        stateField.setAttribute('data-value', sql);
      }
    }, sqlInjection);
  }

  /**
   * Change workflow state selection multiple times
   */
  async changeWorkflowStateMultipleTimes(states: string[]): Promise<void> {
    for (const state of states) {
      await this.selectWorkflowState(state);
      await this.page.waitForTimeout(500); // Brief pause between selections
    }
  }

  /**
   * Create multiple tasks with different states
   */
  async createMultipleTasksWithStates(taskData: Array<{name: string, state: string}>): Promise<void> {
    for (const task of taskData) {
      await this.createTaskWithWorkflowState(task.name, task.state);
      await this.page.waitForTimeout(1000); // Wait between task creations
      
      // Reopen modal for next task if not the last one
      if (taskData.indexOf(task) < taskData.length - 1) {
        await this.taskFlowPage.openAddTaskModal();
      }
    }
  }

  /**
   * Refresh page and verify task persistence
   */
  async refreshPageAndVerifyPersistence(): Promise<void> {
    await this.page.reload();
    await this.page.waitForLoadState('networkidle');
  }

  /**
   * Simulate user logout and login
   */
  async simulateLogoutAndLogin(): Promise<void> {
    // Simulate logout
    await this.page.evaluate(() => {
      sessionStorage.clear();
      localStorage.clear();
    });
    
    // Simulate login by reloading page
    await this.page.reload();
    await this.page.waitForLoadState('networkidle');
  }

  /**
   * Simulate network conditions
   */
  async simulateSlowNetwork(): Promise<void> {
    await this.page.route('**/*', async route => {
      await new Promise(resolve => setTimeout(resolve, 1000)); // Add 1s delay
      await route.continue();
    });
  }

  /**
   * Measure dropdown load time
   */
  async measureDropdownLoadTime(): Promise<number> {
    const startTime = Date.now();
    await this.openWorkflowStateDropdown();
    await this.taskFlowPage.workflowStateOptionsLocator.first().waitFor({ state: 'visible' });
    return Date.now() - startTime;
  }
}