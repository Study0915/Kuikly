# ADR-013 · Task 1 行情证据联动卡与工作区工具隔离

- 状态：Accepted；2026-09-07 用户审阅计划提交 `0060711` 后明确“开始实施”，完整实施合同及 CODE 已获确认。
- 日期：2026-09-07。
- 对应计划：[TASK1-PLAN v1.0](../plans/TASK1-PLAN.md)。CODE 已实现；H5 与构建证据见 [TESTS](../REVIEWS/TASK1-TESTS.md)，Android 设备仍未运行。

## 背景

用户以联合设计 v3 开始 Task 1 正式计划，选择固定 20 日 K 线、成交量、十字光标与 AI 证据双向联动的规划方向，并要求 Git commit 管理及工作区内安装，不污染 base 环境。现有活动工程为空壳，不能继承归档的图表或业务完成度。

## 决定

1. Codex 在 Windows 同一 Task 分支完成 PLAN、CODE、TESTS、LEARNING；本 ADR 不授权其他 Agent 写入。完整 PLAN 由用户确认后才进入 CODE。
2. QuoteEvidenceLens 在现有 shared 内使用 `market/`、`insight/`，并以 `navigation/`、`ui/` 承接页面；不新增 Gradle module，不开发空 KuiklyChart，不迁移归档。
3. Module 的 Interface 为不可变 EvidenceDocument、带 documentKey 的互斥焦点状态、少量动作与带上下文事件。校验、事实、关联、几何、命中与渲染留在 Implementation，caller 管载入、导航、会话及滚动。
4. 首版保留 `finance_home` 单 Pager 入口，通过内部显式 Home/Detail 路由切换；详情是内容 ViewBuilder。宿主只适配生命周期、返回和必要布局信息，具体桥接能力由锁定版本技术探针裁决。
5. 价格/成交量共用日期坐标、分别计算纵轴，Focus 仅能为 Overview、EvidenceFocus 或 DayInspect。点按转为父级纵向滚动后取消选择；固定窗口，无拖动检视/缩放/平移。
6. 确定性 Mock 的数值来自 OHLCV 计算；缺量演示创建独立快照并让关联量能证据真实失效。保留历史、截止时间、解释限制及投资风险提示。
7. T1 通过 A/B 数据变体与测试夹具证明适配/隔离；正式 T2 消息 caller 与会话返回验收等待 T1-LEARNING 后，不提前宣称跨题复用。
8. 工具、依赖、SDK、缓存、测试 profile 和任务临时文件均放在仓库 `.cache/` 或局部 `.venv`；只设置子进程环境，不改系统 PATH、不用 conda base/全局 pip/npm。ADB 等工具未证明配置落点前不启动；Android 构建与运行证据分开。

## 备选与代价

- 单向走势定位与摘要卡复杂度较低，但不能满足 v3 的行情检视和证据回查；仅在核心探针失败、局部修复无效且用户重议范围后采用。
- 新建通用 Chart module 或引入复杂图表库会扩大接口、平台与许可成本，当前没有足够调用需求。
- 单 Pager 内部路由缩小平台导航依赖，但必须单独实现并验证返回、首页滚动恢复与旧请求隔离，不能因避免 push API 而遗漏这些行为。

## 验证与后续

具体文件、公式、状态、命令、P/F/I/Q 验收项与回滚点均在唯一 Task 总计划维护，不另建 Feature 卡。C1–C4 已完成，技术夹具已移除；H5 实际运行和 Android 构建分开裁决。

实现备注：H5 通过 NotifyModule 的宿主扩展接收最小路线参数，popstate 回送同一 Pager；Android 使用锁定源码的 onBackPressed/BackPressModule。文本测量和混合输入兼容只在必要宿主/图形输入位置处理。KSP 增量会丢注册的复现已修复为关闭增量并核对生成入口。C5 仅新增可复现测试/录像脚本及工作区 FFmpeg 运行时（LGPLv2.1），无业务库或模块结构扩张。

下一步：用户体验 Demo 并完成学习自测；之后独立确认 Task 2 PLAN，Android 运行仍需满足工作区配置约束。
