package com.periplus.tests;

import com.periplus.base.BaseTest;
import com.periplus.pages.AccountPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginPageTest extends BaseTest {

    @Test(description = "Authenticate a registered user before running the cart suite.")
    public void shouldLoginSuccessfully() {
        AccountPage accountPage = loginAsRegisteredUser();

        Assert.assertTrue(accountPage.isAuthenticated(), "Expected a logged-in account page.");
        Assert.assertTrue(
                accountPage.getCurrentUrl().contains("/account/Your-Account"),
                "Expected the authenticated account URL to be opened."
        );
    }
}
