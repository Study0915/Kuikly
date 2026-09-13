# Kuikly Finance 当前架构

2026-09-13呈现调整见[ADR-015](decisions/ADR-015-shared-finance-presentation.md)：FinanceTheme统一文字/按钮/本地图标；DetailPresentationState按DocumentKey保存计算展开及详情阅读位置，首次真实布局后恢复。LensFocus仍来自原Session，未增加第二套证据状态。H5根宽度来自最大480的实际容器，Android宿主同步浅色系统栏。测试状态以两题TESTS及交付说明为准。

## 当前状态

2026-09-12更新：Task2证据问答已实现并通过[专项验收](REVIEWS/TASK2-TESTS.md)。`finance_home`仍是唯一Pager，内部Home/Chat/Detail路由；shared新增chat包。Markdown与EvidenceCard显式分离；ChatSession管理请求代次与原位重试，ChatController保存草稿、列表位置及每条消息展开状态。聊天卡消费Task1的EvidenceResolver/LensPresenter/MarketPlot，承接页复用FinanceDetail/QuoteEvidenceLens。结构与授权见[ADR-014](decisions/ADR-014-evidence-chat.md)、[接口](interfaces/EvidenceChat.md)。下文Task1基线说明中的“Task2待实现”仅描述当时边界。

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

2026-09-12持续优化：ChatController分别保存输入草稿和快捷问题，视图代次约束延迟滚动；最新回答按实际消息frame定位。MockChatProvider不为不完整比较补造股票。scripts中的工作区收据将构建输入、JS/APK、实际浏览器结果和录像绑定，包准备拒绝陈旧证据。未改变module、依赖和Task1业务接口。

2026-09-08 在既有包内深化：`insight/LensPresentation.kt` 集中提供面向 UI 的事实与导航投影；`ui/FinanceSession.kt` 封装请求代次、成功文档与选择的原子更新。旧 View 的回调不能引用旧文档修改新状态。H5 将当前快照与焦点写入同一历史条目和 URL，详情顶部显示实际数据场景。没有新增框架/业务依赖或 Gradle module；依据见总计划 D1–D4 与 ADR-013。

- `:KuiklyChart:jsNodeTest`（空 module 当前允许 SKIPPED；不构成图表能力完成证据）
- `:shared:compileKotlinJs`
- `:h5App:jsBrowserProductionWebpack`
- `:shared:testDebugUnitTest`
- `:androidApp:assembleDebug`
- H5 浏览器交互；Android 设备运行单独记录。
