import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;

public class DebugNavVisibility {
    public static void main(String[] args) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            Page page = browser.newPage();
            
            page.navigate("https://casekaro.com/");
            page.waitForLoadState();
            
            Locator links = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("iPhone").setExact(true));
            for (int i = 0; i < links.count(); i++) {
                boolean isVisible = links.nth(i).isVisible();
                System.out.println("LINK " + i + " VISIBLE: " + isVisible);
                if (isVisible) {
                    System.out.println("Clicking link " + i);
                    links.nth(i).click();
                    page.waitForLoadState();
                    System.out.println("NAVIGATED URL: " + page.url());
                    break;
                }
            }
        }
    }
}
