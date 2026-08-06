const { chromium } = require('playwright');

(async () => {
  const browser = await chromium.launch();
  const page = await browser.newPage();
  await page.goto('https://casekaro.com/');
  
  await page.locator('input[placeholder*="search"]').first().fill('iPhone 16 Pro');
  
  await page.waitForSelector('.snize-ac-results', { state: 'visible', timeout: 10000 });
  await page.waitForTimeout(2000); // wait for results to populate
  
  const suggestions = await page.$$('.snize-suggestion, .snize-ac-results li, .snize-ac-results a, .snize-title');
  for (const s of suggestions) {
    const text = await s.innerText();
    const className = await s.getAttribute('class');
    const href = await s.getAttribute('href');
    console.log(`Class: ${className} | Text: '${text}' | Href: ${href}`);
  }
  
  await browser.close();
})();
