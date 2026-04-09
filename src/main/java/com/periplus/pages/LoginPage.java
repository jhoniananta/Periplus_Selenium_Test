package com.periplus.pages;

import com.periplus.pages.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    private final String baseUrl;

    private final By pageHeading = By.xpath("//*[contains(normalize-space(),'Sign In to Your Account')]");
    private final By emailInput = By.name("email");
    private final By passwordInput = By.id("ps");
    private final By loginButton = By.id("button-login");

    public LoginPage(WebDriver driver, String baseUrl) {
        super(driver);
        this.baseUrl = baseUrl;
    }

    // Transition Methods

    public LoginPage open() {
        visit(baseUrl + "/account/Login");
        return this;
    }

    public HomePage submitLogin() {
        click(loginButton);
        return new HomePage(driver, baseUrl);
    }

    // Getter Methods

    public boolean isLoginFormDisplayed() {
        return isVisible(pageHeading) && isVisible(emailInput) && isVisible(passwordInput);
    }

    // Setter Methods

    public LoginPage setEmail(String email) {
        type(emailInput, email);
        return this;
    }

    public LoginPage setPassword(String password) {
        type(passwordInput, password);
        return this;
    }

    // Convenience Methods

    public HomePage loginAs(String email, String password) {
        return setEmail(email)
                .setPassword(password)
                .submitLogin();
    }
}
