# Kuikly Finance

基于 Kuikly 的两题股票原型：Task 1 将行情与 AI 依据双向联动；Task 2 将 Markdown、可核对的行情卡、追问与详情连接起来。全部使用确定性历史 Mock，交互真实运行。见 [Task 1](docs/TASK1-RUN.md)、[Task 2](docs/TASK2-RUN.md) 和 [评分证据](docs/submit/SCORING-AUDIT.md)。

Shape Task 1/2 的原始题面与完成定义以 [docs/REQUIREMENTS.md](docs/REQUIREMENTS.md) 为准。Issue #1477 是独立历史/可选参考，不定义两题的 Must；通用图表、K 线和高级手势只有被用户选入 Task 总计划时才进入范围。

## 当前状态

- `[FACT]` 活动入口为 `finance_home`。
- `[MOCK]` 12 个确定性股票样例；摘要由数值事实和有限模板生成，没有真实行情或模型服务。
- `[VERIFIED]` 49 项共同逻辑测试；Task 1 的 59/7/23/4 项 H5/触摸/深化/桌面检查，Task 2 的 32 项 H5、11 项会话/触摸及独立 32 项桌面检查通过；H5 production 与 Android Debug 构建通过。
- `[VERIFIED]` 两题实现、测试和学习材料已完成；最新带字幕演示与本地候选见 [交付说明](docs/submit/DELIVERY.md)。个人掌握程度未代签。
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

手工调用 Wrapper 前执行 `. .\scripts\use-cli-env.ps1`。H5 生产预览使用 `.\scripts\run-h5.ps1 -Production`；运行后打开 http://127.0.0.1:18761/。浏览器回归见 `.\scripts\test-ui.ps1`。

## 活动模块

- `KuiklyChart/`：保留的空 module，旧 API 不再有效，也不构成 Task 1 核心完成前置。
- `shared/`：market 行情、insight 证据与双图、chat 会话/Markdown/内容块、ui 页面与卡片、navigation 内部路由。
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
