import com.microsoft.playwright.*;
import java.nio.file.Paths;
import java.util.List;

public class DumpPage {
    public static void main(String[] args) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();
            
            System.out.println("Navigating...");
            page.navigate("https://casekaro.com/products/birds-iphone-16-pro-glass-case?variant=40616933163126");
            page.waitForTimeout(5000);
            
            String html = (String) page.evaluate("document.body.innerHTML");
            java.nio.file.Files.write(Paths.get("debug_product.html"), html.getBytes());
            System.out.println("Page saved to debug_product.html");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
