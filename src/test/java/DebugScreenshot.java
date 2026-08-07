import com.microsoft.playwright.*;
import java.nio.file.Paths;

public class DebugScreenshot {
    public static void main(String[] args) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();
            System.out.println("Navigating to mobile covers...");
            page.navigate("https://casekaro.com/pages/mobile-covers");
            page.waitForTimeout(5000);
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("debug_covers.png")));
            System.out.println("Screenshot saved.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
