package listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestListener;
import org.testng.ITestResult;

import utils.ConfigReader;
import utils.DriverFactory;
import utils.ScreenshotUtils;

/**
 * TestNG listener for capturing screenshots on test failure
 */
public class ScreenshotListener implements ITestListener {
    private static final Logger logger = LogManager.getLogger(ScreenshotListener.class);

    /**
     * Called when a test fails
     * @param result ITestResult containing test information
     */
    @Override
    public void onTestFailure(ITestResult result) {
        if (ConfigReader.isScreenshotOnFailure()) {
            try {
                String testName = result.getMethod().getMethodName();
                String className = result.getTestClass().getName();
                String screenshotName = className + "_" + testName;
                
                logger.info("Test failed: {}.{} - Capturing screenshot", className, testName);
                
                if (DriverFactory.isDriverInitialized()) {
                    String screenshotPath = ScreenshotUtils.captureScreenshot(
                        DriverFactory.getDriver(), screenshotName);
                    
                    if (screenshotPath != null) {
                        logger.info("Screenshot captured: {}", screenshotPath);
                        
                        System.setProperty("screenshot.path", screenshotPath);
                    }
                } else {
                    logger.warn("Driver not initialized, cannot capture screenshot");
                }
                
            } catch (Exception e) {
                logger.error("Failed to capture screenshot on test failure", e);
            }
        }
    }

    /**
     * Called when a test starts
     * @param result ITestResult containing test information
     */
    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        logger.info("Starting test: {}.{}", className, testName);
    }

    /**
     * Called when a test passes
     * @param result ITestResult containing test information
     */
    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        logger.info("Test passed: {}.{}", className, testName);
    }

    /**
     * Called when a test is skipped
     * @param result ITestResult containing test information
     */
    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        logger.warn("Test skipped: {}.{}", className, testName);
    }
}
