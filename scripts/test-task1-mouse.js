async (page) => {
  const base = page.url().split('/').slice(0, 3).join('/') + '/';
  if (!/^http:\/\/(127\.0\.0\.1|localhost)(:\d+)?\/$/.test(base)) throw new Error('Use local H5');
  const label = text => page.getByLabel(text, { exact: true });
  await page.goto(base + '?entity=MOCK_A&evidence=E2');
  const p = label('行情双图：价格与成交量，点按检视交易日');
  await p.scrollIntoViewIfNeeded(); const b = await p.boundingBox();
  await page.mouse.click(b.x + 42 + 5.5 * (b.width - 54) / 20, b.y + 100);
  await page.waitForFunction(() => document.querySelector('[aria-label="当前行情事实"]')?.innerText.includes('检视 2026-08-17'));
  const before = await label('当前行情事实').innerText();
  await page.mouse.move(b.x + 120, b.y + 100); await page.mouse.down();
  await page.mouse.move(b.x + 180, b.y + 100, { steps: 8 }); await page.mouse.up();
  await page.waitForTimeout(120);
  if ((await label('当前行情事实').innerText()) !== before) throw new Error('Mouse drag selected a date');
  await page.goto(base); await label('查看示例股票 A').waitFor();
  const row = await label('查看示例股票 A').boundingBox();
  // Coordinate mouse input avoids click() waiting for the history navigation to settle.
  await page.mouse.click(row.x + row.width / 2, row.y + row.height / 2);
  await page.waitForFunction(() => document.body.innerText.includes('正在载入历史 Mock 行情'));
  const back = await label('‹ 返回行情列表').boundingBox();
  await page.mouse.click(back.x + back.width / 2, back.y + back.height / 2);
  // Deliberately wait past the 150ms Mock completion to test a late response.
  await page.waitForTimeout(300);
  if (await label('当前行情事实').count() || !(await label('行情列表，12 支示例股票').count())) throw new Error('Late response overwrote Home');
  return { status: 'TASK1_MOUSE_PASS', checks: ['desktop click selects day', 'mouse drag cancels click fallback', 'loading is visible', 'back ignores late response'] };
}
