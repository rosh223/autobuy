import com.microsoft.playwright.*;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class DebugFilterScreenshot {
    public static void main(String[] args) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            Page page = browser.newPage();
            
            page.navigate("https://casekaro.com/");
            page.waitForLoadState();
            
            Locator menuLink = page.locator("nav a:visible, .header__inline-menu a:visible, .header__menu-item:visible, .list-menu__item:visible").filter(
                new Locator.FilterOptions().setHasText(Pattern.compile("^\\s*iPhone\\s*$", Pattern.CASE_INSENSITIVE))
            ).first();
            menuLink.click(new Locator.ClickOptions().setForce(true));
            page.waitForLoadState();
            page.waitForTimeout(2000);
            
            Locator searchInput = page.getByPlaceholder(Pattern.compile(".*search.*model.*", Pattern.CASE_INSENSITIVE)).last();
            searchInput.scrollIntoViewIfNeeded();
            searchInput.click(new Locator.ClickOptions().setForce(true));
            searchInput.fill("iPhone 16 Pro");
            page.waitForTimeout(3000);
            
            // Take screenshot of the filtered results to see exactly what classes they have!
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("filter_results.png")));
            System.out.println("Saved filter_results.png");
            
            // Dump the HTML of the parent container of the search input
            Locator parent = searchInput.locator("xpath=ancestor::div[contains(@class, 'collection') or contains(@class, 'section') or contains(@class, 'container') or contains(@class, 'grid')]").first();
            try {
                System.out.println("PARENT HTML: \n" + parent.innerHTML());
            } catch(Exception e) {
                System.out.println("Could not dump parent HTML");
            }
        }
    }
}
