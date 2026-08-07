# Casekaro Playwright Automation (QA Intern Assignment 1B)

This repository contains an automated UI testing suite for Casekaro (casekaro.com) built entirely in **Java** and **Playwright**.

## Architecture & Technology Stack
- **Language**: Java 11
- **Automation Framework**: Microsoft Playwright (Java)
- **BDD Framework**: Cucumber (JUnit 4 Runner)
- **Build Tool**: Maven
- **Design Pattern**: Page Object Model (POM)

## Assignment Scope
The script automates the full e-commerce Cart flow, specifically solving for dynamic Vue.js frontend state synchronization and Cloudflare Bot Protections without relying on brittle `Thread.sleep()` or `try-catch` fallbacks.

**Scenario Covered:**
1. Navigate to casekaro.com
2. Access "Mobile Covers" (using direct navigation as the top menu link does not exist).
3. Scroll and interact with the "Phone model" specific search.
4. **Negative Validation**: Confirm rival brands (Samsung, OnePlus, etc.) do not appear when searching "Apple".
5. Search specifically for "iPhone 16 Pro" and strictly select it from the autocomplete dropdown (explicitly avoiding the 'Max' variant).
6. Click into the first product.
7. Select three distinct material variants (Hard, Soft, Glass).
8. Ensure front-end state hydration completes before sequentially adding all three variants to the cart.
9. Open the cart and assert the total item count is exactly 3.
10. Extract and print the **Material**, **Price**, and **Link** of all items to the console in chronological order.

For detailed engineering decisions and notes regarding deviations from the prompt due to live UI changes on Casekaro.com, please refer to the **`Project_Details_and_Deviations.md`** file included in this repository.

## Prerequisites
- Java JDK 11+
- Apache Maven
- Git

## Setup & Installation

1. **Clone the repository:**
   ```bash
   git clone <your-repository-url>
   cd autobuy
   ```

2. **Download Dependencies (Maven):**
   ```bash
   mvn clean install -DskipTests
   ```

## How to Execute the Tests

To run the automated Cucumber test suite from the terminal:

```bash
mvn clean test
```

### Important: Cloudflare Bot Protection
Because this script navigates a live production e-commerce site, Cloudflare Bot Protection may occasionally intercept the execution. 
**The browser is intentionally launched in Headed Mode.** If a Cloudflare "Verify you are human" captcha appears on your screen, **manually click the checkbox**. The script is configured with a 60-second timeout to allow this manual intervention, after which it will seamlessly resume the automation flow.

## Expected Console Output
Upon successful execution, the script will output the validation steps and dynamically extract the Cart details like so:
```
======================================================================
                     CART ITEM DETAILS
======================================================================

📦 Item 1:
   Material : Hard
   Price    : ₹ 99.00
   Link     : https://casekaro.com/products/classic-porsche-911-iphone-16-pro-back-cover?variant=41955668197494

📦 Item 2:
   Material : Soft
   Price    : ₹ 149.00
   Link     : https://casekaro.com/products/classic-porsche-911-iphone-16-pro-back-cover?variant=41955668230262

📦 Item 3:
   Material : Glass
   Price    : ₹ 249.00
   Link     : https://casekaro.com/products/classic-porsche-911-iphone-16-pro-back-cover?variant=41955668263030
```
