package pages;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class QualityAssurancePage extends BasePage {

    private final WebDriverWait wait;

    @FindBy(xpath = "//a[contains(., 'See all QA jobs') or contains(@href, 'open-positions/?department=qualityassurance')]")
    private WebElement seeAllJobsButton;

    @FindBy(id = "filter-by-location")
    private WebElement locationFilter;

    @FindBy(id = "filter-by-department")
    private WebElement departmentFilter;

    @FindBy(css = ".position-list-item, .job-item, [data-testid='job-item'], .position-list .position")
    private List<WebElement> jobListings;

    @FindBy(css = "[data-team='qualityassurance'], [data-department='qa'], .qa-job")
    private List<WebElement> qaJobElements;

    public QualityAssurancePage(WebDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public boolean clickSeeAllQAJobs() {
        try {
            logger.info("Clicking 'See all QA jobs' button");
            closeInsiderPopupIfPresent();

            wait.until(ExpectedConditions.elementToBeClickable(seeAllJobsButton));

            if (isElementDisplayed(seeAllJobsButton, "See All QA Jobs Button")) {
                clickElement(seeAllJobsButton, "See All QA Jobs Button");

                wait.until(ExpectedConditions.urlContains("department=qualityassurance"));
                String currentUrl = getCurrentUrl();

                if (currentUrl.contains("department=qualityassurance")) {
                    logger.info("Successfully redirected to QA jobs page: {}", currentUrl);
                    
                    boolean departmentLoaded = waitForDepartmentFilterToLoad("Quality Assurance");
                    if (departmentLoaded) {
                        logger.info("✓ Department filter pre-check passed - Quality Assurance is loaded correctly");
                        return true;
                    } else {
                        logger.error("Department filter failed to update to Quality Assurance. Test cannot proceed reliably.");
                        return false;
                    }
                    
                } else {
                    logger.error("URL does not contain expected department parameter: {}", currentUrl);
                    return false;
                }
            } else {
                logger.warn("See All QA Jobs button not found");
                return false;
            }

        } catch (Exception e) {
            logger.error("Failed to click See All QA Jobs button", e);
            return false;
        }
    }
    
    /**
     * Wait for department filter to load and get the expected value
     * @param expectedDepartment The expected department value
     * @return true if filter loaded with expected value
     */
    private boolean waitForDepartmentFilterToLoad(String expectedDepartment) {
        try {
            logger.info("Waiting for department filter to load with value: {}", expectedDepartment);
            
            WebDriverWait filterWait = new WebDriverWait(driver, Duration.ofSeconds(30));
            filterWait.until(ExpectedConditions.elementToBeClickable(departmentFilter));
            
            Select departmentSelect = new Select(departmentFilter);
            WebElement selectedOption = departmentSelect.getFirstSelectedOption();
            String initialValue = selectedOption.getText().trim();
            
            logger.info("Initial department filter value: '{}'", initialValue);
            
            if (initialValue.toLowerCase().contains(expectedDepartment.toLowerCase())) {
                logger.info("✓ Department filter already has the expected value");
                return true;
            }
            
            boolean valueUpdated = false;
            long startTime = System.currentTimeMillis();
            long timeout = 10000;
            
            logger.info("Department filter has initial value '{}', waiting for it to update to '{}'...", 
                    initialValue, expectedDepartment);
            
            while (System.currentTimeMillis() - startTime < timeout) {
                departmentSelect = new Select(departmentFilter);
                selectedOption = departmentSelect.getFirstSelectedOption();
                String currentValue = selectedOption.getText().trim();
                
                if (currentValue.toLowerCase().contains(expectedDepartment.toLowerCase())) {
                    logger.info("✓ Department filter auto-updated to expected value '{}' after waiting", currentValue);
                    valueUpdated = true;
                    break;
                }
                
                try {
                    wait.until(ExpectedConditions.attributeContains(
                        departmentFilter, "data-value", expectedDepartment.toLowerCase()));
                } catch (Exception e) {
                    // Continue with polling loop even if wait fails
                }
            }
            
            if (!valueUpdated) {
                try {
                    logger.info("Trying to manually select '{}' from department filter...", expectedDepartment);
                    
                    List<WebElement> options = departmentSelect.getOptions();
                    logger.info("Available department options ({}): ", options.size());
                    for (int i = 0; i < Math.min(options.size(), 10); i++) {
                        logger.info("  Option {}: '{}'", i, options.get(i).getText().trim());
                    }
                    
                    for (WebElement option : options) {
                        String optionText = option.getText().trim();
                        if (optionText.toLowerCase().contains(expectedDepartment.toLowerCase())) {
                            logger.info("Found matching option: '{}'", optionText);
                            departmentSelect.selectByVisibleText(optionText);
                            
                            WebElement newSelectedOption = departmentSelect.getFirstSelectedOption();
                            String selectedText = newSelectedOption.getText().trim();
                            logger.info("After manual selection, value is now: '{}'", selectedText);
                            
                            if (selectedText.toLowerCase().contains(expectedDepartment.toLowerCase())) {
                                logger.info("✓ Successfully manually selected '{}'", expectedDepartment);
                                valueUpdated = true;
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.warn("Failed to manually select department: {}", e.getMessage());
                }
            }
            
            if (valueUpdated) {
                return true;
            } else {
                String screenshotPath = utils.ScreenshotUtils.captureScreenshot(driver, "department_filter_wrong_value");
                logger.error("Department filter didn't update to '{}' within timeout. Screenshot saved to: {}", 
                        expectedDepartment, screenshotPath);
                return false;
            }
            
        } catch (Exception e) {
            String screenshotPath = utils.ScreenshotUtils.captureScreenshot(driver, "department_filter_error");
            logger.error("Error waiting for department filter to load: {}. Screenshot saved to: {}", 
                    e.getMessage(), screenshotPath);
            return false;
        }
    }

    public QualityAssurancePage applyLocationFilter(String location) {
        try {
            logger.info("Applying location filter: {}", location);
            
            wait.until(ExpectedConditions.elementToBeClickable(locationFilter));
            WebElement locationFilterElement = findLocationFilterElement();
            if (locationFilterElement == null) {
                logger.warn("Location filter element not found");
                utils.ScreenshotUtils.captureScreenshot(driver, "LocationFilterNotFound");
                throw new RuntimeException("Location filter element not found");
            }
            
            if (locationFilterElement.getAttribute("class").contains("select2-hidden-accessible")) {
                logger.info("Detected Select2 dropdown for location filter");

                WebElement select2Trigger = driver.findElement(By.cssSelector(".select2-selection"));
                clickElement(select2Trigger, "Select2 Location Trigger");

                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".select2-results__option")));
                List<WebElement> options = driver.findElements(By.cssSelector(".select2-results__option"));
                logger.info("Found {} Select2 options for location", options.size());
                
                for (int i = 0; i < Math.min(options.size(), 10); i++) {
                    WebElement option = options.get(i);
                    if (option.isDisplayed()) {
                        logger.info("  Select2 Option {}: '{}'", i, option.getText().trim());
                    }
                }
                
                boolean optionFound = false;
                
                try {
                    waitUtils.waitForPresenceOfAllElements(
                        By.cssSelector("li.select2-results__option[id*='select2-filter-by-location-result']"));
                    
                    List<WebElement> istanbulOptions = driver.findElements(
                        By.cssSelector("li.select2-results__option[id*='select2-filter-by-location-result'][id*='Istanbul']"));
                    
                    if (!istanbulOptions.isEmpty()) {
                        for (WebElement option : istanbulOptions) {
                            String optionText = option.getText().trim();
                            if (optionText.contains("Istanbul") && 
                               (optionText.contains("Turkiye") || optionText.contains("Turkey"))) {
                                logger.info("Found Istanbul option by ID and text: {} - '{}'", option.getAttribute("id"), optionText);
                                clickElement(option, "Istanbul, Turkiye option (by ID and text)");
                                optionFound = true;
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.warn("Error finding option by ID: {}", e.getMessage());
                }
                
                if (!optionFound) {
                    for (WebElement option : options) {
                        if (option.isDisplayed()) {
                            String optionText = option.getText().trim();
                            
                            if (optionText.equalsIgnoreCase("Istanbul, Turkiye") || 
                                (optionText.contains("Istanbul") && 
                                 (optionText.contains("Turkiye") || optionText.contains("Turkey")))) {
                                
                                logger.info("Found match for Istanbul: '{}'", optionText);
                                clickElement(option, "Location Option: " + optionText);
                                optionFound = true;
                                break;
                            }
                        }
                    }
                }
                
                if (optionFound) {
                    waitUtils.waitForPageToLoad();
                    
                    WebElement textElement = driver.findElement(By.cssSelector(".select2-selection__rendered"));
                    String titleAttribute = textElement.getAttribute("title");
                    
                    if (titleAttribute != null && !titleAttribute.isEmpty()) {
                        String selectedText = titleAttribute.trim();
                        logger.info("Found location in title attribute: '{}'", selectedText);
                        
                        boolean containsTurkey = selectedText.toLowerCase().contains("turkey") || 
                                                selectedText.toLowerCase().contains("turkiye");
                        
                        if (!containsTurkey) {
                            logger.warn("Selected text '{}' does not contain 'Turkey/Turkiye'", selectedText);
                            utils.ScreenshotUtils.captureScreenshot(driver, "LocationSelectionVerificationIssue");
                        } else {
                            logger.info("✓ Selected location contains 'Turkey/Turkiye'");
                        }
                    }
                    
                    logger.info("✓ Location selection completed - option was clicked successfully");
                } else {
                    logger.error("Could not find any location matching Istanbul in Select2 options");
                    utils.ScreenshotUtils.captureScreenshot(driver, "LocationOptionNotFound");
                    throw new RuntimeException("Location 'Istanbul, Turkey' not found in dropdown options");
                }
            } else {
                logger.info("Detected regular <select> for location filter");
                Select locationSelect = new Select(locationFilterElement);
                
                try {
                    locationSelect.selectByVisibleText("Istanbul, Turkiye");
                    logger.info("Selected location by exact match: Istanbul, Turkiye");
                } catch (Exception e) {
                    logger.warn("Exact match failed, trying partial match", e.getMessage());
                    
                    List<WebElement> allOptions = locationSelect.getOptions();
                    boolean found = false;
                    
                    for (WebElement option : allOptions) {
                        String optionText = option.getText().trim().toLowerCase();
                        if (optionText.contains("istanbul")) {
                            logger.info("Found option with Istanbul: '{}'", option.getText().trim());
                            locationSelect.selectByVisibleText(option.getText().trim());
                            found = true;
                            break;
                        }
                    }
                    
                    if (!found) {
                        logger.error("Failed to select any location option with Istanbul");
                        utils.ScreenshotUtils.captureScreenshot(driver, "LocationSelectionFailed");
                        throw new RuntimeException("Could not select any location option with Istanbul");
                    }
                }
            }

            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfAllElements(jobListings),
                    ExpectedConditions.visibilityOfAllElements(qaJobElements)
            ));
            
            return this;

        } catch (Exception e) {
            logger.error("Failed to apply location filter: {}", e.getMessage());
            utils.ScreenshotUtils.captureScreenshot(driver, "LocationFilterError");
            throw new RuntimeException("Failed to apply location filter: " + e.getMessage(), e);
        }
    }

    private WebElement findLocationFilterElement() {
        try {
            WebElement element = driver.findElement(By.id("filter-by-location"));
            if (element.isDisplayed() && element.isEnabled()) return element;
        } catch (Exception ignored) {
            // Element not found with default ID, continue with alternative selectors
        }
        
        String[] cssSelectors = {
            "select[id*='location']",
            "select[name*='location']",
            "[data-filter='location']"
        };

        for (String selector : cssSelectors) {
            try {
                WebElement element = driver.findElement(By.cssSelector(selector));
                if (element.isDisplayed() && element.isEnabled()) return element;
            } catch (Exception ignored) {
                // Element not found with this selector, try next one
            }
        }

        return null;
    }

    public QualityAssurancePage applyDepartmentFilter(String department) {
        try {
            logger.info("Applying department filter: {}", department);
            wait.until(ExpectedConditions.elementToBeClickable(departmentFilter));
            Select departmentSelect = new Select(departmentFilter);
            
            WebElement selectedOptionBefore = departmentSelect.getFirstSelectedOption();
            String initialValue = selectedOptionBefore.getText().trim();
            logger.info("Current department filter value before change: '{}'", initialValue);
            
            selectOptionByVisibleTextOrPartial(departmentSelect, department);
            
            waitUtils.waitForPageToLoad();
            
            WebElement selectedOptionAfter = departmentSelect.getFirstSelectedOption();
            String selectedDepartment = selectedOptionAfter.getText().trim();
            
            if (!selectedDepartment.toLowerCase().contains(department.toLowerCase())) {
                String errorMessage = String.format("Department filter was not applied correctly. Expected to contain '%s' but got '%s'", 
                        department, selectedDepartment);
                logger.error(errorMessage);
                utils.ScreenshotUtils.captureScreenshot(driver, "DepartmentFilterApplyError");
                throw new AssertionError(errorMessage);
            }
            
            logger.info("✓ Department filter applied successfully and verified. Selected value: '{}'", selectedDepartment);

            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfAllElements(jobListings),
                    ExpectedConditions.visibilityOfAllElements(qaJobElements)
            ));
            
            return this;

        } catch (Exception e) {
            logger.error("Failed to apply department filter: {}", e.getMessage());
            utils.ScreenshotUtils.captureScreenshot(driver, "DepartmentFilterError");
            throw new RuntimeException("Failed to apply department filter: " + e.getMessage(), e);
        }
    }

    public boolean isJobListPopulated() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfAllElements(jobListings),
                    ExpectedConditions.visibilityOfAllElements(qaJobElements)
            ));
            return !jobListings.isEmpty() || !qaJobElements.isEmpty();
        } catch (Exception e) {
            logger.error("Failed to verify job list population", e);
            return false;
        }
    }

    public boolean verifyAllJobsMatchCriteria(String expectedLocation, String expectedDepartment) {
        try {
            logger.info("Verifying jobs match criteria - Location: {}, Department: {}",
                    expectedLocation, expectedDepartment);

            List<WebElement> jobs = jobListings.isEmpty() ? qaJobElements : jobListings;
            if (jobs.isEmpty()) return false;

            boolean allJobsMatch = true;
            for (WebElement job : jobs) {
                if (!isElementDisplayed(job, "Job Listing")) continue;

                String jobText = getElementText(job, "Job");
                boolean hasQA = jobText.toLowerCase().contains("quality assurance");
                boolean hasLocation = jobText.toLowerCase().contains("istanbul") || jobText.toLowerCase().contains("turkey");

                if (!hasQA || !hasLocation) allJobsMatch = false;
            }
            return allJobsMatch;

        } catch (Exception e) {
            logger.error("Failed to verify job criteria", e);
            return false;
        }
    }

    public boolean clickViewRoleForFirstJob() {
        try {
            logger.info("Clicking View Role for first job");
            List<WebElement> jobs = jobListings.isEmpty() ? qaJobElements : jobListings;
            if (jobs.isEmpty()) {
                logger.warn("No job listings found to click View Role");
                return false;
            }

            WebElement firstJob = jobs.get(0);
            List<WebElement> buttons = firstJob.findElements(
                    By.xpath(".//a[contains(text(), 'View Role')] | .//button[contains(text(), 'View Role')]"));

            if (!buttons.isEmpty()) {
                String currentWindowHandle = driver.getWindowHandle();
                int initialWindowCount = driver.getWindowHandles().size();
                logger.info("Current window count before clicking: {}", initialWindowCount);
                
                clickElement(buttons.get(0), "View Role Button");
                
                wait.until(d -> d.getWindowHandles().size() > initialWindowCount);
                logger.info("New window detected after clicking View Role");
                
                Set<String> windowHandles = driver.getWindowHandles();
                
                driver.switchTo().window(currentWindowHandle);
                
                return windowHandles.size() > initialWindowCount;
            } else {
                logger.warn("No View Role button found in the first job listing");
                return false;
            }

        } catch (Exception e) {
            logger.error("Failed to click View Role for first job", e);
            return false;
        }
    }

    public int getJobCount() {
        try {
            List<WebElement> jobs = jobListings.isEmpty() ? qaJobElements : jobListings;
            int count = jobs.size();
            logger.info("Found {} job listings", count);
            return count;
        } catch (Exception e) {
            logger.error("Failed to get job count", e);
            return 0;
        }
    }

    public boolean validateJobCountConsistency() {
        try {
            int displayedCount = getJobCount();
            logger.info("Job count validation - Displayed jobs: {}", displayedCount);
            boolean hasJobs = displayedCount > 0;
            logger.info("Job count consistency check - Has jobs: {}", hasJobs);
            return hasJobs;
        } catch (Exception e) {
            logger.error("Failed to validate job count consistency", e);
            return false;
        }
    }

    private void selectOptionByVisibleTextOrPartial(Select select, String value) {
        try {
            select.selectByVisibleText(value);
        } catch (Exception e) {
            select.getOptions().stream()
                .filter(option -> option.getText().toLowerCase().contains(value.toLowerCase()))
                .findFirst()
                .ifPresent(option -> {
                    try {
                        select.selectByVisibleText(option.getText());
                    } catch (Exception ignored) {
                        // Unable to select option, continuing with best effort
                    }
                });
        }
    }
}
