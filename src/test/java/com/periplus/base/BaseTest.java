package com.periplus.base;

import com.periplus.pages.AccountPage;
import com.periplus.pages.HomePage;
import com.periplus.pages.LoginPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class BaseTest {

    protected WebDriver driver;
    protected String baseUrl;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        baseUrl = System.getProperty("baseUrl", "https://www.periplus.com");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected HomePage openHomePage() {
        return new HomePage(driver, baseUrl).open();
    }

    protected AccountPage loginAsRegisteredUser() {
        String email = getRequiredSetting("periplus.email", "PERIPLUS_EMAIL", "periplus.password", "PERIPLUS_PASSWORD");
        String password = getRequiredSetting("periplus.password", "PERIPLUS_PASSWORD", "periplus.email", "PERIPLUS_EMAIL");

        LoginPage loginPage = new LoginPage(driver, baseUrl).open();

        if (!loginPage.isLoginFormDisplayed()) {
            throw new IllegalStateException("The login page did not load as expected.");
        }

        loginPage.loginAs(email, password);

        AccountPage accountPage = new AccountPage(driver, baseUrl).open();
        if (!accountPage.isAuthenticated()) {
            throw new IllegalStateException("Login did not produce an authenticated account session.");
        }

        return accountPage;
    }

    private String getRequiredSetting(
            String propertyName,
            String envName,
            String siblingPropertyName,
            String siblingEnvName
    ) {
        String fromProperty = System.getProperty(propertyName);
        if (fromProperty != null && !fromProperty.isBlank()) {
            return fromProperty;
        }

        String fromEnvironment = System.getenv(envName);
        if (fromEnvironment != null && !fromEnvironment.isBlank()) {
            return fromEnvironment;
        }

        throw new IllegalStateException(
                "Missing login credentials. Set -D" + propertyName + "=... and -D"
                        + siblingPropertyName + "=... or define " + envName + " and "
                        + siblingEnvName + "."
        );
    }
}
