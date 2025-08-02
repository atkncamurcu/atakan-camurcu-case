package utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

/**
 * Screenshot utility class for capturing screenshots
 */
public class ScreenshotUtils {
    private static final Logger logger = LogManager.getLogger(ScreenshotUtils.class);
    private static final String SCREENSHOT_FORMAT = "yyyy-MM-dd_HH-mm-ss";

    /**
     * Capture screenshot and save to file
     * @param driver WebDriver instance
     * @param testName Test name for filename
     * @return Screenshot file path
     */
    public static String captureScreenshot(WebDriver driver, String testName) {
        String screenshotPath = null;
        
        try {
            String screenshotDir = ConfigReader.getScreenshotPath();
            File directory = new File(screenshotDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            String timestamp = new SimpleDateFormat(SCREENSHOT_FORMAT).format(new Date());
            String fileName = testName + "_" + timestamp + ".png";
            screenshotPath = screenshotDir + fileName;
            
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
            File destFile = new File(screenshotPath);
            
            FileUtils.copyFile(sourceFile, destFile);
            
            logger.info("Screenshot captured successfully: {}", screenshotPath);
            
        } catch (IOException e) {
            logger.error("Failed to capture screenshot for test: {}", testName, e);
        } catch (Exception e) {
            logger.error("Unexpected error while capturing screenshot for test: {}", testName, e);
        }
        
        return screenshotPath;
    }

    /**
     * Capture screenshot with custom filename
     * @param driver WebDriver instance
     * @param fileName Custom filename (without extension)
     * @return Screenshot file path
     */
    public static String captureScreenshotWithCustomName(WebDriver driver, String fileName) {
        String screenshotPath = null;
        
        try {
            String screenshotDir = ConfigReader.getScreenshotPath();
            File directory = new File(screenshotDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            screenshotPath = screenshotDir + fileName + ".png";
            
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
            File destFile = new File(screenshotPath);
            
            FileUtils.copyFile(sourceFile, destFile);
            
            logger.info("Screenshot captured successfully: {}", screenshotPath);
            
        } catch (IOException e) {
            logger.error("Failed to capture screenshot with filename: {}", fileName, e);
        } catch (Exception e) {
            logger.error("Unexpected error while capturing screenshot with filename: {}", fileName, e);
        }
        
        return screenshotPath;
    }

    /**
     * Get screenshot as byte array
     * @param driver WebDriver instance
     * @return Screenshot as byte array
     */
    public static byte[] getScreenshotAsBytes(WebDriver driver) {
        try {
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            return takesScreenshot.getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            logger.error("Failed to capture screenshot as bytes", e);
            return new byte[0];
        }
    }
}
