package steps;

import io.cucumber.java.en.*;

public class CasekaroSteps {

    @Given("I navigate to the casekaro website")
    public void i_navigate_to_the_casekaro_website() {
        System.out.println("Step: Navigate to Casekaro website");
    }

    @When("I click on {string} from the Top Navigation Menu")
    public void i_click_on_from_the_top_navigation_menu(String menuName) {
        System.out.println("Step: Click on menu - " + menuName);
    }

    @When("I search for {string} in the phone models search box")
    public void i_search_for_in_the_phone_models_search_box(String searchTerm) {
        System.out.println("Step: Search for - " + searchTerm);
    }

    @Then("other brands should not be visible in the search results")
    public void other_brands_should_not_be_visible_in_the_search_results() {
        System.out.println("Step: Validate other brands are not visible");
    }

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
