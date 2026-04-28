import * as fs from 'fs';
import * as path from 'path';

/**
 * TestDataLoader Utility
 * Manages loading and processing of test data from JSON files
 * Supports environment-specific data loading and data transformation
 */
export class TestDataLoader {
  private static instance: TestDataLoader;
  private dataCache: Map<string, any> = new Map();

  private constructor() {}

  /**
   * Singleton pattern implementation
   */
  public static getInstance(): TestDataLoader {
    if (!TestDataLoader.instance) {
      TestDataLoader.instance = new TestDataLoader();
    }
    return TestDataLoader.instance;
  }

  /**
   * Load test data from JSON file
   * @param fileName - Name of the JSON file (without extension)
   * @param environment - Environment specific data (optional)
   */
  public loadTestData<T>(fileName: string, environment?: string): T {
    const cacheKey = environment ? `${fileName}_${environment}` : fileName;
    
    if (this.dataCache.has(cacheKey)) {
      return this.dataCache.get(cacheKey);
    }

    try {
      const filePath = path.join(__dirname, '../test-data', `${fileName}.json`);
      const rawData = fs.readFileSync(filePath, 'utf-8');
      const jsonData = JSON.parse(rawData);
      
      let data = jsonData;
      if (environment && jsonData[environment]) {
        data = { ...jsonData.common, ...jsonData[environment] };
      }
      
      this.dataCache.set(cacheKey, data);
      return data;
    } catch (error) {
      throw new Error(`Failed to load test data from ${fileName}.json: ${error}`);
    }
  }

  /**
   * Get specific test data by key
   * @param fileName - JSON file name
   * @param key - Data key to retrieve
   * @param environment - Environment (optional)
   */
  public getTestDataByKey<T>(fileName: string, key: string, environment?: string): T {
    const data = this.loadTestData(fileName, environment);
    if (!data[key]) {
      throw new Error(`Test data key '${key}' not found in ${fileName}.json`);
    }
    return data[key];
  }

  /**
   * Generate random test data
   * @param template - Data template object
   */
  public generateRandomData(template: Record<string, string>): Record<string, string> {
    const result: Record<string, string> = {};
    
    for (const [key, pattern] of Object.entries(template)) {
      switch (pattern) {
        case 'email':
          result[key] = `test${Date.now()}@example.com`;
          break;
        case 'username':
          result[key] = `user${Date.now()}`;
          break;
        case 'password':
          result[key] = `Pass${Date.now()}!`;
          break;
        case 'phone':
          result[key] = `+1${Math.floor(Math.random() * 9000000000) + 1000000000}`;
          break;
        default:
          result[key] = pattern;
      }
    }
    
    return result;
  }

  /**
   * Clear data cache
   */
  public clearCache(): void {
    this.dataCache.clear();
  }
}