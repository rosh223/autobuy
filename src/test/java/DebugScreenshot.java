import com.microsoft.playwright.*;
import java.nio.file.Paths;

public class DebugScreenshot {
    public static void main(String[] args) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            Page page = browser.newPage();
            
            System.out.println("Navigating to home...");
            page.navigate("https://casekaro.com/");
            page.waitForLoadState();
            
            System.out.println("Navigating to iPhone category...");
            page.locator("nav a:visible, .header__inline-menu a:visible, .header__menu-item:visible, .list-menu__item:visible")
                .filter(new Locator.FilterOptions().setHasText("iPhone"))
                .first().click(new Locator.ClickOptions().setForce(true));
            page.waitForLoadState();
            
            System.out.println("Searching specific model...");
            Locator searchInput = page.getByPlaceholder(java.util.regex.Pattern.compile(".*search.*model.*", java.util.regex.Pattern.CASE_INSENSITIVE)).last();
            searchInput.scrollIntoViewIfNeeded();
            searchInput.click(new Locator.ClickOptions().setForce(true));
            searchInput.fill("iPhone 16 Pro");
            page.waitForTimeout(2000);
            
            System.out.println("Clicking suggestion...");
            String suggestion = "iPhone 16 Pro";
            String urlSlug = suggestion.replace(" ", "-").toLowerCase();
            Locator dropdownItem = page.locator("a[href*='/collections/" + urlSlug + "']:visible").first();
            if (dropdownItem.count() == 0) {
                dropdownItem = page.locator(".tag-link:not(:has-text('Max')), a.grid-item:has-text('" + suggestion + "'):visible:not(:has-text('Max'))").first();
            }
            dropdownItem.click(new Locator.ClickOptions().setForce(true));
            page.waitForLoadState();
            
            System.out.println("Waiting for URL...");
            page.waitForURL("**/*iphone-16-pro*", new Page.WaitForURLOptions().setTimeout(15000));
            System.out.println("CURRENT URL: " + page.url());
            
            System.out.println("Looking for product link...");
            try {
                Locator productLink = page.locator("a[href*='/products/']:has(img):visible").first();
                productLink.waitFor(new Locator.WaitForOptions().setTimeout(15000));
                System.out.println("Found product!");
            } catch (Exception e) {
                System.out.println("Failed to find product link! Taking screenshot...");
                page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("debug_state.png")));
                System.out.println("Saved debug_state.png");
            }
        }
    }
}
