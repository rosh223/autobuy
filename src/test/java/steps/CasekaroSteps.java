package steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import pages.CasekaroPage;

public class CasekaroSteps {

    private CasekaroPage getCasekaroPage() {
        return new CasekaroPage(hooks.Hooks.page);
    }

    @Given("I navigate to the casekaro website")
    public void i_navigate_to_the_casekaro_website() {
        getCasekaroPage().navigateToHome();
    }

    @When("I search for {string} in the global search bar")
    public void i_search_for_in_the_global_search_bar(String query) {
        getCasekaroPage().searchGlobal(query);
    }

    @Then("other brands should not be visible in the search results")
    public void other_brands_should_not_be_visible_in_the_search_results() {
        String[] otherBrands = {"Samsung", "OnePlus", "Vivo", "Oppo", "Xiaomi", "Realme", "Redmi", "Motorola"};
        getCasekaroPage().validateCompetitorsNotVisible(otherBrands);
    }

    @When("I click on {string} from the Top Navigation Menu")
    public void i_click_on_from_the_top_navigation_menu(String menuName) {
        getCasekaroPage().clickTopNavMenu(menuName);
    }

    @When("I search for {string} in the specific model search box")
    public void i_search_for_in_the_specific_model_search_box(String specificSearch) {
        getCasekaroPage().searchSpecificModel(specificSearch);
    }

    @When("I select exactly {string} from the filtered models list")
    public void i_select_exactly_from_the_filtered_models_list(String suggestion) {
        getCasekaroPage().selectExactModel(suggestion);
    }

    @Then("the page should navigate to the iPhone 16 Pro collection")
    public void the_page_should_navigate_to_the_iphone_16_pro_collection() {
        // Assertions are handled as part of the click product card flow to synchronize UI states
    }

    @When("I click on the First Product Card")
    public void i_click_on_the_first_product_card() {
        getCasekaroPage().assertCollectionPageAndClickFirstProduct();
    }

    @When("I add the {string}, {string}, and {string} material variants to the cart")
    public void i_add_the_and_material_variants_to_the_cart(String var1, String var2, String var3) {
        getCasekaroPage().addVariantsToCart(var1, var2, var3);
    }

    @When("I navigate directly to the cart page")
    public void i_navigate_directly_to_the_cart_page() {
        getCasekaroPage().navigateToCart();
    }

    @Then("I validate that all {int} items are added to the cart")
    public void i_validate_that_all_items_are_added_to_the_cart(Integer itemCount) {
        getCasekaroPage().validateCartItemCount(itemCount);
    }

    @Then("I print the material, price, and link of all items in the cart")
    public void i_print_the_material_price_and_link_of_all_items_in_the_cart() {
        getCasekaroPage().printCartDetails();
    }
}
