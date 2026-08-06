# Casekaro Playwright Automation (QA Intern Assignment 1B)

This repository contains an automated UI testing suite for Casekaro (casekaro.com) built entirely in **Java** and **Playwright**. 

## Assignment Scope
The script automates the full e-commerce Cart flow, specifically solving for dynamic Vue.js frontend state synchronization and Cloudflare Bot Protections without relying on brittle `Thread.sleep()` or `try-catch` fallbacks.

**Scenario Covered:**
1. Navigate to casekaro.com
2. Access "Mobile Covers" via the top navigation menu.
3. Scroll and interact with the "Phone model" specific search.
4. **Negative Validation**: Confirm rival brands (Samsung, OnePlus, etc.) do not appear when searching "Apple".
5. Search specifically for "iPhone 16 Pro" and strictly select it from the autocomplete dropdown (explicitly avoiding the 'Max' variant).
6. Click into the first product.
7. Select three distinct material variants (Hard, Soft, Glass).
8. Ensure front-end state hydration completes before sequentially adding all three variants to the cart.
9. Open the cart and assert the total item count is exactly 3.
10. Extract and print the **Material**, **Price**, and **Link** of all items to the console.

## Architecture & Technology Stack
- **Language**: Java 11
- **Automation Framework**: Microsoft Playwright (Java)
- **BDD Framework**: Cucumber (JUnit 4 Runner)
- **Build Tool**: Maven

## Critical Technical Challenges Solved

### 1. Dynamic DOM & Vue.js Race Conditions
Casekaro relies on a reactive JS framework (Vue.js/Shopify). When a user clicks a variant label (e.g., "Soft"), the UI updates instantly, but the underlying `<form>` payload is updated asynchronously via Javascript.
**Solution**: Built a custom `waitForFunction` that actively polls the DOM and halts the script until the `<input type="hidden" name="id">` inside the primary product form perfectly matches the expected Variant ID. This guarantees no race conditions and no duplicate additions.

### 2. Aggressive Anti-Bot Mechanisms
Direct POST requests to Shopify's `/cart/add.js` API returned `503 Service Unavailable` due to strict bot mitigation.
**Solution**: The script is strictly constrained to imitating human interaction via the UI DOM tree, bypassing backend protections entirely.

### 3. Theme Obfuscation & "Quick Buy" Shadowing
The DOM contains dozens of hidden "Add to Cart" buttons dynamically injected by recommendation algorithms.
**Solution**: Replaced generic visibility locators with highly structural CSS paths (`#main-product form[action*='/cart/add'] button[type='submit']`) to bypass hidden modals and strictly target the primary product form. Cart item parsing leverages generic DOM tree traversal instead of brittle theme-specific CSS classes.

## Prerequisites
- Java JDK 11+
- Apache Maven

## How to Execute the Tests
To run the automated Cucumber test suite from the terminal:

```bash
mvn clean test -Dtest=TestRunner
```

## Expected Console Output
Upon successful execution, the script will output the validation steps and dynamically extract the Cart details like so:
```
======================================================================
                     CART ITEM DETAILS
======================================================================

📦 Item 1:
   Material : Hard
   Price    : ₹ 249.00
   Link     : https://casekaro.com/products/example-link-1

📦 Item 2:
   Material : Soft
   Price    : ₹ 299.00
   Link     : https://casekaro.com/products/example-link-2

📦 Item 3:
   Material : Glass
   Price    : ₹ 399.00
   Link     : https://casekaro.com/products/example-link-3
```
