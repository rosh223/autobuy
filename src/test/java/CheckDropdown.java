import com.microsoft.playwright.*;

import java.nio.file.Files;
import java.nio.file.Paths;

public class CheckDropdown {
    public static void main(String[] args) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();
            page.navigate("https://casekaro.com/");

            Locator searchIcon = page.locator("summary.header__icon--search").first();
            if (searchIcon.isVisible()) searchIcon.click();

            Locator searchInput = page.locator("input[placeholder*='search']").first();
            if (!searchInput.isVisible()) {
                searchInput = page.locator("input[type='search']").first();
            }
            searchInput.fill("iPhone 16 Pro");

            page.waitForTimeout(4000);
            
            try {
                String html = page.content();
                Files.write(Paths.get("debug_page.html"), html.getBytes());
                System.out.println("✅ Saved debug_page.html");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
