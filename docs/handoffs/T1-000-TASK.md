# Task T1-000 · 新版 Task 1 跨端最小空壳

## 目标与价值

- 目标：在不复用旧业务实现的前提下，建立 Android/H5 可编译的 `finance_home` 起点。
- 用户价值：后续每个 Task 1 Feature 都能从可重复验证的干净基线开发。
- 完成定义：活动四模块无旧业务 API；共享路由常量、空壳页面、宿主、测试、构建和证据齐全。

## 执行边界

- Owner：Codex
- 基线 commit：`ab8675d3fa377486d492c39f320745314ec71467`
- 功能分支：`feature/codex-primary-reset`
- 允许修改：活动 `KuiklyChart/`、`shared/`、`androidApp/`、`h5App/`，以及本任务 acceptance/evidence/learning/handoff。
- 禁止修改：`archive/task1-v1/` 历史源码、真实服务、根依赖版本、密钥和外部状态。
- 必须复用：`finance_home` 启动语义、项目内工具链和 Kuikly 2.4.0/Kotlin 2.0.21 基线。
- 明确非目标：稳定图表 API、行情模型、Provider、`stock_detail`、AI 解读和 Task 2。

## 验收

- 自动命令：`:KuiklyChart:jsNodeTest`（允许空模块为 NO-SOURCE）、`:shared:compileKotlinJs`、`:h5App:jsBrowserProductionWebpack`、`:shared:testDebugUnitTest`、`:androidApp:assembleDebug`。
- 人工交互：浏览器显示 reset、Mock/未验证标签和投资免责声明。
- 证据：`docs/acceptance.md`、`docs/evidence/2026-08-19-codex-primary-reset.md`。
- 风险和依赖：空壳没有业务完成度；H5 visual 与 Android device 必须单独验收。
- 失败升级条件：相同编译或运行错误连续复现两次，且 Windows 无安全替代路径。
