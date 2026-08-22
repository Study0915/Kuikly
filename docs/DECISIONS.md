# Kuikly 架构决策记录

本文件是当前项目的决策索引。每条决策记录背景、选择、替代方案、影响和复核条件；项目结构变化必须新增记录，不直接覆盖历史理由。

## ADR-001 · Mock-first 离线原型

- 状态：Accepted
- 选择：Task 1、Task 2 默认使用仓库内确定性 Mock Provider。
- 原因：保证 Android/H5 可复现，避免 API Key、网络、成本和外部服务成为验收变量。
- 复核条件：真实服务接入必须先写入对应 Task 总计划，补齐鉴权、失败、成本和数据来源合同，并经用户确认。

## ADR-002 · 显式聊天内容块

- 状态：Superseded by ADR-008
- 选择：聊天响应使用 Markdown、QuoteCard、TrendChart、Notice 等显式类型；结构化数据不从 Markdown 反解析。
- 原因：防止不可信 HTML/脚本执行，并让 UI、测试和路由边界清晰。

## ADR-003 · 复用图表与详情路由

- 状态：Superseded by ADR-007
- 选择：Task 2 复用 `KuiklyChart` 和 `stock_detail`，不在聊天页复制坐标计算或详情模板。
- 原因：降低跨端差异和维护成本，并让 Task 1 的组件成果可复用。
- 历史边界：这里只描述归档版本；Task 2 只能复用新版 Task 1 后续验证通过的接口。

## ADR-004 · Markdown 跨端降级

- 状态：Accepted
- 选择：先做 Android/H5 依赖探针；H5 不可用时使用受限 commonMain Markdown renderer。
- 原因：公开依赖声明不自动等价于 H5 支持，必须以实际编译和浏览器证据为准。

## ADR-005 · 学习闭环优先于结构重构

- 状态：Superseded by ADR-007
- 选择：当前不物理搬迁或冻结 Task 1；先建立文档、任务卡、学习报告和 Reviewer 合同，后续结构调整用独立 ADR 和任务卡管理。
- 原因：保护已有回归基线，避免为“看起来更整洁”引入不可追踪的跨端风险。

## ADR-006 · Codex 主开发，OpenCode 备选

- 状态：Superseded by ADR-009
- 选择：Windows Codex 负责 Task 1、Task 2 的规划、代码、测试、部署和证据；OpenCode 只在用户明确指定或 Codex 带日志阻塞并经用户确认后启用。
- 原因：减少跨工作树交接成本，让需求、实现和 Windows Android/H5 验收由同一默认 Owner 闭环。
- 影响：OpenCode 不再是固定第二步；其 WSL 原生克隆和安全边界继续保留为故障备选。

## ADR-007 · 旧 Task 1 隔离归档并全部重建

- 状态：Accepted
- 选择：把旧 `KuiklyChart`、`shared`、Android/H5 宿主和历史证据迁入 `archive/task1-v1/`；活动模块从可编译空壳重新开发。
- 原因：用户明确舍弃旧完成部分，并要求 Task 1 使用与 Task 2 相同的 Feature 管线重新建设。
- 影响：旧图表、行情模型、Provider、页面和路由不再是活动 API 或回归基线；旧成果和验证只属于归档版本。原“Feature 管线”协作方式已由 ADR-010 覆盖。
- 恢复：完整历史可从 `ab8675d3fa377486d492c39f320745314ec71467` 获取；归档目录不进入 Gradle、CI 或当前验收。

## ADR-008 · 原始题面优先并解除 Issue/图表绑定

- 状态：Accepted
- 背景：旧规划把 KuiklyUI Issue #1477 的 line/bar、图表 DSL 和平台要求混入 Shape Task 1/2；用户于 2026-08-19 提供原始题面并明确 Issue 不是题面。
- 选择：Task 1 核心以行情列表、个股详情与详情内 AI 分析为完成门；Task 2 核心以聊天、Markdown 外至少一种业务内容，以及至少一次详情承接为完成门；承接页展示基础行情、走势区域，并提供摘要或 AI 解读至少一种。Issue #1477、通用图表、K 线和高级手势均为独立历史或后置 Optional。
- 影响：Task 2 只按实际消费复用 Task 1 已验证能力，不得为了走势区域反向给 Task 1 增加图表前置。阶段顺序与产物已由 ADR-010 覆盖。
- 覆盖边界：ADR-002 中列出的具体 block 不再是固定集合，由 Task 2 总计划选择至少一种非 Markdown 业务内容；ADR-004 只在总计划选择 KuiklyMarkdown 依赖时触发。

## ADR-009 · Task 1 Codex 主实现，Task 2 OpenCode 主实现

- 状态：Superseded by ADR-010
- 日期：2026-08-21
- 背景：用户要求两题继续使用同一套任务卡、四门和验收管线，但交换 Task 2 的默认实现角色，并要求 Task 1 在编码前具备十分详细的计划。
- 选择：Task 1 由 Windows Codex 规划和主实现、OpenCode 备选；Task 2 由 WSL OpenCode 主实现、Codex 备选。Codex 继续负责两题的 Windows Android/H5 复验、证据裁决和集成准备。
- 计划门：Task 1 业务编码前必须有经用户确认的 `docs/plans/TASK1-DETAILED-PLAN.md`，且满足 `docs/agent-workflow.md` 中可检查的七类详细度条件；单个任务卡不能替代该主计划。
- 工作树：OpenCode 只写 `~/code/Kuikly` 的功能分支；Codex 只写 Windows 仓库。同一任务同时只有一个写入者，跨端工具链与缓存不复用。
- 切换条件：Task 1 启用 OpenCode 备选或 Task 2 启用 Codex 备选，都必须先记录可复现阻塞或收到用户明确要求，再由用户确认 handoff。
- 不变项：Task 1 VERIFIED 后才启动 Task 2；两题仍通过 `code/tests/evidence/learning` 四门；推送、合并、PR、tag、release 和外部消息仍由用户决定。

## ADR-010 · 两题统一为 PLAN/CODE/TESTS/LEARNING/SUBMIT 管线

- 状态：Accepted
- 日期：2026-08-22
- 背景：用户的首要目标是获得高分，且时间有限；逐 Feature 任务卡和教程式 learning 增加了管理成本，也让创新、评审叙事与最终提交责任分散。
- 选择：活动流程统一为 `Task 1 PLAN → CODE → TESTS → LEARNING → Task 2 PLAN → CODE → TESTS → LEARNING → SUBMIT`。Codex 负责两题详细规划、Windows 验收、简历/面试级报告与提交包；OpenCode 是两题默认代码写入者，Codex 仅在用户确认后备选接管。
- 评分门：每份 Task 总计划必须映射功能 40%、工程 25%、AI 场景 25%、加分 10%。创新点未决定时，PLAN 只能给候选、权衡与推荐，用户选择前 CODE 保持阻塞。
- 产物：每题只维护一份总计划、一次 CODE handoff、一份 TESTS 报告和一份 LEARNING 报告；测试证据包含在 TESTS，旧 Feature 任务卡退出活动入口但保留历史审计。
- 提交边界：两题 LEARNING 均完成后才进入 SUBMIT；Codex 只准备本地候选包，实际 push、PR、tag、release、报名提交和对外沟通仍由用户决定。
