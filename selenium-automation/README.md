# Insider Automation Framework

A comprehensive Selenium WebDriver automation framework built with Java, TestNG, and Maven following the Page Object Model (POM) design pattern for testing the Insider website (https://useinsider.com).

## 🏗️ Framework Architecture

```
├── pom.xml                             # Maven project configuration
├── src/
│   ├── main/java/
│   │   ├── pages/                      # Page Object Model classes
│   │   │   ├── BasePage.java           # Base page with common functionality
│   │   │   ├── HomePage.java           # Home page interactions
│   │   │   ├── CareersPage.java        # Careers page interactions
│   │   │   └── QualityAssurancePage.java  # QA careers page interactions
│   │   └── utils/                      # Utility classes
│   │       ├── ConfigReader.java       # Configuration management
│   │       ├── DriverFactory.java      # WebDriver initialization
│   │       ├── WaitUtils.java          # Custom wait implementations
│   │       └── ScreenshotUtils.java    # Screenshot capture functionality
│   ├── main/resources/
│   │   ├── config.properties           # Test configuration
│   │   └── log4j2.xml                  # Logging configuration
│   ├── test/java/
│   │   ├── tests/                      # Test classes
│   │   │   ├── BaseTest.java           # Base test setup and teardown
│   │   │   └── InsiderAutomationTest.java  # Main test scenarios
│   │   └── listeners/                  # TestNG listeners
│   │       └── ScreenshotListener.java # Failure screenshot capture
│   └── test/resources/
│       └── testng.xml                  # TestNG configuration
└── test-output/                        # Test execution outputs
    ├── screenshots/                    # Failed test screenshots
    └── logs/                           # Execution logs
```

## 🚀 Features

### Core Framework Features
- ✅ **Page Object Model (POM)** implementation
- ✅ **Cross-browser support** (Chrome, Firefox)
- ✅ **Automatic WebDriver management** using WebDriverManager
- ✅ **Screenshot capture** on test failures
- ✅ **Explicit waits** with custom wait utilities
- ✅ **Comprehensive logging** with Log4j2
- ✅ **Configuration management** via properties file
- ✅ **TestNG integration** with listeners

### Test Scenarios Covered
1. **Insider Home Page Verification**: Navigate to https://useinsider.com and verify successful load
2. **Insider Career Page Navigation**: Access Careers from Company menu and verify sections (Locations, Teams, Life at Insider)
3. **Insider QA Jobs Filtering**: Navigate to Quality Assurance careers, apply filters, and verify job listings
4. **Insider Job Application Flow**: Click "View Role" and verify redirect to Insider application form

## 🛠️ Prerequisites

- **Java 11+** (JDK 11 or higher)
- **Maven 3.6+**
- **Chrome browser** (latest version)
- **Firefox browser** (optional, for cross-browser testing)

## 📦 Installation & Setup

### 1. Clone the Repository
```bash
git clone https://github.com/atkncamurcu/atakan-camurcu-case
cd atakan-camurcu-case/selenium-automation
```

### 2. Install Dependencies
```bash
mvn clean install
```

### Test Configuration
Update `src/main/resources/config.properties` as needed:
```properties
# Insider Website Configuration
base.url=https://useinsider.com/

# Browser Configuration
browser=chrome
headless=false
implicit.wait=10
explicit.wait=20

# Screenshot Configuration
screenshot.on.failure=true
screenshot.path=test-output/screenshots/
```

## 🏃‍♂️ Execution

### Run All Tests
```bash
mvn clean test
```

### Run Specific Test Suite
```bash
mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/testng.xml
```

### Run with Specific Browser
```bash
# Chrome (default)
mvn clean test -Pbrowser-chrome

# Firefox
mvn clean test -Pbrowser-firefox
```

### Run in Headless Mode
Update `config.properties`:
```properties
headless=true
```
Then run:
```bash
mvn clean test
```

### Run Specific Test Class
```bash
mvn clean test -Dtest=InsiderAutomationTest
```

### Run Specific Test Method
```bash
mvn clean test -Dtest=InsiderAutomationTest#testInsiderAutomationScenario
```

## 📊 Test Results

### Screenshots
Failed test screenshots are automatically saved to:
- `test-output/screenshots/`

### Logs
Detailed execution logs are available at:
- `test-output/logs/automation.log`

Test execution can be monitored through console output during test runs.

## 🧪 Test Cases

### 1. Main Automation Scenario (`testInsiderAutomationScenario`)
Complete end-to-end test covering:
- Home page load verification
- Career page navigation and section verification
- QA jobs filtering by location (Istanbul, Turkey) and department
- Job listing validation
- Application form redirect verification

### 2. Home Page Load Test (`testHomePageLoad`)
- Verifies home page loads successfully
- Validates page title and URL

### 3. Careers Page Navigation Test (`testCareersPageNavigation`)
- Tests navigation from home page to careers page
- Verifies careers page loads correctly

### 4. QA Jobs Filtering Test (`testQAJobsFiltering`)
- Direct navigation to QA careers page
- Tests filtering functionality
- Validates job list population

## 🏗️ Framework Components

### Page Objects
- **BasePage**: Common functionality for all page objects
- **HomePage**: Website home page interactions
- **CareersPage**: Careers page navigation and verification
- **QualityAssurancePage**: QA jobs filtering and validation

### Utilities
- **DriverFactory**: WebDriver instance management
- **ConfigReader**: Configuration file management
- **WaitUtils**: Custom wait implementations
- **ScreenshotUtils**: Screenshot capture functionality

### Test Infrastructure
- **BaseTest**: Common test setup and teardown
- **ScreenshotListener**: TestNG listener for failure screenshots
