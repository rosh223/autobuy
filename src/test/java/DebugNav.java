import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;

public class DebugNav {
    public static void main(String[] args) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            Page page = browser.newPage();
            
            page.navigate("https://casekaro.com/");
            page.waitForLoadState();
            
            Locator links = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("iPhone").setExact(true));
            System.out.println("TOTAL 'iPhone' LINKS: " + links.count());
            
            for (int i = 0; i < links.count(); i++) {
                System.out.println("LINK " + i + " HREF: " + links.nth(i).getAttribute("href"));
            }
        }
    }
}
