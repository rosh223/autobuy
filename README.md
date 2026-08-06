# Casekaro QA Automation Assignment

This repository contains an automated End-to-End (E2E) testing framework developed for the Casekaro QA Intern Assignment 1B. It validates the complete purchase journey on the Casekaro website, from navigation and search down to adding multiple product material variants into the shopping cart.

## 🚀 Tech Stack

- **Language:** Java 11
- **Automation Tool:** [Playwright for Java](https://playwright.dev/java/)
- **BDD Framework:** [Cucumber](https://cucumber.io/) (Gherkin syntax)
- **Build Tool:** Maven
- **Assertions:** Native Java assertions (`assert`)

---

## 🎯 Test Scenario Covered

The automation script executes the following exact flow on `https://casekaro.com/`:
1. Navigates to the homepage.
2. Clicks on **"Mobile Covers"** from the Top Navigation Menu.
3. Scrolls down to the phone models search box and searches for **"Apple"**.
4. Validates negatively that other brands (Samsung, OnePlus, Vivo, Oppo, etc.) are **not** visible.
5. Performs a global search for **"iPhone 16 Pro"** and bypasses dynamic popups/empty collections by pressing Enter.
6. Selects **"iPhone 16 Pro"** from the global search results.
7. Clicks **"Choose Options"** on the first product card.
8. Automates the addition of all three material variants (**Hard, Soft, and Glass**) of the exact same case to the cart by directly communicating with the headless Shopify DOM structure.
9. Opens the cart and **validates that exactly 3 distinct items are present**.
10. Prints the `Material`, `Price`, and `Link` of all 3 items to the console.

---

## ⚙️ Prerequisites

Before you begin, ensure you have the following installed on your machine:
- **Java Development Kit (JDK) 11** or higher
- **Maven** (Apache Maven 3.6.0+)
- An IDE such as IntelliJ IDEA, Eclipse, or VS Code (optional but recommended)

---

## 🛠️ How to Run the Tests

To run the full BDD test suite from your terminal, execute the following Maven command at the root of the project directory:

```bash
mvn clean test -Dtest=TestRunner
```

### Viewing the Output
Once the test starts, it will automatically download the necessary Playwright browser binaries (Chromium, Firefox, WebKit) if they are not already cached.
The execution will run headlessly (or headed, depending on the configuration in `Hooks.java`) and output the live logs directly to the console. 

At the very end of a successful run, the script prints the parsed items present in the cart, outputting their Material, Price, and URL Link:
```text
======================================================================
                     CART ITEM DETAILS
======================================================================

📦 Item 1:
   Material : Glass
   Price    : Rs. 249.00
   Link     : https://casekaro.com/products/...

📦 Item 2:
   Material : Soft
   Price    : Rs. 149.00
   Link     : https://casekaro.com/products/...

📦 Item 3:
   Material : Hard
   Price    : Rs. 99.00
   Link     : https://casekaro.com/products/...
```

---

## 📁 Project Structure

```text
autobuy/
├── src/
│   ├── test/
│   │   ├── java/
│   │   │   ├── hooks/
│   │   │   │   └── Hooks.java         # Playwright browser initialization & teardown
│   │   │   ├── runner/
│   │   │   │   └── TestRunner.java    # Cucumber JUnit test runner configuration
│   │   │   └── steps/
│   │   │       └── CasekaroSteps.java # Step definitions containing the Playwright logic
│   │   └── resources/
│   │       └── features/
│   │           └── CasekaroCart.feature # Gherkin feature file defining the scenario
├── pom.xml                            # Maven dependencies (Playwright, Cucumber, JUnit)
└── README.md                          # This file!
```

---

## 🧠 Handling Headless Defenses (Engineering Notes)

Casekaro's website implements several dynamic elements that typically break standard UI automation. This script incorporates robust bypass mechanisms:
* **Aggressive Popups:** Casekaro randomly spawns newsletter and WhatsApp chat overlays that intercept pointer events. The script injects JavaScript to instantly remove these overlays from the DOM before interacting with elements.
* **React/Vue State Ignorance:** Simulating a click on Shopify material labels (`Hard`/`Soft`/`Glass`) sometimes fails to trigger the underlying native radio button `change` event, resulting in duplicate items added to the cart instead of different variants. The script entirely bypasses their frontend UI framework by locating the exact Shopify Variant IDs within the DOM and directly injecting them into the hidden cart submission form just before clicking "Add to Cart". 
* **Custom AJAX Cart Drawer:** Casekaro uses a dynamic slide-out cart instead of a separate `/cart` page. Validations dynamically wait for the drawer to render and cleanly parse the line-item removal buttons to guarantee accurate item counts.
