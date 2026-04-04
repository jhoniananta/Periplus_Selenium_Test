package com.periplus.pages;

import com.periplus.pages.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CartPage extends BasePage {

    private final By cartRows = By.cssSelector(".row-cart-product");
    private final By cartItemTitle = By.cssSelector(".row-cart-product p a");
    private final By quantityInput = By.cssSelector("input[name^='quantity[']");
    private final By plusButton = By.cssSelector("button[data-type='plus']");
    private final By minusButton = By.cssSelector("button[data-type='minus']");
    private final By updateButton = By.cssSelector("input[type='submit'][value='Update']");
    private final By removeButton = By.cssSelector("a[href*='checkout/cart?remove=']");
    private final By continueShoppingLink = By.xpath("//a[contains(normalize-space(),'Continue shopping')]");
    private final By subtotalAmount = By.cssSelector(".right ul li:first-child span");
    private final By totalAmount = By.cssSelector(".right ul li:nth-child(2) span");
    private final By emptyCartMessage = By.xpath(
            "//*[@id='content']/div[contains(@class,'content') and normalize-space()='Your shopping cart is empty']"
    );

    public CartPage(WebDriver driver) {
        super(driver);
        waitForUrlContains("/checkout/cart");
        waitForPageReady();
    }

    // Getter Methods

    public boolean hasItems() {
        return !findAll(cartRows).isEmpty();
    }

    public int getItemCount() {
        return findAll(cartRows).size();
    }

    public boolean isEmpty() {
        return !findAll(emptyCartMessage).isEmpty();
    }

    public String getProductTitle() {
        return getText(cartItemTitle);
    }

    public boolean containsProductTitle(String expectedTitle) {
        String normalizedExpectedTitle = expectedTitle.trim().toLowerCase();

        return findAll(cartItemTitle).stream()
                .map(element -> element.getText().trim().toLowerCase())
                .anyMatch(title -> title.equals(normalizedExpectedTitle));
    }

    public boolean containsProductPath(String expectedProductPath) {
        String normalizedExpectedProductPath = normalizeUrlPath(expectedProductPath);

        return findAll(cartItemTitle).stream()
                .map(element -> normalizeUrlPath(element.getDomAttribute("href")))
                .anyMatch(path -> path.equals(normalizedExpectedProductPath));
    }

    public int getQuantity() {
        return Integer.parseInt(getValue(quantityInput));
    }

    public String getSubtotal() {
        return getText(subtotalAmount);
    }

    public String getTotal() {
        return getText(totalAmount);
    }

    public int getSubtotalValue() {
        return parseRupiah(getSubtotal());
    }

    public int getTotalValue() {
        return parseRupiah(getTotal());
    }

    public String getEmptyCartMessage() {
        return getText(emptyCartMessage);
    }

    public int getHeaderCartCount() {
        return getHeaderCartCountFromDropdown();
    }

    // Setter Methods

    public CartPage increaseQuantityByOne() {
        int currentQuantity = getQuantity();
        click(plusButton);
        waitForCondition(driverInstance ->
                Integer.parseInt(driverInstance.findElement(quantityInput).getDomProperty("value")) == currentQuantity + 1
        );
        return this;
    }

    public CartPage decreaseQuantityByOne() {
        int currentQuantity = getQuantity();
        click(minusButton);
        int expectedQuantity = Math.max(1, currentQuantity - 1);

        waitForCondition(driverInstance ->
                Integer.parseInt(driverInstance.findElement(quantityInput).getDomProperty("value")) == expectedQuantity
        );

        return this;
    }

    // Transition Methods

    public CartPage submitCartUpdate() {
        click(updateButton);
        waitForUrlContains("/checkout/cart");
        waitForPageReady();
        return this;
    }

    public CartPage clickRemoveItem() {
        int initialRowCount = findAll(cartRows).size();

        click(removeButton);
        waitForCondition(driverInstance ->
                driverInstance.findElements(cartRows).size() < initialRowCount
                        || !driverInstance.findElements(emptyCartMessage).isEmpty()
                        || driverInstance.findElements(cartRows).isEmpty()
        );
        waitForPageReady();
        return this;
    }

    public CartPage clearCart() {
        while (hasItems()) {
            clickRemoveItem();
        }

        waitForCondition(driverInstance ->
                !driverInstance.findElements(emptyCartMessage).isEmpty()
                        || driverInstance.findElements(cartRows).isEmpty()
        );

        return this;
    }

    public HomePage clickContinueShopping(String baseUrl) {
        click(continueShoppingLink);
        return new HomePage(driver, baseUrl);
    }

    // Convenience Methods

    public CartPage incrementQuantity() {
        return increaseQuantityByOne();
    }

    public CartPage decrementQuantity() {
        return decreaseQuantityByOne();
    }

    public CartPage updateCart() {
        return submitCartUpdate();
    }

    public CartPage removeItem() {
        return clickRemoveItem();
    }

    public CartPage clear() {
        return clearCart();
    }

    public HomePage continueShopping(String baseUrl) {
        return clickContinueShopping(baseUrl);
    }
}
