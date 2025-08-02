package pages;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import utils.ConfigReader;

/**
 * Careers Page class representing the Insider careers page
 */
public class CareersPage extends BasePage {

    private WebDriverWait wait;

    @FindBy(xpath = "//h1[contains(text(), 'Career')] | //h2[contains(text(), 'Career')] | //*[@class*='career-page']")
    private WebElement careersPageTitle;

    @FindBy(xpath = "//h2[contains(text(), 'Locations')] | //h3[contains(text(), 'Our Locations')] | //*[@id='career-our-location']")
    private WebElement locationsSection;

    @FindBy(css = "#career-our-location .category-title-media, [class*='location-info'], [class*='office-location']")
    private List<WebElement> locationElements;

    @FindBy(xpath = "//h2[contains(text(), 'Teams')] | //h3[contains(text(), 'Find your calling')] | //*[@id='career-find-our-calling']")
    private WebElement teamsSection;

    @FindBy(css = "#career-find-our-calling .job-item, [class*='team'], [class*='department-card']")
    private List<WebElement> teamElements;

    @FindBy(xpath = "//h2[contains(text(), 'Life at Insider')] | //*[@class*='life'] | //*[@data-section='life']")
    private WebElement lifeAtInsiderSection;

    @FindBy(css = "[class*='life'], [class*='culture'], .elementor-heading-title")
    private List<WebElement> lifeAtInsiderElements;

    @FindBy(xpath = "//a[@href='/careers/quality-assurance/'] | //a[contains(@href, 'quality-assurance')]")
    private WebElement qualityAssuranceLink;

    /**
     * Constructor for CareersPage
     * @param driver WebDriver instance
     */
    public CareersPage(WebDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /**
     * Verify if careers page is loaded successfully
     * @return true if careers page is loaded
     */
    public boolean isCareersPageLoaded() {
        try {
            wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOf(careersPageTitle),
                ExpectedConditions.urlContains("careers")
            ));

            closeInsiderPopupIfPresent();

            boolean urlCheck = getCurrentUrl().toLowerCase().contains("careers");
            logger.info("URL contains 'careers': {}", urlCheck);
            return urlCheck;
        } catch (Exception e) {
            logger.error("Failed to verify careers page load", e);
            return false;
        }
    }

    /**
     * Verify Locations section is visible
     * @return true if Locations section is visible
     */
    public boolean isLocationsSectionVisible() {
        try {
            if (isElementDisplayed(locationsSection, "Locations Section")) {
                return true;
            }
            for (WebElement element : locationElements) {
                if (isElementDisplayed(element, "Location Element")) {
                    logger.info("Found Locations section using alternative selector");
                    return true;
                }
            }
            logger.warn("Locations section not found with any selector");
            return false;
        } catch (Exception e) {
            logger.error("Failed to check Locations section", e);
            return false;
        }
    }

    /**
     * Verify Teams section is visible
     * @return true if Teams section is visible
     */
    public boolean isTeamsSectionVisible() {
        try {
            if (isElementDisplayed(teamsSection, "Teams Section")) {
                return true;
            }
            for (WebElement element : teamElements) {
                if (isElementDisplayed(element, "Team Element")) {
                    logger.info("Found Teams section using alternative selector");
                    return true;
                }
            }
            logger.warn("Teams section not found with any selector");
            return false;
        } catch (Exception e) {
            logger.error("Failed to check Teams section", e);
            return false;
        }
    }

    /**
     * Verify Life at Insider section is visible
     * @return true if Life at Insider section is visible
     */
    public boolean isLifeAtInsiderSectionVisible() {
        try {
            if (isElementDisplayed(lifeAtInsiderSection, "Life at Insider Section")) {
                return true;
            }
            for (WebElement element : lifeAtInsiderElements) {
                if (isElementDisplayed(element, "Life at Insider Element")) {
                    logger.info("Found Life at Insider section using alternative selector");
                    return true;
                }
            }
            logger.warn("Life at Insider section not found with any selector");
            return false;
        } catch (Exception e) {
            logger.error("Failed to check Life at Insider section", e);
            return false;
        }
    }

    /**
     * Verify all required sections are visible
     * @return true if all sections are visible
     */
    public boolean areAllSectionsVisible() {
        boolean locations = isLocationsSectionVisible();
        boolean teams = isTeamsSectionVisible();
        boolean lifeAtInsider = isLifeAtInsiderSectionVisible();

        logger.info("Sections visibility - Locations: {}, Teams: {}, Life at Insider: {}", 
                    locations, teams, lifeAtInsider);
        return locations && teams && lifeAtInsider;
    }

    /**
     * Navigate to Quality Assurance careers page
     * @return QualityAssurancePage instance
     */
    public QualityAssurancePage navigateToQualityAssurance() {
        try {
            logger.info("Navigating to Quality Assurance careers page");
            String qaUrl = ConfigReader.getBaseUrl() + "careers/quality-assurance/";
            navigateToUrl(qaUrl);
            wait.until(ExpectedConditions.urlContains("quality-assurance"));
            logger.info("Successfully navigated to Quality Assurance page");
            return new QualityAssurancePage(driver);
        } catch (Exception e) {
            logger.error("Failed to navigate to Quality Assurance page", e);
            throw new RuntimeException("Navigation to Quality Assurance page failed", e);
        }
    }

    /**
     * Click on Quality Assurance link if present
     */
    public void clickQualityAssuranceLink() {
        if (isElementDisplayed(qualityAssuranceLink, "Quality Assurance Link")) {
            clickElement(qualityAssuranceLink, "Quality Assurance Link");
        } else {
            logger.warn("Quality Assurance link not found on careers page");
        }
    }
}
