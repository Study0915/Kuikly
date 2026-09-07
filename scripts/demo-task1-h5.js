// Deliberate reading pauses for a real UI recording, not a test timing workaround.
async (page) => {
  const base = page.url().split('/').slice(0, 3).join('/') + '/';
  const label = text => page.getByLabel(text, { exact: true });
  const pause = () => page.waitForTimeout(3000);
  const wait = text => page.waitForFunction(t => document.body.innerText.includes(t), text);
  const plot = () => label('行情双图：价格与成交量，点按检视交易日');
  const facts = () => label('当前行情事实');
  const showPlot = async () => { await plot().scrollIntoViewIfNeeded(); await pause(); };
  const showFacts = async () => { await facts().scrollIntoViewIfNeeded(); await pause(); };
  await page.goto(base); await label('查看示例股票 A').waitFor(); await pause();
  await label('查看示例股票 L').scrollIntoViewIfNeeded(); await pause();
  await label('查看示例股票 A').click(); await facts().waitFor(); await pause();
  await label('中途回落').click(); await wait('区间变化 -6.09%'); await showPlot(); await showFacts();
  await plot().scrollIntoViewIfNeeded(); let b = await plot().boundingBox();
  await page.touchscreen.tap(b.x + 42 + 11.5 * (b.width - 54) / 20, b.y + 100);
  await wait('检视 2026-08-25'); await showFacts();
  await label('区间内 · 中途回落 ›').click(); await wait('区间变化 -6.09%'); await showFacts();
  await label('量能观察').click(); await wait('量能倍数 1.50 倍'); await showPlot(); await showFacts();
  await label('量能缺失').click(); await wait('成交量缺失：2026-09-02');
  await label('量能观察 · 暂不可用').scrollIntoViewIfNeeded(); await pause(); await showPlot();
  b = await plot().boundingBox();
  await page.touchscreen.tap(b.x + 42 + 17.5 * (b.width - 54) / 20, b.y + 270);
  await wait('检视 2026-09-02'); await showFacts();
  await label('完整行情').click(); await wait('当前：完整行情'); await pause();
  await label('‹ 返回行情列表').click(); await label('查看示例股票 B').waitFor(); await pause();
  await label('查看示例股票 B').click(); await facts().waitFor(); await pause();
  await label('局部反弹').click(); await wait('区间变化 +5.49%'); await showPlot(); await showFacts();
  await label('首次失败').click(); await wait('演示：首次请求失败'); await pause();
  await label('重试当前股票').click(); await facts().waitFor(); await pause();
  await label('‹ 返回行情列表').click(); await label('查看示例股票 B').waitFor(); await pause();
  return 'TASK1_DEMO_RECORDED';
}
