import com.microsoft.playwright.*;
import java.nio.file.Paths;

public class DebugCart {
    public static void main(String[] args) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            Page page = browser.newPage();
            
            page.navigate("https://casekaro.com/collections/iphone-16-pro-back-covers/products/bhagat-singh-trio-iphone-16-pro-back-cover");
            page.waitForLoadState();
            
            page.locator("label, .swatch-element").filter(new Locator.FilterOptions().setHasText("Hard")).first().click(new Locator.ClickOptions().setForce(true));
            page.waitForTimeout(1000);
            
            Locator addToCartBtn = page.locator("form[action*='/cart/add'] button[type='submit']").first();
            page.waitForResponse(response -> response.url().contains("/cart/add") && response.status() == 200, () -> {
                addToCartBtn.click(new Locator.ClickOptions().setForce(true));
            });
            page.waitForTimeout(2000);
            
            page.navigate("https://casekaro.com/cart");
            page.waitForLoadState();
            
            Locator items = page.locator("a[href*='/products/']");
            System.out.println("TOTAL PRODUCT LINKS IN CART: " + items.count());
            for (int i = 0; i < items.count(); i++) {
                System.out.println("HTML: " + items.nth(i).innerHTML());
                System.out.println("HREF: " + items.nth(i).getAttribute("href"));
                Locator parent = items.nth(i).locator("xpath=ancestor::*[contains(@class, 'item') or contains(@class, 'row') or name()='tr']").first();
                if (parent.count() > 0) {
                    System.out.println("PARENT TEXT: " + parent.innerText().replaceAll("\\s+", " "));
                }
            }
        }
    }
}
