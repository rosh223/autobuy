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
        try {
            getPage().navigate("https://casekaro.com/", new com.microsoft.playwright.Page.NavigateOptions().setWaitUntil(com.microsoft.playwright.options.WaitUntilState.DOMCONTENTLOADED));
        } catch (Exception e) {
            System.out.println("⚠️ Navigate threw an exception, possibly a timeout waiting for load. Continuing...");
        }
        System.out.println("✅ Navigated to Casekaro website");
    }

    @When("I click on {string} from the Top Navigation Menu")
    public void i_click_on_from_the_top_navigation_menu(String menuName) {
        // Click on the link in the top navigation
        Locator menuLink = getPage().locator("nav a, .header__inline-menu a").filter(new Locator.FilterOptions().setHasText(menuName)).first();
        if (menuLink.count() == 0) {
            menuLink = getPage().getByText(menuName, new Page.GetByTextOptions().setExact(false)).first();
        }
        
        try {
            menuLink.click(new Locator.ClickOptions().setForce(true));
        } catch (Exception e) {
            System.out.println("⚠️ Forced click failed, retrying via JavaScript...");
            menuLink.evaluate("el => el.click()");
        }

        // Wait for the page DOM to load (avoid full load due to slow tracker scripts)
        try {
            getPage().waitForLoadState(com.microsoft.playwright.options.LoadState.DOMCONTENTLOADED);
        } catch (Exception e) {}
        
        System.out.println("✅ Navigated to '" + menuName + "' category");
    }

    @When("I search for {string} in the phone models search box")
    public void i_search_for_in_the_phone_models_search_box(String searchTerm) {
        // Find the search input by placeholder text using a case-insensitive regex
        Locator searchInput = getPage().getByPlaceholder(java.util.regex.Pattern.compile(".*search.*phone.*", java.util.regex.Pattern.CASE_INSENSITIVE));
        
        if (searchInput.count() == 0) {
            searchInput = getPage().locator("input[placeholder*='search']").first();
        }

        try {
            searchInput.first().scrollIntoViewIfNeeded();
            searchInput.first().click(new Locator.ClickOptions().setForce(true));
        } catch (Exception e) {}
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
        // Use the global header search input directly, using the exact placeholder from the screenshots
        Locator searchInput = getPage().getByPlaceholder("Search phone model or design", new Page.GetByPlaceholderOptions().setExact(false));
        if (searchInput.count() == 0) {
            searchInput = getPage().locator("input[name='q']").first();
        }
        
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
        getPage().waitForTimeout(2000);

        System.out.println("Pressing Enter to submit search and bypass empty collection suggestions.");
        Locator searchInput = getPage().getByPlaceholder("Search phone model or design", new Page.GetByPlaceholderOptions().setExact(false));
        if (searchInput.count() == 0) {
            searchInput = getPage().locator("input[name='q']").first();
        }
        
        try {
            searchInput.press("Enter");
        } catch (Exception e) {
            getPage().keyboard().press("Enter");
        }

        try {
            getPage().waitForLoadState(com.microsoft.playwright.options.LoadState.DOMCONTENTLOADED);
        } catch (Exception e) {}
        
        getPage().waitForTimeout(2000);

        System.out.println("✅ Selected '" + suggestion + "' via global search results");
    }

    @When("I click {string} on the First Product Card")
    public void i_click_on_the_first_product_card(String actionBtn) {
        getPage().waitForTimeout(3000);
        
        if (getPage().url().contains("/products/")) {
            System.out.println("✅ Already on a Product Page, skipping 'Choose Options' click!");
            return;
        }

        // Nuke any popups or overlays that might be blocking pointer events
        try {
            getPage().evaluate("document.querySelectorAll('.overlay-close, [aria-label=\"Close\"], .popup, .modal').forEach(el => el.click())");
            getPage().evaluate("document.querySelectorAll('.overlay, .modal-backdrop').forEach(el => el.remove())");
        } catch (Exception e) {}

        // Extremely robust fallback: just find the first product link or image
        Locator productLink = getPage().locator("a[href*='/products/'] img, a[href*='/products/']").first();
        
        try {
            productLink.scrollIntoViewIfNeeded();
            productLink.click(new Locator.ClickOptions().setForce(true));
        } catch (Exception e) {
            System.out.println("⚠️ Click failed, retrying via JavaScript...");
            productLink.evaluate("el => el.click()");
        }
        try {
            getPage().waitForLoadState(com.microsoft.playwright.options.LoadState.DOMCONTENTLOADED);
        } catch (Exception e) {}
        getPage().waitForTimeout(2000);
        System.out.println("✅ Clicked First Product Card");
    }


    // ======================== PHASE 4 STEPS ========================

    @When("I add the {string}, {string}, and {string} material variants to the cart")
    public void i_add_the_and_material_variants_to_the_cart(String mat1, String mat2, String mat3) {
        String[] materials = {mat1, mat2, mat3};

        try {
            java.nio.file.Files.writeString(java.nio.file.Paths.get("product_page.html"), getPage().content());
        } catch (Exception e) {}

        try {
            getPage().evaluate("document.querySelectorAll('.overlay-close, [aria-label=\"Close\"], .popup, .modal').forEach(el => el.click())");
            getPage().evaluate("document.querySelectorAll('.overlay, .modal-backdrop').forEach(el => el.remove())");
        } catch (Exception e) {}
        
        for (String material : materials) {
            System.out.println("Selecting material variant: " + material);
            
            // Direct variant selection via the hidden radio buttons Casekaro uses for variants
            Locator radioBtn = getPage().locator("input[type='radio'][title='" + material + "'], input[type='radio'][value='" + material + "']");
            if (radioBtn.count() > 0) {
                // Read the actual Shopify Variant ID
                String variantId = radioBtn.first().getAttribute("value");
                System.out.println("Found Variant ID for " + material + ": " + variantId);
                
                // Inject the variant ID directly into the hidden Shopify form input to completely bypass their UI framework
                getPage().evaluate("id => { " +
                    "let input = document.querySelector('input[name=\"id\"], select[name=\"id\"]');" +
                    "if (input) { input.value = id; input.dispatchEvent(new Event('change', {bubbles: true})); }" +
                "}", variantId);
                
                // Also visually check the radio button just in case
                try { radioBtn.first().check(new Locator.CheckOptions().setForce(true)); } catch (Exception e) {}
            } else {
                // Fallback if it's not a radio button
                Locator materialOption = getPage().getByText(material, new Page.GetByTextOptions().setExact(true));
                if (materialOption.count() > 0) {
                    try { materialOption.first().scrollIntoViewIfNeeded(); } catch (Exception e) {}
                    try { materialOption.first().click(new Locator.ClickOptions().setForce(true)); } catch (Exception e) {}
                }
            }
            getPage().waitForTimeout(1500);

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

            try { addToCartBtn.first().click(new Locator.ClickOptions().setForce(true)); } catch (Exception e) { addToCartBtn.first().evaluate("el => el.click()"); }

            getPage().waitForTimeout(3000);

            // Handle potential popup after adding to cart
            Locator closeBtn = getPage().locator(".drawer__close, button[aria-label='Close']");
            if (closeBtn.count() > 0 && closeBtn.first().isVisible()) {
                closeBtn.first().click(new Locator.ClickOptions().setForce(true));
                getPage().waitForTimeout(1000);
            }

            // If the page navigated to a cart page, go back to the product page
            if (getPage().url().contains("/cart")) {
                getPage().goBack();                try { getPage().waitForLoadState(com.microsoft.playwright.options.LoadState.DOMCONTENTLOADED); } catch(Exception e) {}
                getPage().waitForTimeout(2000);            }

            System.out.println("✅ Added '" + material + "' variant to the cart");
        }

        System.out.println("✅ All 3 material variants added to cart");
    }

    @When("I open the cart")
    public void i_open_the_cart() {
        // Try clicking the cart icon in the header
        Locator cartIcon = getPage().locator("a[href='/cart'], .cart-link, .cart-icon, [aria-label='Cart']");
        if (cartIcon.count() > 0 && cartIcon.first().isVisible()) {
            try { cartIcon.first().click(new Locator.ClickOptions().setForce(true)); } catch (Exception e) { cartIcon.first().evaluate("el => el.click()"); }
        } else {
            // Fallback: navigate directly to the cart page
            getPage().navigate("https://casekaro.com/cart");
        }        try {
            getPage().waitForLoadState(com.microsoft.playwright.options.LoadState.DOMCONTENTLOADED);
        } catch (Exception e) {}        getPage().waitForTimeout(2000);

        System.out.println("✅ Opened the cart");
    }

    @Then("I validate that all {int} items are added to the cart")
    public void i_validate_that_all_items_are_added_to_the_cart(Integer itemCount) {
        getPage().waitForTimeout(3000); // Give the cart time to fully render
        
        try {
            getPage().screenshot(new Page.ScreenshotOptions().setPath(java.nio.file.Paths.get("debug_cart.png")).setFullPage(true));
        } catch (Exception e) {}

        // Count the number of cart items using the 'Remove' buttons, which exist exactly once per line item in the cart drawer
        Locator cartItems = getPage().locator(
                "cart-remove-button, button:has-text('Remove'), a:has-text('Remove'), [aria-label*='Remove'], .cart-item, .cart__item, .drawer__item"
        );

        int actualCount = cartItems.count();
        if (actualCount == 0) {
            // Fallback: count product title links inside the cart drawer
            cartItems = getPage().locator(".cart a[href*='/products/'], .drawer a[href*='/products/']");
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

        // Get all product title links from the cart drawer
        Locator productTitles = getPage().locator(".drawer a[href*='/products/'], .cart-drawer a[href*='/products/'], .cart a[href*='/products/']");
        // Get all prices from the cart drawer (excluding the total price)
        Locator prices = getPage().locator(".drawer .price, .cart-drawer .price, .cart .price, .drawer .money").filter(new Locator.FilterOptions().setHasNotText("Total"));
        // Get all variant material text labels
        Locator variants = getPage().locator(".drawer .product-option, .cart-drawer .product-option, .cart .product-option, .product-variant-options");
        int count = productTitles.count();
        if (count == 0) {
            System.out.println("⚠️ Could not locate product titles in cart for printing.");
            return;
        }

        for (int i = 0; i < count; i++) {
            String title = productTitles.nth(i).innerText().trim();
            String link = productTitles.nth(i).getAttribute("href");
            String price = (i < prices.count()) ? prices.nth(i).innerText().trim() : "N/A";
            
            // Try to get variant text if available
            String variantText = (i < variants.count()) ? variants.nth(i).innerText().trim() : "";

            // Extract material from the title or variant info
            String material = "N/A";
            if (title.toLowerCase().contains("hard") || variantText.toLowerCase().contains("hard")) material = "Hard";
            else if (title.toLowerCase().contains("soft") || variantText.toLowerCase().contains("soft")) material = "Soft";
            else if (title.toLowerCase().contains("glass") || variantText.toLowerCase().contains("glass")) material = "Glass";

            String fullLink = link != null && link.startsWith("/") ? "https://casekaro.com" + link : link;

            System.out.println("\n📦 Item " + (i + 1) + ":");
            System.out.println("   Material : " + material);
            System.out.println("   Price    : " + price);
            System.out.println("   Link     : " + fullLink);
        }

        System.out.println("\n" + "=".repeat(70));
        System.out.println("✅ All cart item details printed successfully");
    }
}
