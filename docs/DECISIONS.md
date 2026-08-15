# Kuikly 架构决策记录

本文件是当前项目的决策索引。每条决策记录背景、选择、替代方案、影响和复核条件；项目结构变化必须新增记录，不直接覆盖历史理由。

## ADR-001 · Mock-first 离线原型

- 状态：Accepted
- 选择：Task 1、Task 2 默认使用仓库内确定性 Mock Provider。
- 原因：保证 Android/H5 可复现，避免 API Key、网络、成本和外部服务成为验收变量。
- 复核条件：真实服务接入必须单独建任务卡，补齐鉴权、失败、成本和数据来源合同。

## ADR-002 · 显式聊天内容块

- 状态：Accepted
- 选择：聊天响应使用 Markdown、QuoteCard、TrendChart、Notice 等显式类型；结构化数据不从 Markdown 反解析。
- 原因：防止不可信 HTML/脚本执行，并让 UI、测试和路由边界清晰。

## ADR-003 · 复用图表与详情路由

- 状态：Accepted
- 选择：Task 2 复用 `KuiklyChart` 和 `stock_detail`，不在聊天页复制坐标计算或详情模板。
- 原因：降低跨端差异和维护成本，并让 Task 1 的组件成果可复用。

## ADR-004 · Markdown 跨端降级

- 状态：Accepted
- 选择：先做 Android/H5 依赖探针；H5 不可用时使用受限 commonMain Markdown renderer。
- 原因：公开依赖声明不自动等价于 H5 支持，必须以实际编译和浏览器证据为准。

## ADR-005 · 学习闭环优先于结构重构

- 状态：Accepted
- 选择：当前不物理搬迁或冻结 Task 1；先建立文档、任务卡、学习报告和 Reviewer 合同，后续结构调整用独立 ADR 和任务卡管理。
- 原因：保护已有回归基线，避免为“看起来更整洁”引入不可追踪的跨端风险。
