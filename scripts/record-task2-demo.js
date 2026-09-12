async (page) => {
  const base = page.url().split('/').slice(0,3).join('/')+'/';
  await page.setViewportSize({width:520,height:1020});await page.goto(base+'demo-frame.html');
  const app=page.frames().find(f=>f!==page.mainFrame());const label=name=>app.getByLabel(name,{exact:true});
  const steps=[];const start=await page.evaluate(()=>Date.now());
  async function scene(caption,action){if(action)await action();await page.evaluate(t=>document.getElementById('caption').textContent=t,caption);steps.push({seconds:((await page.evaluate(()=>Date.now()))-start)/1000,caption});await page.waitForTimeout(6500)}
  await label('打开证据问答').click();
  await scene('Task 2｜提问 → Markdown + 证据卡 → 详情\n离线规则模板，支持示例股票 A–L');
  await scene('输入一个问题，真实发送到会话\n消息、响应状态和输入区分离',async()=>{await label('股票问题输入').fill('分析 A 的走势')});
  await scene('回答含格式化摘要、风险与数据边界\n同时返回可交互的行情业务卡',async()=>{await label('发送问题').click();await label('A 行情证据卡').waitFor();await label('回答 1').scrollIntoViewIfNeeded()});
  await scene('三条依据分别对应区间与量能计算\n每张卡都有历史时间与明确的不确定性',async()=>{await label('A 行情证据卡').scrollIntoViewIfNeeded()});
  await scene('点“中途回落”，直达相同快照的 E2\n复用 Task 1 的详情与证据联动组件',async()=>{await label('A · 核对中途回落').click();await label('当前行情事实').scrollIntoViewIfNeeded()});
  await scene('返回原会话，记录与位置保留\n继续比较 A 和 B',async()=>{await label('‹ 返回问答会话').click();await label('股票问题输入').fill('比较 A 和 B')});
  await scene('相同窗口、各自分母：A +12%，B −4%\n同一证据卡真实复用两组数据',async()=>{await label('发送问题').click();await label('回答 2').getByLabel('B 行情证据卡',{exact:true}).waitFor();await label('回答 2').getByLabel('B 行情证据卡',{exact:true}).scrollIntoViewIfNeeded()});
  await scene('追问自动携带 B 的实体上下文\n明确历史表现不等于未来收益',async()=>{await label('追问 B 风险').click();await label('回答 3').getByLabel('B 行情证据卡',{exact:true}).waitFor();await label('回答 3').scrollIntoViewIfNeeded()});
  await scene('检验缺量情景：价格可读，倍数不可算\n不让不完整数据产生完整结论',async()=>{await label('回答 3').getByLabel('检验 B 缺量',{exact:true}).click();await label('回答 4').getByLabel('B 行情证据卡',{exact:true}).waitFor();await label('回答 4').getByLabel('B 行情证据卡',{exact:true}).scrollIntoViewIfNeeded()});
  await scene('未知股票返回能力范围提示\n不编造行情或跳转到错误详情',async()=>{await label('股票问题输入').fill('查询 MOCK_Z');await label('发送问题').click();await app.waitForFunction(()=>document.querySelector('[aria-label="回答 5"]')?.innerText.includes('暂无匹配'));await label('回答 5').scrollIntoViewIfNeeded()});
  await scene('演示首次失败：原问题保留，可原位重试\n正常流程不依赖网络或真实模型',async()=>{await label('切换回答场景').click();await label('股票问题输入').fill('分析 B');await label('发送问题').click();await label('重试问题 6').waitFor();await label('回答 6').scrollIntoViewIfNeeded()});
  await scene('重试成功，仍然是 B，无重复问题\n旧响应、取消和新会话均有隔离测试',async()=>{await label('重试问题 6').click();await label('回答 6').getByLabel('B 行情证据卡',{exact:true}).waitFor();await label('回答 6').getByLabel('B 行情证据卡',{exact:true}).scrollIntoViewIfNeeded()});
  await scene('展开共享走势图，可直接检视交易日\n会话仅本次保留；H5实测，设备运行未验证',async()=>{await label('回答 6').getByLabel('B · 展开/收起走势',{exact:true}).click();await label('行情双图：价格与成交量，点按检视交易日').scrollIntoViewIfNeeded()});
  return {result:'TASK2_RECORDING_COMPLETE',steps,durationSeconds:((await page.evaluate(()=>Date.now()))-start)/1000};
}
