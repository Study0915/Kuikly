# QuoteEvidenceLens 调用合同

2026-09-13：保留四参数调用合同（公式直接显示）。新版详情使用额外的 `calculationExpanded` getter 与 `onToggleCalculation` 回调，由Pager按DocumentKey保存展开状态；两者只控制公式的呈现，不改变LensFocus、URL或数值。端点、样本和相关依据入口持续可用，首次真实布局后恢复该文档阅读位置。视觉依赖仅为共享FinanceTheme，不依赖页面或路由。

## 数据到交互

`MockMarketProvider → EvidenceDocument → EvidenceResolver.resolve → ResolvedDocument → QuoteEvidenceLens`

`market` 保存 20 日历史 Mock；价格为整数分，量为股。`EvidenceDocument` 绑定实体、不可变快照和结构化引用。构造时复制输入集合，不依赖设计 fixture 的 expected 字段。

`resolve(document, now)` 校验行情、引用、连续五日量能样本，计算区间收益与量能倍数，生成有限模板摘要及质量原因。日期不能排序补救；引用缺失不能选近邻；缺量不能跳过样本凑足五天。失效证据不参与反查和绘图。

`LensState.initial / normalize / reduce` 管理 `Overview | EvidenceFocus | DayInspect`。caller 持有每实例 `LensUiState`，事件必须带当前 DocumentKey。跨文档事件被忽略；新文档初始化；非法恢复焦点回 Overview 并提示。宽度变化不改语义选择。

```kotlin
val resolved = EvidenceResolver.resolve(document, now)
var state = LensState.initial(resolved, requestedFocus)
// 下列 state 在 Kuikly caller 中使用 observable 持有。
QuoteEvidenceLens(resolved, { state }, tap) { action ->
    state = LensState.reduce(resolved, state, resolved.key, action)
}
```

上例只适用于文档不变的独立卡片。异步换页的 Task 1 caller 使用 `FinanceSession`：一次加载产生唯一 FinanceRequest；成功后 FinanceContent 原子持有 document/lens。挂载的 View 回传该 request，session 用当前文档处理事件；旧文档和同文档上一次挂载的事件均被拒绝。不能把旧闭包捕获的 document 与当前 lens 混用。

`QuoteEvidenceLens` 不依赖 FinanceHomePage、导航、消息或宿主。内部封装摘要、按钮、事实、已有证据反查、Canvas 和点选。`LensPresenter` 每实例缓存一个选择的纯展示投影，集中提供计算过程、样本/端点入口、稳定排序的关联与前后交易日；多个响应式属性读取不会重复计算关联。caller 提供 `PlotTap`，父 List 滚动时调用 `cancel()`；文档替换/页面销毁时调用 `reset()`。这一个小型取消接口用于让外层滚动拥有手势裁决权。

## 事实与标记

| 焦点 | 事实 | 图形 |
|---|---|---|
| 整体/局部证据 | 起止收盘、区间变化、计算式、适用限制；端点可点选 | 双图同一区间、价格端点方标 |
| 量能证据 | 目标量、五日明细/合计/均量、倍数；样本/目标可点选 | 五日浅色范围、目标竖线及量柱 |
| 单日 | OHLC、成交量、相对前收变化 | 双图同日竖线，价格收盘横线 |
| Overview | 无可用解释或失效原因 | 保留合法原始行情，不伪造高亮 |

反查顺序：目标日 → 局部区间 → 比较样本 → 整体观察。只有整体关联时明确没有单日解读。相关入口恢复完整证据，并不把区间变化当成单日解释。

日期导航使用窗口内有序交易日，不按自然日加减。日检视以当前日为锚点；证据模式以结束日/目标日为锚点，中央按钮明确“检视”，点击后进入 DayInspect；前后按钮也提交同一个 InspectDay 动作。窗口边界按钮不可操作，单击不会越界或绕回。

## caller 与导航

Task1的详情caller覆盖A–L与异常快照。2026-09-12 Task2已接入：EvidenceAnswerCard复用同一事实/presenter层，展开走势调用MarketPlot，精确承接调用FinanceDetail/QuoteEvidenceLens；单股票、A/B比较与缺量均有运行证据。C1临时夹具不计当前复用成果。

`FinanceRoute.Detail(entityId, snapshotId?, focus?, fromChat=false)`为承接合同。显式不存在的snapshot不换最新；省略snapshot才使用完整Mock。FinanceRequests用请求票据拒绝晚到结果，fromChat决定返回原会话或列表。Task2聊天上下文的独立合同见[EvidenceChat](EvidenceChat.md)。

唯一 Pager 为 `finance_home`。首页 caller 保存列表偏移。H5 宿主用 NotifyModule 扩展接收路线变化，以 history 保存最小实体/快照/焦点参数；popstate 回送同一路由。Android 使用锁定版本的 `onBackPressed` 与 BackPressModule 消费合同。两端实际验证状态以 TESTS 为准。

H5 支持可复现承接地址参数 `entity`、`snapshot`、`date`、`evidence`、`overview=1`；显式 overview 优先，其次 evidence、date。进入详情 pushState，当前快照/选择变化 replaceState，前进和刷新按参数重新加载 Mock。刷新保留当前历史条目；首次直接打开承接地址会建立可返回的真实首页条目。宿主不保存数据文档或像素位置；“首次失败”演示重新载入时重新执行该场景的首次请求规则。

## 兼容性与边界

不新增 Gradle module，不引入图表库，不复制归档。Kuikly 2.4.0 H5 的普通文本测量、混合输入与多指差异通过最小宿主/输入适配处理，原因和回归见 [技术探针](../REVIEWS/TASK1-PROBES.md)。不实现缩放、平移、拖动检视、多周期、真实行情或模型调用。
