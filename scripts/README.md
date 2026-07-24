# Scripts

- `.\scripts\verify.ps1`：依次执行组件 JVM 单测、Kotlin/JS 编译、H5 Webpack 和 Android Debug 构建。
- `.\scripts\verify.ps1 -SkipAndroid`：本机没有 Android SDK 时仅验证组件与 H5。
- `.\scripts\run-h5.ps1`：启动 Kotlin/JS development server；按 `Ctrl+C` 停止。

脚本始终把 Gradle 缓存写入仓库内 `.cache/gradle`。
