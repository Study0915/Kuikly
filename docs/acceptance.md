# 当前验收状态

本文件只记录活动 reset scaffold；历史 Task 1 证据见 `archive/task1-v1/docs/`。

| 能力 | 验收方式 | 当前状态 |
|---|---|---|
| 项目内 CLI | `scripts\doctor.ps1` | 通过（2026-08-19，`CLI_ENV_OK`） |
| 图表模块空壳 | `:KuiklyChart:jsNodeTest` | 任务到达但 SKIPPED：空壳没有可测试图表行为 |
| shared JS 编译 | `:shared:compileKotlinJs` | 通过（2026-08-19） |
| H5 production bundle | `:h5App:jsBrowserProductionWebpack` | 通过（2026-08-19） |
| Android JVM 测试 | `:shared:testDebugUnitTest` | 通过（2026-08-19） |
| Android Debug APK | `:androidApp:assembleDebug` | 审阅修复后通过（2026-08-19，SHA-256 见 evidence） |
| H5 空壳页面 | dev server + 真实浏览器 | **通过**（2026-08-19，Edge 151 / Playwright CLI 0.1.18，页面文本、root 挂载、boot 移除、入口与 JS 200；截图见 evidence） |
| Android 设备运行 | 真机或模拟器 | 未验证 |
| Task 1 活动业务 | `T1-PLAN → T1-CODE → T1-TESTS → T1-LEARNING` | 未开始；旧切片卡不代表当前进度 |
| Task 2 活动业务 | `T2-PLAN → T2-CODE → T2-TESTS → T2-LEARNING` | 未开始；等待 T1-LEARNING VERIFIED |
| SUBMIT | 两题评分审计、代码、文档、视频与真实性清单 | 未开始；等待两题 LEARNING VERIFIED |
| 历史 T1-000 reset scaffold | 历史四门记录 | **VERIFIED**（2026-08-19；只证明空壳，不证明当前 Task 1 业务完成） |

未执行的项目不得标记为通过。

详细命令与日志见 `docs/evidence/2026-08-19-codex-primary-reset.md`。
