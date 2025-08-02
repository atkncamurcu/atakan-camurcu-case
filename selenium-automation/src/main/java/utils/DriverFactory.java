package utils;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * Driver Factory class to manage WebDriver instances
 */
public class DriverFactory {
    private static final Logger logger = LogManager.getLogger(DriverFactory.class);
    private static ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    /**
     * Create WebDriver instance based on browser name
     * @param browserName Browser name (chrome/firefox)
     * @return WebDriver instance
     */
    public static WebDriver createDriver(String browserName) {
        WebDriver driver = null;
        
        try {
            switch (browserName.toLowerCase()) {
                case "chrome":
                    driver = createChromeDriver();
                    break;
                case "firefox":
                    driver = createFirefoxDriver();
                    break;
                default:
                    logger.warn("Browser '{}' not supported, defaulting to Chrome", browserName);
                    driver = createChromeDriver();
                    break;
            }
            
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigReader.getImplicitWait()));
            
            driver.manage().window().maximize();
            
            driverThreadLocal.set(driver);
            
            logger.info("Driver initialized successfully: {}", browserName);
            
        } catch (Exception e) {
            logger.error("Failed to create driver for browser: {}", browserName, e);
            throw new RuntimeException("Driver initialization failed", e);
        }
        
        return driver;
    }

    /**
     * Create Chrome WebDriver instance
     * @return Chrome WebDriver instance
     */
    private static WebDriver createChromeDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        
        if (ConfigReader.isHeadless()) {
            options.addArguments("--headless");
        }
        
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-web-security");
        options.addArguments("--allow-running-insecure-content");
        
        return new ChromeDriver(options);
    }

    /**
     * Create Firefox WebDriver instance
     * @return Firefox WebDriver instance
     */
    private static WebDriver createFirefoxDriver() {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();
        
        if (ConfigReader.isHeadless()) {
            options.addArguments("--headless");
        }
        
        options.addArguments("--width=1920");
        options.addArguments("--height=1080");
        
        return new FirefoxDriver(options);
    }

    /**
     * Get current WebDriver instance from ThreadLocal
     * @return Current WebDriver instance
     */
    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    /**
     * Quit and remove WebDriver instance from ThreadLocal
     */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
                logger.info("Driver quit successfully");
            } catch (Exception e) {
                logger.error("Error while quitting driver", e);
            } finally {
                driverThreadLocal.remove();
            }
        }
    }

    /**
     * Check if driver is initialized
     * @return true if driver is initialized
     */
    public static boolean isDriverInitialized() {
        return driverThreadLocal.get() != null;
    }
}
