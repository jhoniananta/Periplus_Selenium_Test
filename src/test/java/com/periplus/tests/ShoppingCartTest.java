package com.periplus.tests;

import com.periplus.base.BaseTest;
import com.periplus.pages.CartPage;
import com.periplus.pages.HomePage;
import com.periplus.pages.ProductDetailPage;
import com.periplus.pages.SearchResultsPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ShoppingCartTest extends BaseTest {

    private static final String SEARCH_KEYWORD = "harry potter";

    @BeforeMethod(alwaysRun = true)
    public void loginBeforeCartTests() {
        loginAsRegisteredUser();
        driver.get(baseUrl + "/checkout/cart");
        new CartPage(driver).clear();
    }

    @Test(description = "Open the first product detail page, add the product to the cart, and verify it appears in the cart.")
    public void shouldAddProductToCartFromProductDetailPage() {
        HomePage homePage = openHomePage();
        SearchResultsPage searchResultsPage = homePage.searchFor(SEARCH_KEYWORD);

        Assert.assertTrue(searchResultsPage.hasResults(), "Expected at least one search result.");

        ProductDetailPage productDetailPage = searchResultsPage.openFirstProductDetail();
        String expectedTitle = productDetailPage.getProductTitle();
        int initialCartCount = productDetailPage.getHeaderCartCount();

        productDetailPage.addToCart();

        Assert.assertEquals(
                productDetailPage.getHeaderCartCount(),
                initialCartCount + 1,
                "Expected cart count to increase after adding from the product detail page."
        );

        CartPage cartPage = productDetailPage.openCart();

        Assert.assertTrue(cartPage.hasItems(), "Expected the cart to contain at least one product.");
        Assert.assertTrue(
                cartPage.containsProductTitle(expectedTitle),
                "Expected the selected product detail item to appear in the cart."
        );
    }

    @Test(description = "Update the cart quantity and verify subtotal and total are recalculated.")
    public void shouldUpdateCartQuantityAndTotals() {
        CartPage cartPage = addFirstSearchResultToCart();
        int unitPrice = cartPage.getSubtotalValue();

        cartPage.incrementQuantity()
                .updateCart();

        Assert.assertEquals(cartPage.getQuantity(), 2, "Expected quantity to update to 2.");
        Assert.assertEquals(cartPage.getSubtotalValue(), unitPrice * 2, "Expected subtotal to double.");
        Assert.assertEquals(cartPage.getTotalValue(), unitPrice * 2, "Expected total to double.");
    }

    @Test(description = "Prevent quantity from decreasing below the minimum allowed value.")
    public void shouldKeepMinimumQuantityAtOne() {
        CartPage cartPage = addFirstSearchResultToCart();
        int originalSubtotal = cartPage.getSubtotalValue();

        cartPage.decrementQuantity()
                .updateCart();

        Assert.assertEquals(cartPage.getQuantity(), 1, "Expected quantity to stay at 1.");
        Assert.assertEquals(cartPage.getSubtotalValue(), originalSubtotal, "Expected subtotal to remain unchanged.");
    }

    @Test(description = "Remove the last cart item and verify the cart becomes empty.")
    public void shouldRemoveProductFromCart() {
        CartPage cartPage = addFirstSearchResultToCart();

        cartPage.removeItem();

        Assert.assertTrue(cartPage.isEmpty(), "Expected the shopping cart to become empty.");
        Assert.assertEquals(
                cartPage.getEmptyCartMessage(),
                "Your shopping cart is empty",
                "Expected the empty cart message to be displayed."
        );
        Assert.assertEquals(cartPage.getHeaderCartCount(), 0, "Expected cart count to reset to 0.");
    }

    @Test(description = "Return to shopping from the cart without losing the cart contents.")
    public void shouldContinueShoppingWithoutClearingCart() {
        CartPage cartPage = addFirstSearchResultToCart();

        HomePage homePage = cartPage.continueShopping(baseUrl);

        Assert.assertTrue(homePage.isSearchInputDisplayed(), "Expected the home page search bar to be visible.");
        Assert.assertFalse(
                homePage.getCurrentUrl().contains("/checkout/cart"),
                "Expected Continue shopping to leave the cart page."
        );
        Assert.assertEquals(homePage.getCartCount(), 1, "Expected the cart item count to remain unchanged.");
    }

    @Test(description = "Add two different search results to the cart and verify both appear in the cart.")
    public void shouldAddTwoDifferentProductsToCart() {
        HomePage homePage = openHomePage();
        SearchResultsPage searchResultsPage = homePage.searchFor(SEARCH_KEYWORD);

        Assert.assertTrue(searchResultsPage.getResultCount() >= 2, "Expected at least two search results.");

        String firstProductPath = searchResultsPage.getProductPath(1);
        String secondProductPath = searchResultsPage.getProductPath(2);

        Assert.assertFalse(
                firstProductPath.equals(secondProductPath),
                "Expected the first two search results to be different items."
        );

        searchResultsPage.addProductToCart(1);
        Assert.assertEquals(
                searchResultsPage.getHeaderCartCount(),
                1,
                "Expected the cart count to become 1 after the first product is added."
        );

        searchResultsPage.addProductToCart(2);
        Assert.assertEquals(
                searchResultsPage.getHeaderCartCount(),
                2,
                "Expected the cart count to become 2 after the second product is added."
        );

        CartPage cartPage = searchResultsPage.openCart();

        Assert.assertEquals(cartPage.getItemCount(), 2, "Expected the cart to contain two different items.");
        Assert.assertTrue(
                cartPage.containsProductPath(firstProductPath),
                "Expected the first selected product to appear in the cart."
        );
        Assert.assertTrue(
                cartPage.containsProductPath(secondProductPath),
                "Expected the second selected product to appear in the cart."
        );
        Assert.assertEquals(cartPage.getHeaderCartCount(), 2, "Expected the cart count to remain 2.");
    }

    private CartPage addFirstSearchResultToCart() {
        HomePage homePage = openHomePage();
        SearchResultsPage searchResultsPage = homePage.searchFor(SEARCH_KEYWORD);

        Assert.assertTrue(searchResultsPage.hasResults(), "Expected at least one search result.");

        searchResultsPage.addFirstProductToCart();
        Assert.assertEquals(searchResultsPage.getHeaderCartCount(), 1, "Expected the cart count to become 1.");

        return searchResultsPage.openCart();
    }
}
