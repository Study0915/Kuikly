// Workspace Playwright CLI run-code; execute after the production bundle is built.
async (page) => {
  const checks = [], errors = [];
  const base = page.url().split('/').slice(0, 3).join('/') + '/';
  if (!/^http:\/\/(127\.0\.0\.1|localhost)(:\d+)?\/$/.test(base)) throw new Error('Use the local Task 1 session');
  const label = text => page.getByLabel(text, { exact: true });
  const chooseScenario = async text => {
    await label('演示设置').click();
    await label(text).click();
    await label('演示设置面板').waitFor({state:'detached'});
  };
  const check = (ok, message) => { if (!ok) throw new Error(message); checks.push(message); };
  const fact = async () => { if (await label('查看计算').count()) { await label('查看计算').click(); await label('计算明细').waitFor(); } return label('当前行情事实').innerText(); };
  const waitFact = text => page.waitForFunction(t => document.querySelector('[aria-label="当前行情事实"]')?.innerText.includes(t), text);
  const waitText = text => page.waitForFunction(t => document.body.innerText.includes(t), text);
  const waitDate = date => page.waitForFunction(d => history.state?.date === d && document.body.innerText.includes(`检视 ${d}`), date);
  const shot = name => page.screenshot({ path: `.cache/task1-improvements-20260908/${name}.png` });
  const onError = error => errors.push(error.message);
  page.on('pageerror', onError);
  try {
    await page.goto(base);
    if (await label('‹ 返回行情列表').count()) await label('‹ 返回行情列表').click();
    await label('查看示例股票 A').click(); await label('当前行情事实').waitFor();
    const enteredLength = await page.evaluate(() => history.length);
    await label('中途回落').click(); await waitFact('区间变化 -6.09%');
    check((await fact()).includes('(10.80 − 11.50) ÷ 11.50 × 100%'), 'D3: A endpoint denominator is explicit');
    await label('检视起点 08-24 ›').click(); await waitDate('2026-08-24');
    await label('前一日').click(); await waitDate('2026-08-21');
    check((await fact()).includes('检视 2026-08-21'), 'D3: previous trading day skips weekend');
    await label('后一日').click(); await waitDate('2026-08-24');
    await label('区间内 · 中途回落 ›').click(); await waitFact('区间变化 -6.09%');
    await label('检视终点 08-27 ›').click(); await waitDate('2026-08-27');
    check(!(await fact()).includes('-6.09%') && (await fact()).includes('相对前收'), 'D3: endpoint inspection switches to day basis');
    for (const width of [320, 390, 430, 1024]) {
      await page.setViewportSize({ width, height: 844 });
      await label('交易日导航').scrollIntoViewIfNeeded();
      await page.waitForFunction(w => document.querySelector('[aria-label="行情双图：价格与成交量，点按检视交易日"]').getBoundingClientRect().width === Math.min(w,480) - 32, width);
      const boxes = await Promise.all(['前一日', '后一日'].map(t => label(t).boundingBox()));
      check(boxes.every(b => b.x >= 0 && b.x + b.width <= width && b.height >= 42), `D3: date controls fit ${width}px with 42px targets`);
      await shot(`navigation-${width}`);
      await label('当前行情事实').scrollIntoViewIfNeeded(); await shot(`day-facts-${width}`);
    }
    await label('返回解读').click(); await waitFact('区间变化 +12.00%');
    await label('检视起点 08-10 ›').click(); await waitDate('2026-08-10');
    await label('前一日').click({ force: true });
    check((await fact()).includes('检视 2026-08-10'), 'D3: first-day control cannot leave window');
    for (let index = 1; index < 20; index++) {
      await label('后一日').click();
      await page.waitForFunction(i => document.querySelector('[aria-label="交易日导航"]')?.innerText.includes(`${i}/20`), index + 1);
    }
    await waitDate('2026-09-04');
    await label('后一日').click({ force: true });
    check((await fact()).includes('检视 2026-09-04'), 'D3: twenty sequential dates reach last day without overflow');
    await label('目标日 · 量能观察 ›').click(); await waitFact('量能倍数 1.50 倍');
    check((await fact()).includes('样本合计 600.00 万股 ÷ 5 = 120.00 万股'), 'D3: five-sample mean can be audited');
    check(await page.getByLabel(/^样本 .* ›$/).count() === 5, 'D3: exactly five samples shown');
    await label('当前行情事实').scrollIntoViewIfNeeded(); await shot('volume-breakdown-390');
    const sample = page.getByLabel(/^样本 09-02 ·/);
    await sample.click(); await waitDate('2026-09-02');
    await label('比较样本 · 量能观察 ›').click(); await waitFact('量能倍数 1.50 倍');
    check((await fact()).includes('样本合计 600.00'), 'D3: sample to day to full evidence round trip');
    await chooseScenario('量能缺失'); await waitText('当前：量能缺失');
    const plot = label('行情双图：价格与成交量，点按检视交易日');
    await plot.scrollIntoViewIfNeeded(); const box = await plot.boundingBox();
    await page.touchscreen.tap(box.x + 42 + 17.5 * (box.width - 54) / 20, box.y + 270);
    await waitDate('2026-09-02');
    check((await fact()).includes('成交量 缺失') && !(await fact()).includes('比较样本'), 'D3: missing sample has no available-volume backlink');
    const savedUrl = page.url();
    check((await page.evaluate(() => history.length)) === enteredLength, 'D2: all selections and scenarios replace one detail entry');
    await page.goBack(); await label('行情列表，12 支示例股票').waitFor();
    await page.goForward(); await waitDate('2026-09-02');
    check((await page.locator('body').innerText()).includes('当前：量能缺失') && (await fact()).includes('成交量 缺失'), 'D2: forward restores missing snapshot, label and date');
    const beforeReload = await page.evaluate(() => history.length);
    await page.reload(); await waitDate('2026-09-02');
    check((await page.evaluate(() => history.length)) === beforeReload, 'D2: reload preserves detail without adding history entries');
    check((await fact()).includes('成交量 缺失'), 'D2: reload keeps snapshot and selection');
    const other = await page.context().newPage();
    try {
      await other.goto(savedUrl);
      await other.waitForFunction(() => document.body.innerText.includes('检视 2026-09-02'));
      check((await other.locator('body').innerText()).includes('当前：量能缺失'), 'D2: URL restores same context in a fresh tab');
      await other.getByLabel('‹ 返回行情列表', { exact: true }).click();
      await other.getByLabel('行情列表，12 支示例股票', { exact: true }).waitFor();
      check(!other.url().includes('entity='), 'D2: direct-link back reaches real home URL');
    } finally { await other.close(); }
    await label('当前行情事实').scrollIntoViewIfNeeded(); await shot('restored-missing');
    await chooseScenario('完整行情'); await waitText('当前：完整行情');
    await label('中途回落').click(); await waitFact('区间变化 -6.09%');
    await page.goBack(); await label('行情列表，12 支示例股票').waitFor();
    await page.goForward(); await waitFact('区间变化 -6.09%');
    check(await label('已选 · 中途回落').count() === 1, 'D2: evidence focus also survives forward');
    await label('行情双图：价格与成交量，点按检视交易日').scrollIntoViewIfNeeded(); await shot('evidence-chart-390');
    await label('当前行情事实').scrollIntoViewIfNeeded(); await shot('return-formula-390');
    await label('‹ 返回行情列表').click(); await label('行情列表，12 支示例股票').waitFor();
    await label('查看示例股票 B').click(); await label('当前行情事实').waitFor();
    await label('局部反弹').click(); await waitFact('区间变化 +5.49%');
    check((await fact()).includes('(9.60 − 9.10) ÷ 9.10 × 100%'), 'D4: B uses its own formula through same component');
    await label('量能观察').click(); await waitFact('量能倍数 0.80 倍');
    check((await fact()).includes('样本合计 500.00 万股 ÷ 5 = 100.00 万股'), 'D4: B samples calculated independently');
    await page.goto(base + '?entity=MOCK_A&overview=1'); await waitFact('点选上方依据');
    check(!(await fact()).includes('暂无可用解读') && await page.getByLabel(/^已选 ·/).count() === 0, 'D2: explicit overview keeps valid evidence unselected');
    await page.reload(); await waitFact('点选上方依据');
    check((await page.evaluate(() => history.state.overview)) === '1', 'D2: overview survives reload without becoming default evidence');
    await page.goto(base + '?entity=MOCK_A&evidence=E3'); await label('当前行情事实').waitFor();
    check(await label('计算明细').count() === 0, 'V05: fresh detail formula is folded');
    const beforeDisclosure = {url:page.url(),history:await page.evaluate(()=>history.length),value:await label('当前行情事实').innerText()};
    await label('查看计算').click(); await label('计算明细').waitFor();
    check(page.url()===beforeDisclosure.url && await page.evaluate(()=>history.length)===beforeDisclosure.history && (await label('当前行情事实').innerText()).includes('量能倍数 1.50 倍'), 'V12: disclosure preserves focus value and history');
    await label('计算明细').scrollIntoViewIfNeeded();
    await page.evaluate(()=>new Promise(resolve=>requestAnimationFrame(()=>requestAnimationFrame(resolve))));
    const savedDetailOffset = await label('个股详情').evaluate(e=>e.scrollTop);
    await page.goBack(); await label('行情列表，12 支示例股票').waitFor();
    await page.goForward(); await label('当前行情事实').waitFor();
    await page.waitForFunction(y=>Math.abs(document.querySelector('[aria-label="个股详情"]').scrollTop-y)<3,savedDetailOffset);
    check(await label('收起计算').count() === 1, 'V06: detail return preserves calculation expansion');
    check(Math.abs(await label('个股详情').evaluate(e=>e.scrollTop)-savedDetailOffset)<3, 'V07: detail restores actual reading position');
    await page.goBack(); await label('查看示例股票 B').click(); await label('当前行情事实').waitFor();
    check(await label('计算明细').count()===0 && await label('个股详情').evaluate(e=>e.scrollTop)<3, 'V08: another document starts with independent presentation');
    await label('查看计算').click(); await label('计算明细').waitFor();
    await chooseScenario('量能缺失'); await waitText('当前：量能缺失');
    check(await label('计算明细').count()===0, 'V09: missing snapshot has independent expansion');
    await page.setViewportSize({width:390,height:360}); await label('演示设置').click();
    await label('长文说明').scrollIntoViewIfNeeded(); const option=await label('长文说明').boundingBox();
    check(option.y>=0 && option.y+option.height<=361, 'V10: short-viewport settings scroll to last option');
    await label('长文说明').click(); await waitText('当前：长文说明');
    check(await label('演示设置面板').count()===0, 'V11: choosing a scenario leaves no covering panel');
    await page.setViewportSize({width:390,height:844});
    check(errors.length === 0, 'D4: no browser runtime errors');
    return { result: 'TASK1_DEEPENING_PASS', count: checks.length, checks, errors };
  } catch (error) {
    throw new Error(`${error.message}; completed=${checks.length}; last=${checks.at(-1) || 'none'}`);
  } finally { page.off('pageerror', onError); }
}
