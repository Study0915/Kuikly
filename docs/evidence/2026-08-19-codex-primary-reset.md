# 2026-08-19 Codex 主开发与 Task 1 reset 证据

## 基线与范围

- 基线：`feature/learning-workflow@ab8675d3fa377486d492c39f320745314ec71467`
- 实施分支：`feature/codex-primary-reset`
- 旧实现：迁入 `archive/task1-v1/`
- 活动实现：`KuiklyChart`、`shared`、`androidApp`、`h5App` 最小空壳

## 归档隔离

- 归档 Git 文件数（写入 README 前的已跟踪快照）：31。
- 归档中命中的 build/cache/node_modules/APK/密钥类已跟踪文件：0。
- `settings.gradle.kts`、根 build、CI、`verify.ps1`、`run-h5.ps1` 对 `archive/task1-v1` 的引用：0。
- 活动四模块对旧 `LineChart`、`BarChart`、`CandleChart`、`MarketDataSource`、`AnalysisProvider`、`stock_detail` 的引用：0。

## CLI 环境

执行：

```powershell
.\scripts\doctor.ps1
```

结果：`CLI_ENV_OK`。

- JDK：17.0.12
- Node.js：24.12.0
- npm：11.6.2
- Gradle Wrapper：8.0
- Android SDK：Build Tools 30.0.3、platform-tools、platforms 33/34
- E 盘可用空间：74.1 GB

## 自动门禁

执行：

```powershell
.\scripts\verify.ps1
```

结果：exit 0。

- `:KuiklyChart:jsNodeTest`：通过。
- `:shared:compileKotlinJs`：通过。
- `:h5App:jsBrowserProductionWebpack`：通过，JS/H5 阶段 `BUILD SUCCESSFUL in 2m 50s`。
- `:shared:testDebugUnitTest`：通过。
- `:androidApp:assembleDebug`：通过，Android 阶段 `BUILD SUCCESSFUL in 1m 43s`。

已知非阻塞警告：Webpack bundle 347 KiB 超过推荐值；AGP 7.4.2 只测试到 compileSdk 33，而工程使用 34。

## Android 产物

- 路径：`androidApp/build/outputs/apk/debug/androidApp-debug.apk`
- 大小：6,397,564 bytes
- SHA-256：`30DED8628080CB6990F5A13517532F013A2518FF91E903AAE5AF2FFC8FCC9785`
- 状态：Debug APK 构建通过；ADB、真机和模拟器未执行。

## H5 服务与视觉边界

执行 `scripts\run-h5.ps1` 后，Webpack dev server 在 `http://localhost:8080/` 编译成功；HTTP 探针返回 200，并读到 `Kuikly Task 1 Reset` 与 `INITIALIZING TASK 1 RESET`。

浏览器可视检查未完成：Browser 连接报 `Trusted RPC dependency must resolve within a configured trusted code path`。这是浏览器控制插件的受信任路径错误，不是 Gradle 或 H5 编译失败。当前只能声明 H5 bundle 与本地服务通过，不能声明页面已完成可视运行验收。

## 外部操作

- 未推送、未合并、未创建 PR、未发布、未发送外部消息。
- 未启动 OpenCode 或 WorkBuddy。
