import * as taskData from '../test-data/task-data.json';
import * as validationMessages from '../test-data/validation-messages.json';

/**
 * Utility class for loading and managing test data
 * Supports environment-specific configurations
 */
export class TestDataLoader {
  private static instance: TestDataLoader;
  private environment: string;

  private constructor() {
    this.environment = process.env.TEST_ENV || 'dev';
  }

  /**
   * Get singleton instance
   */
  public static getInstance(): TestDataLoader {
    if (!TestDataLoader.instance) {
      TestDataLoader.instance = new TestDataLoader();
    }
    return TestDataLoader.instance;
  }

  /**
   * Get valid task data
   */
  getValidTaskData(): typeof taskData.validTask {
    return taskData.validTask;
  }

  /**
   * Get alternate valid task data
   */
  getValidTaskDataAlternate(): typeof taskData.validTaskAlternate {
    return taskData.validTaskAlternate;
  }

  /**
   * Get special character task data
   */
  getSpecialCharacterTaskData(): typeof taskData.specialCharacterTask {
    return taskData.specialCharacterTask;
  }

  /**
   * Get long description task data
   */
  getLongDescriptionTaskData(): typeof taskData.longDescriptionTask {
    return taskData.longDescriptionTask;
  }

  /**
   * Get invalid assignee task data
   */
  getInvalidAssigneeTaskData(): typeof taskData.invalidAssigneeTask {
    return taskData.invalidAssigneeTask;
  }

  /**
   * Get valid priorities
   */
  getValidPriorities(): string[] {
    return taskData.priorities.valid;
  }

  /**
   * Get invalid priorities
   */
  getInvalidPriorities(): string[] {
    return taskData.priorities.invalid;
  }

  /**
   * Get valid assignees
   */
  getValidAssignees(): string[] {
    return taskData.assignees.valid;
  }

  /**
   * Get invalid assignees
   */
  getInvalidAssignees(): string[] {
    return taskData.assignees.invalid;
  }

  /**
   * Get error message by key
   */
  getErrorMessage(key: keyof typeof validationMessages.errorMessages): string {
    return validationMessages.errorMessages[key];
  }

  /**
   * Get success message by key
   */
  getSuccessMessage(key: keyof typeof validationMessages.successMessages): string {
    return validationMessages.successMessages[key];
  }

  /**
   * Get character limit by field
   */
  getCharacterLimit(field: keyof typeof validationMessages.characterLimits): number {
    return validationMessages.characterLimits[field];
  }

  /**
   * Get validation rule by key
   */
  getValidationRule(key: keyof typeof validationMessages.validationRules): any {
    return validationMessages.validationRules[key];
  }

  /**
   * Generate task data with custom values
   */
  generateTaskData(overrides: Partial<typeof taskData.validTask>): typeof taskData.validTask {
    return {
      ...taskData.validTask,
      ...overrides
    };
  }

  /**
   * Generate long text of specified length
   */
  generateLongText(length: number, baseText: string = "Sample text "): string {
    return baseText.repeat(Math.ceil(length / baseText.length)).substring(0, length);
  }

  /**
   * Get environment-specific configuration
   */
  getEnvironmentConfig(): { baseUrl: string; timeout: number } {
    const configs = {
      dev: { baseUrl: 'http://localhost:3000', timeout: 30000 },
      test: { baseUrl: 'http://test.scib.com', timeout: 60000 },
      staging: { baseUrl: 'http://staging.scib.com', timeout: 60000 },
      prod: { baseUrl: 'http://prod.scib.com', timeout: 90000 }
    };
    
    return configs[this.environment as keyof typeof configs] || configs.dev;
  }
}