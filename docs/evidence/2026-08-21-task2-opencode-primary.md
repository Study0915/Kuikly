# 2026-08-21 Task 角色切换证据

## 范围

- Task：`WF-004`。
- 基线：`d909b4c`。
- 分支：`feature/task2-opencode-primary`。
- 目标：Task 1 Codex 主实现并增加详细计划门；Task 2 OpenCode 主实现、Codex 备选。

## 静态一致性检查

执行：

```powershell
rg -n -S "Task 2.*OpenCode|OpenCode.*Task 2|Task 1.*详细|详细.*Task 1|Codex.*备选" `
  AGENTS.md README.md docs\agent-workflow.md docs\task1-readiness.md `
  docs\task2-readiness.md docs\workboard.md docs\skills.md docs\DECISIONS.md

rg -n -S "Codex 负责 Task 1、Task 2|Codex 是 Task 1、Task 2|OpenCode 仅是备选|Task 1、Task 2 的默认" `
  AGENTS.md README.md docs\agent-workflow.md docs\task1-readiness.md `
  docs\task2-readiness.md docs\workboard.md docs\skills.md
```

结果：第一条命令在所有活动入口找到新角色与计划门；第二条命令无匹配并以 `1` 退出，表示当前活动规则中没有残留旧默认角色表述。ADR-006 和 CHANGELOG 保留旧决策作为明确标注已被覆盖的历史记录。

## Git 与范围检查

执行：

```powershell
git diff --check
git status --short --branch
git diff --name-only
```

结果：`git diff --check` 通过；仓库变更只涉及规则、工作流、readiness、任务板、ADR、README/CHANGELOG 与 WF-004 文档，没有 Kotlin、Gradle、Android/H5 业务代码或 `archive/task1-v1/` 变更。工作区级 `AGENTS.md` 同步更新，但其不属于 Kuikly Git 仓库。

## 未执行项与真实性边界

- 未运行 Gradle、H5 或 Android 构建：本任务没有修改业务代码、构建脚本或依赖。
- 未启动 OpenCode 或 WorkBuddy。
- 未创建 Task 1 详细主计划正文；规则已明确其固定路径与完成条件，因此 T1-VERTICAL 暂退回 `BACKLOG`。
- 未推送、合并、创建 PR、发布或发送外部消息。
