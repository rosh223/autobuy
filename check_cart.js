const { chromium } = require('playwright');
const fs = require('fs');

(async () => {
  const browser = await chromium.launch();
  const page = await browser.newPage();
  console.log("Navigating...");
  await page.goto('https://casekaro.com/products/birds-iphone-16-pro-glass-case?variant=40616933163126');
  
  await page.waitForLoadState('networkidle');
  console.log("Network idle.");
  
  await page.screenshot({ path: 'debug_cart_page.png', fullPage: true });
  console.log("Screenshot taken.");

  const forms = await page.$$eval('form[action="/cart/add"]', forms => forms.map(f => f.outerHTML));
  fs.writeFileSync('debug_cart_forms.html', forms.join('\n<hr>\n'));
  console.log("Forms saved.");

  await browser.close();
})();
