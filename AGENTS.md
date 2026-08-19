# Kuikly Finance 项目规则

## 当前目标

- 本仓库服务于 Tencent-TDS/KuiklyUI Issue #1477 与 2026 犀牛鸟 Shape with AI Task 1、Task 2。
- 历史 Task 1 已隔离到 `archive/task1-v1/`；其“Issue 完成者”和“TOP 3”成果只属于归档版本。
- 活动 Task 1 从跨端可编译空壳重新开发；Task 1 验证完成后再开发 Task 2。
- 详细产品范围见 `docs/REQUIREMENTS.md`，当前架构见 `docs/ARCHITECTURE.md`，任务合同见对应 readiness 文件。

## 开发管线

- Windows Codex 是 Task 1、Task 2 的默认规划者、实现者、测试者和证据裁决者。
- OpenCode 仅是备选：用户明确指定，或 Codex 带日志进入 `BLOCKED` 且用户接受 handoff 后，才可在 `~/code/Kuikly` 实现指定任务。
- WorkBuddy 只做可选里程碑评审，不修改主仓库。
- 每项 Feature 必须先登记完整任务卡，并通过 `code`、`tests`、`evidence`、`learning` 四门；完整流程只在 `docs/agent-workflow.md` 维护。
- 当前任务与状态只在 `docs/workboard.md` 维护，不在本文件复制。

## 环境与依赖

- 使用 JDK 17 和 `./gradlew`/`.\gradlew.bat`，不依赖全局 Gradle。
- Windows CLI、Gradle、npm 和 Android SDK 固定在 `.cache/`；先运行 `scripts\bootstrap-cli.ps1`、`scripts\doctor.ps1`，手工 Wrapper 前点源 `scripts\use-cli-env.ps1`。
- Android SDK、本机配置和依赖不得安装到 C 盘或提交到仓库。
- `.cache`、`.gradle`、`build`、`node_modules`、`.venv`、APK、签名和本机配置保持忽略。

## 安全与真实性

- 默认使用确定性 Mock 数据；真实行情、真实模型、付费服务、联网检索、外部发布和持久化连接必须单独确认。
- 不读取、输出或提交 `.env`、Token、密码、API Key、签名材料或真实用户数据。
- 股票页面必须展示“仅作技术演示，不构成投资建议”。
- 归档源码不参与活动 Gradle 构建，不得直接复制为新实现；需要借鉴时重新核对需求、API 和证据。

## 工程边界

- `KuiklyChart/`、`shared/`、`androidApp/`、`h5App/` 是活动空壳与后续新实现。
- `archive/task1-v1/` 是只读历史快照；CI、Gradle settings 和验收脚本不得包含它。
- 需求、架构、ADR、学习报告、Reviewer、交接和证据分别进入 `docs/` 对应入口。
- 计算逻辑与 Canvas/UI/宿主分离，边界行为必须可独立测试。

## 验收与 Git

- 每次功能变更至少运行相关单测、Kotlin/JS 编译、H5 production bundle 和 Android Debug 构建；无法运行时记录命令、原因和风险。
- H5 浏览器运行、Android APK 构建与 Android 设备运行分别记证据；Windows 构建不能证明 iOS/鸿蒙支持。
- 分支使用 `feature/<topic>` 或 `bugfix/<topic>`，提交信息使用 Angular Convention。
- Agent 可以创建聚焦 commit；推送、合并、PR、tag、release 和外部消息由用户决定。

## 下一步导航

- 每次完成、交接、阻塞或验收回复必须给出 `下一步`。
- 默认写明“本步不需要外部 Agent”并指定 Codex 或用户动作。
- 触发 OpenCode 备选时，按 `docs/agent-workflow.md` 给出完整 handoff；不得自动启动 OpenCode、WorkBuddy、PR、发布或外部沟通。
