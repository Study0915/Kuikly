# Kuikly Finance Charts

跨端 Kuikly 图表组件与 Shape with AI 股票 Demo 工作区。Task 1 行情原型已有 Android/H5 构建证据；Task 2 问答应用当前处于接口与验收基线准备阶段，尚未实现聊天页面。项目默认离线运行，行情与 AI 解读均为确定性的 Mock 实现。

## 项目成果

- 入选腾讯犀牛鸟开源人才培养计划「Issue 完成者」。
- 获得腾讯犀牛鸟开源人才培养计划 TOP 3。

## 环境

- JDK 17（Kotlin/Android 编译目标）；项目本地验证使用 `.cache\jdk17`，不要求全局安装。
- Kotlin/KMP `2.0.21`。
- Kuikly `2.4.0-2.0.21`（Kuikly 版本 `2.4.0` + Kotlin 兼容后缀）。
- Gradle Wrapper `8.0`；分发与依赖缓存均固定在项目 `.cache\gradle`，不使用系统全局 Gradle。
- Node.js `24.12.0` 位于 `.cache\node`；Yarn 1.22.17 通过 `.cache\npm` 项目缓存管理。
- Android SDK 路径只写入未提交的 `local.properties`；构建产物和 SDK 缓存均在忽略目录。

## 模块

- `KuiklyChart`：KMP 折线图、柱状图、K 线/成交量、DSL、坐标与交互计算。
- `shared`：行情列表、个股详情、Mock Provider、Mock AI 分析和页面路由。
- `androidApp`：Android 验收宿主。
- `h5App`：H5 验收宿主与静态加载壳。

## 运行与验证

在仓库根目录执行（PowerShell）：

```powershell
.\scripts\bootstrap-cli.ps1
.\scripts\doctor.ps1
.\scripts\verify.ps1
```

一键脚本为 `scripts\verify.ps1`；没有 Android SDK 时可先使用
`.\scripts\verify.ps1 -SkipAndroid`。脚本会选择项目 `.cache\jdk17`、`.cache\gradle` 和 `.cache\android-sdk`，并以单 worker、低内存参数调用 Wrapper；不会把未执行的平台写成通过。

手工运行 Gradle 命令前先执行 `. .\scripts\use-cli-env.ps1`。当前 Windows `adb.exe` 仍会访问系统用户配置目录；为保持 C 盘零写入，本工作区暂不把 ADB/真机运行纳入自动门禁。

H5 预览：

```powershell
.\scripts\run-h5.ps1
```

构建日志、版本和截图放在 `docs\evidence\`，当前验收状态以
[`docs/acceptance.md`](docs/acceptance.md) 为准。

## Task 2 准备状态

Task 2 将新增 `finance_chat` 页面和可替换的 Mock Chat Provider，返回 Markdown 与显式的股票/指数卡片、图表内容块；卡片复用现有 `stock_detail` 路由，图表复用 `KuiklyChart`。接口、Markdown 的 Android/H5 兼容策略、失败状态和测试门禁见 [`docs/task2-readiness.md`](docs/task2-readiness.md)。

## Agent 协作与证据

项目使用 Windows Codex 负责规划/集成验收、WSL OpenCode 负责隔离实现、WorkBuddy 负责里程碑评审的协作流程。任务卡、交接格式、分支权限、Mock/构建/浏览器/设备证据边界见 [`docs/agent-workflow.md`](docs/agent-workflow.md) 与 [`docs/workboard.md`](docs/workboard.md)。

## DSL 示例

```kotlin
LineChart {
    attr {
        data(series)
        showAxis(true)
        showGrid(true)
        showDots(true)
        showTooltip(true)
        theme(ChartTheme.FINANCE_DARK)
    }
    event { onPointSelected { selection -> println(selection) } }
}

CandleChart {
    attr {
        data(candles)
        showVolume(true)
        visibleCount(28)
        theme(ChartTheme.FINANCE_DARK)
    }
    event { onCandleSelected { _, candle -> println(candle.close) } }
}
```

折线/柱状图支持点击和横向拖动选点；K 线支持窗口缩放和平移。计算层位于
`commonMain`，可独立测试空数据、单点、全等值、正负值、非有限值及边界坐标。

## 数据与免责声明

`MockMarketDataSource` 使用仓库内固定 JSON，`MockAnalysisProvider` 根据趋势、波动率和成交量生成结构化演示结果。没有 API Key、真实行情或真实模型服务依赖。

股票页面仅作技术演示，不构成投资建议；不提供交易、荐股或收益承诺。iOS 与鸿蒙只有在对应环境构建/运行成功后才会记录为支持，当前默认不宣称已验证。
