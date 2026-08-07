import com.microsoft.playwright.*;

public class DebugHTML {
    public static void main(String[] args) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();
            page.navigate("https://casekaro.com/pages/mobile-covers");
            page.waitForTimeout(5000);
            
            System.out.println("Search Box Count: " + page.locator("#search-bar-cover-page").count());
            if (page.locator("#search-bar-cover-page").count() > 0) {
                System.out.println("Is Visible: " + page.locator("#search-bar-cover-page").isVisible());
                System.out.println("HTML: " + page.locator("#search-bar-cover-page").evaluate("el => el.outerHTML"));
            }
            
            Locator buttons = page.locator("div.brand-name-container, a.brand-name-container");
            System.out.println("Button count: " + buttons.count());
            if (buttons.count() > 0) {
                System.out.println("First button HTML: " + buttons.first().evaluate("el => el.outerHTML"));
            }
        }
    }
}
