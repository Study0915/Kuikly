// Actual browser checks for the post-review fixes; never queries application internals.
async (page) => {
  const base = page.url().split('/').slice(0,3).join('/') + '/';
  if (!/^http:\/\/(127\.0\.0\.1|localhost)(:\d+)?\/$/.test(base)) throw new Error('Local demo required');
  const label = name => page.getByLabel(name,{exact:true});
  const checks=[],errors=[];
  const check=(ok,name)=>{if(!ok)throw new Error(name);checks.push(name)};
  const onError=e=>errors.push(e.message);page.on('pageerror',onError);
  let id=0;
  async function answered() {
    id++;
    await page.waitForFunction(n=>{
      const answer=document.querySelector(`[aria-label="回答 ${n}"]`);
      return answer && !answer.innerText.includes('正在整理') && answer.innerText.includes('证据助手');
    },id);
    await page.waitForTimeout(120);
    return label(`回答 ${id}`);
  }
  async function send(q) {await label('股票问题输入').fill(q);await label('发送问题').click();return answered()}
  async function latestStartsInView() {
    const list=await label('问答会话记录').boundingBox();
    const question=await label(`问题 ${id}`).boundingBox();
    const answer=await label(`回答 ${id}`).boundingBox();
    return question && answer && question.y>=list.y-2 && question.y<list.y+list.height-30 && answer.y<list.y+list.height-40;
  }
  try {
    await page.setViewportSize({width:390,height:844});await page.goto(base+'?page=chat');
    for (const q of ['比较 C','比较 C 和 Z','比较 A 和 A','比较 A B C','分析 A 和 B','比较 A 和 600519']) {
      const answer=await send(q);
      check(await answer.getByLabel(/^[A-L] 行情证据卡$/).count()===0,`ambiguous or unsupported request never invents cards: ${q}`);
    }
    const dated=await send('分析 A 在 20260904 的风险，成交量1000000股');
    check(await dated.getByLabel('A 行情证据卡',{exact:true}).count()===1,'date and quantity retain the requested A');
    const comparison=await send('比较 C 和 D');
    check(await comparison.getByLabel('C 行情证据卡',{exact:true}).count()===1 && await comparison.getByLabel('D 行情证据卡',{exact:true}).count()===1,'comparison uses precisely C and D');
    check(await latestStartsInView(),'completion reveals latest question and summary, not card footer');
    await label('股票问题输入').fill('分析 L 的风险：这份草稿尚未发送');
    await comparison.getByLabel('追问 D 风险',{exact:true}).click();
    const followup=await answered();
    check(await label('股票问题输入').inputValue()==='分析 L 的风险：这份草稿尚未发送','card follow-up preserves unrelated draft');
    check(await followup.getByLabel('D 行情证据卡',{exact:true}).count()===1,'preserved draft cannot override follow-up entity D');
    await followup.getByLabel('D · 查看行情详情',{exact:true}).click();await label('‹ 返回问答会话').waitFor();
    await label('‹ 返回问答会话').click();await label('问答会话记录').waitFor();await page.waitForTimeout(120);
    check(await label('股票问题输入').inputValue()==='分析 L 的风险：这份草稿尚未发送','draft persists through follow-up detail round-trip');
    await label('问答会话记录').evaluate(el=>{el.scrollTop=0});
    await label('定位最新回答').click();await page.waitForTimeout(120);
    check(await latestStartsInView(),'latest control visibly reveals final question and answer heading');
    await label('发送问题').click();const draftAnswer=await answered();
    check(await label('股票问题输入').inputValue()==='' && await draftAnswer.getByLabel('L 行情证据卡',{exact:true}).count()===1,'explicit send consumes only the submitted draft');
    await label('股票问题输入').fill('分析 B');await label('发送问题').click();
    await label('返回行情').click();await label('打开证据问答').waitFor();
    await page.waitForTimeout(800);await label('打开证据问答').click();await label('问答会话记录').waitFor();
    id++;await label('定位最新回答').click();await page.waitForTimeout(150);
    check(await label(`回答 ${id}`).getByLabel('B 行情证据卡',{exact:true}).count()===1 && await latestStartsInView(),'detached response and remount use only the current list');
    await label('演示设置').click(); await label('新建会话').click();await page.waitForTimeout(120);
    check(await label('问答会话记录').evaluate(el=>el.scrollTop)<2 && await page.getByLabel(/^问题 \d+$/).count()===0,'new session resets actual scroll and messages');
    check(errors.length===0,'refinement flows have no browser runtime errors');
    await page.screenshot({path:'.cache/task2-evidence/refinement-end.png'});
    return {result:'TASK2_REFINEMENT_PASS',count:checks.length,checks,errors};
  } catch(e){throw new Error(`${e.message}; completed=${checks.length}; last=${checks.at(-1)}`)}
  finally{page.off('pageerror',onError)}
}
