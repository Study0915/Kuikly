# Contributing

## 环境

- JDK 17
- 项目 Gradle Wrapper
- `GRADLE_USER_HOME=$PWD/.cache/gradle`
- npm 缓存 `$PWD/.cache/npm`

## 工作流

1. 从 `main` 建立 `feature/<topic>` 或 `bugfix/<topic>` 分支。
2. 先在 `docs/` 记录功能边界和验证方式。
3. 保持计算逻辑与 Canvas/宿主解耦，并补齐边界测试。
4. 执行 `scripts/verify.ps1`。
5. 提交信息遵循 Angular Convention。

每个 Feature 还必须提交一份面向初学者的学习报告，解释调用链、状态变化、关键 Kuikly API、设计取舍、证据和 5 道面试题。代码、测试、证据和学习四门齐全后，Codex 才能将任务标记为 `VERIFIED`。模板见 `docs/learning/_TEMPLATE.md`。

## 多 Agent 工作流

- 先读取 `AGENTS.md`、`docs/agent-workflow.md` 和 `docs/workboard.md`。
- 任务必须从功能分支开始，并在 `docs/` 写清边界、测试和证据要求。
- Windows Codex 负责集成与 Android/H5 验收；WSL OpenCode 只能在 Linux 原生克隆中实现任务；WorkBuddy 只做独立评审。
- 允许推送功能分支；禁止自动推送 `main`、force-push、合并、发布或对外发送内容。
- 交接时必须提供 commit、测试结果、未执行项和证据路径。
- 独立 Reviewer 只读检查当前 diff；原始结果保存在仓库外，采纳后的结论写入 `docs/REVIEWS/`、`docs/BUG_LOG.md` 或 `docs/DECISIONS.md`。
- 当前不物理搬迁或冻结 Task 1；后续结构调整必须使用独立任务卡和 ADR。

详细状态机、任务卡和交接模板见 [`docs/agent-workflow.md`](docs/agent-workflow.md)。

不得提交密钥、本机 SDK 路径、构建缓存或运行产物。平台支持声明必须附实际证据。
