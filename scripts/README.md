# Scripts

- `.\scripts\bootstrap-cli.ps1`：幂等核对项目内 JDK 17、Android command-line tools，在缺少时把已有非 C 盘 Node.js 复制到 `.cache\node`，并把缺失 SDK 包安装到 `.cache\android-sdk`。
- `.\scripts\doctor.ps1`：输出 JDK、Node、npm、Wrapper、Android SDK 包与 E 盘余量，成功标记为 `CLI_ENV_OK`。
- `. .\scripts\use-cli-env.ps1`：向当前 PowerShell 会话注入项目内 CLI、缓存与 Android 路径；手工执行 Wrapper 前应先点源此脚本。
- `.\scripts\verify.ps1`：依次执行活动 `KuiklyChart` JS 单测、shared Kotlin/JS 编译、H5 Webpack、shared Android 本地 JVM 单测和 Android Debug 构建；`archive/task1-v1/` 不参与。
- `.\scripts\verify.ps1 -SkipAndroid`：本机没有 Android SDK 时仅验证组件与 H5，不强制要求 Android command-line tools。
- `.\scripts\run-h5.ps1`：启动 Kotlin/JS development server；按 `Ctrl+C` 停止。
- `.\scripts\run-h5.ps1 -Production`：只在localhost提供已经编译的H5生产包。
- `.\scripts\test-ui.ps1 -Task Both`：使用工作区缓存的Playwright CLI 0.1.18与独立Edge profile复验两题；先启动本地生产服务器。若缺CLI，点源use-cli-env后执行`npx --yes --package @playwright/cli@0.1.18 playwright-cli --version`，下载仅进入工作区npm缓存。
- `.\scripts\prepare-submission.ps1`：从明确的tracked文件清单导出当前源码、H5预览、APK和两题录像；生成逐文件SHA清单并核验ZIP。仅本地候选，已有目录不会被覆盖。
- `record-task1-demo.js`与`record-task2-demo.js`：Playwright CLI run-code录制脚本，先video-start再执行；`demo-frame.html`将字幕置于真实应用iframe外，不修改应用内容。

脚本统一使用 `.cache\jdk17`、`.cache\node`、`.cache\android-sdk` 与 `.cache\gradle`。当前 Windows `adb.exe` 仍会解析系统用户配置目录，因此在“不写 C 盘”的约束下只把 Android 编译/APK 作为自动门禁，不把真机运行标为已验证。
