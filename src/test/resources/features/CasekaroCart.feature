Feature: Casekaro Cart Functionality

  Scenario: Add three different material variants of a phone case to the cart
    Given I navigate to the casekaro website
    When I click on "Mobile Covers" from the Top Navigation Menu
    And I search for "Apple" in the phone models search box
    Then other brands should not be visible in the search results
    When I search specifically for "iPhone 16 Pro"
    And I select "iPhone 16 Pro" from the autocomplete suggestion list
    And I click "Choose Options" on the First Product Card
    And I add the "Hard", "Soft", and "Glass" material variants to the cart
    And I open the cart
    Then I validate that all 3 items are added to the cart
    And I print the material, price, and link of all items in the cart
