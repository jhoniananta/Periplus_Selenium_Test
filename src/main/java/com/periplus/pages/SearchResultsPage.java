package com.periplus.pages;

import com.periplus.pages.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SearchResultsPage extends BasePage {

    private static final String ADDABLE_PRODUCT_CARD_XPATH =
            "//div[contains(@class,'single-product')][.//a[contains(@class,'addtocart')]]";

    private final By productCards = By.xpath(ADDABLE_PRODUCT_CARD_XPATH);

    public SearchResultsPage(WebDriver driver) {
        super(driver);
        waitForCondition(driverInstance -> !driverInstance.findElements(productCards).isEmpty());
    }

    // Getter Methods

    public boolean hasResults() {
        return !findAll(productCards).isEmpty();
    }

    public int getResultCount() {
        return findAll(productCards).size();
    }

    public String getFirstProductName() {
        return getProductName(1);
    }

    public String getFirstProductCurrentPrice() {
        return getProductCurrentPrice(1);
    }

    public String getProductName(int position) {
        waitForProductPosition(position);
        return getText(productName(position));
    }

    public String getProductCurrentPrice(int position) {
        waitForProductPosition(position);
        return extractCurrentPrice(getText(productPrice(position)));
    }

    public String getProductPath(int position) {
        waitForProductPosition(position);
        return normalizeUrlPath(waitForVisible(productName(position)).getDomAttribute("href"));
    }

    public int getHeaderCartCount() {
        return getHeaderCartCountFromDropdown();
    }

    // Transition Methods

    public SearchResultsPage clickFirstProductAddToCart() {
        return clickProductAddToCart(1);
    }

    public SearchResultsPage clickProductAddToCart(int position) {
        waitForProductPosition(position);
        int initialCartCount = getHeaderCartCount();

        hover(productCard(position));
        jsClick(productAddToCart(position));

        waitForCondition(driverInstance ->
                extractDigits(driverInstance.findElement(By.id("cart_total")).getText()) == initialCartCount + 1
        );

        return this;
    }

    public CartPage openCart() {
        openCartFromHeaderDropdown();
        return new CartPage(driver);
    }

    public ProductDetailPage openFirstProductDetail() {
        return openProductDetail(1);
    }

    public ProductDetailPage openProductDetail(int position) {
        waitForProductPosition(position);
        click(productName(position));
        waitForUrlContains("/p/");
        return new ProductDetailPage(driver);
    }

    // Convenience Methods

    public void addFirstProductToCart() {
        addProductToCart(1);
    }

    public void addProductToCart(int position) {
        clickProductAddToCart(position);
    }

    private By productCard(int position) {
        return By.xpath("(" + ADDABLE_PRODUCT_CARD_XPATH + ")[" + position + "]");
    }

    private By productName(int position) {
        return By.xpath("((" + ADDABLE_PRODUCT_CARD_XPATH + ")[" + position + "])//h3/a");
    }

    private By productPrice(int position) {
        return By.xpath(
                "((" + ADDABLE_PRODUCT_CARD_XPATH + ")[" + position + "])//div[contains(@class,'product-price')]"
        );
    }

    private By productAddToCart(int position) {
        return By.xpath("((" + ADDABLE_PRODUCT_CARD_XPATH + ")[" + position + "])//a[contains(@class,'addtocart')]");
    }

    private void waitForProductPosition(int position) {
        if (position < 1) {
            throw new IllegalArgumentException("Product positions are 1-based.");
        }

        waitForCondition(driverInstance -> driverInstance.findElements(productCards).size() >= position);
    }
}
