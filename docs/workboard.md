# Kuikly 高分交付工作台

当前活动只使用 `BACKLOG`、`IN_PROGRESS`、`WAITING_USER`、`BLOCKED`、`VERIFIED` 五种状态。阶段顺序固定为：

`Task 1 PLAN → CODE → TESTS → LEARNING → Task 2 PLAN → CODE → TESTS → LEARNING → SUBMIT`

## 当前阶段

| 阶段 | Owner | 前置条件 | 状态 | 唯一产物 / 完成条件 |
|---|---|---|---|---|
| T1-PLAN | Codex | 无 | BACKLOG | `docs/plans/TASK1-PLAN.md`；40/25/25/10 完整映射、2–4 个创新候选、用户选择与逐项验收计划 |
| T1-CODE | OpenCode | T1-PLAN VERIFIED | BACKLOG | 按已确认计划实现并提交 CODE handoff；不得自行改变创新点或评分目标 |
| T1-TESTS | Codex | T1-CODE handoff 完整 | BACKLOG | Windows 构建、自动测试、H5/Android 分级验证、评分证据与缺口报告 |
| T1-LEARNING | Codex | T1-TESTS VERIFIED | BACKLOG | 简历与面试版项目复盘；不写成 API 教程 |
| T2-PLAN | Codex | T1-LEARNING VERIFIED | BACKLOG | `docs/plans/TASK2-PLAN.md`；独立完成 40/25/25/10 映射和用户决策门 |
| T2-CODE | OpenCode | T2-PLAN VERIFIED | BACKLOG | 按已确认计划实现并提交 CODE handoff |
| T2-TESTS | Codex | T2-CODE handoff 完整 | BACKLOG | Windows 构建、自动测试、端到端交互、评分证据与缺口报告 |
| T2-LEARNING | Codex | T2-TESTS VERIFIED | BACKLOG | 简历与面试版项目复盘；能讲清职责、难点、方案、结果和边界 |
| SUBMIT | Codex / 用户 | T1、T2 LEARNING 均 VERIFIED | BACKLOG | Codex 生成并核验本地提交候选；用户决定仓库、上传、提交和对外沟通 |

## 当前下一步

1. Codex 编写 T1-PLAN，只提供创新候选、评分预测、成本与风险，不替用户选定具体创新。
2. 用户确认核心创新、通用组件、AI 载体和删减线后，T1-PLAN 才能进入 `VERIFIED`。
3. 再生成给 OpenCode 的 T1-CODE handoff。

## 历史审计

- `WF-001` 至 `WF-005`、`T1-000` 及旧 `T1-VERTICAL` / `T1-EXPERIENCE` / `T2-*` 卡片只保存过去的流程、空壳与研究事实，不是当前活动入口，也不授权代码修改。
- 旧 Task 1 源码与证据位于 `archive/task1-v1/`，不计入当前实现完成度。
- Issue #1477、通用图表、K 线和高级手势不定义题面 Must；最小趋势区域与 AI 标注可以作为 PLAN 中的评分候选，由用户决定是否采用。

## 更新规则

- 每个 Task 只有一份总计划、一次 CODE handoff、一份 TESTS 报告和一份 LEARNING 报告，不再拆活动 Feature 任务卡。
- PLAN 未获用户确认时必须停在 `WAITING_USER`；任何 Agent 不得进入 CODE。
- CODE 只有一个写入者。默认 OpenCode；切换 Codex 必须先由用户确认。
- TESTS 的 `VERIFIED` 只能由 Codex 根据实际命令、交互和平台证据标记；构建成功不能冒充运行成功。
- LEARNING 必须在 TESTS 后撰写，且不补写未经验证的成果。
- SUBMIT 只有在两题 LEARNING 均 `VERIFIED` 后启动；本地 `SUBMIT_READY` 不等于已经上传、发布或获奖。
