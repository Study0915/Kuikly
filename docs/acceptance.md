# 验收记录

本文件只记录实际执行证据。构建脚本不会自动把未执行平台标记为通过。

| 能力 | 验收方式 | 当前状态 |
|---|---|---|
| 数值范围/刻度/坐标/K 线窗口 | `:KuiklyChart:jsNodeTest` | 通过（2026-07-24，见 `docs/evidence/2026-07-24-build.md`） |
| 折线图/柱状图/K 线 DSL | Kotlin 编译 + H5 Demo 页面 | H5 首页折线图与 `BarChart`、详情页 K 线/成交量均实际渲染通过（2026-07-25） |
| Tooltip/点击/拖动/缩放平移 | H5 浏览器交互 + 截图 | 首页分时图和柱图、详情分时图与 K线均已实际点击并显示 Tooltip；浏览器自动化拖动未触发 Kuikly `pan`，缩放/平移仍待专门运行时覆盖 |
| Mock 行情列表/详情 | H5 页面交互 + 测试 | H5 观察列表实际滚动，点击“贵州茅台”后进入详情页（2026-07-25）；Android 本地 JVM Provider 测试 5/5 通过，Provider JS 单测仍受 Kotlin/JS 编译器内部异常阻断 |
| Mock AI 分析 | Provider 单测 + 页面 | H5 详情页已实际展示确定性 Mock 分析；Android 本地 JVM 测试覆盖确定性与免责声明，Provider JS 单测仍受 Kotlin/JS 编译器内部异常阻断 |
| Kotlin/JS | `:shared:compileKotlinJs` | 通过（2026-07-24） |
| H5 production bundle | `:h5App:jsBrowserProductionWebpack` | 通过（2026-07-24） |
| Android Debug APK | `scripts\verify.ps1` / `:androidApp:assembleDebug` | 通过（2026-07-24；项目内 Wrapper 8.0、JDK 17.0.12，APK 与 SHA-256 见构建证据） |
| Android 运行 | 真机或模拟器启动、截图 | 未验证：当前未检测到可用 ADB 设备，且未安装模拟器镜像 |
| iOS/鸿蒙 | 不在 Windows 验收范围 | 未验证 |

最终证据写入 `docs/evidence/`，包括命令、工具版本、日志摘要和截图路径。
