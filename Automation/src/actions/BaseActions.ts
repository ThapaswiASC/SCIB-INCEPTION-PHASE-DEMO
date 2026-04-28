import { Page } from '@playwright/test';
import { BasePage } from '@pages/BasePage';

/**
 * BaseActions Class
 * Foundation class for all Action classes containing reusable business logic flows.
 * Actions consume Page Objects and implement complex user workflows.
 */
export abstract class BaseActions {
  readonly page: Page;
  protected basePage: BasePage;

  constructor(page: Page) {
    this.page = page;
    this.basePage = new (class extends BasePage {})(page);
  }

  /**
   * Generic form filling action
   * @param formData - Object containing field selectors and values
   */
  async fillForm(formData: Record<string, string>): Promise<void> {
    for (const [selector, value] of Object.entries(formData)) {
      const field = this.page.locator(selector);
      await field.waitFor({ state: 'visible' });
      await field.fill(value);
    }
  }

  /**
   * Generic button click with wait
   * @param selector - Button selector
   */
  async clickButton(selector: string): Promise<void> {
    const button = this.page.locator(selector);
    await button.waitFor({ state: 'visible' });
    await button.click();
  }

  /**
   * Generic dropdown selection
   * @param selector - Dropdown selector
   * @param value - Value to select
   */
  async selectFromDropdown(selector: string, value: string): Promise<void> {
    const dropdown = this.page.locator(selector);
    await dropdown.waitFor({ state: 'visible' });
    await dropdown.selectOption(value);
  }

  /**
   * Wait for navigation after action
   */
  async waitForNavigation(): Promise<void> {
    await this.page.waitForLoadState('networkidle');
  }

  /**
   * Handle file upload
   * @param selector - File input selector
   * @param filePath - Path to file
   */
  async uploadFile(selector: string, filePath: string): Promise<void> {
    const fileInput = this.page.locator(selector);
    await fileInput.setInputFiles(filePath);
  }

  /**
   * Generic table interaction
   * @param tableSelector - Table selector
   * @param rowIndex - Row index to interact with
   * @param action - Action to perform (click, getText, etc.)
   */
  async interactWithTableRow(
    tableSelector: string, 
    rowIndex: number, 
    action: 'click' | 'getText' = 'click'
  ): Promise<string | void> {
    const row = this.page.locator(`${tableSelector} tbody tr`).nth(rowIndex);
    await row.waitFor({ state: 'visible' });
    
    if (action === 'click') {
      await row.click();
    } else if (action === 'getText') {
      return await row.textContent() || '';
    }
  }
}