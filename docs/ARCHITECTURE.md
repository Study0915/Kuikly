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
- Task 2 在顺序上等待 T1-LEARNING VERIFIED，只按实际消费复用契约匹配的行情模型或详情能力；不要求 Task 1 预造通用图表接口。
- 若后续启动可选图表任务，图表数据、range、layout 和 hit-test 与 Canvas/UI 分离，并独立建立测试和平台证据。

## 依赖基线

- Kuikly UI `2.4.0`
- Kotlin/KMP `2.0.21`
- Kuikly 制品 `2.4.0-2.0.21`
- JDK 17
- Gradle Wrapper 8.0

版本升级、真实服务或项目结构变化必须写回对应 Task 总计划并新增/更新 ADR，不另建活动 Feature 任务卡。

## Task 1 待实施目标（2026-09-07）

[TASK1-PLAN v1.0](plans/TASK1-PLAN.md) 将用户本轮指定的联合设计 v3 落实为正式实施合同；[ADR-013](decisions/ADR-013-task1-evidence-lens.md) 记录拟议组织方式。完整合同待确认，以下不是现有源码能力：

- `shared` 内新增 `market/`、`insight/`、`navigation/`，由 `ui/` 完成行情首页及内部详情；不新增 Gradle module，不修改归档或空 KuiklyChart 的功能。
- QuoteEvidenceLens 封装不可变文档校验、派生事实、互斥焦点、日期反查、双图几何和 Kuikly 渲染；页面持有每实例状态并管理路由/滚动。
- `finance_home` 保持唯一 Pager 入口，Home/Detail 采用内部显式路由；Android/H5 只承担必要的返回、生命周期及宽度适配，具体桥接先做版本探针。
- 固定 20 日 K 线与成交量共用时间位置；证据聚焦和日检视互斥，点按转为滚动时取消选择。K 线与双向交互是本计划核心选择，不是原始题面新增要求。
- T1 用 A/B 与异常数据证明同一实现的适配和隔离；正式聊天 caller、消息密度与返回恢复留待 Task 2。
- 工具、依赖、缓存与临时输出位于工作区；C0 先补齐进程级临时目录等隔离，再执行构建/运行。已有工具目录不等于本轮验证通过。

## 验证基线

- `:KuiklyChart:jsNodeTest`（空 module 当前允许 SKIPPED；不构成图表能力完成证据）
- `:shared:compileKotlinJs`
- `:h5App:jsBrowserProductionWebpack`
- `:shared:testDebugUnitTest`
- `:androidApp:assembleDebug`
- H5 浏览器交互；Android 设备运行单独记录。
