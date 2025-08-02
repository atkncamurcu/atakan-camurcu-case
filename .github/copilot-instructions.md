<!-- Use this file to provide workspace-specific custom instructions to Copilot. For more details, visit https://code.visualstudio.com/docs/copilot/copilot-customization#_use-a-githubcopilotinstructionsmd-file -->

# Website Selenium Automation Framework - Copilot Instructions

## Project Overview
This is a comprehensive Selenium WebDriver automation framework built with Java 11+, TestNG, and Maven. The framework follows the Page Object Model (POM) design pattern and implements clean, maintainable automation practices for testing web applications.

## Code Style and Standards

### General Guidelines
- Use Java 11+ features and syntax
- Follow camelCase naming convention for methods and variables
- Use PascalCase for class names
- Add comprehensive JavaDoc comments for all public methods
- Implement proper error handling with meaningful error messages
- Use SLF4J/Log4j2 for logging with appropriate log levels

### Framework-Specific Patterns

#### Page Object Model (POM)
- All page classes should extend `BasePage`
- Use `@FindBy` annotations for element locators
- Implement business methods that return page objects or meaningful data
- Use descriptive element names in logs
- Prefer CSS selectors over XPath when possible

#### Test Structure
- All test classes should extend `BaseTest`
- Use TestNG annotations (`@Test`, `@BeforeMethod`, `@AfterMethod`)
- Include descriptive test names and descriptions
- Use assertions with meaningful messages
- Implement proper test data management

#### Wait Strategy
- Always use explicit waits via `WaitUtils` class
- No Thread.sleep() calls - use WebDriverWait instead
- Implement custom wait conditions when needed
- Handle dynamic content with appropriate wait strategies

#### Error Handling
- Wrap Selenium operations in try-catch blocks
- Log errors with context information
- Take screenshots on failures using `ScreenshotUtils`
- Throw meaningful exceptions with descriptive messages

### Dependencies and Libraries
- Selenium WebDriver 4.x
- TestNG for test execution
- WebDriverManager for driver management
- Log4j2 for logging
- Maven for build management
- Apache Commons IO for file operations

### Configuration Management
- Use `ConfigReader` class for all configuration access
- Store configuration in `config.properties`
- Support environment-specific configurations
- Use system properties for runtime overrides

### Utility Classes
- `DriverFactory`: WebDriver instance management with ThreadLocal
- `WaitUtils`: Custom wait implementations
- `ScreenshotUtils`: Screenshot capture functionality
- `ConfigReader`: Configuration file management

## Code Examples

### Page Object Implementation
```java
public class ExamplePage extends BasePage {
    @FindBy(xpath = "//button[@id='submit']")
    private WebElement submitButton;
    
    public ExamplePage(WebDriver driver) {
        super(driver);
    }
    
    public void clickSubmit() {
        clickElement(submitButton, "Submit Button");
    }
}
```

### Test Method Structure
```java
@Test(description = "Verify example functionality")
public void testExampleFunctionality() {
    logger.info("Starting example test");
    
    ExamplePage page = new ExamplePage(driver);
    Assert.assertTrue(page.isLoaded(), "Page should be loaded");
    
    logger.info("Test completed successfully");
}
```

### Wait Implementation
```java
// Use WaitUtils for explicit waits
WebElement element = waitUtils.waitForElementToBeClickable(locator);

// Handle dynamic content
waitUtils.waitForTextToBePresentInElement(locator, expectedText);
```

## Testing Patterns
- Use data-driven testing approaches when applicable
- Implement cross-browser testing capabilities
- Support parallel test execution
- Use TestNG groups for test categorization
- Implement proper test reporting

## Best Practices
1. **Maintainability**: Write self-documenting code with clear method names
2. **Reliability**: Use stable locators and robust wait strategies
3. **Scalability**: Design for easy addition of new pages and tests
4. **Debugging**: Include comprehensive logging and screenshot capture
5. **Performance**: Optimize wait times and resource usage

When generating code for this framework, ensure it follows these patterns and integrates seamlessly with the existing codebase structure.
