# Petstore API Test Framework

A comprehensive REST API testing framework for Swagger Petstore using Java 11, TestNG, RestAssured, Allure reporting, and JavaFaker for realistic test data generation.

## 🚀 Features

- **Complete CRUD Testing**: POST, GET, PUT, DELETE operations for /pet endpoints
- **Realistic Test Data**: JavaFaker integration for dynamic, realistic test data generation
- **Positive & Negative Scenarios**: Comprehensive test coverage including error cases
- **Advanced Allure Reporting**: Beautiful test reports with request/response details, attachments, and test categorization
- **Clean Architecture**: Well-structured codebase with DTO pattern and helper utilities
- **Test Isolation**: Proper @BeforeMethod/@AfterMethod setup for independent test execution
- **Automated Test Execution**: Maven-based build and test execution
- **Image Upload Testing**: File upload testing for pet images with multipart/form-data

## 📋 Prerequisites

- Java 11 or higher
- Maven 3.6+
- Internet connection (for API calls to petstore.swagger.io)

## 📂 Project Structure

```
src/test/java/
├── base/
│   └── BaseTest.java          # Base configuration and logging for all tests
├── model/dto/
│   ├── Pet.java               # Main Pet DTO with Status enum
│   ├── Category.java          # Category DTO for pet categorization
│   ├── Tag.java               # Tag DTO for pet tagging
│   └── ApiResponse.java       # Standard API response DTO
├── utils/
│   ├── PetHelper.java         # Utility methods for API calls and test data generation
│   └── HttpStatusCode.java    # Enum for HTTP status codes
└── tests/
    └── PetCrudTests.java      # Main test class with all CRUD scenarios

src/test/resources/
├── allure.properties          # Allure configuration
├── test-pet-image.jpg         # Sample image for upload testing
└── testng.xml                 # TestNG test suite configuration

pom.xml                        # Maven dependencies and plugins
```

## 🧪 Test Scenarios

### Positive Tests
1. **Create Pet** - POST /pet with realistic data using JavaFaker
2. **Get Pet by ID** - GET /pet/{id} for existing pet
3. **Update Pet** - PUT /pet with modified realistic data
4. **Delete Pet** - DELETE /pet/{id} with verification
5. **Upload Pet Image** - POST /pet/{id}/uploadImage with multipart/form-data

### Negative Tests
1. **Create Pet with Invalid Body** - POST /pet with malformed JSON (expects 400)
2. **Create Pet with Empty Body** - POST /pet with empty JSON body
3. **Create Pet with Invalid Data Types** - POST /pet with wrong data types
4. **Get Non-existing Pet** - GET /pet/{non-existing-id} (expects 404)
5. **Get Pet with Invalid ID Type** - GET /pet/{string-id} (expects 404)
6. **Get Pet with Negative ID** - GET /pet/{negative-id} (expects 404)
7. **Update Non-existing Pet** - PUT /pet with non-existing ID
8. **Update Pet with Empty Body** - PUT /pet with empty JSON body
9. **Update Pet with Missing ID** - PUT /pet without ID field
10. **Update Pet with Invalid Data Types** - PUT /pet with wrong data types
11. **Delete Non-existing Pet** - DELETE /pet/{non-existing-id} (expects 404)
12. **Delete Pet with Invalid ID Type** - DELETE /pet/{string-id} (expects 400)
13. **Delete Pet with Negative ID** - DELETE /pet/{negative-id} (expects 404)
14. **Find Pets by Invalid Status** - GET /pet/findByStatus with invalid status value

### Test Features
- **Test Isolation**: Each test runs independently with proper setup/cleanup
- **Dynamic Data**: Random pet names, categories, and IDs using JavaFaker
- **Exception Handling**: Graceful handling of RestAssured exceptions for 404 responses
- **Comprehensive Validation**: Validates both response status codes and response body content
- **Test Grouping**: Tests organized into groups (create, get, update, delete, upload)

## 🏃‍♀️ How to Run

### Run All Tests
```bash
mvn clean test
```

### Run Tests and Generate Allure Report
```bash
mvn clean test
mvn allure:report
```

### Open Allure Report
```bash
# Serve and open the report in browser
mvn allure:serve
```

### Run Specific Test Groups
```bash
# Run only positive tests
mvn clean test -Dgroups="create,get,update,delete,upload"

# Run only negative tests  
mvn clean test -Dgroups="negative-update"
```

### Run Specific Test Method
```bash
mvn clean test -Dtest=PetCrudTests#testCreatePet
```

## 📊 Test Reports

### Allure Reports
Allure generates comprehensive HTML reports with:
- **Test execution results** with pass/fail status
- **Request/Response details** for each API call
- **Test steps and annotations** (@Epic, @Feature, @Story)
- **Execution timeline** and duration metrics
- **Failed test details** with error messages
- **Test categorization** by features and stories

Reports are generated in: `target/site/allure-maven-plugin/`

## 🔧 Configuration

### Base URL
The API base URL is configured in `BaseTest.java`:
```java
protected static final String BASE_URI = "https://petstore.swagger.io/v2";
```

### Allure Configuration
Allure settings are in `src/test/resources/allure.properties`:
```properties
allure.results.directory=target/allure-results
```

### Test Suite Configuration
TestNG suite configuration in `src/test/resources/testng.xml`:
```xml
<suite name="Petstore API Test Suite">
    <test name="Pet CRUD Tests">
        <classes>
            <class name="tests.PetCrudTests"/>
        </classes>
    </test>
</suite>
```

## 🛠️ Dependencies

### Core Testing Libraries
- **TestNG 7.8.0**: Test framework with annotations and grouping
- **RestAssured 5.3.2**: REST API testing with fluent interface
- **Allure TestNG 2.24.0**: Advanced reporting and test visualization
- **JavaFaker 1.0.2**: Realistic test data generation
- **Awaitility 4.2.0**: Asynchronous testing with polling mechanisms

### Supporting Libraries
- **Jackson 2.15.2**: JSON serialization/deserialization
- **SLF4J 2.0.7**: Logging framework integration