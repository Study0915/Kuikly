async (page) => {
  const base = page.url().split('/').slice(0, 3).join('/') + '/';
  if (!/^http:\/\/(127\.0\.0\.1|localhost)(:\d+)?\/$/.test(base)) throw new Error('Use local H5');
  await page.goto(base + '?entity=MOCK_A&evidence=E2');
  const plot = page.getByLabel('行情双图：价格与成交量，点按检视交易日', { exact: true });
  const facts = page.getByLabel('当前行情事实', { exact: true });
  await plot.waitFor(); await plot.scrollIntoViewIfNeeded();
  await page.waitForFunction(() => document.querySelector('[aria-label="当前行情事实"]')?.innerText.includes('区间变化 -6.09%'));
  const client = await page.context().newCDPSession(page);
  const checks = [];
  const sameFocus = async name => {
    // Let the input and render queues drain; this is part of the gesture probe.
    await page.waitForTimeout(120);
    if (!(await facts.innerText()).includes('区间变化 -6.09%')) throw new Error(name);
    checks.push(name);
  };
  try {
    const scroller = page.getByLabel('个股详情', { exact: true });
    const before = await scroller.evaluate(el => el.scrollTop);
    let b = await plot.boundingBox();
    await client.send('Input.dispatchTouchEvent', { type: 'touchStart', touchPoints: [{ x: b.x + 140, y: b.y + 150 }] });
    for (let distance = 15; distance <= 120; distance += 15) {
      await client.send('Input.dispatchTouchEvent', { type: 'touchMove', touchPoints: [{ x: b.x + 140, y: b.y + 150 - distance }] });
      await page.waitForTimeout(35);
    }
    await client.send('Input.dispatchTouchEvent', { type: 'touchEnd', touchPoints: [] });
    await sameFocus('P04: vertical touch scroll never selects a day');
    if (Math.abs(await scroller.evaluate(el => el.scrollTop) - before) < 20) throw new Error('Parent did not scroll');
    checks.push('P04: parent list actually scrolls');
    await plot.scrollIntoViewIfNeeded(); b = await plot.boundingBox();
    await client.send('Input.dispatchTouchEvent', { type: 'touchStart', touchPoints: [{ x: b.x + 140, y: b.y + 100 }] });
    await client.send('Input.dispatchTouchEvent', { type: 'touchStart', touchPoints: [{ x: b.x + 140, y: b.y + 100 }, { x: b.x + 190, y: b.y + 100 }] });
    await client.send('Input.dispatchTouchEvent', { type: 'touchEnd', touchPoints: [] });
    await sameFocus('P04: multitouch cancels selection');
    await client.send('Input.dispatchTouchEvent', { type: 'touchStart', touchPoints: [{ x: b.x + 140, y: b.y + 100 }] });
    await client.send('Input.dispatchTouchEvent', { type: 'touchCancel', touchPoints: [] });
    await sameFocus('P04: touchcancel cancels selection');
    await page.touchscreen.tap(b.x + 20, b.y + 100); await sameFocus('P02: price axis does not select');
    await page.touchscreen.tap(b.x + 140, b.y + 220); await sameFocus('P02: panel gap does not select');
    for (const index of [1, 7, 19]) {
      await page.touchscreen.tap(b.x + 42 + (index + .5) * (b.width - 54) / 20, b.y + 100);
    }
    await page.waitForFunction(() => document.querySelector('[aria-label="当前行情事实"]')?.innerText.includes('检视 2026-09-04'));
    checks.push('P04: rapid taps resolve to the last selected date');
    await facts.scrollIntoViewIfNeeded(); await page.screenshot({ path: '.cache/task1-evidence/browser/t1-touch-final.png' });
    return { status: 'TASK1_TOUCH_PASS', checks };
  } finally { await client.detach(); }
}
