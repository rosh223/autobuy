import com.microsoft.playwright.*;
import java.util.List;

public class DebugProducts {
    public static void main(String[] args) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            Page page = browser.newPage();
            page.navigate("https://casekaro.com/collections/iphone-16-pro-back-covers");
            page.waitForTimeout(5000);
            
            Locator links = page.locator("a");
            System.out.println("TOTAL LINKS: " + links.count());
            
            for (int i = 0; i < links.count(); i++) {
                String href = links.nth(i).getAttribute("href");
                if (href != null && href.contains("product")) {
                    System.out.println("FOUND PRODUCT LINK: " + href);
                    System.out.println("HTML: " + links.nth(i).innerHTML());
                }
            }
        }
    }
}
