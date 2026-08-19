# Kuikly Codex 主开发工作流

## 1. 目标

Task 1、Task 2 共用一条可追溯管线：需求、任务卡、唯一写入者、实现、测试、证据、学习报告、审查和用户集成。

## 2. 角色

| 角色 | 工作区 | 默认职责 | 启用条件 |
|---|---|---|---|
| Windows Codex | `E:\Internship\tecent\Kuikly` | 规划、任务卡、代码、测试、Android/H5 验收、证据和审查 | 默认启用 |
| WSL OpenCode | `~/code/Kuikly` | 被 handoff 的边界实现、测试和聚焦 commit | 用户明确指定，或 Codex 阻塞后经用户确认 |
| WorkBuddy | 独立评审目录 | 产品、交互、展示和阶段复盘 | 用户选择里程碑评审 |
| 用户 | Windows 主机 | 范围、handoff、合并、PR、发布和外部沟通 | 需要外部状态变化时 |

同一任务同一时间只有一个代码写入者。OpenCode 不写 `/mnt/e` 工作树，WorkBuddy 不写主仓库。

## 3. 标准管线

1. **READY**：Codex读取规则与 readiness，登记任务 ID、目标、Owner、基线 SHA、分支、允许/禁止路径、测试和证据。
2. **IN_PROGRESS**：Codex在 Windows 功能分支实现；每次修改保持任务边界，旧归档不作为复制源。
3. **HANDOFF**：实现者给出 commit、实际文件、命令结果、未执行项、证据、风险和未 merge/release 声明。
4. **REVIEW**：Codex检查 diff、任务合同、测试和平台证据；作者为 Codex 时仍执行独立的 diff/contract 审查清单。
5. **VERIFIED**：`code/tests/evidence/learning` 四门齐全，指定构建和交互实际通过。
6. **INTEGRATED**：仅由用户确认后合并或推进外部流程。

状态机：`BACKLOG → READY → IN_PROGRESS → HANDOFF → REVIEW → VERIFIED → INTEGRATED`，另设 `BLOCKED`。

## 4. 任务卡

```markdown
# Task <ID> · <标题>

## 目标与价值
- 目标：
- 用户价值：
- 完成定义：

## 执行边界
- Owner：Codex
- 基线 commit：
- 功能分支：`feature/<topic>` 或 `bugfix/<topic>`
- 允许修改：
- 禁止修改：
- 必须复用：
- 明确非目标：

## 验收
- 自动命令：
- 人工交互：
- 证据：
- 风险、依赖和失败升级条件：
```

任务卡不完整时只能只读分析。

## 5. 四门完成门

- `code`：活动代码与任务卡一致，归档未进入活动依赖图。
- `tests`：自动测试实际执行，或清楚记录无法执行的原因。
- `evidence`：命令、版本、日志、截图和构建产物与声明一致。
- `learning`：报告解释调用链、状态变化、Kuikly API、设计取舍、限制和 5 道面试题。

四门未齐只能停留在 `HANDOFF` 或 `REVIEW`。

## 6. OpenCode 备选流程

Codex 连续两次获得相同的可复现失败、遇到 Windows 无法提供的必要 Linux 环境，或用户明确要求时，先进入 `BLOCKED` 并给出最小 handoff。用户确认后才启用 OpenCode。

handoff 必须包含任务 ID、基线 SHA、`feature/<topic>` 分支、允许/禁止路径、失败日志、WSL 目录 `~/code/Kuikly`、启动命令、首条提示词和验收命令。标准入口：

```bash
wsl -d Ubuntu-24.04
source ~/.bashrc
cd ~/code/Kuikly
opencode
```

OpenCode 只提交功能分支，不推送 main、不合并、不发布。完成后 Codex 在 Windows 仓库重新验证。

## 7. 交接与下一步

```markdown
# Handoff <Task ID>
- commit / 分支：
- 实际修改文件：
- 已执行命令及结果：
- 未执行项及原因：
- 证据路径：
- 已知风险：
- 外部操作声明：未 merge、未 release、未发送外部消息
```

每次完成、交接、阻塞或验收都提供下一步。默认注明“本步不需要外部 Agent”；只有已触发备选流程时才提供 OpenCode 卡。

## 8. 顺序与边界

- 旧 Task 1 位于 `archive/task1-v1/`，不参与当前构建或验收。
- 新 Task 1 按 `docs/task1-readiness.md` 完成并进入 `VERIFIED` 后，Task 2 才进入实现。
- Task 2 按 `docs/task2-readiness.md` 复用新版 Task 1 已验证的接口，不复用归档接口。
- 推送、合并、PR、tag、release、真实服务和外部消息始终需要用户决定。
