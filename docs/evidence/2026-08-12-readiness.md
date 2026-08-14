# 2026-08-12 Task 1 回归与 Task 2 准备证据

## 当前工具与磁盘

- 项目 JDK：Oracle JDK `17.0.12`，位于 `.cache\jdk17`。
- 项目 Node.js：`24.12.0`，npm `11.6.2`，已复制到 `.cache\node`。
- Gradle：Wrapper `8.0`，缓存位于 `.cache\gradle`。
- Android SDK：platforms 33/34、Build Tools 30.0.3、platform-tools 37，位于 `.cache\android-sdk`。
- 盘点时 E 盘可用空间约 `45.5 GB`；未安装 Android Studio、模拟器或系统镜像。

## 当前回归

先执行 `scripts\bootstrap-cli.ps1` 与 `scripts\doctor.ps1`，输出 `CLI_ENV_OK`；随后执行 `scripts\verify.ps1`，更新后全量回归总耗时约 50 秒：

- `:KuiklyChart:jsNodeTest`：通过。
- `:shared:compileKotlinJs`：通过。
- `:h5App:jsBrowserProductionWebpack`：通过。
- `:shared:testDebugUnitTest`：通过。
- `:androidApp:assembleDebug`：通过。

APK 路径为 `androidApp\build\outputs\apk\debug\androidApp-debug.apk`，大小 `6,522,693` bytes，SHA-256 为 `B16BDC82A11E9923253E73EC274B82EF3770BA9C4888DBEBE1B99D970E0303FF`。任务为 up-to-date，因此哈希与 7 月验收产物一致。

构建仍提示 AGP 7.4.2 仅测试到 compileSdk 33，而工程使用 compileSdk 34；这是已知维护债，不影响本次成功构建。本轮不做高风险 AGP/Kuikly 大版本升级。

## 已知边界

- 这是 2026-08-12 盘点时的系统默认 Java 观察；受支持脚本均强制使用项目内 `.cache\jdk17`，当前环境以 `scripts\doctor.ps1` 的 JDK 17 检查为准，直接调用 Wrapper 仍不属于受支持入口。
- Windows platform-tools 37 的 `adb.exe` 会解析系统用户目录下的 `.android`。为遵守 C 盘零写入，本轮不运行 ADB、不安装模拟器，Android 状态保持“Debug APK 构建通过、设备运行未验证”。
- KuiklyMarkdown 的公开说明未声明 H5；本轮只建立兼容探针与 fallback 合同，没有添加依赖或声称 Task 2 Markdown 已通过。
