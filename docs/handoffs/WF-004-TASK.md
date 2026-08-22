# Task WF-004 · Task 角色切换与详细计划门

## 目标与价值

- 目标：把活动管线调整为 Task 1 Codex 主实现、Task 2 OpenCode 主实现，并把 Codex 设为 Task 2 备选实现者。
- 用户价值：两题的默认写入者和交接路径明确，Task 1 在编码前拥有可逐项验收的详细主计划。
- 完成定义：工作区规则、仓库规则、工作流、readiness、任务板、Skill 路径说明和 ADR 使用同一角色语义；历史记录不被改写为当时不存在的决定。

## 执行边界

- Owner：Codex。
- 基线 commit：`d909b4c`。
- 功能分支：`feature/task2-opencode-primary`。
- 允许修改：工作区级 `AGENTS.md`、仓库级 `AGENTS.md`、`docs/agent-workflow.md`、`docs/task1-readiness.md`、`docs/task2-readiness.md`、`docs/workboard.md`、`docs/DECISIONS.md`、`docs/skills.md`、本任务卡及对应证据/学习记录。
- 禁止修改：活动 Kotlin/Gradle/Android/H5 代码、归档内容、历史 Handoff 的事实陈述、依赖版本和本机配置。
- 必须保留：Task 1 → Task 2 顺序、唯一写入者、Windows/WSL 工作树隔离、四门、Codex Windows 复验、用户决定外部状态变化。
- 明确非目标：编写 Task 1 详细主计划正文、实现 T1-VERTICAL、启动 OpenCode、修改 Task 2 代码、推送、合并、PR 或发布。

## 验收

- 静态检查：搜索所有活动规则中的 Codex/OpenCode/Task 1/Task 2 表述，不得残留“Codex 默认实现 Task 2”或“OpenCode 仅在阻塞时启用”的当前规则。
- 计划门：明确计划文件、用户确认点和七类可检查内容；缺失时 Task 1 不得进入 `IN_PROGRESS`。
- 角色门：Task 2 工作板 Owner 为 OpenCode；Codex 只在用户确认后作为备选代码写入者，但继续负责 Windows 复验和证据裁决。
- 历史门：ADR-006 标为被覆盖并新增 ADR；既有 WF-003/T1-000 历史 Handoff 不因新决策被追改。
- Git 检查：`git diff --check` 通过，且 diff 不包含业务代码或归档文件。
- 证据：记录实际检查命令、结果、未执行项和外部操作声明。
- 风险、依赖和失败升级条件：如果“第二步”无法从 Task 顺序唯一映射到 Task 2，停止修改并向用户确认；当前上下文已明确 Task 1/Task 2 顺序，因此按 Task 2 执行。
