package com.periplus.pages.base;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class BasePage {

    private static final Pattern RUPIAH_PATTERN = Pattern.compile("Rp\\s*[\\d.,]+");
    private static final By PRELOADER = By.cssSelector(".preloader");
    private static final By DESKTOP_CART_COUNT = By.id("cart_total");
    private static final By DESKTOP_CART_DROPDOWN = By.id("show-your-cart");
    private static final By DROPDOWN_CART_HEADER_LINK =
            By.cssSelector("#show-your-cart .shopping-item .dropdown-cart-header a[href*='checkout/cart']");

    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final JavascriptExecutor javascriptExecutor;
    protected final Actions actions;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        this.javascriptExecutor = (JavascriptExecutor) driver;
        this.actions = new Actions(driver);
    }

    protected void visit(String url) {
        driver.get(url);
        waitForPageReady();
    }

    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void click(By locator) {
        waitForInteractionReady();
        WebElement element = waitForClickable(locator);
        scrollIntoView(element);

        try {
            element.click();
        } catch (ElementClickInterceptedException exception) {
            waitForInteractionReady();
            jsClick(locator);
        } catch (RuntimeException exception) {
            jsClick(locator);
        }
    }

    protected void jsClick(By locator) {
        waitForInteractionReady();
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        scrollIntoView(element);
        javascriptExecutor.executeScript("arguments[0].click();", element);
    }

    protected void type(By locator, String text) {
        waitForInteractionReady();
        WebElement field = waitForVisible(locator);
        scrollIntoView(field);
        try {
            field.click();
        } catch (ElementClickInterceptedException exception) {
            waitForInteractionReady();
            javascriptExecutor.executeScript("arguments[0].focus();", field);
        }
        field.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        field.sendKeys(Keys.DELETE);
        field.sendKeys(text);
    }

    protected void hover(By locator) {
        waitForInteractionReady();
        WebElement element = waitForVisible(locator);
        scrollIntoView(element);
        actions.moveToElement(element).pause(Duration.ofMillis(300)).perform();
    }

    protected String getText(By locator) {
        return waitForVisible(locator).getText().trim();
    }

    protected String getValue(By locator) {
        return waitForVisible(locator).getDomProperty("value");
    }

    protected List<WebElement> findAll(By locator) {
        return driver.findElements(locator);
    }

    protected boolean isVisible(By locator) {
        try {
            return waitForVisible(locator).isDisplayed();
        } catch (TimeoutException exception) {
            return false;
        }
    }

    protected void waitForUrlContains(String urlPart) {
        wait.until(ExpectedConditions.urlContains(urlPart));
    }

    protected void waitForPageReady() {
        wait.until(driver -> "complete".equals(javascriptExecutor.executeScript("return document.readyState")));
        waitForInteractionReady();
    }

    protected void waitForCondition(ExpectedCondition<?> condition) {
        wait.until(condition);
    }

    protected int extractDigits(String text) {
        String digitsOnly = text.replaceAll("\\D", "");
        return digitsOnly.isEmpty() ? 0 : Integer.parseInt(digitsOnly);
    }

    protected int parseRupiah(String value) {
        return extractDigits(value);
    }

    protected String extractCurrentPrice(String rawPriceText) {
        Matcher matcher = RUPIAH_PATTERN.matcher(rawPriceText);
        String lastPrice = null;

        while (matcher.find()) {
            lastPrice = matcher.group();
        }

        return lastPrice == null ? rawPriceText.trim() : lastPrice.trim();
    }

    protected int getHeaderCartCountFromDropdown() {
        return extractDigits(getText(DESKTOP_CART_COUNT));
    }

    protected String normalizeUrlPath(String rawUrl) {
        if (rawUrl == null || rawUrl.isBlank()) {
            return "";
        }

        try {
            return URI.create(rawUrl.trim()).getPath().toLowerCase();
        } catch (IllegalArgumentException exception) {
            int queryIndex = rawUrl.indexOf('?');
            String withoutQuery = queryIndex >= 0 ? rawUrl.substring(0, queryIndex) : rawUrl;
            return withoutQuery.trim().toLowerCase();
        }
    }

    protected void waitForInteractionReady() {
        wait.until(driverInstance -> {
            List<WebElement> preloaders = driverInstance.findElements(PRELOADER);

            if (preloaders.isEmpty()) {
                return true;
            }

            for (WebElement preloader : preloaders) {
                try {
                    Object result = javascriptExecutor.executeScript(
                            "const el = arguments[0];"
                                    + " const style = window.getComputedStyle(el);"
                                    + " const rect = el.getBoundingClientRect();"
                                    + " return style.display === 'none'"
                                    + " || style.visibility === 'hidden'"
                                    + " || style.pointerEvents === 'none'"
                                    + " || rect.width === 0"
                                    + " || rect.height === 0;",
                            preloader
                    );

                    if (!Boolean.TRUE.equals(result)) {
                        return false;
                    }
                } catch (StaleElementReferenceException ignored) {
                    // The preloader DOM can be replaced during transitions.
                }
            }

            return true;
        });
    }

    protected void openCartFromHeaderDropdown() {
        waitForInteractionReady();
        WebElement cartDropdown = waitForVisible(DESKTOP_CART_DROPDOWN);
        scrollIntoView(cartDropdown);
        actions.moveToElement(cartDropdown).pause(Duration.ofMillis(250)).perform();
        javascriptExecutor.executeScript(
                "['mouseover','mouseenter','mousemove'].forEach(type => arguments[0].dispatchEvent("
                        + "new MouseEvent(type, {view: window, bubbles: true, cancelable: true})));\n"
                        + "const anchor = arguments[0].querySelector('a.single-icon');\n"
                        + "if (anchor) {\n"
                        + "  ['mouseover','mouseenter','mousemove'].forEach(type => anchor.dispatchEvent("
                        + "new MouseEvent(type, {view: window, bubbles: true, cancelable: true})));\n"
                        + "}",
                cartDropdown
        );

        WebElement dropdownLink = wait.until(ExpectedConditions.presenceOfElementLocated(DROPDOWN_CART_HEADER_LINK));
        javascriptExecutor.executeScript("arguments[0].click();", dropdownLink);
        waitForUrlContains("/checkout/cart");
        waitForPageReady();
    }

    protected void scrollIntoView(By locator) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        scrollIntoView(element);
    }

    private void scrollIntoView(WebElement element) {
        try {
            javascriptExecutor.executeScript(
                    "arguments[0].scrollIntoView({block: 'center', inline: 'center'});",
                    element
            );
        } catch (StaleElementReferenceException ignored) {
            // The next interaction will re-locate the element if the DOM refreshed.
        }
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getPageTitle() {
        return driver.getTitle();
    }
}
