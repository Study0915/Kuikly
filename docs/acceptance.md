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
| H5 空壳页面 | dev server + 浏览器 | HTTP 200；浏览器插件受信任路径错误，可视渲染未验证 |
| Android 设备运行 | 真机或模拟器 | 未验证 |
| 新版 Task 1 业务能力 | `T1-VERTICAL`、`T1-EXPERIENCE` | 未实现 |
| Task 2 | `T2-*` | 未实现；等待新版 Task 1 VERIFIED |

未执行的项目不得标记为通过。

详细命令与日志见 `docs/evidence/2026-08-19-codex-primary-reset.md`。
