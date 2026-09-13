# 浅色行情界面的设计依据

2026-09-13。采用白色内容面板、浅灰背景、深色正文和蓝色主动作；红绿只表达行情方向，保留正负号。系统中文字体不依赖远程加载，本地 Canvas 绘制导航图标。

[Android 布局指南](https://developer.android.com/design/ui/mobile/guides/layout-and-content/layout-basics)的内容分组和主要动作原则，用在报价头、图表、依据、事实的顺序；[TradingView 精简行情列表](https://www.tradingview.com/blog/en/minimalistic-display-mode-for-watchlist-41721/)提供紧凑价格/涨跌列表参考；[Android 无障碍指南](https://developer.android.com/guide/topics/ui/accessibility/apps)用于 48 触控区和文字对比度目标。落实结果见[视觉审查](../evidence/task2/visual-review.json)，这不是全量无障碍认证。

| 参考项目 | 公开材料可借鉴之处 | 本轮取舍 |
|---|---|---|
| [KuiklyStock / PR 1](https://github.com/Kuikly-contrib/Kuikly-awesome/pull/1) | 行情失败降级及完整操作 | 保留明确状态与恢复动作，不加入未实现的搜索入口 |
| [StockChat / PR 2](https://github.com/Kuikly-contrib/Kuikly-awesome/pull/2) | 问答、业务卡和详情追问 | 缩短欢迎内容，统一输入区与证据卡 |
| [SaiRen / PR 3](https://github.com/Kuikly-contrib/Kuikly-awesome/pull/3) | 紧凑行情与分类卡片、项目预览 | 减少卡片嵌套，三条依据纵向清晰排列 |
| [KuiklyAIStock / PR 4](https://github.com/Kuikly-contrib/Kuikly-awesome/pull/4) | 完整导航和分析信息分区 | 只提供行情/问答两个真实入口，优先看事实再展开细节 |

上述参考来自作者公开材料，不是对其真机、接口或性能的独立验收；不认定所有同学都完成了物理真机部署。登记格式另按[官方指南](https://github.com/Kuikly-contrib/Kuikly-awesome/tree/Tencent/OpenSourceTalent)办理，仅准备自己的 GitHub ID 目录 README。

本项目保留的重点：结论与区间、交易日、计算式可双向核对；缺量影响可计算性；两题按股票/快照/证据身份实际复用 resolver、presenter、plot 和详情。没有因为视觉模仿而移除数值口径或改变图表命中几何，也不把其他项目已有的问答卡说成本项目独有。
