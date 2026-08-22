# Kuikly Finance 当前架构

## 当前状态

活动工程是 Task 1 reset scaffold，不是旧版实现的兼容升级：

- `KuiklyChart/`：仅保留可编译的空 module；它不代表 Task 1 必须交付图表，也不冻结任何图表类型。
- `shared/`：只注册 `finance_home` 空壳页。
- `androidApp/`、`h5App/`：最小 Kuikly 宿主。
- `archive/task1-v1/`：历史源码和证据，不在活动 Gradle dependency graph 中。

## 目标边界

- 行情 domain、字段格式化、页面状态和 AI 分析生成放在可独立测试的 common 逻辑中。
- 页面只依赖新定义的 Provider 接口；默认实现离线、确定性、无密钥。
- Android/H5 共享业务状态和模型，宿主只负责生命周期、模块注册和平台导航。
- Task 2 在顺序上等待 Task 1 VERIFIED，只按实际消费复用契约匹配的行情模型或详情能力；不要求 Task 1 预造图表接口。
- 若后续启动可选图表任务，图表数据、range、layout 和 hit-test 与 Canvas/UI 分离，并独立建立测试和平台证据。

## 依赖基线

- Kuikly UI `2.4.0`
- Kotlin/KMP `2.0.21`
- Kuikly 制品 `2.4.0-2.0.21`
- JDK 17
- Gradle Wrapper 8.0

版本升级、真实服务或项目结构变化必须独立建卡和新增 ADR。

## 验证基线

- `:KuiklyChart:jsNodeTest`（空 module 当前允许 SKIPPED；不构成图表能力完成证据）
- `:shared:compileKotlinJs`
- `:h5App:jsBrowserProductionWebpack`
- `:shared:testDebugUnitTest`
- `:androidApp:assembleDebug`
- H5 浏览器交互；Android 设备运行单独记录。
