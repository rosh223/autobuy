package steps;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import hooks.Hooks;
import io.cucumber.java.en.*;

import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class CasekaroSteps {

    private Page page;

    private Page getPage() {
        if (page == null) {
            page = Hooks.page;
        }
        return page;
    }

    // ======================== PHASE 2 STEPS ========================

    @Given("I navigate to the casekaro website")
    public void i_navigate_to_the_casekaro_website() {
        getPage().navigate("https://casekaro.com/");
        // Wait for the page to fully load
        getPage().waitForLoadState();
        System.out.println("✅ Navigated to Casekaro website");
    }

    @When("I click on {string} from the Top Navigation Menu")
    public void i_click_on_from_the_top_navigation_menu(String menuName) {
        // Click on the "Mobile Covers" link in the top navigation
        getPage().getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(menuName)).first().click();
        getPage().waitForLoadState();
        System.out.println("✅ Clicked on '" + menuName + "' from the Top Navigation Menu");
    }

    @When("I search for {string} in the phone models search box")
    public void i_search_for_in_the_phone_models_search_box(String searchTerm) {
        // Scroll down to find the "Phone cases by model" section
        getPage().locator("text=Phone cases by model").first().scrollIntoViewIfNeeded();
        getPage().waitForTimeout(1000);

        // Find the search input by placeholder text
        Locator searchInput = getPage().getByPlaceholder("search your phone model");
        searchInput.first().click();
        searchInput.first().fill(searchTerm);
        getPage().waitForTimeout(1000);

        System.out.println("✅ Searched for '" + searchTerm + "' in the phone models search box");
    }

    @Then("other brands should not be visible in the search results")
    public void other_brands_should_not_be_visible_in_the_search_results() {
        // After searching for "Apple", validate that non-Apple brands are NOT visible
        String[] otherBrands = {"Samsung", "OnePlus", "Vivo", "Oppo", "Xiaomi", "Realme", "Redmi", "Motorola"};

        // Get all visible phone model items/links in the search results
        getPage().waitForTimeout(1000);

        for (String brand : otherBrands) {
            // Check that other brand items are either hidden or not present in the filtered results
            Locator brandItems = getPage().locator(".tag-item:visible, .tag-link:visible").filter(
                    new Locator.FilterOptions().setHasText(brand)
            );
            int count = brandItems.count();
            if (count > 0) {
                // If items are found, assert they are not visible
                for (int i = 0; i < count; i++) {
                    assertThat(brandItems.nth(i)).not().isVisible();
                }
            }
            System.out.println("✅ Verified '" + brand + "' is not visible in search results");
        }

        System.out.println("✅ Negative validation passed: Other brands are not visible after searching Apple");
    }

    // ======================== PHASE 3 STEPS (Placeholder) ========================

    @When("I search specifically for {string}")
    public void i_search_specifically_for(String specificSearch) {
        System.out.println("Step: Search specifically for - " + specificSearch);
    }

    @When("I select {string} from the autocomplete suggestion list")
    public void i_select_from_the_autocomplete_suggestion_list(String suggestion) {
        System.out.println("Step: Select suggestion - " + suggestion);
    }

    @When("I click {string} on the First Product Card")
    public void i_click_on_the_first_product_card(String actionBtn) {
        System.out.println("Step: Click - " + actionBtn);
    }

    // ======================== PHASE 4 STEPS (Placeholder) ========================

    @When("I add the {string}, {string}, and {string} material variants to the cart")
    public void i_add_the_and_material_variants_to_the_cart(String mat1, String mat2, String mat3) {
        System.out.println("Step: Add variants - " + mat1 + ", " + mat2 + ", " + mat3);
    }

    @When("I open the cart")
    public void i_open_the_cart() {
        System.out.println("Step: Open the cart");
    }

    @Then("I validate that all {int} items are added to the cart")
    public void i_validate_that_all_items_are_added_to_the_cart(Integer itemCount) {
        System.out.println("Step: Validate cart contains - " + itemCount + " items");
    }

    @Then("I print the material, price, and link of all items in the cart")
    public void i_print_the_material_price_and_link_of_all_items_in_the_cart() {
        System.out.println("Step: Print item details");
    }
}
