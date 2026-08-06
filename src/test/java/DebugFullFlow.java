import com.microsoft.playwright.*;
import java.nio.file.Paths;

public class DebugFullFlow {
    public static void main(String[] args) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            Page page = browser.newPage();
            
            System.out.println("Step 1: Navigate");
            page.navigate("https://casekaro.com/");
            page.waitForLoadState(com.microsoft.playwright.options.LoadState.DOMCONTENTLOADED);
            
            System.out.println("Step 2: Global Search 'Apple'");
            Locator globalSearch = page.getByPlaceholder(java.util.regex.Pattern.compile(".*search.*model.*design.*", java.util.regex.Pattern.CASE_INSENSITIVE)).first();
            globalSearch.click(new Locator.ClickOptions().setForce(true));
            globalSearch.fill("Apple");
            page.waitForTimeout(4000);
            
            System.out.println("Step 3: Click iPhone top nav");
            Locator menuLink = page.locator("nav a:visible, .header__inline-menu a:visible, .header__menu-item:visible, .list-menu__item:visible").filter(
                new Locator.FilterOptions().setHasText(java.util.regex.Pattern.compile("^\\s*iPhone\\s*$", java.util.regex.Pattern.CASE_INSENSITIVE))
            ).first();
            menuLink.click(new Locator.ClickOptions().setForce(true));
            page.waitForLoadState(com.microsoft.playwright.options.LoadState.DOMCONTENTLOADED);
            page.waitForTimeout(2000);
            
            System.out.println("Step 4: Local Search 'iPhone 16 Pro'");
            Locator searchInput = page.getByPlaceholder(java.util.regex.Pattern.compile(".*search.*model.*", java.util.regex.Pattern.CASE_INSENSITIVE)).last();
            searchInput.scrollIntoViewIfNeeded();
            searchInput.click(new Locator.ClickOptions().setForce(true));
            searchInput.fill("iPhone 16 Pro");
            page.waitForTimeout(3000);
            
            System.out.println("Step 5: Click Suggestion");
            String suggestion = "iPhone 16 Pro";
            String urlSlug = suggestion.replace(" ", "-").toLowerCase();
            Locator dropdownItem = page.locator("a[href*='/collections/" + urlSlug + "']:visible").first();
            if (dropdownItem.count() == 0) {
                dropdownItem = page.locator(".tag-link:not(:has-text('Max')), a.grid-item:has-text('" + suggestion + "'):visible:not(:has-text('Max'))").first();
            }
            dropdownItem.click(new Locator.ClickOptions().setForce(true));
            page.waitForLoadState(com.microsoft.playwright.options.LoadState.DOMCONTENTLOADED);
            page.waitForTimeout(2000);
            
            System.out.println("Step 6: Wait URL");
            page.waitForURL("**/*iphone-16-pro*", new Page.WaitForURLOptions().setTimeout(15000));
            System.out.println("CURRENT URL: " + page.url());
            
            System.out.println("Step 7: Find product");
            try {
                Locator productLink = page.locator("a[href*='/products/']:has(img):visible").first();
                productLink.waitFor(new Locator.WaitForOptions().setTimeout(15000));
                System.out.println("Found product!");
            } catch (Exception e) {
                System.out.println("FAILED TO FIND PRODUCT! Taking screenshot...");
                page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("debug_full_state.png")));
                System.out.println("Saved debug_full_state.png");
            }
        }
    }
}
