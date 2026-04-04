package com.periplus.pages;

import com.periplus.pages.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage extends BasePage {

    private final String baseUrl;

    private final By searchInput = By.id("filter_name_desktop");
    private final By searchButton = By.cssSelector(".search-bar button[type='submit']");

    public HomePage(WebDriver driver, String baseUrl) {
        super(driver);
        this.baseUrl = baseUrl;
    }

    // Transition Methods

    public HomePage open() {
        visit(baseUrl);
        return this;
    }

    public SearchResultsPage submitSearch() {
        click(searchButton);
        waitForUrlContains("/product/Search");
        return new SearchResultsPage(driver);
    }

    public CartPage openCart() {
        openCartFromHeaderDropdown();
        return new CartPage(driver);
    }

    // Getter Methods

    public boolean isSearchInputDisplayed() {
        return isVisible(searchInput);
    }

    public int getCartCount() {
        return getHeaderCartCountFromDropdown();
    }

    // Setter Methods

    public HomePage setSearchKeyword(String keyword) {
        type(searchInput, keyword);
        return this;
    }

    // Convenience Methods

    public SearchResultsPage searchFor(String keyword) {
        return setSearchKeyword(keyword).submitSearch();
    }
}
