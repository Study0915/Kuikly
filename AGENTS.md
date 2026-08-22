# Kuikly Finance 项目规则

## 当前目标

- 本仓库的活动目标是 2026 犀牛鸟 Shape with AI Task 1、Task 2；原始题面和完成定义只以 `docs/REQUIREMENTS.md` 为准。
- Tencent-TDS/KuiklyUI Issue #1477 是独立历史/可选参考，不定义 Shape 题面、Must 或验收；图表、K 线和高级手势只有经用户选入 Task 总计划后才进入活动范围。
- 历史 Task 1 已隔离到 `archive/task1-v1/`；其“Issue 完成者”和“TOP 3”成果只属于归档版本。
- 活动 Task 1 从跨端可编译空壳重新开发；T1-LEARNING 验证完成后再启动 Task 2 PLAN。
- 详细产品范围见 `docs/REQUIREMENTS.md`，当前架构见 `docs/ARCHITECTURE.md`，任务合同见对应 readiness 文件。

## 开发管线

- 活动管线固定为 `Task 1 PLAN → CODE → TESTS → LEARNING → Task 2 PLAN → CODE → TESTS → LEARNING → SUBMIT`，不得跳序。
- PLAN：Windows Codex 为两题编写详细总计划，覆盖题面、40/25/25/10 评分映射、候选创新取舍、文件/接口/状态、测试、证据、风险、回滚和删减线；用户确认后才能进入 CODE。
- CODE：WSL OpenCode 是两题默认唯一写入者；Windows Codex 只在用户确认代码备选接管后写业务代码。
- TESTS：Windows Codex 在 Windows 主仓库完成 diff、单测、H5、Android、浏览器/设备和评分证据验收；失败返回同一 Task 的 CODE 修复。
- LEARNING：Windows Codex 在 TESTS 通过后编写简历与面试可复述级项目报告，不做逐 API 教程式展开。
- SUBMIT：两题 LEARNING 均完成后，Windows Codex 按评分和导师要求准备代码、README、演示视频、评分证据与提交清单；实际外部提交由用户确认。
- WorkBuddy 只做可选里程碑评审，不修改主仓库。
- 不再创建活动 Feature 任务卡；每个 Task 只维护一份总计划、一次 CODE handoff、一份 TESTS 报告和一份 LEARNING 报告。旧任务卡仅作历史审计。
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

- `shared/`、`androidApp/`、`h5App/` 是 Task 1、Task 2 的活动实现边界；`KuiklyChart/` 当前只是保留的空 module，不构成 Task 1 完成前置。
- `archive/task1-v1/` 是只读历史快照；CI、Gradle settings 和验收脚本不得包含它。
- 需求、架构、ADR、学习报告、Reviewer、交接和证据分别进入 `docs/` 对应入口。
- 计算逻辑与 Canvas/UI/宿主分离，边界行为必须可独立测试。

## 验收与 Git

- TESTS 至少运行相关单测、Kotlin/JS 编译、H5 production bundle 和 Android Debug 构建；无法运行时记录命令、原因和风险。
- H5 浏览器运行、Android APK 构建与 Android 设备运行分别记证据；Windows 构建不能证明 iOS/鸿蒙支持。
- 分支生命周期、命名与跨工作树交接以 `docs/GIT-WORKFLOW.md` 为准，提交信息使用 Angular Convention。
- Agent 可以创建聚焦 commit；推送、合并、PR、tag、release 和外部消息由用户决定。

## 下一步导航

- 每次完成、交接、阻塞或验收回复必须给出 `下一步`。
- PLAN/TESTS/LEARNING/SUBMIT 指定 Codex 或用户动作；CODE 提供 OpenCode 完整 handoff。Codex 备选接管需要用户确认。
- 不得自动启动 OpenCode、WorkBuddy、PR、发布、实际提交或外部沟通。
