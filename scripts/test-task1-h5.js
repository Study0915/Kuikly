// Run with the workspace Playwright CLI's run-code command; see docs/TASK1-RUN.md.
async (page) => {
  const checks = [], errors = [], external = [];
  const base = page.url().split('/').slice(0, 3).join('/') + '/';
  if (!/^http:\/\/(127\.0\.0\.1|localhost)(:\d+)?\/$/.test(base)) throw new Error('Use the local Task 1 browser session');
  const onError = e => errors.push(e.message);
  const onRequest = r => { if (!r.url().startsWith(base) && !r.url().startsWith('data:')) external.push(r.url()); };
  page.on('pageerror', onError); page.on('request', onRequest);
  const check = (ok, text) => { if (!ok) throw new Error(text); checks.push(text); };
  const label = text => page.getByLabel(text, { exact: true });
  const chooseScenario = async text => {
    await label('演示设置').click();
    await label(text).click();
    await label('演示设置面板').waitFor({state:'detached'});
  };
  const waitText = text => page.waitForFunction(t => document.body.innerText.includes(t), text);
  const facts = async () => { if (await label('查看计算').count()) { await label('查看计算').click(); await label('计算明细').waitFor(); } return label('当前行情事实').innerText(); };
  const waitFact = text => page.waitForFunction(t => document.querySelector('[aria-label="当前行情事实"]')?.innerText.includes(t), text);
  const shot = name => page.screenshot({ path: `.cache/task1-evidence/browser/${name}.png` });
  const plot = () => label('行情双图：价格与成交量，点按检视交易日');
  const touchDay = async (index, volume = false) => {
    await plot().scrollIntoViewIfNeeded(); const b = await plot().boundingBox();
    await page.touchscreen.tap(b.x + 42 + (index + .5) * (b.width - 54) / 20, b.y + (volume ? 270 : 100));
    return b;
  };
  const back = async () => {
    await label('‹ 返回行情列表').click();
    await label('行情列表，12 支示例股票').waitFor();
    await page.waitForFunction(() => history.state?.financeDetail === false);
  };
  try {
    await page.goto(base); await label('行情列表，12 支示例股票').waitFor();
    check(await page.getByLabel(/^查看示例股票 /).count() === 12, 'F01: 12 quote rows');
    await shot('t1-home-390');
    for (const letter of 'ABCDEFGHIJKL') {
      const row = label(`查看示例股票 ${letter}`); await row.scrollIntoViewIfNeeded();
      const before = await row.boundingBox();
      await row.click(); await waitText(`MOCK_${letter}`); await label('当前行情事实').waitFor();
      check((await page.locator('body').innerText()).includes(`示例股票 ${letter} / MOCK_${letter}`), `F02: correct detail ${letter}`);
      await back();
      await page.waitForFunction(({ letter, y }) => Math.abs(document.querySelector(`[aria-label="查看示例股票 ${letter}"]`).getBoundingClientRect().y - y) < 3, { letter, y: before.y });
      const after = await label(`查看示例股票 ${letter}`).boundingBox();
      check(Math.abs(before.y - after.y) < 3, `F03: restored list position ${letter}`);
    }
    await label('查看示例股票 A').click(); await label('当前行情事实').waitFor();
    const detailA = (await page.locator('body').innerText()).replace(/\s+/g,' ');
    check(['11.20 元', '+0.15 元', '+1.36%', '11.26', '10.99', '180.00 万股', 'AI 解读（Mock）'].every(t => detailA.includes(t)), 'F04/F05: A quote fields and AI');
    check(await label('计算明细').count() === 0, 'V01: formula is folded on initial detail');
    const firstPlot = await plot().boundingBox();
    check(firstPlot.y >= 0 && firstPlot.y + firstPlot.height <= 844, 'V02: complete chart fits first detail screen');
    check(await label('解读依据选择').evaluate(e => e.querySelectorAll('p').length === 3 && Array.from(e.querySelectorAll('p')).every(p => {const a=p.getBoundingClientRect(),b=p.parentElement.getBoundingClientRect();return a.top>=b.top-1 && a.bottom<=b.bottom+1;})), 'V04: chip labels fit within their touch targets');
    await shot('t1-detail-390');
    await label('中途回落').click();
    await waitFact('区间变化 -6.09%');
    check((await facts()).includes('11.50 → 10.80') && (await facts()).includes('-6.09%'), 'I02: complete local return');
    await plot().scrollIntoViewIfNeeded(); await shot('t1-e2-390');
    await touchDay(11); await waitText('检视 2026-08-25');
    check((await facts()).includes('-1.74%') && !(await facts()).includes('-6.09%'), 'I02: day return is separate from interval');
    const local = await label('区间内 · 中途回落 ›').boundingBox();
    const whole = await label('整体观察 · 区间表现 ›').boundingBox();
    check(local.y < whole.y, 'I03: local evidence precedes whole-window evidence');
    await label('当前行情事实').scrollIntoViewIfNeeded(); await shot('t1-day-390');
    await label('区间内 · 中途回落 ›').click(); await waitText('区间变化 -6.09%');
    await touchDay(11, true); await waitText('检视 2026-08-25');
    check((await facts()).includes('135.00 万股'), 'P02: volume selects same day');
    for (const width of [320, 390, 430, 1024]) {
      await page.setViewportSize({ width, height: 844 });
      await page.waitForFunction(w => document.querySelector('[aria-label="行情双图：价格与成交量，点按检视交易日"]').getBoundingClientRect().width === Math.min(w,480) - 32, width);
      check((await facts()).includes('检视 2026-08-25'), `P01: focus survives width ${width}`);
      await plot().scrollIntoViewIfNeeded(); await shot(`t1-plot-${width}`);
      await label('当前行情事实').scrollIntoViewIfNeeded(); await shot(`t1-facts-${width}`);
    }
    await touchDay(0); await waitText('检视 2026-08-10');
    check((await facts()).includes('没有针对该日的单独解读') && (await facts()).includes('首日无前收'), 'I04: no invented first-day explanation');
    await touchDay(19, true); await waitText('检视 2026-09-04');
    check((await facts()).includes('目标日 · 量能观察'), 'I03: volume target association');
    await touchDay(17, true); await waitText('检视 2026-09-02');
    check((await facts()).includes('比较样本 · 量能观察'), 'I03: baseline association');
    await label('比较样本 · 量能观察 ›').click(); await waitText('量能倍数 1.50 倍');
    await label('当前行情事实').scrollIntoViewIfNeeded(); await shot('t1-volume-facts');
    await chooseScenario('量能缺失'); await waitText('成交量缺失：2026-09-02');
    await page.setViewportSize({width:320,height:844});
    await page.waitForFunction(()=>document.querySelector('[aria-label="行情双图：价格与成交量，点按检视交易日"]').getBoundingClientRect().width===288);
    check(await label('量能观察 · 暂不可用').evaluate(e=>{const p=e.querySelector('p');if(!p)return false;const a=p.getBoundingClientRect(),b=e.getBoundingClientRect();return a.top>=b.top-1&&a.bottom<=b.bottom+1&&a.left>=b.left-1&&a.right<=b.right+1;}), 'V13: unavailable chip fits 320px');
    await page.setViewportSize({width:390,height:844});
    check(!(await page.locator('body').innerText()).includes('1.50'), 'I05: missing-volume summary has no stale ratio');
    await label('量能观察 · 暂不可用').click({ force: true });
    check((await facts()).includes('区间变化 +12.00%'), 'I05: unavailable evidence cannot replace valid focus');
    await touchDay(17, true); await waitText('检视 2026-09-02');
    check((await facts()).includes('成交量 缺失'), 'Q01: missing volume is inspectable and distinct from zero');
    await label('当前行情事实').scrollIntoViewIfNeeded(); await shot('t1-missing-volume');
    await chooseScenario('完整行情'); await waitText('当前：完整行情');
    check((await page.locator('body').innerText()).includes('1.50'), 'I05: complete snapshot restores ratio');
    await chooseScenario('首次失败'); await waitText('演示：首次请求失败');
    await shot('t1-error'); await label('重试当前股票').click(); await label('当前行情事实').waitFor();
    check((await page.locator('body').innerText()).includes('示例股票 A / MOCK_A'), 'F06: retry preserves entity');
    await chooseScenario('空行情'); await waitText('暂无演示行情');
    check(await plot().count() === 0, 'F06: empty has no fake chart');
    await label('恢复完整演示行情').click(); await label('当前行情事实').waitFor();
    await chooseScenario('暂无解读'); await waitText('该股票暂无 AI 解读');
    await touchDay(11); await waitText('检视 2026-08-25');
    check((await facts()).includes('收 11.30') && (await facts()).includes('没有针对该日的单独解读'), 'F06/I04: known entity without AI retains data');
    await chooseScenario('无效引用'); await waitText('区间引用无效');
    check(await label('中途回落 · 暂不可用').count() === 1, 'Q02: invalid reference disabled with reason');
    await chooseScenario('长文说明'); await waitText('当前：长文说明');
    await label('个股详情').evaluate(el => { el.scrollTop = el.scrollHeight; });
    await page.setViewportSize({ width: 320, height: 844 });
    await page.getByText(/^解读边界：/).scrollIntoViewIfNeeded(); await shot('t1-long-text-320');
    check((await page.getByText(/^解读边界：/).innerText()).endsWith('不能据此推断买卖原因。'), 'Q04: complete long text retained');
    await page.setViewportSize({ width: 390, height: 844 }); await back();
    await label('查看示例股票 B').click(); await label('当前行情事实').waitFor();
    const detailB = (await page.locator('body').innerText()).replace(/\s+/g,' ');
    check(['9.60 元', '+0.05 元', '+0.52%', '9.66', '9.49', '80.00 万股', '-4.00%', '0.80'].every(t => detailB.includes(t)), 'F04/I06: B distinct fields using same lens');
    await label('局部反弹').click(); await waitFact('区间变化 +5.49%'); check((await facts()).includes('+5.49%'), 'I01: B local rebound');
    await touchDay(17); await waitText('检视 2026-09-02');
    const ordered = await Promise.all(['区间内 · 局部反弹 ›', '比较样本 · 量能观察 ›', '整体观察 · 区间表现 ›'].map(async t => (await label(t).boundingBox()).y));
    check(ordered[0] < ordered[1] && ordered[1] < ordered[2], 'I03: three related roles ordered visually');
    await label('当前行情事实').scrollIntoViewIfNeeded(); await shot('t1-b-related');
    await page.goBack(); await label('行情列表，12 支示例股票').waitFor();
    check(await label('当前行情事实').count() === 0, 'F03: browser back returns to list');
    await page.goForward(); await waitText('示例股票 B / MOCK_B'); await label('当前行情事实').waitFor();
    check((await facts()).includes('检视 2026-09-02') && (await facts()).includes('局部反弹'), 'F03: browser forward restores entity and current day');
    for (const [query, expected] of [
      ['?entity=UNKNOWN', '无法识别该股票：UNKNOWN'],
      ['?entity=MOCK_B&snapshot=unavailable', '原快照不可用：MOCK_B / unavailable'],
      ['?entity=MOCK_A&date=bad', '原选择已失效'],
      ['?entity=MOCK_A&date=2026-08-25', '检视 2026-08-25'],
      ['?entity=MOCK_B&evidence=E2', '区间变化 +5.49%'],
    ]) {
      await page.goto(base + query); await waitText(expected);
      check((await page.locator('body').innerText()).includes(expected), `F06/I06: route ${query}`);
      if (query.includes('UNKNOWN') || query.includes('unavailable')) check(await plot().count() === 0, 'F06: invalid route has no fake market');
    }
    await page.goto(base+'?entity=MOCK_A'); await label('当前行情事实').waitFor();
    await label('演示设置').click(); await page.goBack(); await label('行情列表，12 支示例股票').waitFor();
    await label('打开证据问答').click(); await label('返回行情').click(); await label('行情列表，12 支示例股票').waitFor();
    check(await label('演示设置面板').count()===0 && !page.url().includes('page=chat'), 'V15: browser back clears detail settings before later navigation');
    check(await page.locator('#root p:not([id])').count() === 0, 'Q05: no leaked text measurement node');
    check(errors.length === 0, 'Q05: no uncaught browser errors');
    check(external.length === 0, 'Q05: no external service requests');
    return { status: 'TASK1_H5_PASS', checks };
  } catch (error) {
    throw new Error(`${error.message}; completed=${checks.length}; last=${checks.at(-1) || 'none'}`);
  } finally {
    page.off('pageerror', onError); page.off('request', onRequest);
  }
}
