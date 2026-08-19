# Contributing

## 开始任务

1. 读取 `AGENTS.md`、`docs/agent-workflow.md`、`docs/workboard.md` 和对应 readiness 文件。
2. Codex登记任务卡、基线 SHA、Owner、分支、允许路径、测试和证据要求。
3. 从当前基线创建 `feature/<topic>` 或 `bugfix/<topic>` 分支。
4. 在 Windows 仓库实现、测试并补齐证据和学习报告。

OpenCode 不是默认步骤；只有用户指定或 Codex 带日志阻塞并经用户确认后，才使用 WSL `~/code/Kuikly` 备选 handoff。

## 完成门

- `code`：变更范围与任务卡一致；归档目录不进入活动依赖。
- `tests`：执行相关单测和构建门禁。
- `evidence`：记录版本、命令、日志、截图、产物和未验证项。
- `learning`：解释调用链、状态变化、Kuikly API、设计取舍和 5 道面试题。

执行：

```powershell
.\scripts\doctor.ps1
.\scripts\verify.ps1
git diff --check
```

提交遵循 Angular Convention。Agent 可创建聚焦 commit，但推送、合并、PR、tag、release 和外部消息由用户决定。

不得提交 `.env`、Token、密钥、签名、本机 SDK 路径、缓存、构建产物或真实用户数据。Mock、构建、浏览器运行和设备运行必须分别描述。
