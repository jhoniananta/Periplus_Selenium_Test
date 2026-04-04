# Periplus Shopping Cart Test Cases

## Run Tests
```bash
    mvn clean test
```

## Test Cases Description

| ID | Name | Objective | Preconditions | Data | Steps | Expected Result |
|------------|-----------------------------------|------------------------------------------------------|------------------------------------------------------------------|----------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------|
| TC-SC-001 | Add a product from product detail page | Ensure product addition from the product detail page works correctly | User is registered, logged in, stable internet, and cart is empty | Search keyword: `harry potter` | 1. Open browser<br>2. Go to `https://www.periplus.com/`<br>3. Login<br>4. Search `harry potter`<br>5. Open the first product detail page<br>6. Click `Add to Cart`<br>7. Open cart from the header cart dropdown<br>8. Verify the selected product is in the cart | The cart contains the selected product and the cart count increases to `1` |
| TC-SC-002 | Increase quantity in cart | Verify quantity increase updates subtotal and total | User is logged in and cart is empty before test execution | Search keyword: `harry potter` | 1. Search `harry potter`<br>2. Add the first product to cart<br>3. Open cart<br>4. Increase quantity once<br>5. Click `Update`<br>6. Verify quantity, subtotal, and total | Quantity becomes `2` and subtotal/total are updated correctly |
| TC-SC-003 | Keep minimum quantity at one | Verify quantity cannot be reduced below the minimum value | User is logged in and cart is empty before test execution | Search keyword: `harry potter` | 1. Search `harry potter`<br>2. Add the first product to cart<br>3. Open cart<br>4. Decrease quantity once<br>5. Click `Update`<br>6. Verify quantity and subtotal | Quantity remains `1` and subtotal stays unchanged |
| TC-SC-004 | Remove product from cart | Ensure removing the only product works correctly | User is logged in and cart is empty before test execution | Search keyword: `harry potter` | 1. Search `harry potter`<br>2. Add the first product to cart<br>3. Open cart<br>4. Remove the product<br>5. Verify cart state and message | Product is removed, empty-cart message is shown, and cart count becomes `0` |
| TC-SC-005 | Continue shopping from cart | Ensure user can leave the cart page without losing current cart content | User is logged in and cart is empty before test execution | Search keyword: `harry potter` | 1. Search `harry potter`<br>2. Add the first product to cart<br>3. Open cart<br>4. Click `Continue shopping`<br>5. Verify the landing page and cart count | User returns to the storefront, search bar is visible, and cart count remains `1` |
| TC-SC-006 | Add two different products to cart | Verify two different items can be added in one session | User is logged in and cart is empty before test execution | Search keyword: `harry potter` | 1. Search `harry potter`<br>2. Confirm at least two addable results are available<br>3. Add the first product to cart<br>4. Add the second product to cart<br>5. Open cart<br>6. Verify both products and cart count | Two different products are present in the cart and cart count remains `2` |

## Test Results

| ID | Name | Expected Result | Actual Result | Status | Comments |
|------------|-----------------------------------|------------------------------------------------------|------------------------------------------------------|--------|--------------------------------------------|
| TC-SC-001 | Add a product from product detail page | The cart contains the selected product and the cart count increases to `1` | The cart contains the selected product and the cart count increases to `1` | Pass | Product addition from the product detail page works correctly |
| TC-SC-002 | Increase quantity in cart | Quantity becomes `2` and subtotal/total are updated correctly | Quantity becomes `2` and subtotal/total are updated correctly | Pass | Quantity increase is reflected in subtotal and total |
| TC-SC-003 | Keep minimum quantity at one | Quantity remains `1` and subtotal stays unchanged | Quantity remains `1` and subtotal stays unchanged | Pass | Minimum quantity rule is enforced correctly |
| TC-SC-004 | Remove product from cart | Product is removed, empty-cart message is shown, and cart count becomes `0` | Product is removed, empty-cart message is shown, and cart count becomes `0` | Pass | Product removal works correctly |
| TC-SC-005 | Continue shopping from cart | User returns to the storefront, search bar is visible, and cart count remains `1` | User returns to the storefront, search bar is visible, and cart count remains `1` | Pass | Continue shopping keeps the current cart content |
| TC-SC-006 | Add two different products to cart | Two different products are present in the cart and cart count remains `2` | Two different products are present in the cart and cart count remains `2` | Pass | Multiple product addition works as expected |
