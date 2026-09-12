async (page) => {
  const base = page.url().split('/').slice(0,3).join('/')+'/';
  await page.setViewportSize({width:520,height:1020});await page.goto(base+'demo-frame.html');
  const app = page.frames().find(f=>f!==page.mainFrame());
  const label = name=>app.getByLabel(name,{exact:true});
  const steps=[];const start=await page.evaluate(()=>Date.now());
  async function scene(caption,action){if(action)await action();await page.evaluate(t=>document.getElementById('caption').textContent=t,caption);steps.push({seconds:((await page.evaluate(()=>Date.now()))-start)/1000,caption});await page.waitForTimeout(7000)}
  await label('查看示例股票 A').waitFor();
  await scene('Task 1｜行情 → 详情 → AI 证据\n12支确定性示例股票，字段与涨跌口径完整');
  await scene('打开 A：基础行情和历史时间一目了然\n所有解读均为 Mock，不是实时投资建议',async()=>{await label('查看示例股票 A').click();await label('个股详情').waitFor()});
  await scene('点击“中途回落”\n价格与成交量同时标出同一证据区间',async()=>{await label('中途回落').click();await label('行情双图：价格与成交量，点按检视交易日').scrollIntoViewIfNeeded()});
  await scene('展开事实核对：11.50 → 10.80，−6.09%\n显示端点、分母与“不是最大回撤”的边界',async()=>{await label('当前行情事实').scrollIntoViewIfNeeded()});
  await scene('从区间端点进入单日，再逐日检视\n单日涨跌与区间变化使用不同分母',async()=>{await label('检视起点 08-24 ›').click();await label('后一日').click();await label('当前行情事实').scrollIntoViewIfNeeded()});
  await scene('从一天反查已有依据\n日期与解释形成双向联系',async()=>{await app.getByLabel('区间内 · 中途回落 ›',{exact:true}).click();await label('当前行情事实').scrollIntoViewIfNeeded()});
  await scene('量能也能核对：600 ÷ 5 = 120万股\n180 ÷ 120 = 1.50倍',async()=>{await label('量能观察').click();await label('当前行情事实').scrollIntoViewIfNeeded()});
  await scene('点击任一比较样本，回到当天原始行情\n依据不是无法检查的一段文字',async()=>{await app.getByLabel(/^样本 09-02 ·/).click();await label('当前行情事实').scrollIntoViewIfNeeded()});
  await scene('切换缺量：09-02缺失，量能比较不可用\n不跳过缺失日补样本，价格依据仍然有效',async()=>{await label('个股详情').evaluate(el=>el.scrollTop=el.scrollHeight);await label('量能缺失').click();await label('当前行情事实').waitFor();await label('量能观察 · 暂不可用').scrollIntoViewIfNeeded()});
  await scene('同一组件适配 B：整体−4%，局部+5.49%\n证明独立数据与相同交互的复用',async()=>{await label('‹ 返回行情列表').click();await label('查看示例股票 B').click();await label('局部反弹').click();await label('当前行情事实').scrollIntoViewIfNeeded()});
  await scene('首次失败有明确恢复动作\n重试保留同一股票，不伪造加载成功',async()=>{await label('个股详情').evaluate(el=>el.scrollTop=el.scrollHeight);await label('首次失败').click();await label('重试当前股票').waitFor()});
  await scene('恢复完成｜共同逻辑、H5交互与APK分别验收\nAndroid设备、iOS与鸿蒙仍未验证',async()=>{await label('重试当前股票').click();await label('个股详情').waitFor()});
  return {result:'TASK1_RECORDING_COMPLETE',steps,durationSeconds:((await page.evaluate(()=>Date.now()))-start)/1000};
}
