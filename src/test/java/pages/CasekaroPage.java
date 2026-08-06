package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class CasekaroPage {
    private final Page page;

    public CasekaroPage(Page page) {
        this.page = page;
    }

    public void navigateToHome() {
        page.navigate("https://casekaro.com/");
        page.waitForLoadState(com.microsoft.playwright.options.LoadState.DOMCONTENTLOADED);
        System.out.println("✅ Navigated to Casekaro website");
    }

    public void searchGlobal(String query) {
        Locator searchInput = page.getByPlaceholder(java.util.regex.Pattern.compile(".*search.*model.*design.*", java.util.regex.Pattern.CASE_INSENSITIVE)).first();
        if (searchInput.count() == 0) searchInput = page.locator("input[name='q']").first();
        
        searchInput.scrollIntoViewIfNeeded();
        searchInput.click(new Locator.ClickOptions().setForce(true));
        searchInput.clear();
        searchInput.pressSequentially(query, new Locator.PressSequentiallyOptions().setDelay(150));
        page.waitForTimeout(4000); 
        System.out.println("✅ Typed '" + query + "' into global search bar and triggered autocomplete");
    }

    public void validateCompetitorsNotVisible(String[] brands) {
        Locator searchResults = page.locator(".predictive-search, .snize-ac-results, #search-results, .search-results");
        for (String brand : brands) {
            Locator brandInResults = searchResults.locator("a, li, span, p").filter(
                    new Locator.FilterOptions().setHasText(java.util.regex.Pattern.compile(brand, java.util.regex.Pattern.CASE_INSENSITIVE))
            );
            // Strict Playwright assertion
            assertThat(brandInResults).hasCount(0);
        }
        System.out.println("✅ Negative validation passed: Competitor brands are not visible after searching Apple");
    }

    public void clickTopNavMenu(String menuName) {
        // Dismiss any open dropdowns (like predictive search) so they don't intercept the click
        page.keyboard().press("Escape");
        page.waitForTimeout(500);

        // Strict AriaRole locator EXACTLY as requested by the user instructions
        page.getByRole(com.microsoft.playwright.options.AriaRole.LINK, 
            new Page.GetByRoleOptions().setName(menuName).setExact(true)).first().click();
        
        page.waitForLoadState(com.microsoft.playwright.options.LoadState.DOMCONTENTLOADED);
        
        if (menuName.equalsIgnoreCase("iPhone")) {
            page.waitForURL("**/pages/iphone-back-covers*", new Page.WaitForURLOptions().setTimeout(15000));
            assertTrue("URL must contain /pages/iphone-back-covers", page.url().toLowerCase().contains("/pages/iphone-back-covers"));
        }
        
        page.waitForTimeout(2000);
        System.out.println("✅ Navigated to " + menuName + " Category");
    }

    public void searchSpecificModel(String specificSearch) {
        Locator searchInput = page.getByPlaceholder(java.util.regex.Pattern.compile(".*search.*model.*", java.util.regex.Pattern.CASE_INSENSITIVE)).last();
        searchInput.scrollIntoViewIfNeeded();
        searchInput.click(new Locator.ClickOptions().setForce(true));
        searchInput.clear();
        searchInput.pressSequentially(specificSearch, new Locator.PressSequentiallyOptions().setDelay(100));
        page.waitForTimeout(3000);
        
        // Shopify's predictive search triggers on ALL search inputs. We MUST dismiss it so it doesn't intercept clicks.
        // DO NOT press "Escape" because that clears input[type="search"] natively, which resets the filtered list!
        // Instead, click outside to close the global dropdown safely.
        page.mouse().click(10, 10);
        page.waitForTimeout(1000);
        System.out.println("✅ Searched for '" + specificSearch + "' in specific local filter box");
    }

    public void selectExactModel(String suggestion) {
        // Highly robust cross-layout locator: Finds any anchor or button containing exactly the suggestion text, 
        // while explicitly avoiding 'Max' or other variants.
        Locator dropdownItem = page.locator("a:visible, button:visible, .tag-link:visible")
            .filter(new Locator.FilterOptions().setHasText(java.util.regex.Pattern.compile("^\\s*" + suggestion + "\\s*$", java.util.regex.Pattern.CASE_INSENSITIVE)))
            .first();
        
        if (dropdownItem.count() == 0) {
            dropdownItem = page.locator("a:visible, button:visible").filter(new Locator.FilterOptions().setHasText(suggestion)).filter(new Locator.FilterOptions().setHasText(java.util.regex.Pattern.compile("^(?!.*Max).*$"))).first();
        }

        dropdownItem.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
        dropdownItem.click(new Locator.ClickOptions().setForce(true));
        page.waitForLoadState(com.microsoft.playwright.options.LoadState.DOMCONTENTLOADED);
        page.waitForTimeout(2000);
        System.out.println("✅ Clicked exact model link for '" + suggestion + "'");
    }

    public void assertCollectionPageAndClickFirstProduct() {
        // Assert we navigated successfully (Native Auto-Wait)
        page.waitForURL("**/*iphone-16-pro*", new Page.WaitForURLOptions().setTimeout(15000));
        assertTrue("URL must contain iphone-16-pro", page.url().toLowerCase().contains("iphone-16-pro"));

        Locator productLink = page.locator("a[href*='/products/']:has(img):visible").first();
        productLink.click(new Locator.ClickOptions().setForce(true));
        
        page.waitForLoadState(com.microsoft.playwright.options.LoadState.DOMCONTENTLOADED);
        page.waitForTimeout(2000);
        System.out.println("✅ Asserted collection page and Clicked First Product Card");
    }

    public void addVariantsToCart(String var1, String var2, String var3) {
        String[] materials = {var1, var2, var3};
        
        for (String material : materials) {
            Locator materialOption = page.locator("label, .variant-input, .product-form__input input + label, .swatch-element")
                .filter(new Locator.FilterOptions().setHasText(java.util.regex.Pattern.compile("^\\s*" + material + "\\s*$", java.util.regex.Pattern.CASE_INSENSITIVE)))
                .first();
            
            materialOption.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
            materialOption.click(new Locator.ClickOptions().setForce(true));

            // Vue.js frontend sync (Wait for data state to update the invisible form without try-catch)
            String variantId = (String) materialOption.evaluate("el => el.getAttribute('data-value') || el.previousElementSibling?.value || el.htmlFor?.split('-').pop() || (el.querySelector('input') ? el.querySelector('input').value : null)");
            if (variantId != null) {
                page.waitForFunction("() => {" +
                    "  let formInput = document.querySelector('#main-product form[action*=\"/cart/add\"] input[type=\"hidden\"][name=\"id\"], form[action*=\"/cart/add\"] input[type=\"hidden\"][name=\"id\"]');" +
                    "  return formInput && formInput.value === '" + variantId + "';" +
                    "}", null, new Page.WaitForFunctionOptions().setTimeout(10000));
            }

            Locator addToCartBtn = page.locator("#main-product form[action*='/cart/add'] button[type='submit'], .product-form form[action*='/cart/add'] button[type='submit'], form[action*='/cart/add'] button[type='submit']:has-text('Add')").first();
            
            // Native auto-wait for network response
            page.waitForResponse(response -> response.url().contains("/cart/add") && response.status() == 200, () -> {
                addToCartBtn.click(new Locator.ClickOptions().setForce(true));
            });
            
            page.waitForTimeout(1000);
            System.out.println("✅ Added '" + material + "' variant to cart and synced UI");

            Locator closeBtn = page.locator(".drawer__close, button[aria-label='Close'], cart-drawer .drawer__close-button, .cart-drawer__close");
            if (closeBtn.count() > 0 && closeBtn.first().isVisible()) {
                closeBtn.first().click(new Locator.ClickOptions().setForce(true));
                page.waitForTimeout(1000);
            }
        }
    }

    public void navigateToCart() {
        page.navigate("https://casekaro.com/cart", new Page.NavigateOptions().setWaitUntil(com.microsoft.playwright.options.WaitUntilState.DOMCONTENTLOADED));
        page.waitForTimeout(2000);
        System.out.println("✅ Navigated directly to cart page");
    }

    public void validateCartItemCount(int expectedCount) {
        page.waitForSelector("cart-remove-button, .drawer__item, .cart-item, a[href*='/products/']", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
        page.waitForTimeout(2000); 

        Locator cartItems = page.locator("cart-remove-button, button:has-text('Remove'), a:has-text('Remove')");
        int actualCount = cartItems.count();
        if (actualCount == 0) actualCount = page.locator(".cart a[href*='/products/'], .drawer__item").count();
        
        assertEquals("Expected exactly " + expectedCount + " items in the cart", (long) expectedCount, actualCount);
        System.out.println("✅ Validated " + actualCount + " items in cart");
    }

    public void printCartDetails() {
        System.out.println("\n======================================================================");
        System.out.println("                     CART ITEM DETAILS");
        System.out.println("======================================================================\n");
        
        page.waitForSelector("cart-remove-button, button:has-text('Remove'), a:has-text('Remove'), .cart-item", new Page.WaitForSelectorOptions().setTimeout(10000));
        
        Locator cartRows = page.locator("cart-remove-button, button:has-text('Remove'), a:has-text('Remove')");
        if (cartRows.count() == 0) cartRows = page.locator(".cart-item, .drawer__item");
        
        for (int i = 0; i < cartRows.count(); i++) {
            String rowData = (String) cartRows.nth(i).evaluate("el => {" +
                "let parent = el.closest('tr, cart-item, .cart-item, .cart__row, [data-cart-item], .drawer__item, li');" +
                "if (!parent) parent = el.parentElement.parentElement;" +
                "if (!parent) parent = el;" +
                "let linkEl = parent.querySelector('a[href*=\"/products/\"]');" +
                "let link = linkEl ? linkEl.href : 'Unknown';" +
                "return (parent.textContent || parent.innerText) + '|||' + link;" +
            "}");
            
            String[] parts = rowData.split("\\|\\|\\|");
            String text = parts[0];
            String link = parts.length > 1 ? parts[1] : "Unknown";

            String material = "N/A";
            if (text.toLowerCase().contains("hard")) material = "Hard";
            else if (text.toLowerCase().contains("soft")) material = "Soft";
            else if (text.toLowerCase().contains("glass")) material = "Glass";
            
            String price = "N/A";
            java.util.regex.Matcher m = java.util.regex.Pattern.compile("₹\\s*[0-9,.]+").matcher(text);
            if (m.find()) {
                price = m.group();
                if (m.find()) price = m.group(); // Get real price if first is strikethrough
            }

            System.out.println("📦 Item " + (i + 1) + ":");
            System.out.println("   Material : " + material);
            System.out.println("   Price    : " + price);
            System.out.println("   Link     : " + link + "\n");
        }
    }
}
