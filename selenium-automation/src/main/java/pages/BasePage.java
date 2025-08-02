package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.WaitUtils;

public abstract class BasePage {
    protected final Logger logger = LogManager.getLogger(this.getClass());
    protected WebDriver driver;
    protected WaitUtils waitUtils;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver);
        PageFactory.initElements(driver, this);
    }

    public String getPageTitle() {
        String title = driver.getTitle();
        return title;
    }

    public String getCurrentUrl() {
        String url = driver.getCurrentUrl();
        return url;
    }

    public void navigateToUrl(String url) {
        logger.info("Navigating to: {}", url);
        driver.get(url);
    }

    protected boolean isElementPresent(By locator) {
        try {
            driver.findElement(locator);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    protected boolean isElementDisplayed(WebElement element, String elementName) {
        try {
            boolean displayed = element.isDisplayed();
            return displayed;
        } catch (Exception e) {
            return false;
        }
    }

    protected void clickElement(WebElement element, String elementName) {
        try {
            waitUtils.scrollToElement(element);
            element.click();
            logger.info("Clicked on: {}", elementName);
        } catch (Exception e) {
            logger.error("Failed to click on: {}", elementName, e);
            throw e;
        }
    }

    protected void typeText(WebElement element, String text, String elementName) {
        try {
            element.clear();
            element.sendKeys(text);
            logger.info("Typed '{}' into: {}", text, elementName);
        } catch (Exception e) {
            logger.error("Failed to type into: {}", elementName, e);
            throw e;
        }
    }

    protected String getElementText(WebElement element, String elementName) {
        try {
            String text = element.getText();
            return text;
        } catch (Exception e) {
            logger.error("Failed to get text from: {}", elementName, e);
            throw e;
        }
    }

    protected void acceptCookiesIfPresent() {
        try {
            WebElement acceptButton = waitUtils.waitForElementToBeClickable(
                By.xpath("//a[contains(text(), 'Accept') or contains(@class, 'accept') or @id='accept-all-cookies']"));
            
            if (acceptButton != null) {
                acceptButton.click();
                logger.info("Accepted cookies");
            }
        } catch (Exception ignored) {
            // Cookie banner may not be present, ignore
        }
    }

    protected void closeInsiderPopupIfPresent() {
        try {
            try {
                WebElement popupContainer = driver.findElement(By.xpath("//div[contains(@class, 'ins-notification-content')]"));
                if (popupContainer.isDisplayed()) {
                    
                    // Try different close button selectors
                    String[] closeSelectors = {
                        "//span[contains(@class, 'ins-close-button')]",
                        "//button[contains(@class, 'ins-close-button')]",
                        "//span[text()='×']",
                        "//button[text()='×']",
                        "//span[contains(@class, 'close')]",
                        "//button[contains(@class, 'close')]"
                    };
                    
                    for (String selector : closeSelectors) {
                        try {
                            WebElement closeButton = driver.findElement(By.xpath(selector));
                            if (closeButton.isDisplayed() && closeButton.isEnabled()) {
                                closeButton.click();
                                logger.info("Closed Insider popup using selector: " + selector);
                                
                                try {
                                    waitUtils.waitForElementToBeInvisible(By.xpath("//div[contains(@class, 'ins-notification-content')]"));
                                } catch (Exception ignore) {
                                    // Not waiting for invisibility, popup may have already disappeared
                                }
                                
                                return;
                            }
                        } catch (NoSuchElementException ignore) {
                            // Close button not found with this selector, try next one
                        }
                    }
                }
            } catch (NoSuchElementException ignored) {
                // Popup may not be present, ignore
            }
            
        } catch (Exception ignored) {
            // Any other error during popup handling, ignore
        }
    }
}
