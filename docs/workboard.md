# Kuikly 高分交付工作台

当前活动只使用 `BACKLOG`、`IN_PROGRESS`、`WAITING_USER`、`BLOCKED`、`VERIFIED` 五种状态。阶段顺序固定为：

`Task 1 PLAN → CODE → TESTS → LEARNING → Task 2 PLAN → CODE → TESTS → LEARNING → SUBMIT`

## 当前阶段

| 阶段 | Owner | 前置条件 | 状态 | 唯一产物 / 完成条件 |
|---|---|---|---|---|
| T1-PLAN | Codex | 用户审阅计划提交后明确“开始实施” | VERIFIED | [TASK1-PLAN v1.0](plans/TASK1-PLAN.md) 已于 2026-09-07 获确认；确认时提交 `0060711` |
| T1-CODE | Codex | T1-PLAN VERIFIED | IN_PROGRESS | [CODE 实施记录](handoffs/TASK1-CODE.md)；依次进行 C0–C4，按已确认计划保存聚焦 commit |
| T1-TESTS | Codex | T1-CODE 实施记录完整 | BACKLOG | Windows 构建、自动测试、H5/Android 分级验证、评分证据与缺口报告 |
| T1-LEARNING | Codex | T1-TESTS VERIFIED | BACKLOG | 简历与面试版项目复盘；不写成 API 教程 |
| T2-PLAN | Codex | T1-LEARNING VERIFIED | BACKLOG | `docs/plans/TASK2-PLAN.md`；独立完成 40/25/25/10 映射和用户决策门 |
| T2-CODE | Codex | T2-PLAN VERIFIED | BACKLOG | 按已确认计划在 Windows 工作树实施并维护 CODE 实施记录 |
| T2-TESTS | Codex | T2-CODE 实施记录完整 | BACKLOG | Windows 构建、自动测试、端到端交互、评分证据与缺口报告 |
| T2-LEARNING | Codex | T2-TESTS VERIFIED | BACKLOG | 简历与面试版项目复盘；能讲清职责、难点、方案、结果和边界 |
| SUBMIT | Codex / 用户 | T1、T2 LEARNING 均 VERIFIED | BACKLOG | Codex 生成并核验本地提交候选；用户决定仓库、上传、提交和对外沟通 |

## 当前下一步

1. 用户已确认 [TASK1-PLAN v1.0](plans/TASK1-PLAN.md)；Codex 持续实施 C0–C4，再进入 TESTS 与 LEARNING。
2. 实施分支 `feature/task1-quote-evidence-lens`，基线 `0060711`；既存差异单列保留，按清单提交本轮文件。
3. 实施先做 C0 工作区工具隔离，再做 C1 双图绘制、点选、滚动取消及返回探针；不把 APK 构建写成 Android 运行，不提前启动 Task 2。

## 2026-09-07 PLAN 记录

- 已将固定 20 日 K 线、成交量、十字光标和双向证据联动纳入正式计划；拟议包组织见 [ADR-013](decisions/ADR-013-task1-evidence-lens.md)。
- 已核验两组设计 fixture 的 40 条行情和 6 条证据计算；该结果仅为算例核验，未进行业务构建、浏览器或设备运行。
- 已创建 Task 短期分支，计划使用聚焦 commit；本轮不安装依赖、不改 base 环境，不将本机路径/配置或缓存加入 Git。
- 开始时有既存流程文档差异。本轮 workboard 同步保留其 Codex 角色修改；其他非本轮文件继续保留原状，不自动整仓提交。

## 历史审计

- `WF-001` 至 `WF-005`、`T1-000` 及旧 `T1-VERTICAL` / `T1-EXPERIENCE` / `T2-*` 卡片只保存过去的流程、空壳与研究事实，不是当前活动入口，也不授权代码修改。
- 旧 Task 1 源码与证据位于 `archive/task1-v1/`，不计入当前实现完成度。
- Issue #1477、通用图表、K 线和高级手势不定义题面 Must；最小趋势区域与 AI 标注可以作为 PLAN 中的评分候选，由用户决定是否采用。

## 更新规则

- 每个 Task 只有一份总计划、一份 CODE 实施记录、一份 TESTS 报告和一份 LEARNING 报告，不再拆活动 Feature 任务卡。
- PLAN 未获用户确认时必须停在 `WAITING_USER`；任何 Agent 不得进入 CODE。
- CODE 只有一个写入者：Codex。在用户明确重设角色并更新总计划与 ADR 前，Claude Code 与 OpenCode 不参与活动实施。
- TESTS 的 `VERIFIED` 只能由 Codex 根据实际命令、交互和平台证据标记；构建成功不能冒充运行成功。
- LEARNING 必须在 TESTS 后撰写，且不补写未经验证的成果。
- SUBMIT 只有在两题 LEARNING 均 `VERIFIED` 后启动；本地 `SUBMIT_READY` 不等于已经上传、发布或获奖。
