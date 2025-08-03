package base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

/**
 * Base test class for API tests
 * Contains common configuration and setup for RestAssured
 */
public class BaseTest {
    
    protected static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
    protected static final String BASE_URI = "https://petstore.swagger.io/v2";
    
    protected RequestSpecification requestSpec;
    protected ResponseSpecification responseSpec;
    
    @BeforeClass
    public void setUp() {
        logger.info("Setting up API test configuration");
        
        RestAssured.baseURI = BASE_URI;
        
        requestSpec = new RequestSpecBuilder()
                .setContentType("application/json")
                .setAccept("application/json")
                .addFilter(new AllureRestAssured())
                .log(LogDetail.ALL)
                .build();
        
        responseSpec = new ResponseSpecBuilder()
                .log(LogDetail.ALL)
                .build();
        
        RestAssured.requestSpecification = requestSpec;

        logger.info("API test configuration completed. Base URI: {}", BASE_URI);
    }
    
    /**
     * Get the base URI for tests
     * @return base URI string
     */
    protected String getBaseUri() {
        return BASE_URI;
    }
    
    /**
     * Log test start
     * @param testName name of the test
     */
    protected void logTestStart(String testName) {
        logger.info("========== Starting Test: {} ==========", testName);
    }
    
    /**
     * Log test end
     * @param testName name of the test
     */
    protected void logTestEnd(String testName) {
        logger.info("========== Completed Test: {} ==========", testName);
    }
}
