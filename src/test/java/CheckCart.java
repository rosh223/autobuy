import com.microsoft.playwright.*;
import java.nio.file.Paths;
import java.util.List;

public class CheckCart {
    public static void main(String[] args) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();
            
            System.out.println("Navigating...");
            page.navigate("https://casekaro.com/products/birds-iphone-16-pro-glass-case?variant=40616933163126");
            
            page.waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
            System.out.println("Network idle.");
            
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("debug_cart_page.png")).setFullPage(true));
            System.out.println("Screenshot taken.");

            List<String> forms = (List<String>) page.evaluate("Array.from(document.querySelectorAll('form[action=\"/cart/add\"]')).map(f => f.outerHTML)");
            java.nio.file.Files.write(Paths.get("debug_cart_forms.html"), String.join("\n<hr>\n", forms).getBytes());
            System.out.println("Forms saved.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
