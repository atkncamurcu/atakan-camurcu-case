package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Home Page class representing the Insider home page
 */
public class HomePage extends BasePage {

    @FindBy(css = "a.navbar-brand img, a[class*='navbar-brand'] img, img[class*='logo']")
    private WebElement logo;

    @FindBy(xpath = "//nav//a[contains(normalize-space(text()), 'Company') or contains(@href, 'company')]")
    private WebElement companyMenu;

    @FindBy(xpath = "//nav//a[contains(@href, '/careers') or contains(normalize-space(text()), 'Careers')]")
    private WebElement careersLink;

    @FindBy(css = "#desktop_hero_24, .hp_hero_with_animation, [class*='HeroContentContainer']")
    private WebElement heroSection;

    @FindBy(id = "wt-cli-accept-all-btn")
    private WebElement acceptCookiesButton;

    @FindBy(css = "a.wt-cli-accept-all-btn, [data-cli_action='accept_all']")
    private WebElement acceptAllButtonFallback;

    /**
     * Constructor for HomePage
     * @param driver WebDriver instance
     */
    public HomePage(WebDriver driver) {
        super(driver);
    }

    /**
     * Check if home page is loaded successfully
     * @return true if home page is loaded
     */
    public boolean isHomePageLoaded() {
        try {
            acceptCookiesIfPresent();
            
            closeInsiderPopupIfPresent();
            
            boolean logoPresent = isElementDisplayed(logo, "Logo");
            boolean companyMenuPresent = isElementDisplayed(companyMenu, "Company Menu");
            
            logger.info("Insider home page loaded successfully - Logo: {}, Company Menu: {}", 
                       logoPresent, companyMenuPresent);
            
            return logoPresent && companyMenuPresent;
        } catch (Exception e) {
            logger.error("Failed to verify Insider home page load", e);
            return false;
        }
    }

    /**
     * Navigate to Insider Careers page from Company menu
     * @return CareersPage instance
     */
    public CareersPage navigateToCareers() {
        try {
            logger.info("Navigating to Insider Careers page from Company menu");
            
            acceptCookiesIfPresent();
            
            closeInsiderPopupIfPresent();
            
            waitUtils.hoverOverElement(companyMenu);
            waitUtils.waitForElementToBeVisible(careersLink);
            
            clickElement(careersLink, "Careers Link");
            
            logger.info("Successfully navigated to Insider Careers page");
            return new CareersPage(driver);
            
        } catch (Exception e) {
            logger.error("Failed to navigate to Insider Careers page", e);
            throw new RuntimeException("Navigation to Insider Careers page failed", e);
        }
    }

    /**
     * Click on Company menu
     */
    public void clickCompanyMenu() {
        clickElement(companyMenu, "Company Menu");
    }

    /**
     * Accept cookies if the button is present
     */
        @Override
    protected void acceptCookiesIfPresent() {
        try {
            try {
                WebElement cookieBanner = driver.findElement(By.id("cookie-law-info-bar"));
                if (cookieBanner.isDisplayed()) {
                    
                    // Try different selectors for Accept All button
                    String[] acceptSelectors = {
                        "#wt-cli-accept-all-btn",
                        ".cli-accept-all-btn",
                        ".accept-cookies",
                        "#cookie-accept",
                        "a.accept-cookies",
                        "button.accept-cookies"
                    };
                    
                    for (String selector : acceptSelectors) {
                        try {
                            WebElement acceptBtn = driver.findElement(By.cssSelector(selector));
                            if (acceptBtn.isDisplayed() && acceptBtn.isEnabled()) {
                                acceptBtn.click();
                                logger.info("Accepted cookies using selector: {}", selector);
                        
                                waitUtils.waitForElementToBeInvisible(By.id("cookie-consent-banner"));
                                return;
                            }
                        } catch (NoSuchElementException ignore) {
                            // Accept button not found with this selector, try next one
                        }
                    }
                    
                    // Fallback to the original element if specific selectors don't work
                    if (isElementDisplayed(acceptCookiesButton, "Accept Cookies Button")) {
                        clickElement(acceptCookiesButton, "Accept Cookies Button");
                        logger.info("Accepted cookies using fallback selector");
                        waitUtils.waitForElementToBeInvisible(By.id("cookie-consent-banner"));
                    }
                }
            } catch (NoSuchElementException e) {
            }
            
        } catch (Exception e) {
        }
    }

    /**
     * Verify home page title contains expected text
     * @param expectedTitleText Expected title text
     * @return true if title contains expected text
     */
    public boolean verifyPageTitle(String expectedTitleText) {
        String actualTitle = getPageTitle();
        boolean titleMatches = actualTitle.toLowerCase().contains(expectedTitleText.toLowerCase());
        logger.info("Page title verification - Expected: '{}', Actual: '{}', Matches: {}", 
                   expectedTitleText, actualTitle, titleMatches);
        return titleMatches;
    }

    /**
     * Verify home page URL contains expected text
     * @param expectedUrlText Expected URL text
     * @return true if URL contains expected text
     */
    public boolean verifyPageUrl(String expectedUrlText) {
        String actualUrl = getCurrentUrl();
        boolean urlMatches = actualUrl.toLowerCase().contains(expectedUrlText.toLowerCase());
        logger.info("Page URL verification - Expected: '{}', Actual: '{}', Matches: {}", 
                   expectedUrlText, actualUrl, urlMatches);
        return urlMatches;
    }

    /**
     * Check if hero section is visible
     * @return true if hero section is visible
     */
    public boolean isHeroSectionVisible() {
        return isElementDisplayed(heroSection, "Hero Section");
    }
}
