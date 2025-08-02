package tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import utils.ConfigReader;
import utils.DriverFactory;

/**
 * Base Test class containing common setup and teardown methods
 */
public class BaseTest {
    protected static final Logger logger = LogManager.getLogger(BaseTest.class);
    protected WebDriver driver;
    protected String browser;

    /**
     * Set up test suite before all tests
     */
    @BeforeSuite
    public void suiteSetup() {
        logger.info("=== Starting Test Suite ===");
        logger.info("Browser: {}", ConfigReader.getBrowser());
        logger.info("Base URL: {}", ConfigReader.getBaseUrl());
        logger.info("Headless: {}", ConfigReader.isHeadless());
    }

    /**
     * Set up before each test method
     */
    @BeforeMethod
    @Parameters({"browser"})
    public void setUp(@Optional String browserParam) {
        try {
            browser = (browserParam != null) ? browserParam : ConfigReader.getBrowser();
            
            logger.info("Setting up test with browser: {}", browser);
            
            driver = DriverFactory.createDriver(browser);
            
            logger.info("Test setup completed successfully");
            
        } catch (Exception e) {
            logger.error("Failed to setup test", e);
            throw new RuntimeException("Test setup failed", e);
        }
    }

    /**
     * Tear down after each test method
     */
    @AfterMethod
    public void tearDown() {
        try {
            if (driver != null) {
                logger.info("Tearing down test - closing browser");
                DriverFactory.quitDriver();
                logger.info("Test teardown completed successfully");
            }
        } catch (Exception e) {
            logger.error("Error during test teardown", e);
        }
    }

    /**
     * Tear down test suite after all tests
     */
    @AfterSuite
    public void suiteTeardown() {
        logger.info("=== Test Suite Completed ===");
    }

    /**
     * Get current WebDriver instance
     * @return WebDriver instance
     */
    protected WebDriver getDriver() {
        return driver;
    }

    /**
     * Navigate to base URL
     */
    protected void navigateToBaseUrl() {
        String baseUrl = ConfigReader.getBaseUrl();
        logger.info("Navigating to base URL: {}", baseUrl);
        driver.get(baseUrl);
    }

    /**
     * Get current browser name
     * @return Browser name
     */
    protected String getBrowser() {
        return browser;
    }
}
