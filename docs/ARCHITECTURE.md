# Kuikly Finance 架构与边界

> 本文是当前架构基线。若项目结构发生变化，新增 ADR 和迁移任务，不直接覆盖历史决策。

## 目标

工程同时交付两个可复用层级：

1. `KuiklyChart`：与业务无关的折线图、柱状图、K 线/成交量、坐标与交互组件。
2. `shared`：离线可复现的股票行情与 Mock AI 分析 Demo。

## 模块

- `KuiklyChart/commonMain`：数据模型、范围/刻度/坐标换算、标签抽样、命中测试、K 线窗口算法、DSL 与 Canvas 组件。
- `KuiklyChart/commonTest`：不依赖 UI 的确定性测试。
- `shared/commonMain`：页面、路由、金融模型、Provider 接口和固定 Mock 数据。
- `androidApp`：Android JVM 模式宿主。
- `h5App`：Web 渲染器宿主。

## Task 2 规划边界

- `shared/commonMain` 负责消息模型、ChatProvider、聊天状态和内容块；默认使用确定性 Mock。
- 聊天页只负责消息展示和用户操作，股票/指数卡片与图表通过显式事件复用 `stock_detail` 和 `KuiklyChart`。
- Markdown 先做 Android/H5 依赖探针；H5 不兼容时使用受限 commonMain renderer。
- 当前没有真实 NetworkModule 或模型服务接入；未来接入必须独立建卡并补齐密钥、失败和成本边界。

## 依赖与版本

- Kuikly UI：`2.4.0`
- Kotlin/KMP：`2.0.21`
- Kuikly 制品：`2.4.0-2.0.21`
- JDK：17
- Gradle：8.0（Wrapper，实际来源以 `gradle/wrapper/gradle-wrapper.properties` 为准）

Kuikly 官方制品版本格式为 `{Kuikly版本}-{Kotlin版本}`。因此目标中的 `2.0.21`
落实为 Kotlin 兼容后缀，并对 core、KSP、Android renderer 和 Web renderer 保持一致。

## 设计原则

- 计算核心不得引用 Kuikly Canvas 或宿主 API。
- 无效数值在进入几何计算前过滤；空数据返回显式空状态。
- 单点和全等值数据使用稳定的最小范围，避免除零。
- 点击与拖动统一映射到最近有效点。
- 页面只依赖 Provider 接口；默认实现不联网、不读取密钥。
- 股票页面始终展示“仅作技术演示，不构成投资建议”。

## 版本范围

- `v0.1.0-issue1477`：折线图、柱状图、DSL、Tooltip、点击/拖动选点。
- `v0.2.0-shape-demo`：K 线与成交量组合、可测试的窗口缩放/平移、面积填充和股票详情集成。
- 当前不包含实时行情、真实模型调用或交易能力。
- Mock Provider 不代表真实行情或模型接入。
- Windows 构建不能证明 iOS/鸿蒙支持。

## 验证

- `:KuiklyChart:jsNodeTest`
- `:shared:compileKotlinJs`
- `:h5App:jsBrowserProductionWebpack`
- `:androidApp:assembleDebug`
- H5 浏览器手工/自动截图；Android 仅在设备或模拟器真实启动后记录运行证据。
