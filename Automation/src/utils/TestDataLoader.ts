import * as fs from 'fs';
import * as path from 'path';

export class TestDataLoader {
  private static readonly TEST_DATA_DIR = path.join(__dirname, '../test-data');

  /**
   * Load test data from JSON file
   * @param fileName - Name of the JSON file in test-data directory
   * @returns Parsed JSON object
   */
  static loadTestData(fileName: string): any {
    try {
      const filePath = path.join(this.TEST_DATA_DIR, fileName);
      const rawData = fs.readFileSync(filePath, 'utf-8');
      return JSON.parse(rawData);
    } catch (error) {
      throw new Error(`Failed to load test data from ${fileName}: ${error}`);
    }
  }

  /**
   * Load environment-specific test data
   * @param fileName - Base name of the JSON file
   * @param environment - Environment name (dev, staging, prod)
   * @returns Parsed JSON object with environment-specific overrides
   */
  static loadEnvironmentTestData(fileName: string, environment: string = 'dev'): any {
    const baseData = this.loadTestData(fileName);
    
    try {
      const envFileName = fileName.replace('.json', `-${environment}.json`);
      const envData = this.loadTestData(envFileName);
      return { ...baseData, ...envData };
    } catch {
      // If environment-specific file doesn't exist, return base data
      return baseData;
    }
  }

  /**
   * Get test data for specific test case
   * @param fileName - Name of the JSON file
   * @param testCaseKey - Key of the specific test case data
   * @returns Test case specific data
   */
  static getTestCaseData(fileName: string, testCaseKey: string): any {
    const allData = this.loadTestData(fileName);
    
    if (!allData[testCaseKey]) {
      throw new Error(`Test case data '${testCaseKey}' not found in ${fileName}`);
    }
    
    return allData[testCaseKey];
  }
}