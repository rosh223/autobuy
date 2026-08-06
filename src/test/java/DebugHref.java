import com.microsoft.playwright.*;

public class DebugHref {
    public static void main(String[] args) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            Page page = browser.newPage();
            page.navigate("https://casekaro.com/collections/iphone-cases");
            page.waitForLoadState();
            
            Locator searchInput = page.getByPlaceholder(java.util.regex.Pattern.compile(".*search.*model.*", java.util.regex.Pattern.CASE_INSENSITIVE)).last();
            searchInput.scrollIntoViewIfNeeded();
            searchInput.click(new Locator.ClickOptions().setForce(true));
            searchInput.fill("iPhone 16 Pro");
            page.waitForTimeout(2000);
            
            Locator dropdownItem = page.locator(".tag-link, .tag-item, .collection-list a, .grid-item a")
                .filter(new Locator.FilterOptions().setHasText(java.util.regex.Pattern.compile("^\\s*iPhone 16 Pro\\s*$", java.util.regex.Pattern.CASE_INSENSITIVE)))
                .first();
            
            if (dropdownItem.count() == 0 || !dropdownItem.isVisible()) {
                dropdownItem = page.locator(".tag-link:not(:has-text('Max')), a:has-text('iPhone 16 Pro'):visible:not(:has-text('Max')):not([name='q'])").first();
            }
            
            System.out.println("Clicking element: " + dropdownItem.innerHTML());
            System.out.println("HREF: " + dropdownItem.getAttribute("href"));
        }
    }
}
