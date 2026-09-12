// Run with the workspace Playwright CLI against the production localhost bundle.
async (page) => {
  const base = page.url().split('/').slice(0, 3).join('/') + '/';
  if (!/^http:\/\/(127\.0\.0\.1|localhost)(:\d+)?\/$/.test(base)) throw new Error('Local demo required');
  const checks = [], errors = [], external = [];
  const label = name => page.getByLabel(name, {exact: true});
  const check = (ok, title) => { if (!ok) throw new Error(title); checks.push(title); };
  const waitText = text => page.waitForFunction(t => document.body.innerText.includes(t), text);
  const onError = error => errors.push(error.message);
  const onRequest = req => { if (!req.url().startsWith(base) && /^https?:/.test(req.url())) external.push(req.url()); };
  page.on('pageerror', onError); page.on('request', onRequest);
  const shot = name => page.screenshot({path: `.cache/task2-evidence/${name}.png`});
  let id = 0;
  async function send(question) {
    await label('股票问题输入').fill(question);
    await label('发送问题').click();
    id++;
    await label(`回答 ${id}`).waitFor();
    await page.waitForFunction(n => {
      const answer = document.querySelector(`[aria-label="回答 ${n}"]`);
      return answer?.innerText.includes('证据助手') && !answer.innerText.includes('正在整理') && document.querySelector('[aria-label="发送问题"]')?.innerText === '发送';
    }, id);
    return label(`回答 ${id}`);
  }
  try {
    await page.setViewportSize({width:390,height:844});
    await page.goto(base); await label('打开证据问答').click();
    await page.waitForURL('**/?page=chat');
    check(page.url().includes('page=chat'), 'home opens chat route');
    await label('发送问题').click(); await waitText('请输入问题后发送');
    check(await page.getByLabel(/^问题 \d+$/).count() === 0, 'empty send creates no message');
    await label('股票问题输入').fill('字'.repeat(501)); await label('发送问题').click(); await waitText('问题最多 500 字');
    check(await page.getByLabel(/^问题 \d+$/).count() === 0, 'oversize rejected without truncation');
    const compare = await send('比较 A 和 B');
    await label('B 行情证据卡').waitFor();
    check((await compare.innerText()).includes('同窗口对比'), 'Markdown heading rendered');
    check(await compare.locator('span').evaluateAll(nodes => nodes.some(n => getComputedStyle(n).fontWeight === '700' || getComputedStyle(n).fontWeight === 'bold')), 'Markdown bold rendered as styled span');
    check((await label('A 行情证据卡').innerText()).includes('+12.00%') && (await label('B 行情证据卡').innerText()).includes('-4.00%'), 'same card implementation renders independent A/B values');
    check((await compare.innerText()).includes('历史 Mock') && (await compare.innerText()).includes('2026-09-04') && (await compare.innerText()).includes('风险'), 'answer exposes source time and uncertainty');
    await label('A 行情证据卡').scrollIntoViewIfNeeded(); await shot('comparison-a');
    await label('股票问题输入').fill('保留的草稿');
    await label('A · 核对中途回落').click(); await label('当前行情事实').waitFor();
    check(page.url().includes('entity=MOCK_A') && page.url().includes('evidence=E2') && page.url().includes('fromChat=1'), 'card route binds entity snapshot evidence and origin');
    check((await label('当前行情事实').innerText()).includes('-6.09%'), 'detail resolves exact selected evidence');
    await label('当前行情事实').scrollIntoViewIfNeeded(); await shot('chat-detail-evidence');
    await label('‹ 返回问答会话').click(); await label('问答会话记录').waitFor();
    check(await label('股票问题输入').inputValue() === '保留的草稿', 'detail return preserves draft');
    check((await label('回答 1').innerText()).includes('同窗口对比'), 'detail return preserves full comparison');
    await page.goForward(); await label('当前行情事实').waitFor();
    check((await label('当前行情事实').innerText()).includes('-6.09%'), 'browser forward restores evidence');
    await page.goBack(); await label('问答会话记录').waitFor();
    await label('B · 核对局部反弹').click(); await label('当前行情事实').waitFor();
    check(page.url().includes('MOCK_B') && (await label('当前行情事实').innerText()).includes('+5.49%'), 'B card cannot navigate to A evidence');
    await label('‹ 返回问答会话').click(); await label('追问 B 风险').click(); id++;
    await waitText('先核对风险边界');
    const followup = label(`回答 ${id}`);
    check(await followup.getByLabel('B 行情证据卡', {exact:true}).count() === 1 && await followup.getByLabel('A 行情证据卡', {exact:true}).count() === 0, 'follow-up captures B context');
    await followup.getByLabel('检验 B 缺量', {exact:true}).click(); id++; await waitText('量能需要完整样本');
    const missing = label(`回答 ${id}`);
    check((await missing.innerText()).includes('成交量缺失：2026-09-02') && !(await missing.innerText()).includes('量能倍数 0.80'), 'missing sample disables volume conclusion');
    await missing.getByLabel('B 行情证据卡', {exact:true}).scrollIntoViewIfNeeded(); await shot('missing-volume');
    await missing.getByLabel('B · 核对量能观察', {exact:true}).click({force:true});
    check(page.url().includes('page=chat'), 'invalid evidence does not navigate');
    await missing.getByLabel('B · 查看行情详情', {exact:true}).click(); await label('当前行情事实').waitFor();
    check(page.url().includes('missing_volume') && (await page.locator('body').innerText()).includes('当前：量能缺失'), 'missing snapshot survives detail handoff');
    await label('‹ 返回问答会话').click();
    await label('新建会话').click();
    const unknown = await send('查询 MOCK_Z');
    check((await unknown.innerText()).includes('暂无匹配') && await unknown.getByLabel(/行情证据卡$/).count() === 0, 'unknown entity yields no fabricated card');
    const malicious = await send('<img src="https://invalid.test/x" onerror="alert(1)"><script>alert(1)</script>');
    check((await malicious.innerText()).includes('暂无匹配') && await page.locator('img, iframe, script[src*="invalid.test"]').count() === 0, 'untrusted user text does not create active DOM');
    await label('切换回答场景').click(); await send('分析 B');
    await label(`重试问题 ${id}`).waitFor(); await shot('failure-retry');
    await label(`重试问题 ${id}`).click(); await label(`回答 ${id}`).getByLabel('B 行情证据卡', {exact:true}).waitFor();
    check(await label(`问题 ${id}`).count() === 1, 'retry replaces failed answer without duplicate question');
    await label('切换回答场景').click();
    await label('股票问题输入').fill('分析 A'); await label('发送问题').click(); id++;
    await label('取消本次回答').click(); await waitText('已取消');
    await page.waitForTimeout(750);
    check((await label(`回答 ${id}`).innerText()).includes('已取消'), 'late completion cannot overwrite cancellation');
    await label(`重试问题 ${id}`).click(); await label(`回答 ${id}`).getByLabel('A 行情证据卡',{exact:true}).waitFor();
    check((await label(`回答 ${id}`).innerText()).includes('11.20'), 'cancelled request retries successfully');
    await label('股票问题输入').fill('分析 B'); await label('发送问题').click(); id++;
    await label('发送问题').click();
    await waitText('请等待当前回答');
    await label('新建会话').click(); await page.waitForTimeout(750);
    check(await page.getByLabel(/^问题 \d+$/).count() === 0, 'new session rejects pending response and duplicate send');
    const responsive = await send('分析 A');
    await responsive.getByLabel('A · 展开/收起走势', {exact:true}).click();
    await label('行情双图：价格与成交量，点按检视交易日').waitFor();
    check(await responsive.locator('canvas').count() > 0, 'chat expands shared chart renderer');
    for (const width of [320,390,1024]) {
      await page.setViewportSize({width,height:844});
      await page.waitForFunction(w => document.querySelector('[aria-label="问答输入区"]')?.getBoundingClientRect().width === w, width);
      const b = await label('问答输入区').boundingBox(), input = await label('股票问题输入').boundingBox();
      check(b.x >= 0 && b.x + b.width <= width+1 && b.y+b.height <= 845 && input.width > 100, `${width}px composer fits viewport`);
      await responsive.getByLabel('A 行情证据卡', {exact:true}).scrollIntoViewIfNeeded(); await shot(`chat-${width}`);
    }
    await page.setViewportSize({width:390,height:500});
    await page.waitForFunction(() => document.querySelector('[aria-label="问答输入区"]')?.getBoundingClientRect().bottom <= 501);
    const small = await label('问答输入区').boundingBox();
    check(small.y+small.height <= 501, 'composer fits reduced viewport height');
    await page.setViewportSize({width:390,height:844});
    await page.reload(); await label('问答会话记录').waitFor();
    check(await page.getByLabel(/^问题 \d+$/).count() === 0, 'refresh explicitly starts a fresh in-memory conversation');
    check(page.url().includes('page=chat') && !page.url().includes('分析'), 'URL contains route only, no question');
    check(errors.length === 0, 'no browser runtime errors');
    check(external.length === 0, 'no external requests');
    return {result:'TASK2_H5_PASS',count:checks.length,checks,errors,external};
  } catch (e) { throw new Error(`${e.message}; completed=${checks.length}; last=${checks.at(-1)}`); }
  finally { page.off('pageerror',onError); page.off('request',onRequest); }
}
