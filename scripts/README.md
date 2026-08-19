# Scripts

- `.\scripts\bootstrap-cli.ps1`：幂等核对项目内 JDK 17、Android command-line tools，在缺少时把已有非 C 盘 Node.js 复制到 `.cache\node`，并把缺失 SDK 包安装到 `.cache\android-sdk`。
- `.\scripts\doctor.ps1`：输出 JDK、Node、npm、Wrapper、Android SDK 包与 E 盘余量，成功标记为 `CLI_ENV_OK`。
- `. .\scripts\use-cli-env.ps1`：向当前 PowerShell 会话注入项目内 CLI、缓存与 Android 路径；手工执行 Wrapper 前应先点源此脚本。
- `.\scripts\verify.ps1`：依次执行活动 `KuiklyChart` JS 单测、shared Kotlin/JS 编译、H5 Webpack、shared Android 本地 JVM 单测和 Android Debug 构建；`archive/task1-v1/` 不参与。
- `.\scripts\verify.ps1 -SkipAndroid`：本机没有 Android SDK 时仅验证组件与 H5，不强制要求 Android command-line tools。
- `.\scripts\run-h5.ps1`：启动 Kotlin/JS development server；按 `Ctrl+C` 停止。

脚本统一使用 `.cache\jdk17`、`.cache\node`、`.cache\android-sdk` 与 `.cache\gradle`。当前 Windows `adb.exe` 仍会解析系统用户配置目录，因此在“不写 C 盘”的约束下只把 Android 编译/APK 作为自动门禁，不把真机运行标为已验证。
