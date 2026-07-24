# Kuikly Finance Charts

跨端 Kuikly 图表组件与 Shape with AI Task 1 股票行情演示工程。项目默认离线运行，行情与 AI 解读均为确定性的 Mock 实现。

## 环境

- JDK 17（Kotlin/Android 编译目标）；项目本地验证使用 `.cache\jdk17`，不要求全局安装。
- Kotlin/KMP `2.0.21`。
- Kuikly `2.4.0-2.0.21`（Kuikly 版本 `2.4.0` + Kotlin 兼容后缀）。
- Gradle Wrapper `8.0`；本机已安装的 Gradle 位于 `D:\Gradle`，仅作为诊断/缓存准备，不替代 Wrapper 契约。
- Node.js 使用机器已有运行时；Yarn 1.22.17 通过 `.cache/npm` 项目缓存管理。
- Android SDK 路径只写入未提交的 `local.properties`；构建产物和 SDK 缓存均在忽略目录。

## 模块

- `KuiklyChart`：KMP 折线图、柱状图、K 线/成交量、DSL、坐标与交互计算。
- `shared`：行情列表、个股详情、Mock Provider、Mock AI 分析和页面路由。
- `androidApp`：Android 验收宿主。
- `h5App`：H5 验收宿主与静态加载壳。

## 运行与验证

在仓库根目录执行（PowerShell）：

```powershell
.\scripts\verify.ps1
```

一键脚本为 `scripts\verify.ps1`；没有 Android SDK 时可先使用
`.\scripts\verify.ps1 -SkipAndroid`。脚本会选择项目 `.cache\jdk17`、`.cache\gradle` 和 `.cache\android`，并以单 worker、低内存参数调用 Wrapper；不会把未执行的平台写成通过。

H5 预览：

```powershell
.\scripts\run-h5.ps1
```

构建日志、版本和截图放在 `docs\evidence\`，当前验收状态以
[`docs/acceptance.md`](docs/acceptance.md) 为准。

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
