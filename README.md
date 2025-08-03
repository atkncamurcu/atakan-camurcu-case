# Atakan Camurcu Test Automation Portfolio

This repository contains comprehensive automation and API testing frameworks for demonstration purposes, showcasing modern testing practices and tools.

## Projects Overview

This portfolio includes two complete test automation frameworks:
- **Selenium WebDriver Framework** for UI automation testing
- **REST API Testing Framework** for comprehensive API testing

Both projects follow industry best practices with clean architecture, proper reporting, and maintainable code structure.

## Projects

### 1. Selenium Automation Framework

**Directory:** [/selenium-automation](/selenium-automation)

A comprehensive Selenium WebDriver automation framework built with Java, TestNG, and Maven following the Page Object Model (POM) design pattern for testing the Insider website.

**Key Features:**
- Page Object Model implementation
- Cross-browser support (Chrome, Firefox)
- Automatic WebDriver management
- Screenshot capture on failures
- Explicit waits with custom utilities
- Comprehensive logging with Log4j2

**How to run:**
```bash
cd selenium-automation
mvn clean test
```

For more details, check the project [README](/selenium-automation/README.md).

### 2. API Test Framework

**Directory:** [/api-test](/api-test)

A comprehensive REST API testing framework for Swagger Petstore using Java 11, TestNG, RestAssured, Allure reporting, and JavaFaker for realistic test data generation.

**Key Features:**
- Complete CRUD testing for /pet endpoints
- Realistic test data generation with JavaFaker
- Advanced Allure reporting with request/response details
- Test isolation with proper setup/cleanup
- Exception handling for negative scenarios
- Comprehensive DTO models (Pet, Category, Tag, ApiResponse)
- Image upload testing for pets with multipart/form-data
- Extensive negative testing (14+ negative scenarios)

**How to run:**
```bash
cd api-test
mvn clean test
```

For more details, check the project [README](/api-test/README.md).