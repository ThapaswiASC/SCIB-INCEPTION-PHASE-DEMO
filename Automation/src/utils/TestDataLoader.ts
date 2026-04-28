import * as taskData from '../test-data/task-creation-data.json';
import * as validationMessages from '../test-data/validation-messages.json';

/**
 * Utility class for loading and managing test data
 * Provides environment-specific data loading and validation
 */
export class TestDataLoader {
  private static instance: TestDataLoader;
  private environment: string;

  private constructor() {
    this.environment = process.env.TEST_ENV || 'development';
  }

  /**
   * Get singleton instance of TestDataLoader
   */
  static getInstance(): TestDataLoader {
    if (!TestDataLoader.instance) {
      TestDataLoader.instance = new TestDataLoader();
    }
    return TestDataLoader.instance;
  }

  /**
   * Get valid task data for successful creation scenarios
   */
  getValidTaskData(): any {
    return taskData.validTaskData;
  }

  /**
   * Get invalid data sets for negative testing
   */
  getInvalidData(): any {
    return taskData.invalidData;
  }

  /**
   * Get edge case data for boundary testing
   */
  getEdgeCaseData(): any {
    return taskData.edgeCaseData;
  }

  /**
   * Get test data for specific scenario by test key
   */
  getScenarioData(testKey: string): any {
    return taskData.testScenarios[testKey as keyof typeof taskData.testScenarios];
  }

  /**
   * Get expected validation error messages
   */
  getValidationMessages(): any {
    return validationMessages.errorMessages;
  }

  /**
   * Get expected success messages
   */
  getSuccessMessages(): any {
    return validationMessages.successMessages;
  }

  /**
   * Get info messages
   */
  getInfoMessages(): any {
    return validationMessages.infoMessages;
  }

  /**
   * Get environment-specific base URL
   */
  getBaseUrl(): string {
    const urls = {
      development: 'https://dev-scib-task-management.com',
      staging: 'https://staging-scib-task-management.com',
      production: 'https://scib-task-management.com'
    };
    
    return urls[this.environment as keyof typeof urls] || urls.development;
  }

  /**
   * Get environment-specific timeout values
   */
  getTimeouts(): { navigation: number; action: number; assertion: number } {
    const timeouts = {
      development: { navigation: 60000, action: 30000, assertion: 10000 },
      staging: { navigation: 45000, action: 20000, assertion: 8000 },
      production: { navigation: 30000, action: 15000, assertion: 5000 }
    };
    
    return timeouts[this.environment as keyof typeof timeouts] || timeouts.development;
  }

  /**
   * Generate test data with dynamic values
   */
  generateDynamicTaskData(overrides: Partial<any> = {}): any {
    const timestamp = Date.now();
    const defaultData = {
      title: `SCIB-${timestamp}: Dynamic Test Task`,
      description: `Automated test task created at ${new Date().toISOString()}`,
      priority: 'Medium',
      assignee: `test.user.${timestamp}@scib.com`
    };
    
    return { ...defaultData, ...overrides };
  }

  /**
   * Validate test data structure
   */
  validateTestData(data: any): boolean {
    const requiredFields = ['title', 'description', 'priority', 'assignee'];
    return requiredFields.every(field => data.hasOwnProperty(field));
  }

  /**
   * Get random valid SCIB email
   */
  getRandomValidEmail(): string {
    const validEmails = this.getEdgeCaseData().validScibEmails;
    return validEmails[Math.floor(Math.random() * validEmails.length)];
  }

  /**
   * Get random valid priority
   */
  getRandomValidPriority(): string {
    const validPriorities = this.getEdgeCaseData().validPriorities;
    return validPriorities[Math.floor(Math.random() * validPriorities.length)];
  }
}