package com.periplus.pages;

import com.periplus.pages.base.BasePage;
import org.openqa.selenium.WebDriver;

public class AccountPage extends BasePage {

    private final String baseUrl;

    public AccountPage(WebDriver driver, String baseUrl) {
        super(driver);
        this.baseUrl = baseUrl;
    }

    // Transition Methods

    public AccountPage open() {
        visit(baseUrl + "/account/Your-Account");
        return this;
    }

    // Getter Methods

    public boolean isAuthenticated() {
        String currentUrl = getCurrentUrl();
        return currentUrl.contains("/account/Your-Account") && !currentUrl.contains("/account/Login");
    }
}
