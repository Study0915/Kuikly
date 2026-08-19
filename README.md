# Kuikly Finance

Kuikly 跨端股票 Demo 的重新开发工作区。当前活动工程是 Task 1 的 Android/H5 可编译空壳；旧版图表、行情、详情和 Mock AI 实现已隔离归档，不参与当前构建。

## 历史归档

旧版 Task 1 与对应证据位于 [`archive/task1-v1/`](archive/task1-v1/README.md)。归档版本曾：

- 入选腾讯犀牛鸟开源人才培养计划「Issue 完成者」。
- 获得腾讯犀牛鸟开源人才培养计划 TOP 3。

这些成果不自动代表新版 Task 1 已完成。

## 当前状态

- `[FACT]` 活动入口为 `finance_home`。
- `[MOCK]` 当前只显示重启状态和投资免责声明，没有行情或 AI 服务。
- `[UNVERIFIED]` 新版图表、行情、详情、AI 解读和 Task 2 尚未实现。
- `[DECISION]` Codex 负责 Task 1、Task 2 的规划和实际代码；OpenCode 仅作备选。

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

- `KuiklyChart/`：新版图表组件空壳，旧 API 不再有效。
- `shared/`：新版 `finance_home` 页面空壳。
- `androidApp/`、`h5App/`：Android/H5 最小宿主。
- `docs/`：需求、架构、任务合同、证据和学习报告。

## 开发顺序

1. 完成并验证新版 Task 1；
2. 基于新版 Task 1 已验证接口开发 Task 2；
3. 每个 Feature 通过 `code/tests/evidence/learning` 四门；
4. 用户决定合并、PR、发布和外部沟通。

详细流程见 [`docs/agent-workflow.md`](docs/agent-workflow.md)，当前任务见 [`docs/workboard.md`](docs/workboard.md)。

## 真实性边界

默认使用离线、确定性的 Mock。真实行情、真实模型、登录、交易、联网检索和付费服务不在默认范围。股票内容仅作技术演示，不构成投资建议；未实际运行的平台不会标为支持。
