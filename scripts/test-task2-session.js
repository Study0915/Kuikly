async (page) => {
  const base = page.url().split('/').slice(0,3).join('/') + '/';
  if (!/^http:\/\/(127\.0\.0\.1|localhost)(:\d+)?\/$/.test(base)) throw new Error('Local demo required');
  const label = name => page.getByLabel(name,{exact:true});
  const checks=[],errors=[];
  const check=(ok,name)=>{if(!ok)throw new Error(name);checks.push(name)};
  const onError=e=>errors.push(e.message);page.on('pageerror',onError);
  try {
    await page.setViewportSize({width:390,height:844}); await page.goto(base+'?page=chat');
    for(let i=1;i<=20;i++) {
      await label('股票问题输入').fill(`未知问题 ${i}`);await label('发送问题').click();
      await page.waitForFunction(n=>document.querySelector(`[aria-label="回答 ${n}"]`)?.innerText.includes('暂无匹配'),i);
    }
    check((await label('回答 20').innerText()).includes('暂无匹配'),'20 turns remain responsive');
    await label('股票问题输入').fill('第21轮');await label('发送问题').click();
    await page.waitForFunction(()=>document.body.innerText.includes('已达到 20 轮'));
    check(await label('问题 21').count()===0,'capacity stops new sends without deleting old messages');
    await label('问答会话记录').evaluate(el=>{el.scrollTop=0});await label('问题 1').waitFor();
    check((await label('问题 1').innerText()).includes('未知问题 1'),'first turn can still be read after 20 turns');
    await label('定位最新回答').click();await label('问题 20').waitFor();
    check((await label('问题 20').innerText()).includes('未知问题 20'),'latest answer navigation reaches retained last turn');
    await label('新建会话').click();await label('股票问题输入').fill('分析 A');await label('发送问题').click();
    await label('返回行情').click();await label('打开证据问答').waitFor();
    await page.waitForTimeout(750);await label('打开证据问答').click();await label('A 行情证据卡').waitFor();
    check((await label('A 行情证据卡').innerText()).includes('11.20'),'reply completes while chat is unmounted and resumes safely');
    await label('A · 展开/收起走势').click();
    const plot=label('行情双图：价格与成交量，点按检视交易日');await plot.scrollIntoViewIfNeeded();
    const client=await page.context().newCDPSession(page);
    try {
      let b=await plot.boundingBox();
      const before=await label('问答会话记录').evaluate(el=>el.scrollTop);
      await client.send('Input.dispatchTouchEvent',{type:'touchStart',touchPoints:[{x:b.x+100,y:b.y+150}]});
      for(let d=15;d<=90;d+=15){await client.send('Input.dispatchTouchEvent',{type:'touchMove',touchPoints:[{x:b.x+100,y:b.y+150+d}]});await page.waitForTimeout(35)}
      await client.send('Input.dispatchTouchEvent',{type:'touchEnd',touchPoints:[]});await page.waitForTimeout(150);
      check(page.url().includes('page=chat'),'dragging chat chart does not navigate accidentally');
      check(Math.abs(await label('问答会话记录').evaluate(el=>el.scrollTop)-before)>15,'dragging chart actually scrolls conversation');
      await plot.scrollIntoViewIfNeeded();b=await plot.boundingBox();
      await page.touchscreen.tap(b.x+42+5.5*(b.width-54)/20,b.y+100);
      await label('当前行情事实').waitFor();
      check(page.url().includes('date=2026-08-17') && (await label('当前行情事实').innerText()).includes('检视 2026-08-17'),'tap in chat chart opens exact date in shared detail');
      await label('‹ 返回问答会话').click();await label('A 行情证据卡').waitFor();
      check(await plot.count()===1,'detail return retains expanded chart state for the same message');
      const saved=page.url();await page.reload();await label('问答会话记录').waitFor();
      check(page.url()===saved,'chat reload keeps route without multiplying history');
      check(errors.length===0,'long-session and gesture flow has no runtime errors');
    } finally {await client.detach()}
    return {result:'TASK2_SESSION_PASS',count:checks.length,checks,errors};
  } catch(e){throw new Error(`${e.message}; completed=${checks.length}; last=${checks.at(-1)}`)}
  finally{page.off('pageerror',onError)}
}
