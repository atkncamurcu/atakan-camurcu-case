package utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.interactions.Actions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.List;

/**
 * Wait utility class providing various wait methods
 */
public class WaitUtils {
    private static final Logger logger = LogManager.getLogger(WaitUtils.class);
    private final WebDriverWait wait;
    private final WebDriver driver;

    public WaitUtils(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()));
    }

    /**
     * Wait for element to be visible
     * @param locator Element locator
     * @return WebElement when visible
     */
    public WebElement waitForElementToBeVisible(By locator) {
        try {
            logger.debug("Waiting for element to be visible: {}", locator);
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (Exception e) {
            logger.error("Element not visible within timeout: {}", locator, e);
            throw e;
        }
    }

    /**
     * Wait for element to be clickable
     * @param locator Element locator
     * @return WebElement when clickable
     */
    public WebElement waitForElementToBeClickable(By locator) {
        try {
            logger.debug("Waiting for element to be clickable: {}", locator);
            return wait.until(ExpectedConditions.elementToBeClickable(locator));
        } catch (Exception e) {
            logger.error("Element not clickable within timeout: {}", locator, e);
            throw e;
        }
    }

    /**
     * Wait for element to be present
     * @param locator Element locator
     * @return WebElement when present
     */
    public WebElement waitForElementToBePresent(By locator) {
        try {
            logger.debug("Waiting for element to be present: {}", locator);
            return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (Exception e) {
            logger.error("Element not present within timeout: {}", locator, e);
            throw e;
        }
    }

    /**
     * Wait for elements to be present
     * @param locator Element locator
     * @return List of WebElements when present
     */
    public List<WebElement> waitForElementsToBePresent(By locator) {
        try {
            logger.debug("Waiting for elements to be present: {}", locator);
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
        } catch (Exception e) {
            logger.error("Elements not present within timeout: {}", locator, e);
            throw e;
        }
    }

    /**
     * Wait for all elements to be present
     * @param locator Element locator
     * @return List of WebElements when present
     */
    public List<WebElement> waitForPresenceOfAllElements(By locator) {
        try {
            logger.debug("Waiting for all elements to be present: {}", locator);
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
        } catch (Exception e) {
            logger.error("Elements not present within timeout: {}", locator, e);
            throw e;
        }
    }

    /**
     * Wait for text to be present in element
     * @param locator Element locator
     * @param text Expected text
     * @return true when text is present
     */
    public boolean waitForTextToBePresentInElement(By locator, String text) {
        try {
            logger.debug("Waiting for text '{}' to be present in element: {}", text, locator);
            return wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
        } catch (Exception e) {
            logger.error("Text '{}' not present in element within timeout: {}", text, locator, e);
            throw e;
        }
    }

    /**
     * Wait for page title to contain text
     * @param title Expected title
     * @return true when title contains text
     */
    public boolean waitForTitleToContain(String title) {
        try {
            logger.debug("Waiting for title to contain: {}", title);
            return wait.until(ExpectedConditions.titleContains(title));
        } catch (Exception e) {
            logger.error("Title does not contain '{}' within timeout", title, e);
            throw e;
        }
    }

    /**
     * Wait for URL to contain text
     * @param url Expected URL fragment
     * @return true when URL contains text
     */
    public boolean waitForUrlToContain(String url) {
        try {
            logger.debug("Waiting for URL to contain: {}", url);
            return wait.until(ExpectedConditions.urlContains(url));
        } catch (Exception e) {
            logger.error("URL does not contain '{}' within timeout", url, e);
            throw e;
        }
    }

    /**
     * Scroll element into view using JavaScript
     * @param element WebElement to scroll to
     */
    public void scrollToElement(WebElement element) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
            logger.debug("Scrolled to element successfully");
        } catch (Exception e) {
            logger.error("Failed to scroll to element", e);
            throw e;
        }
    }
    
    /**
     * Wait for element to be visible
     * @param element WebElement to wait for
     * @return WebElement when visible
     */
    public WebElement waitForElementToBeVisible(WebElement element) {
        try {
            logger.debug("Waiting for element to be visible");
            return wait.until(ExpectedConditions.visibilityOf(element));
        } catch (Exception e) {
            logger.error("Element not visible within timeout", e);
            throw e;
        }
    }

    /**
     * Hover over element using Actions
     * @param element WebElement to hover over
     */
    public void hoverOverElement(WebElement element) {
        try {
            Actions actions = new Actions(driver);
            actions.moveToElement(element).perform();
            logger.debug("Hovered over element successfully");
        } catch (Exception e) {
            logger.error("Failed to hover over element", e);
            throw e;
        }
    }
    
    /**
     * Wait for page to load completely (JavaScript readyState)
     */
    public void waitForPageToLoad() {
        try {
            wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                .executeScript("return document.readyState").equals("complete"));
            logger.debug("Page loaded completely");
        } catch (Exception e) {
            logger.error("Page did not load completely within timeout", e);
            throw e;
        }
    }

    /**
     * Wait for element to be invisible
     * @param locator Element locator
     * @return true when element is invisible
     */
    public boolean waitForElementToBeInvisible(By locator) {
        try {
            logger.debug("Waiting for element to be invisible: {}", locator);
            return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (Exception e) {
            logger.error("Element still visible after timeout: {}", locator, e);
            throw e;
        }
    }
}
