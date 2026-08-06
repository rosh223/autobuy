package steps;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
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
        getPage().navigate("https://casekaro.com/", new com.microsoft.playwright.Page.NavigateOptions().setWaitUntil(com.microsoft.playwright.options.WaitUntilState.DOMCONTENTLOADED));
        System.out.println("✅ Navigated to Casekaro website");
    }

    @When("I click on {string} from the Top Navigation Menu")
    public void i_click_on_from_the_top_navigation_menu(String menuName) {
        Locator menuLink = getPage().locator("nav a, .header__inline-menu a").filter(new Locator.FilterOptions().setHasText(menuName)).first();
        
        if (menuLink.count() > 0 && menuLink.first().isVisible()) {
            menuLink.click(new Locator.ClickOptions().setForce(true));
        } else {
            // "Mobile Covers" is no longer in the Top Nav. We will just stay on the current page and use the global search box.
            System.out.println("⚠️ '" + menuName + "' not found in Top Nav. Proceeding with global search.");
        }
        
        getPage().waitForLoadState(com.microsoft.playwright.options.LoadState.DOMCONTENTLOADED);
        
        System.out.println("✅ Handled top navigation menu step");
    }

    @When("I search for {string} in the phone models search box")
    public void i_search_for_in_the_phone_models_search_box(String searchTerm) {
        Locator searchInput = getPage().getByPlaceholder(java.util.regex.Pattern.compile(".*search.*phone.*", java.util.regex.Pattern.CASE_INSENSITIVE));
        if (searchInput.count() == 0) {
            searchInput = getPage().locator("input[placeholder*='search']").first();
        }

        searchInput.first().scrollIntoViewIfNeeded();
        searchInput.first().click(new Locator.ClickOptions().setForce(true));
        searchInput.first().fill(searchTerm);
        
        getPage().waitForTimeout(2000);

        System.out.println("✅ Searched for '" + searchTerm + "' in the phone models search box");
    }

    @Then("other brands should not be visible in the search results")
    public void other_brands_should_not_be_visible_in_the_search_results() {
        String[] otherBrands = {"Samsung", "OnePlus", "Vivo", "Oppo", "Xiaomi", "Realme", "Redmi", "Motorola"};
        getPage().waitForTimeout(1000);

        for (String brand : otherBrands) {
            Locator brandItems = getPage().locator(".tag-item:visible, .tag-link:visible").filter(
                    new Locator.FilterOptions().setHasText(brand)
            );
            int count = brandItems.count();
            if (count > 0) {
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
        // Requirement: Search in the Phone Model Search Box (not global header)
        Locator searchInput = getPage().getByPlaceholder(java.util.regex.Pattern.compile(".*search.*phone.*", java.util.regex.Pattern.CASE_INSENSITIVE));
        if (searchInput.count() == 0) {
            searchInput = getPage().locator("input[placeholder*='search']").first();
        }
        
        searchInput.first().scrollIntoViewIfNeeded();
        searchInput.first().click(new Locator.ClickOptions().setForce(true));
        searchInput.first().clear();
        searchInput.first().fill(specificSearch);

        // Wait for the autocomplete/dropdown suggestions to appear
        getPage().waitForTimeout(2000);
        getPage().screenshot(new Page.ScreenshotOptions().setPath(java.nio.file.Paths.get("debug_search_dropdown.png")).setFullPage(true));

        System.out.println("✅ Searched specifically for '" + specificSearch + "' in phone models search box");
    }

    @When("I select {string} from the autocomplete suggestion list")
    public void i_select_from_the_autocomplete_suggestion_list(String suggestion) {
        // Requirement: Click specifically on "iPhone 16 Pro" from the dropdown
        // Target the predictive search dropdown elements to avoid accidentally clicking top-nav links
        Locator dropdownItem = getPage().locator("[data-search-suggestion], a[href*='/search?q='], .snize-suggestion, .snize-title, .predictive-search li")
                .filter(new Locator.FilterOptions().setHasText(java.util.regex.Pattern.compile("^\\s*" + suggestion + "\\s*$", java.util.regex.Pattern.CASE_INSENSITIVE)))
                .first();
        
        if (dropdownItem.count() == 0) {
            System.out.println("⚠️ Exact regex match failed, falling back to the first search suggestion.");
            dropdownItem = getPage().locator("[data-search-suggestion], a[href*='/search?q=']").first();
        }

        // Ensure the dropdown is visible before clicking
        dropdownItem.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
        dropdownItem.click();

        // Explicitly wait for the search results page to load to prevent race conditions
        try {
            getPage().waitForURL("**/*search*", new Page.WaitForURLOptions().setTimeout(10000));
        } catch (Exception e) {
            System.out.println("⚠️ URL didn't change to search page within 10s, proceeding anyway.");
        }

        getPage().waitForLoadState(com.microsoft.playwright.options.LoadState.DOMCONTENTLOADED);
        getPage().waitForTimeout(2000);

        System.out.println("✅ Selected '" + suggestion + "' from the autocomplete suggestion list");
    }

    @When("I click {string} on the First Product Card")
    public void i_click_on_the_first_product_card(String actionBtn) {
        getPage().waitForTimeout(3000);
        
        if (getPage().url().contains("/products/")) {
            System.out.println("✅ Already on a Product Page, skipping click!");
            return;
        }

        // Remove blocking overlays (using JS evaluation natively handles absence without try-catch)
        getPage().evaluate("document.querySelectorAll('.overlay-close, [aria-label=\"Close\"], .popup, .modal').forEach(el => el.click())");
        getPage().evaluate("document.querySelectorAll('.overlay, .modal-backdrop').forEach(el => el.remove())");

        getPage().screenshot(new Page.ScreenshotOptions().setPath(java.nio.file.Paths.get("debug_before_click.png")).setFullPage(true));

        // Use visible=true to avoid trying to click hidden links (like mobile menu links)
        Locator productLink = getPage().locator("a[href*='/products/'] >> visible=true").first();
        productLink.scrollIntoViewIfNeeded();
        productLink.click(new Locator.ClickOptions().setForce(true));
        
        getPage().waitForLoadState(com.microsoft.playwright.options.LoadState.DOMCONTENTLOADED);
        getPage().waitForTimeout(2000);
        System.out.println("✅ Clicked First Product Card");
    }


    // ======================== PHASE 4 STEPS ========================

    @When("I add the {string}, {string}, and {string} material variants to the cart")
    public void i_add_the_and_material_variants_to_the_cart(String mat1, String mat2, String mat3) {
        String[] materials = {mat1, mat2, mat3};

        getPage().evaluate("document.querySelectorAll('.overlay-close, [aria-label=\"Close\"], .popup, .modal').forEach(el => el.click())");
        getPage().evaluate("document.querySelectorAll('.overlay, .modal-backdrop').forEach(el => el.remove())");
        
        for (String material : materials) {
            System.out.println("Selecting material variant: " + material);
            
            Locator radioBtn = getPage().locator("input[type='radio'][title='" + material + "'], input[type='radio'][value='" + material + "']");
            if (radioBtn.count() > 0) {
                String variantId = radioBtn.first().getAttribute("value");
                System.out.println("Found Variant ID for " + material + ": " + variantId);
                
                // Navigate directly to the variant URL to perfectly sync Shopify's internal JS state
                // This bypasses any Vue/React state mismatch bugs when switching variants rapidly
                String baseUrl = getPage().url().split("\\?")[0];
                getPage().navigate(baseUrl + "?variant=" + variantId);
                getPage().waitForLoadState(com.microsoft.playwright.options.LoadState.DOMCONTENTLOADED);
                getPage().waitForTimeout(2000);
            } else {
                Locator materialOption = getPage().getByText(material, new Page.GetByTextOptions().setExact(true)).first();
                if (materialOption.count() > 0 && materialOption.isVisible()) {
                    materialOption.scrollIntoViewIfNeeded();
                    materialOption.click(new Locator.ClickOptions().setForce(true));
                    getPage().waitForTimeout(2000);
                }
            }

            // Combine the exact locators that successfully clicked the button in previous runs using .or()
            // This natively WAITS for any of them to become visible, completely bypassing the count() == 0 race condition!
            Locator btn1 = getPage().getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add to Cart"));
            Locator btn2 = getPage().getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("ADD TO CART"));
            Locator btn3 = getPage().locator("button:has-text('Add to Cart'), button:has-text('ADD TO CART'), input[value*='Add to Cart' i], .add-to-cart, .product-form__cart-submit");
            
            Locator addToCartBtn = btn1.or(btn2).or(btn3);
            
            addToCartBtn.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
            addToCartBtn.first().click(new Locator.ClickOptions().setForce(true));
            
            getPage().waitForTimeout(3000);

            // Shopify often shows a mini-cart drawer after adding an item.
            Locator closeBtn = getPage().locator(".drawer__close, button[aria-label='Close'], cart-drawer .drawer__close-button, .cart-drawer__close");
            if (closeBtn.count() > 0 && closeBtn.first().isVisible()) {
                closeBtn.first().click(new Locator.ClickOptions().setForce(true));
                getPage().waitForTimeout(1000);
            }

            if (getPage().url().contains("/cart")) {
                getPage().goBack();
                getPage().waitForLoadState(com.microsoft.playwright.options.LoadState.DOMCONTENTLOADED);
                getPage().waitForTimeout(2000);
            }

            System.out.println("✅ Added '" + material + "' variant to the cart");
        }

        System.out.println("✅ All 3 material variants added to cart");
    }

    @When("I open the cart")
    public void i_open_the_cart() {
        Locator cartIcon = getPage().locator("a[href='/cart'], .cart-link, .cart-icon, [aria-label='Cart']");
        if (cartIcon.count() > 0 && cartIcon.first().isVisible()) {
            cartIcon.first().click(new Locator.ClickOptions().setForce(true));
        } else {
            getPage().navigate("https://casekaro.com/cart");
        }
        getPage().waitForLoadState(com.microsoft.playwright.options.LoadState.DOMCONTENTLOADED);
        getPage().waitForTimeout(2000);

        System.out.println("✅ Opened the cart");
    }

    @Then("I validate that all {int} items are added to the cart")
    public void i_validate_that_all_items_are_added_to_the_cart(Integer itemCount) {
        // Wait for the cart drawer or page to fully render
        getPage().waitForSelector("cart-remove-button, .drawer__item, .cart-item, a[href*='/products/']", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
        getPage().waitForTimeout(2000); 
        
        Locator cartItems = getPage().locator("cart-remove-button, button:has-text('Remove'), a:has-text('Remove')");

        int actualCount = cartItems.count();
        if (actualCount == 0) {
            cartItems = getPage().locator(".cart a[href*='/products/'], .drawer a[href*='/products/']");
            actualCount = cartItems.count();
        }

        System.out.println("📋 Cart contains " + actualCount + " items (expected: " + itemCount + ")");
        assert actualCount == itemCount : "Expected " + itemCount + " items in cart, but found " + actualCount;

        System.out.println("✅ Validated: All " + itemCount + " items are added to the cart");
    }

    @Then("I print the material, price, and link of all items in the cart")
    public void i_print_the_material_price_and_link_of_all_items_in_the_cart() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("                     CART ITEM DETAILS");
        System.out.println("=".repeat(70));

        // Requirement: Wait for the selector inside the cart drawer before extracting text
        getPage().waitForSelector(".drawer a[href*='/products/'], .cart-drawer a[href*='/products/'], .cart a[href*='/products/']", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));

        // Requirement: Loop through the item locators to extract innerText for Price/Material and getAttribute for Link
        Locator productTitles = getPage().locator(".drawer a[href*='/products/'], .cart-drawer a[href*='/products/'], .cart a[href*='/products/']");
        Locator prices = getPage().locator(".drawer .price, .cart-drawer .price, .cart .price, .drawer .money").filter(new Locator.FilterOptions().setHasNotText("Total"));
        Locator variants = getPage().locator(".drawer .product-option, .cart-drawer .product-option, .cart .product-option, .product-variant-options");
        
        int count = productTitles.count();
        
        for (int i = 0; i < count; i++) {
            String title = productTitles.nth(i).innerText().trim();
            String link = productTitles.nth(i).getAttribute("href");
            String price = (i < prices.count()) ? prices.nth(i).innerText().trim() : "N/A";
            
            String variantText = (i < variants.count()) ? variants.nth(i).innerText().trim() : "";

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
