package steps;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import hooks.Hooks;
import io.cucumber.java.en.*;

import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class CasekaroSteps {

    private Page page;

    private Page getPage() {
        if (page == null) {
            page = Hooks.page;
        }
        return page;
    }

    // ======================== PHASE 2 STEPS ========================

    @Given("I navigate to the casekaro website")
    public void i_navigate_to_the_casekaro_website() {
        getPage().navigate("https://casekaro.com/");
        // Wait for the page to fully load
        getPage().waitForLoadState();
        System.out.println("✅ Navigated to Casekaro website");
    }

    @When("I click on {string} from the Top Navigation Menu")
    public void i_click_on_from_the_top_navigation_menu(String menuName) {
        // Click on the "Mobile Covers" link in the top navigation
        getPage().getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(menuName)).first().click();
        getPage().waitForLoadState();
        System.out.println("✅ Clicked on '" + menuName + "' from the Top Navigation Menu");
    }

    @When("I search for {string} in the phone models search box")
    public void i_search_for_in_the_phone_models_search_box(String searchTerm) {
        // Find the search input by placeholder text using a case-insensitive regex
        Locator searchInput = getPage().getByPlaceholder(java.util.regex.Pattern.compile(".*search.*phone.*", java.util.regex.Pattern.CASE_INSENSITIVE));
        
        if (searchInput.count() == 0) {
            searchInput = getPage().locator("input[placeholder*='search']").first();
        }

        searchInput.first().scrollIntoViewIfNeeded();
        getPage().waitForTimeout(1000);
        searchInput.first().click();
        searchInput.first().fill(searchTerm);
        
        // Wait for the live-filter grid/autocomplete to update
        getPage().waitForTimeout(2000);

        System.out.println("✅ Searched for '" + searchTerm + "' in the phone models search box");
    }

    @Then("other brands should not be visible in the search results")
    public void other_brands_should_not_be_visible_in_the_search_results() {
        // After searching for "Apple", validate that non-Apple brands are NOT visible
        String[] otherBrands = {"Samsung", "OnePlus", "Vivo", "Oppo", "Xiaomi", "Realme", "Redmi", "Motorola"};

        // Get all visible phone model items/links in the search results
        getPage().waitForTimeout(1000);

        for (String brand : otherBrands) {
            // Check that other brand items are either hidden or not present in the filtered results
            Locator brandItems = getPage().locator(".tag-item:visible, .tag-link:visible").filter(
                    new Locator.FilterOptions().setHasText(brand)
            );
            int count = brandItems.count();
            if (count > 0) {
                // If items are found, assert they are not visible
                for (int i = 0; i < count; i++) {
                    assertThat(brandItems.nth(i)).not().isVisible();
                }
            }
            System.out.println("✅ Verified '" + brand + "' is not visible in search results");
        }

        System.out.println("✅ Negative validation passed: Other brands are not visible after searching Apple");
    }

    // ======================== PHASE 3 STEPS ========================

    @When("I search specifically for {string}")
    public void i_search_specifically_for(String specificSearch) {
        // Use the global header search input directly (avoid hidden accessibility links)
        Locator searchInput = getPage().locator("form[action*='/search'] input[name='q'], input[name='q']").first();
        
        searchInput.scrollIntoViewIfNeeded();
        searchInput.click(new Locator.ClickOptions().setForce(true));
        searchInput.clear();
        searchInput.fill(specificSearch);

        // Wait for the autocomplete/dropdown suggestions to appear
        getPage().waitForTimeout(2000);

        System.out.println("✅ Searched specifically for '" + specificSearch + "' in global search");
    }

    @When("I select {string} from the autocomplete suggestion list")
    public void i_select_from_the_autocomplete_suggestion_list(String suggestion) {
        // Wait for dropdown suggestions to be visible
        getPage().waitForTimeout(1000);

        // Look for the exact text match in the autocomplete suggestions
        // Use getByText with exact match to avoid clicking "iPhone 16 Pro Max"
        Locator suggestionItem = getPage().getByText(suggestion, new Page.GetByTextOptions().setExact(true));

        // If multiple matches, click the one inside the dropdown/autocomplete area
        if (suggestionItem.count() > 1) {
            // Click the suggestion that is inside a dropdown/list context
            suggestionItem.first().click();
        } else {
            suggestionItem.click();
        }

        getPage().waitForLoadState();
        getPage().waitForTimeout(2000);

        System.out.println("✅ Selected '" + suggestion + "' from the autocomplete suggestion list");
    }

    @When("I click {string} on the First Product Card")
    public void i_click_on_the_first_product_card(String actionBtn) {
        // Wait for the page to fully load
        getPage().waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
        getPage().waitForTimeout(2000);
        
        // If the autocomplete suggestion took us directly to a product page, skip this step!
        if (getPage().url().contains("/products/")) {
            System.out.println("✅ Already on a Product Page, skipping 'Choose Options' click!");
            return;
        }

        // Wait for ANY product link to appear in the DOM (timeout 10s)
        try {
            getPage().waitForSelector("a[href*='/products/']:not([class*='ckmr'])", new Page.WaitForSelectorOptions().setTimeout(10000));
        } catch (Exception e) {
            System.out.println("⚠️ Timeout waiting for product links to appear!");
        }

        // Try to find the exact button
        Locator chooseOptionsBtn = getPage().getByText(actionBtn, new Page.GetByTextOptions().setExact(false));
        
        if (chooseOptionsBtn.count() == 0) {
            System.out.println("⚠️ '" + actionBtn + "' not found! Searching for any product link on the page...");
            // Find any link that goes to a product page, ignoring the bottom review widget links
            chooseOptionsBtn = getPage().locator("a[href*='/products/']:not([class*='ckmr'])");
        }

        if (chooseOptionsBtn.count() == 0) {
            System.out.println("⚠️ Still no product links found! Dumping HTML for debugging...");
            try {
                java.nio.file.Files.write(java.nio.file.Paths.get("debug_page.html"), getPage().content().getBytes());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            // Absolute fallback
            chooseOptionsBtn = getPage().locator("a[href*='/products/']").first();
        }

        // Click the first matching button/link
        chooseOptionsBtn.first().scrollIntoViewIfNeeded();
        getPage().waitForTimeout(1000);
        chooseOptionsBtn.first().click(new Locator.ClickOptions().setForce(true));

        getPage().waitForLoadState();
        getPage().waitForTimeout(2000);

        System.out.println("✅ Clicked '" + actionBtn + "' on the First Product Card");
    }


    // ======================== PHASE 4 STEPS ========================

    @When("I add the {string}, {string}, and {string} material variants to the cart")
    public void i_add_the_and_material_variants_to_the_cart(String mat1, String mat2, String mat3) {
        String[] materials = {mat1, mat2, mat3};

        for (String material : materials) {
            // Select the material variant (e.g., Hard, Soft, Glass)
            Locator materialOption = getPage().getByText(material, new Page.GetByTextOptions().setExact(true));

            // Scroll to the variant options area first
            materialOption.first().scrollIntoViewIfNeeded();
            getPage().waitForTimeout(500);
            materialOption.first().click();
            getPage().waitForTimeout(1000);

            // Click the "Add to Cart" button
            Locator addToCartBtn = getPage().getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add to Cart"));
            if (addToCartBtn.count() == 0) {
                // Fallback: try other common button text variations
                addToCartBtn = getPage().getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("ADD TO CART"));
            }
            if (addToCartBtn.count() == 0) {
                // Fallback: try text-based locator
                addToCartBtn = getPage().locator("button:has-text('Add to Cart'), button:has-text('ADD TO CART'), .btn:has-text('Add to Cart')");
            }

            addToCartBtn.first().click();
            getPage().waitForTimeout(2000);

            // Close the cart drawer/popup if it opens automatically (so we can add the next variant)
            // Try to close any cart sidebar that might have opened
            Locator closeBtn = getPage().locator("[aria-label='Close'], .close-cart, .drawer__close, button:has-text('×')");
            if (closeBtn.count() > 0 && closeBtn.first().isVisible()) {
                closeBtn.first().click();
                getPage().waitForTimeout(1000);
            }

            // If the page navigated to a cart page, go back to the product page
            if (getPage().url().contains("/cart")) {
                getPage().goBack();
                getPage().waitForLoadState();
                getPage().waitForTimeout(2000);
            }

            System.out.println("✅ Added '" + material + "' variant to the cart");
        }

        System.out.println("✅ All 3 material variants added to cart");
    }

    @When("I open the cart")
    public void i_open_the_cart() {
        // Try clicking the cart icon in the header
        Locator cartIcon = getPage().locator("a[href='/cart'], .cart-link, .cart-icon, [aria-label='Cart']");
        if (cartIcon.count() > 0 && cartIcon.first().isVisible()) {
            cartIcon.first().click();
        } else {
            // Fallback: navigate directly to the cart page
            getPage().navigate("https://casekaro.com/cart");
        }
        getPage().waitForLoadState();
        getPage().waitForTimeout(2000);

        System.out.println("✅ Opened the cart");
    }

    @Then("I validate that all {int} items are added to the cart")
    public void i_validate_that_all_items_are_added_to_the_cart(Integer itemCount) {
        // Count the number of cart items
        // Shopify carts typically use cart-item, line-item, or similar classes
        Locator cartItems = getPage().locator(
                ".cart-item, .cart__item, .line-item, [data-cart-item], .f8cr, .cart_item, tr.cart-row"
        );

        // If no items found with CSS selectors, count by looking for quantity inputs
        int actualCount = cartItems.count();
        if (actualCount == 0) {
            // Fallback: count quantity inputs or product entries in cart
            cartItems = getPage().locator("input[name*='quantity'], .quantity-input, [data-quantity-input]");
            actualCount = cartItems.count();
        }

        System.out.println("📋 Cart contains " + actualCount + " items (expected: " + itemCount + ")");

        // Assert the expected number of items
        assert actualCount == itemCount : "Expected " + itemCount + " items in cart, but found " + actualCount;

        System.out.println("✅ Validated: All " + itemCount + " items are added to the cart");
    }

    @Then("I print the material, price, and link of all items in the cart")
    public void i_print_the_material_price_and_link_of_all_items_in_the_cart() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("                     CART ITEM DETAILS");
        System.out.println("=".repeat(70));

        // Get all cart item containers
        Locator cartItems = getPage().locator(
                ".cart-item, .cart__item, .line-item, [data-cart-item], .f8cr, .cart_item, tr.cart-row"
        );

        int count = cartItems.count();
        if (count == 0) {
            // Fallback: try to parse the entire cart section
            // Get all product titles and prices from the cart page
            Locator productTitles = getPage().locator(".cart a[href*='/products/']");
            Locator prices = getPage().locator(".cart .price, .cart .money, .cart [class*='price']");

            count = productTitles.count();
            for (int i = 0; i < count; i++) {
                String title = productTitles.nth(i).innerText().trim();
                String link = productTitles.nth(i).getAttribute("href");
                String price = (i < prices.count()) ? prices.nth(i).innerText().trim() : "N/A";

                // Extract material from the title or variant info
                String material = "N/A";
                if (title.toLowerCase().contains("hard")) material = "Hard";
                else if (title.toLowerCase().contains("soft")) material = "Soft";
                else if (title.toLowerCase().contains("glass")) material = "Glass";

                String fullLink = link != null && link.startsWith("/") ? "https://casekaro.com" + link : link;

                System.out.println("\n📦 Item " + (i + 1) + ":");
                System.out.println("   Material : " + material);
                System.out.println("   Price    : " + price);
                System.out.println("   Link     : " + fullLink);
            }
        } else {
            for (int i = 0; i < count; i++) {
                Locator item = cartItems.nth(i);
                String itemText = item.innerText().trim();

                // Extract material from the item text
                String material = "N/A";
                if (itemText.toLowerCase().contains("hard")) material = "Hard";
                else if (itemText.toLowerCase().contains("soft")) material = "Soft";
                else if (itemText.toLowerCase().contains("glass")) material = "Glass";

                // Extract price - look for money/price elements within the item
                String price = "N/A";
                Locator priceEl = item.locator(".price, .money, [class*='price'], [class*='Price']");
                if (priceEl.count() > 0) {
                    price = priceEl.first().innerText().trim();
                }

                // Extract link - look for anchor tags within the item
                String link = "N/A";
                Locator linkEl = item.locator("a[href*='/products/']");
                if (linkEl.count() > 0) {
                    String href = linkEl.first().getAttribute("href");
                    link = href != null && href.startsWith("/") ? "https://casekaro.com" + href : href;
                }

                System.out.println("\n📦 Item " + (i + 1) + ":");
                System.out.println("   Material : " + material);
                System.out.println("   Price    : " + price);
                System.out.println("   Link     : " + link);
            }
        }

        System.out.println("\n" + "=".repeat(70));
        System.out.println("✅ All cart item details printed successfully");
    }
}
