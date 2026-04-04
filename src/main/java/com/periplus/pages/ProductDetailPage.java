package com.periplus.pages;

import com.periplus.pages.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ProductDetailPage extends BasePage {

    private final By productTitle = By.cssSelector("h2");
    private final By addToCartButton = By.cssSelector("button.btn-add-to-cart");

    public ProductDetailPage(WebDriver driver) {
        super(driver);
        waitForVisible(productTitle);
        waitForClickable(addToCartButton);
    }

    public String getProductTitle() {
        return getText(productTitle);
    }

    public int getHeaderCartCount() {
        return getHeaderCartCountFromDropdown();
    }

    public void addToCart() {
        int initialCartCount = getHeaderCartCount();

        click(addToCartButton);

        waitForCondition(driverInstance ->
                extractDigits(driverInstance.findElement(By.id("cart_total")).getText()) == initialCartCount + 1
        );

    }

    public CartPage openCart() {
        openCartFromHeaderDropdown();
        return new CartPage(driver);
    }
}
