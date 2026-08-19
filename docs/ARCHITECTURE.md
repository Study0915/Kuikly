# Kuikly Finance 当前架构

## 当前状态

活动工程是 Task 1 reset scaffold，不是旧版实现的兼容升级：

- `KuiklyChart/`：仅保留可编译的新版模块边界，尚无稳定图表 API。
- `shared/`：只注册 `finance_home` 空壳页。
- `androidApp/`、`h5App/`：最小 Kuikly 宿主。
- `archive/task1-v1/`：历史源码和证据，不在活动 Gradle dependency graph 中。

## 目标边界

- 图表的数据模型、范围、刻度、坐标和命中测试放在可独立测试的 common 逻辑中。
- 页面只依赖新定义的 Provider 接口；默认实现离线、确定性、无密钥。
- Android/H5 共享业务状态和模型，宿主只负责生命周期、模块注册和平台导航。
- Task 2 只在 Task 1 的新接口通过验证后复用，不引用归档包名或类型。

## 依赖基线

- Kuikly UI `2.4.0`
- Kotlin/KMP `2.0.21`
- Kuikly 制品 `2.4.0-2.0.21`
- JDK 17
- Gradle Wrapper 8.0

版本升级、真实服务或项目结构变化必须独立建卡和新增 ADR。

## 验证基线

- `:KuiklyChart:jsNodeTest`
- `:shared:compileKotlinJs`
- `:h5App:jsBrowserProductionWebpack`
- `:shared:testDebugUnitTest`
- `:androidApp:assembleDebug`
- H5 浏览器交互；Android 设备运行单独记录。
