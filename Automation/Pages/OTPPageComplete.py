from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import TimeoutException, NoSuchElementException
import logging
import time

class OTPPage:
    """
    Page Object Model class for OTP (One-Time Password) verification
    Handles OTP entry, validation, and security lockout scenarios
    """
    
    def __init__(self, driver):
        self.driver = driver
        self.wait = WebDriverWait(driver, 10)
        self.logger = logging.getLogger(__name__)
        self.failed_attempts_count = 0
    
    # Locators
    OTP_INPUT_FIELD = (By.ID, "otpCode")
    VERIFY_OTP_BUTTON = (By.ID, "verifyOtpButton")
    RESEND_OTP_BUTTON = (By.ID, "resendOtpButton")
    ERROR_MESSAGE = (By.CLASS_NAME, "otp-error-message")
    INVALID_CODE_ERROR = (By.XPATH, "//div[contains(text(), 'Invalid Code')]")
    SECURITY_WARNING = (By.CLASS_NAME, "security-warning")
    ACCOUNT_LOCKED_MESSAGE = (By.XPATH, "//div[contains(text(), 'Account temporarily locked') or contains(text(), 'Account locked')]")
    RATE_LIMIT_MESSAGE = (By.CLASS_NAME, "rate-limit-message")
    OTP_PROMPT_CONTAINER = (By.CLASS_NAME, "otp-container")
    REMAINING_ATTEMPTS = (By.ID, "remainingAttempts")
    LOCKOUT_TIMER = (By.ID, "lockoutTimer")
    
    def wait_for_otp_page_load(self):
        try:
            self.wait.until(EC.presence_of_element_located(self.OTP_INPUT_FIELD))
            self.wait.until(EC.element_to_be_clickable(self.VERIFY_OTP_BUTTON))
            self.logger.info("OTP page loaded successfully")
            return True
        except TimeoutException:
            self.logger.error("OTP page failed to load")
            return False
    
    def enter_otp(self, otp_code):
        try:
            otp_element = self.wait.until(EC.element_to_be_clickable(self.OTP_INPUT_FIELD))
            otp_element.clear()
            otp_element.send_keys(otp_code)
            self.logger.info(f"OTP code entered: {otp_code}")
            return True
        except TimeoutException:
            self.logger.error("OTP input field not found or not clickable")
            return False
    
    def click_verify_otp_button(self):
        try:
            verify_button = self.wait.until(EC.element_to_be_clickable(self.VERIFY_OTP_BUTTON))
            verify_button.click()
            self.logger.info("Verify OTP button clicked")
            return True
        except TimeoutException:
            self.logger.error("Verify OTP button not found or not clickable")
            return False
    
    def submit_otp(self, otp_code):
        try:
            if self.enter_otp(otp_code) and self.click_verify_otp_button():
                time.sleep(1)
                self.logger.info(f"OTP submitted successfully: {otp_code}")
                return True
            else:
                self.logger.error("Failed to submit OTP")
                return False
        except Exception as e:
            self.logger.error(f"Exception during OTP submission: {str(e)}")
            return False
    
    def submit_failed_otp_attempt(self, invalid_otp):
        try:
            self.submit_otp(invalid_otp)
            self.failed_attempts_count += 1
            time.sleep(2)
            
            result = {
                'success': False,
                'attempt_number': self.failed_attempts_count,
                'otp_submitted': invalid_otp,
                'error_message': self.get_error_message(),
                'is_invalid_code_error': self.is_invalid_code_error_displayed(),
                'is_security_warning_displayed': self.is_security_warning_displayed(),
                'is_account_locked': self.is_account_locked(),
                'is_rate_limited': self.is_rate_limited()
            }
            
            self.logger.info(f"Failed OTP attempt #{self.failed_attempts_count}: {invalid_otp}")
            return result
            
        except Exception as e:
            self.logger.error(f"Exception during failed OTP attempt: {str(e)}")
            return {'success': False, 'error': str(e)}
    
    def perform_multiple_failed_attempts(self, invalid_otp_list):
        results = []
        
        for i, invalid_otp in enumerate(invalid_otp_list, 1):
            self.logger.info(f"Attempting failed OTP #{i}: {invalid_otp}")
            
            if self.is_account_locked():
                self.logger.warning(f"Account already locked before attempt #{i}")
                break
            
            result = self.submit_failed_otp_attempt(invalid_otp)
            results.append(result)
            
            if result.get('is_account_locked'):
                self.logger.warning(f"Account locked after attempt #{i}")
                break
            elif result.get('is_security_warning_displayed'):
                self.logger.warning(f"Security warning displayed after attempt #{i}")
            
            time.sleep(1)
        
        return results
    
    def is_invalid_code_error_displayed(self):
        try:
            self.driver.find_element(*self.INVALID_CODE_ERROR)
            self.logger.info("Invalid Code error is displayed")
            return True
        except NoSuchElementException:
            self.logger.info("Invalid Code error is not displayed")
            return False
    
    def is_security_warning_displayed(self):
        try:
            self.driver.find_element(*self.SECURITY_WARNING)
            self.logger.info("Security warning is displayed")
            return True
        except NoSuchElementException:
            self.logger.info("Security warning is not displayed")
            return False
    
    def is_account_locked(self):
        try:
            self.driver.find_element(*self.ACCOUNT_LOCKED_MESSAGE)
            self.logger.info("Account locked message is displayed")
            return True
        except NoSuchElementException:
            self.logger.info("Account is not locked")
            return False
    
    def is_rate_limited(self):
        try:
            self.driver.find_element(*self.RATE_LIMIT_MESSAGE)
            self.logger.info("Rate limiting is activated")
            return True
        except NoSuchElementException:
            self.logger.info("No rate limiting detected")
            return False
    
    def get_error_message(self):
        try:
            error_element = self.driver.find_element(*self.ERROR_MESSAGE)
            error_text = error_element.text
            self.logger.info(f"Error message found: {error_text}")
            return error_text
        except NoSuchElementException:
            try:
                invalid_code_element = self.driver.find_element(*self.INVALID_CODE_ERROR)
                return invalid_code_element.text
            except NoSuchElementException:
                try:
                    locked_message_element = self.driver.find_element(*self.ACCOUNT_LOCKED_MESSAGE)
                    return locked_message_element.text
                except NoSuchElementException:
                    self.logger.info("No error message displayed")
                    return ""
    
    def get_remaining_attempts(self):
        try:
            attempts_element = self.driver.find_element(*self.REMAINING_ATTEMPTS)
            attempts_text = attempts_element.text
            import re
            match = re.search(r'(\\d+)', attempts_text)
            if match:
                remaining = int(match.group(1))
                self.logger.info(f"Remaining attempts: {remaining}")
                return remaining
            else:
                return -1
        except (NoSuchElementException, ValueError):
            self.logger.info("Remaining attempts information not available")
            return -1
    
    def get_lockout_timer(self):
        try:
            timer_element = self.driver.find_element(*self.LOCKOUT_TIMER)
            timer_text = timer_element.text
            self.logger.info(f"Lockout timer: {timer_text}")
            return timer_text
        except NoSuchElementException:
            self.logger.info("Lockout timer not displayed")
            return ""
    
    def click_resend_otp(self):
        try:
            resend_button = self.wait.until(EC.element_to_be_clickable(self.RESEND_OTP_BUTTON))
            resend_button.click()
            self.logger.info("Resend OTP button clicked")
            return True
        except TimeoutException:
            self.logger.error("Resend OTP button not found or not clickable")
            return False
    
    def reset_failed_attempts_counter(self):
        self.failed_attempts_count = 0
        self.logger.info("Failed attempts counter reset")
    
    def get_failed_attempts_count(self):
        return self.failed_attempts_count