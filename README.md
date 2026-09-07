# Kuikly Finance

Kuikly 跨端股票 Demo。当前 Task 1 已完成行情列表、个股详情，以及 K 线/成交量/AI 证据双向联动；使用历史 Mock。旧版实现只作归档，不参与活动构建。运行与操作见 [Task 1 使用说明](docs/TASK1-RUN.md)，实际结果见 [TESTS](docs/REVIEWS/TASK1-TESTS.md)。

Shape Task 1/2 的原始题面与完成定义以 [docs/REQUIREMENTS.md](docs/REQUIREMENTS.md) 为准。Issue #1477 是独立历史/可选参考，不定义两题的 Must；通用图表、K 线和高级手势只有被用户选入 Task 总计划时才进入范围。

## 历史归档

旧版 Task 1 与对应证据位于 [`archive/task1-v1/`](archive/task1-v1/README.md)。归档版本曾：

- 入选腾讯犀牛鸟开源人才培养计划「Issue 完成者」。
- 获得腾讯犀牛鸟开源人才培养计划 TOP 3。

这些成果不自动代表新版 Task 1 已完成。

## 当前状态

- `[FACT]` 活动入口为 `finance_home`。
- `[MOCK]` 12 个确定性股票样例；摘要由数值事实和有限模板生成，没有真实行情或模型服务。
- `[VERIFIED]` 21 个共同逻辑单测、H5 59 项功能检查、7 项触摸检查及桌面鼠标/加载取消通过；JS/H5 和 Android Debug 构建通过。
- `[UNVERIFIED]` Task 2 尚未实现；学习复盘已交付，用户掌握程度待自测。当前角色与阶段以 [workboard](docs/workboard.md) 为准。
- `[UNVERIFIED]` Android 设备运行、iOS 和 HarmonyOS 未执行；APK 构建不等于设备运行。
- `[DECISION]` Codex 负责两题 PLAN、TESTS、LEARNING 和 SUBMIT 准备；OpenCode 是两题默认 CODE 写入者，Codex 只有经用户确认后才备选接管。

## 环境

- JDK 17：`.cache\jdk17`
- Node.js/npm：`.cache\node`、`.cache\npm`
- Gradle Wrapper 8.0：缓存位于 `.cache\gradle`
- Android SDK：`.cache\android-sdk`

依赖和 SDK 不安装到 C 盘，也不要求全局 Gradle。PowerShell 入口：

```powershell
.\scripts\bootstrap-cli.ps1
.\scripts\doctor.ps1
.\scripts\verify.ps1
```

手工调用 Wrapper 前执行 `. .\scripts\use-cli-env.ps1`。H5 预览使用 `.\scripts\run-h5.ps1`。

## 活动模块

- `KuiklyChart/`：保留的空 module，旧 API 不再有效，也不构成 Task 1 核心完成前置。
- `shared/`：新版 `finance_home` 页面空壳。
- `androidApp/`、`h5App/`：Android/H5 最小宿主。
- `docs/`：需求、架构、任务合同、证据和学习报告。

## 开发顺序

1. Task 1：Codex PLAN → OpenCode CODE → Codex TESTS → Codex LEARNING；
2. Task 2：Codex PLAN → OpenCode CODE → Codex TESTS → Codex LEARNING；
3. 两题 LEARNING 均通过后，Codex 按 40/25/25/10 和导师要求准备 SUBMIT 候选；
4. 用户决定合并、PR、发布、报名提交和外部沟通。

详细流程见 [`docs/agent-workflow.md`](docs/agent-workflow.md)，分支与交接规则见 [`docs/GIT-WORKFLOW.md`](docs/GIT-WORKFLOW.md)，当前任务见 [`docs/workboard.md`](docs/workboard.md)。

## 真实性边界

默认使用离线、确定性的 Mock。真实行情、真实模型、登录、交易、联网检索和付费服务不在默认范围。股票内容仅作技术演示，不构成投资建议；未实际运行的平台不会标为支持。
