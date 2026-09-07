# QuoteEvidenceLens 调用合同

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

`QuoteEvidenceLens` 不依赖 FinanceHomePage、导航、消息或宿主。内部封装摘要、按钮、事实、已有证据反查、Canvas 和点选。caller 提供 `PlotTap`，父 List 滚动时调用 `cancel()`；文档替换/页面销毁时调用 `reset()`。这一个小型取消接口用于让外层滚动拥有手势裁决权。

## 事实与标记

| 焦点 | 事实 | 图形 |
|---|---|---|
| 整体/局部证据 | 起止收盘、区间变化、适用限制 | 双图同一区间、价格端点方标 |
| 量能证据 | 目标量、前五日均量、倍数、样本日期 | 五日浅色范围、目标竖线及量柱 |
| 单日 | OHLC、成交量、相对前收变化 | 双图同日竖线，价格收盘横线 |
| Overview | 无可用解释或失效原因 | 保留合法原始行情，不伪造高亮 |

反查顺序：目标日 → 局部区间 → 比较样本 → 整体观察。只有整体关联时明确没有单日解读。相关入口恢复完整证据，并不把区间变化当成单日解释。

## caller 与导航

Task 1 只有一个正式详情 caller，A–L 与异常快照共用同一实现；C1 双实例夹具已从活动入口移除。Task 2 的真实第二 caller 尚未实现，不能宣称完成跨题集成。

`FinanceRoute.Detail(entityId, snapshotId?, focus?)` 为承接合同。显式不存在的 snapshot 不换最新；省略 snapshot 才使用完整 Mock。`FinanceRequests` 用递增请求票据阻止晚到结果覆写离开或新请求。

唯一 Pager 为 `finance_home`。首页 caller 保存列表偏移。H5 宿主用 NotifyModule 扩展接收路线变化，以 history 保存最小实体/快照/焦点参数；popstate 回送同一路由。Android 使用锁定版本的 `onBackPressed` 与 BackPressModule 消费合同。两端实际验证状态以 TESTS 为准。

H5 支持可复现承接地址参数 `entity`、`snapshot`、`date`、`evidence`；evidence 与 date 同时给出时 evidence 优先。刷新允许重新加载 Mock，不保证恢复旧进程状态。宿主不保存数据文档或像素位置。

## 兼容性与边界

不新增 Gradle module，不引入图表库，不复制归档。Kuikly 2.4.0 H5 的普通文本测量、混合输入与多指差异通过最小宿主/输入适配处理，原因和回归见 [技术探针](../REVIEWS/TASK1-PROBES.md)。不实现缩放、平移、拖动检视、多周期、真实行情或模型调用。
