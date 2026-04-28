import { Page, expect } from '@playwright/test';

/**
 * TestHelpers Utility
 * Collection of reusable helper functions for common test operations
 */
export class TestHelpers {
  
  /**
   * Generate unique timestamp-based identifier
   */
  static generateUniqueId(): string {
    return `test_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
  }

  /**
   * Wait for specific time (use sparingly, prefer Playwright's auto-waiting)
   * @param milliseconds - Time to wait in milliseconds
   */
  static async wait(milliseconds: number): Promise<void> {
    await new Promise(resolve => setTimeout(resolve, milliseconds));
  }

  /**
   * Retry operation with exponential backoff
   * @param operation - Function to retry
   * @param maxRetries - Maximum number of retries
   * @param baseDelay - Base delay in milliseconds
   */
  static async retryOperation<T>(
    operation: () => Promise<T>,
    maxRetries: number = 3,
    baseDelay: number = 1000
  ): Promise<T> {
    let lastError: Error;
    
    for (let attempt = 0; attempt <= maxRetries; attempt++) {
      try {
        return await operation();
      } catch (error) {
        lastError = error as Error;
        if (attempt === maxRetries) break;
        
        const delay = baseDelay * Math.pow(2, attempt);
        await this.wait(delay);
      }
    }
    
    throw lastError!;
  }

  /**
   * Validate email format
   * @param email - Email to validate
   */
  static isValidEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }

  /**
   * Format currency value
   * @param amount - Numeric amount
   * @param currency - Currency code (default: USD)
   */
  static formatCurrency(amount: number, currency: string = 'USD'): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: currency
    }).format(amount);
  }

  /**
   * Get current date in specified format
   * @param format - Date format ('YYYY-MM-DD', 'MM/DD/YYYY', etc.)
   */
  static getCurrentDate(format: string = 'YYYY-MM-DD'): string {
    const now = new Date();
    
    switch (format) {
      case 'YYYY-MM-DD':
        return now.toISOString().split('T')[0];
      case 'MM/DD/YYYY':
        return `${(now.getMonth() + 1).toString().padStart(2, '0')}/${now.getDate().toString().padStart(2, '0')}/${now.getFullYear()}`;
      case 'DD-MM-YYYY':
        return `${now.getDate().toString().padStart(2, '0')}-${(now.getMonth() + 1).toString().padStart(2, '0')}-${now.getFullYear()}`;
      default:
        return now.toISOString().split('T')[0];
    }
  }

  /**
   * Scroll element into view
   * @param page - Playwright page object
   * @param selector - Element selector
   */
  static async scrollIntoView(page: Page, selector: string): Promise<void> {
    await page.locator(selector).scrollIntoViewIfNeeded();
  }

  /**
   * Take full page screenshot with timestamp
   * @param page - Playwright page object
   * @param name - Screenshot name prefix
   */
  static async takeTimestampedScreenshot(page: Page, name: string): Promise<string> {
    const timestamp = new Date().toISOString().replace(/[:.]/g, '-');
    const fileName = `${name}_${timestamp}.png`;
    await page.screenshot({ 
      path: `screenshots/${fileName}`, 
      fullPage: true 
    });
    return fileName;
  }

  /**
   * Validate URL pattern
   * @param page - Playwright page object
   * @param expectedPattern - Expected URL pattern (regex or string)
   */
  static async validateCurrentUrl(page: Page, expectedPattern: string | RegExp): Promise<void> {
    const currentUrl = page.url();
    
    if (typeof expectedPattern === 'string') {
      expect(currentUrl).toContain(expectedPattern);
    } else {
      expect(currentUrl).toMatch(expectedPattern);
    }
  }

  /**
   * Extract numbers from text
   * @param text - Text containing numbers
   */
  static extractNumbers(text: string): number[] {
    const matches = text.match(/\d+\.?\d*/g);
    return matches ? matches.map(Number) : [];
  }

  /**
   * Generate random string
   * @param length - Length of string
   * @param includeNumbers - Include numbers in string
   */
  static generateRandomString(length: number = 8, includeNumbers: boolean = true): string {
    const letters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz';
    const numbers = '0123456789';
    const chars = includeNumbers ? letters + numbers : letters;
    
    let result = '';
    for (let i = 0; i < length; i++) {
      result += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    return result;
  }
}