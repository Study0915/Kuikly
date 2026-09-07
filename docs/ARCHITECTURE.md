# Kuikly Finance 当前架构

## 当前状态

活动工程已完成 Task 1 行情与证据联动原型，从 reset scaffold 独立实现，未迁移旧归档：

- `KuiklyChart/`：仅保留可编译的空 module；它不代表 Task 1 必须交付图表，也不冻结任何图表类型。
- `shared/`：以 `finance_home` 为唯一 Pager；market、insight、navigation、ui 承接行情与联动。
- `androidApp/`、`h5App/`：最小 Kuikly 宿主。
- `archive/task1-v1/`：历史源码和证据，不在活动 Gradle dependency graph 中。

## 目标边界

- 行情 domain、字段格式化、页面状态和 AI 分析生成放在可独立测试的 common 逻辑中。
- 页面调用确定性 Mock Provider；首版没有无需求的可替换 Provider 抽象，也不读取密钥。
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

## Task 1 当前实现（2026-09-07）

[TASK1-PLAN v1.0](plans/TASK1-PLAN.md) 已获用户确认并完成 CODE；[ADR-013](decisions/ADR-013-task1-evidence-lens.md) 与[模块合同](interfaces/QuoteEvidenceLens.md) 记录实际组织方式。当前验证见 [TESTS](REVIEWS/TASK1-TESTS.md)：

- `shared` 内已新增 `market/`、`insight/`、`navigation/`，由 `ui/` 完成行情首页及内部详情；没有新增 Gradle module，没有修改归档或空 KuiklyChart 的功能。
- QuoteEvidenceLens 封装不可变文档校验、派生事实、互斥焦点、日期反查、双图几何和 Kuikly 渲染；页面持有每实例状态并管理路由/滚动。
- `finance_home` 保持唯一 Pager 入口，Home/Detail 采用内部显式路由；H5 NotifyModule/history 与 Android BackPressModule 适配返回，H5 另处理窗口宽度及锁定版本的文本/混合输入兼容。
- 固定 20 日 K 线与成交量共用时间位置；证据聚焦和日检视互斥，点按转为滚动时取消选择。K 线与双向交互是本计划核心选择，不是原始题面新增要求。
- T1 用 A/B 与异常数据证明同一实现的适配和隔离；正式聊天 caller、消息密度与返回恢复留待 Task 2。
- 工具、依赖、缓存与临时输出位于工作区；C0 已补齐进程级隔离，未改 base 或系统 PATH。KSP 增量关闭并检查页面注册，避免非 Page 修改产生无法运行的包。

## 验证基线

- `:KuiklyChart:jsNodeTest`（空 module 当前允许 SKIPPED；不构成图表能力完成证据）
- `:shared:compileKotlinJs`
- `:h5App:jsBrowserProductionWebpack`
- `:shared:testDebugUnitTest`
- `:androidApp:assembleDebug`
- H5 浏览器交互；Android 设备运行单独记录。
