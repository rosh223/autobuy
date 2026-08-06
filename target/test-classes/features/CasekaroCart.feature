Feature: Casekaro Cart Functionality

  Scenario: Add three different material variants of a phone case to the cart
    Given I navigate to the casekaro website
    When I search for "Apple" in the global search bar
    Then other brands should not be visible in the search results
    When I click on "iPhone" from the Top Navigation Menu
    And I search for "iPhone 16 Pro" in the specific model search box
    And I select exactly "iPhone 16 Pro" from the filtered models list
    Then the page should navigate to the iPhone 16 Pro collection
    When I click on the First Product Card
    And I add the "Hard", "Soft", and "Glass" material variants to the cart
    And I navigate directly to the cart page
    Then I validate that all 3 items are added to the cart
    And I print the material, price, and link of all items in the cart
