# Kuikly 多 Agent 协作工作流

## 1. 目标与边界

本文件是 Task 1、Task 2 的跨工具协作唯一来源，目标是让每个实现都能追溯到需求、基线 commit、执行者、测试和证据。

本阶段不接入真实行情、真实模型、登录、联网检索、付费服务或外部发布。Mock、构建成功、浏览器运行、设备运行和未验证必须分开描述。

## 2. 角色矩阵

| 角色 | 工作区 | 允许工作 | 不负责 |
|---|---|---|---|
| Windows Codex | `E:\Internship\tecent\Kuikly` | 总计划、任务卡、集成、Windows Android/H5 验收、证据裁决 | 绕过任务卡直接并行写同一文件 |
| WSL OpenCode | `~/code/Kuikly` | 任务卡指定的 Kotlin/common/UI/测试实现，commit，推送功能分支 | 写 `/mnt/e` 工作树、推送 main、合并或发布 |
| WorkBuddy | 独立评审目录 | 产品定位、交互、创新、展示叙事、阶段复盘 | 修改主仓库、替代 Codex 做最终裁决 |
| 用户 | Windows 主机 | 确认范围、采纳建议、合并、PR、发布、对外沟通 | 将未经验证的 Agent 输出直接当作项目事实 |

OpenCode 的仓库应位于 WSL Linux 文件系统；Windows 集成仓库与 WSL 仓库通过 Git 功能分支交接，不共享可写工作树。

## 3. 指令和权限优先级

1. `AGENTS.md` 中的安全、真实性、平台和验收规则；
2. 当前任务卡中的范围、允许路径和验收条件；
3. `.codebuddy/rules/kuiklyDSL.mdc` 中固定版本的官方 Kuikly DSL 规范；
4. 工具本地配置、记忆和默认行为。

任何规则冲突都要暂停实现，记录冲突和证据；不得用 Agent 的“常识”覆盖仓库规则。

## 4. 任务生命周期

```text
BACKLOG → READY → IN_PROGRESS → HANDOFF → REVIEW → VERIFIED → INTEGRATED
                         └──────────────→ BLOCKED
```

- `BACKLOG`：只有目标和优先级，尚未具备实现条件。
- `READY`：任务卡、基线 SHA、Owner、功能分支、允许路径和验收命令齐全。
- `IN_PROGRESS`：唯一写入者正在实现；不得再分配重叠路径。
- `HANDOFF`：实现者已提交 commit，并返回完整交接报告。
- `REVIEW`：Codex 检查 diff、测试、任务边界和证据。
- `VERIFIED`：Windows 必要门禁和指定人工交互已经实际通过。
- `INTEGRATED`：用户确认后才可合并到集成分支或 main。
- `BLOCKED`：官方资料冲突、重复可复现失败、缺少环境或范围未决；必须写清解除条件。

## 5. 任务卡模板

复制以下模板到任务描述、Agent 会话或 `docs/handoffs/` 临时文件中：

```markdown
# Task <ID> · <标题>

## 目标与价值
- 目标：
- 用户/评审价值：
- 完成定义：

## 执行边界
- Owner：
- 基线 commit：
- 功能分支：`feature/<topic>` 或 `bugfix/<topic>`
- 允许修改：
- 禁止修改：
- 必须复用：
- 明确非目标：

## 验收
- 自动命令：
- 人工交互：
- 证据：日志、截图、构建产物或链接
- 风险和依赖：
- 失败升级条件：
```

没有完整任务卡时，Agent 只能做只读分析，不得修改代码。

## 6. 交接报告模板

```markdown
# Handoff <Task ID>

- commit：
- 分支：
- 实际修改文件：
- 变更摘要：
- 已执行命令及结果：
- 未执行项及原因：
- 证据路径：
- 已知风险和建议复核点：
- 外部操作声明：未 merge、未 release、未发送外部消息
```

Codex 只有在交接报告完整、diff 可读、测试结果可复现后，才能把任务从 `HANDOFF` 推进到 `REVIEW`。

## 6.1 学习报告与四门完成门

每个 Feature 必须同时提交代码、测试结果、证据和学习报告。学习报告由实现 Agent 起草，Codex 审查准确性，用户通过口述和面试题自答确认理解。模板见 `docs/learning/_TEMPLATE.md`，自答记录见 `docs/INTERVIEW_NOTES.md`。

任务板继续使用原有生命周期，不新增 `TEACHING` 或 `ORAL_CHECK` 状态；四门未齐时任务只能停留在 `HANDOFF` 或 `REVIEW`。

## 7. 交付后的下一步导航

每次完成、交接、阻塞或验收回复都必须给用户一个可执行的 `下一步`，并明确是否需要外部 Agent。

### 需要 OpenCode 时

必须提供任务 ID、基线 SHA、功能分支、允许路径、WSL 目录 `~/code/Kuikly`、启动命令、首条提示词和验收命令。标准入口：

```bash
wsl -d Ubuntu-24.04
source ~/.bashrc
cd ~/code/Kuikly
opencode
```

不得只写“让 OpenCode 继续”，也不得把 OpenCode 引导到 `/mnt/e/Internship/tecent/Kuikly`。

### 需要 WorkBuddy 时

必须提供 WB-01～WB-04 评审点、独立评审目录、Default Permissions、输入材料、期望输出和不得写入主仓库的边界。原始输出保留在仓库外，Codex 验证且用户接受后才登记任务板。

### 不需要外部 Agent 时

明确写“本步不需要外部 Agent”，并指定由 Codex 或用户执行的具体动作。下一步导航只是交接信息，不授权自动启动 Agent、评审、合并、PR、发布或外部沟通。

## 8. Git 与本地配置

- 分支命名使用 `feature/<topic>` 或 `bugfix/<topic>`；提交信息使用 Angular Convention。
- Agent 可以创建聚焦 commit，并推送功能分支；禁止推送 `main`、force-push、自动 merge、PR、tag、release 或外部消息。
- `.env`、密钥、Token、`local.properties`、签名材料、机器路径和工具账户不进入 Git。
- OpenCode/WorkBuddy 的本地权限配置不提交；仓库只提交共享规则和可复现的文档模板。
- WorkBuddy 使用 Default Permissions，并将原始输出放在仓库外的独立评审目录。用户确认后，只把去重后的行动项登记到任务板。

## 9. 优秀学生质量闭环

Task 1、Task 2 均按以下六个维度检查：

1. 完成度：题目核心路径可用，边界状态有处理；
2. 创新性：AI 能力带来可见的业务交互价值，而非装饰性文本；
3. 跨端一致性：common 逻辑复用，Android/H5 状态和视觉差异有记录；
4. 工程质量：模块边界、测试、可维护性和依赖治理清晰；
5. 证据可信度：命令、版本、日志、截图与声明一致；
6. 演示表达：用户路径、亮点、限制和免责声明能在短时间内讲清楚。

WorkBuddy Token 只用于四个里程碑评审：

1. 产品定位与两题联动；
2. Task 1 技术深挖与创新表达；
3. Task 2 聊天、结构化行情和交互体验；
4. 最终 Demo、README、演示脚本和答辩叙事。

原始评审结果留在本地；Codex 验证并经用户接受的建议才进入 `docs/workboard.md` 或产品文档。

## 10. 证据和阻塞升级

- 每次功能变更至少记录组件测试、Kotlin/JS 编译、H5 production bundle 和 Android Debug 构建的执行状态；实际未运行的项目不能标为通过。
- H5 浏览器交互、Android APK 构建和 Android 真机/模拟器运行分别记录；一个不能替代另一个。
- 连续两次有日志的复现失败、官方文档与工程行为冲突、或需求边界无法从公开资料确定时，进入 `BLOCKED`。
- 阻塞报告必须包含问题、已尝试方案、命令和日志、影响、最小问题以及希望导师确认的事项；用户审核后才发送给导师。

## 11. 演练任务

首次启用 OpenCode 前先执行无业务改动演练：领取一个只读任务卡，在 Linux 原生克隆创建功能分支，生成交接报告并推送分支；Codex 只审查状态、权限和证据，不合并代码。`T1-TOUR` 当前暂缓，不阻塞本阶段文档基础设施或后续项目结构讨论。

## 12. 结构变化与任务顺序

- 当前阶段先完成 `WF-002` 工作区文档、规则和 Skill 审计，不搬迁或冻结 Task 1 源码。
- 项目结构变化必须独立建卡、说明迁移范围、回滚方式和 Task 1 回归证据，并在 `docs/DECISIONS.md` 新增 ADR。
- Task 2 的首个实现任务是 Android 最小可运行页面；聊天 UI、Mock 对话、Markdown、结构化卡片、详情、路由、图表、AI Service 和 UI 打磨按独立 Feature 依次推进。
