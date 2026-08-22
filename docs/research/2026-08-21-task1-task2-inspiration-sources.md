# Shape with AI Task 1 / Task 2：资料地图与产品启发

> 调研日期：2026-08-21（Asia/Shanghai）
> 范围权威：`docs/REQUIREMENTS.md` 仍是唯一题面与完成定义；本文件只提供设计、实现、验证与展示启发，不增加 Task Must。
> 资料原则：优先 Kuikly/Kotlin 官方文档、上游源码仓库、标准组织、监管机构和数据提供方的一手资料。链接和版本可能变化，真正采用前仍需在任务分支做依赖、构建、许可与平台探针。
> 当前工程边界：活动工程固定 Kuikly UI `2.4.0`、Kotlin/KMP `2.0.21`，实际 target 为 Android/H5；旧 `archive/task1-v1/`、Issue #1477 和通用图表均不定义本轮完成度。

## 1. 先看结论

1. **两题的高分杠杆是完整、可信、可讲清楚的业务闭环，不是堆技术。** Task 1 先把“行情列表 → 正确详情 → 可理解且克制的 AI 解读”做稳；Task 2 再把“输入 → 会话状态 → Markdown + typed business block → 正确详情承接”做稳。
2. **跨端应共享领域、状态、格式化和 fixture，平台宿主只处理必要差异。** Kotlin 官方建议把可跨平台 API 放在最宽的 source set、统一合法/非法输入行为，并在所支持平台运行共同测试；这正好支持当前 commonMain-first 与 Android/H5 分级验收策略。
3. **结构化业务块优于从 Markdown 反解析行情。** A2UI 与 Adaptive Cards 的共同思想是：模型/服务发送声明式数据，客户端只从预先批准的组件目录中渲染。Task 2 不需要实现这些协议，但可借鉴为封闭的 `ChatBlock` sealed model。
4. **金融 AI 的可信表达比“大胆荐股”更有价值。** 监管机构明确提醒，AI 输出可能基于错误、过时或虚构信息。推荐把 AI 卡设计成“可观察事实 → 有条件的解释 → 风险/不确定性 → 数据时间与 Mock 标记”，避免无条件买卖指令、保证收益或伪造实时性。
5. **图形信息必须有文本等价物，涨跌不能只靠红绿。** W3C 与 Apple 都要求为图表提供上下文/文本描述并避免只用颜色区分。中国市场与英文市场对红绿涨跌含义还可能相反，所以应同时显示正负号、涨/跌文字或方向符号。
6. **`KuiklyMarkdown` 是高价值参考，但当前不能直接承诺可用。** 上游 README 声明 Android/iOS/HarmonyOS、GFM、可定制块和流式块级更新；没有声明 H5，标准制品示例为 `1.0.6-2.1.21`，与本项目 Kotlin `2.0.21` 不同。Task 2 必须先做 Android/JS/H5 独立探针，并保留受限 commonMain renderer 的降级方案。
7. **Mock-first 是正确的展示策略，不是“低配版”。** 确定性数据让评委稳定复现完整路径；真实 API 会引入密钥、许可、延迟/时区、空值、429、网络失败和数据再分发限制。真实 API 只适合作为核心闭环 VERIFIED 后的独立 Optional adapter。
8. **展示材料也是交付的一部分。** README 应让陌生人快速理解价值、运行方式和限制；Demo 视频要在一条短路径中明确表现交互、跨端与边界，并提供包含画面信息的描述性文字稿；构建、浏览器、APK、设备运行必须分别留证。

## 2. 资料速查表（55 条）

表中“成本/风险”指把该资料的启发真正纳入本仓库的成本或边界，不是对资料本身的评价。

### A. Kuikly、KMP 与官方示例

| # | 用途 | 适用 Task | 可直接借鉴的启发 | 采用成本 / 风险 | 官方链接 |
|---:|---|---|---|---|---|
| 1 | Kuikly 总体能力与平台状态 | T1/T2 | 上游声明一套 Kotlin 代码支持 Android、iOS、HarmonyOS、Web Beta、Mini Programs Beta、macOS Alpha，并同时提供传统响应式 DSL 与 Compose DSL；适合在答辩中解释框架价值。 | 上游“支持平台”不等于本仓库“已验证平台”；本项目只能声明实际配置并运行过的 Android/H5。 | [Tencent-TDS/KuiklyUI README](https://github.com/Tencent-TDS/KuiklyUI) |
| 2 | 环境和版本核对 | T1/T2 | 在任务开始前固定 JDK、IDE、Kotlin/Kuikly 版本并记录，减少“本机能跑”的偶然性。 | 上游文档会随版本变化；不要因此顺手升级当前冻结版本。 | [Kuikly 环境配置](https://github.com/Tencent-TDS/KuiklyUI/blob/main/docs/QuickStart/env-setup.md) |
| 3 | 最小页面和宿主调用链 | T1/T2 | 用最小 Hello World 理解 `@Page`、Pager、宿主加载与多端入口；学习报告可沿真实调用链解释。 | 只证明框架入口，不证明业务、浏览器或设备体验。 | [Kuikly Hello World](https://github.com/Tencent-TDS/KuiklyUI/blob/main/docs/QuickStart/hello-world.md) |
| 4 | 行情/消息滚动列表 | T1/T2 | Kuikly `List` 继承 `Scroller`，支持纵横方向、滚动、首屏加载上限、预加载距离和可见位置；适合行情列表和长会话。 | 列表存在不等于稳定高性能；要给稳定 key、控制 observable 范围并在真机/浏览器实滚。 | [Kuikly List API](https://github.com/Tencent-TDS/KuiklyUI/blob/main/docs/API/components/list.md) |
| 5 | 聊天输入与发送 | T2 | `Input` 提供 placeholder、文本变化、焦点、`returnKeyTypeSend` 和 IME 行为，可让软键盘发送与按钮发送走同一 submit 路径。 | Android/H5 的中文输入法、回车、键盘遮挡和焦点行为必须分别实测；不要只测点击按钮。 | [Kuikly Input API](https://github.com/Tencent-TDS/KuiklyUI/blob/main/docs/API/components/input.md) |
| 6 | 最小走势区域 / 自绘提示 | T2 Optional | Canvas 可用于非常聚焦的 sparkline 或走势区；坐标、range、空数据和语义应在 UI 外计算。 | 不是 Task 1 Must；Canvas 自绘会增加 H5/Android 一致性、命中、无障碍和边界测试成本，禁止先造通用 Chart 平台。 | [Kuikly Canvas API](https://github.com/Tencent-TDS/KuiklyUI/blob/main/docs/API/components/canvas.md) |
| 7 | 富文本与点击文本 | T2 | 先核对框架自带 RichText 能否覆盖受限 Markdown renderer 的 inline text、链接和样式需求。 | RichText 不是完整 Markdown parser；不要用正则拼接未受信 HTML。 | [Kuikly RichText API](https://github.com/Tencent-TDS/KuiklyUI/blob/main/docs/API/components/rich-text.md) |
| 8 | 列表/聊天到详情导航 | T1/T2 | 路由参数显式编码和校验；列表项与业务卡只携带稳定实体 id，详情 resolver 统一处理 unknown code。 | Android/H5 页面打开、返回栈和参数传递可能有宿主差异，必须点击验证至少两个实体和一次返回。 | [Kuikly 页面打开与关闭](https://github.com/Tencent-TDS/KuiklyUI/blob/main/docs/DevGuide/open-and-close-page.md) |
| 9 | 响应式状态更新 | T1/T2 | 把状态转移放入可测 state holder，页面只订阅/渲染；列表增量更新而不是整页重建。 | observable 容器的 item 原位修改是否触发刷新需用真实 API 与测试确认。 | [Kuikly 响应式更新](https://github.com/Tencent-TDS/KuiklyUI/blob/main/docs/DevGuide/reactive-update.md) |
| 10 | Kuikly 性能检查 | T1/T2 | 对列表滚动、重复格式化、过大响应范围和布局层级做有目标的性能检查。 | 不要在无数据前提下“性能优化”；先用真实关键路径和 release/production 构建观察。 | [Kuikly 性能指南](https://github.com/Tencent-TDS/KuiklyUI/blob/main/docs/DevGuide/kuikly-perf-guidelines.md) |
| 11 | H5 实际运行 | T1/T2 | 明确 H5 是单独宿主与构建产物，README 给出真实启动命令、入口 URL 和已知限制。 | JS 编译或 bundle 成功不能替代浏览器可视/点击证据。 | [Kuikly H5 开发](https://github.com/Tencent-TDS/KuiklyUI/blob/main/docs/DevGuide/h5-dev.md) |
| 12 | H5 多页/SPA 导航启发 | T1/T2 | 详情路由、刷新/直达和返回行为应作为明确演示路径，而不是只在内存状态中偶然成立。 | 是否采用 SPA 方案取决于当前宿主；结构变化需任务卡/ADR。 | [Kuikly H5 SPA Demo 指南](https://github.com/Tencent-TDS/KuiklyUI/blob/main/docs/DevGuide/h5-spa-demo.md) |
| 13 | 可运行官方示例索引 | T1/T2 | 优先从上游 `demo/src` 找 List、Input、Canvas、Pager 的真实用法，再适配本项目版本。 | `main` 示例可能领先于本仓库 2.4.0；复制 API 前必须按固定依赖编译。 | [Kuikly 官方 Demo 源码](https://github.com/Tencent-TDS/KuiklyUI/tree/main/demo/src) |
| 14 | KMP source set 边界 | T1/T2 | `commonMain` 放共享领域/状态/格式化，平台 source set 只放实际平台能力；不能在 common 误用 JVM-only API。 | 平台特例过多会破坏“一处测试、两端一致”的价值。 | [Kotlin Multiplatform 项目结构](https://kotlinlang.org/docs/multiplatform/multiplatform-discover-project.html) |
| 15 | 跨端 API 与测试准则 | T1/T2 | Kotlin 官方建议 API 放在最广适用 source set、各平台对相同合法/非法输入保持一致，并在所有支持平台运行 common tests。 | “编译所有 target”与“运行所有平台测试”成本不同，应按本项目 Android/H5 合同诚实分级。 | [Kotlin Multiplatform API 设计指南](https://kotlinlang.org/docs/api-guidelines-build-for-multiplatform.html) |

### A.2 Kuikly 平台、路由与交付边界补充

| # | 用途 | 适用 Task | 可直接借鉴的启发 | 采用成本 / 风险 | 官方链接 |
|---:|---|---|---|---|---|
| K1 | KMP 共用工程接入 | T1/T2 | 官方把 shared 业务页与各平台宿主分开；H5 需要 shared JS、h5App 产物和浏览器访问三个环节。 | “shared 编译”“H5 bundle”“浏览器交互”是三种证据，不能合并成一句“跨端通过”。 | [Kuikly KMP 工程接入](https://github.com/Tencent-TDS/KuiklyUI/blob/main/docs/QuickStart/common.md) |
| K2 | Android 宿主接入 | T1/T2 | 用一手接入文档核对 Android host/renderer 生命周期和启动入口。 | APK 构建不等于设备 attach 页面成功；设备运行单列。 | [Kuikly Android 接入](https://github.com/Tencent-TDS/KuiklyUI/blob/main/docs/QuickStart/android.md) |
| K3 | H5 宿主接入 | T1/T2 | 用官方 H5 target/host 配置核对本项目 bundle 与浏览器入口。 | Kuikly 上游把 Web 标成 Beta；浏览器、DPR、输入法与 Canvas 行为都需实测。 | [Kuikly H5 接入](https://github.com/Tencent-TDS/KuiklyUI/blob/main/docs/QuickStart/h5.md) |
| K4 | 点击与基础事件 | T1/T2 | 行情行和业务卡用框架基础 click/press 事件；交互热区、反馈和语义一起设计。 | `main` 文档中的较新事件/API 未必存在于 2.4.0，必须查 tag/source 或编译探针。 | [Kuikly 基础属性与事件](https://github.com/Tencent-TDS/KuiklyUI/blob/main/docs/API/components/basic-attr-event.md) |
| K5 | 长详情/Markdown 外层滚动 | T1/T2 | `Scroller` 适合不定高详情或 Markdown 容器，支持方向、offset 和滚动事件。 | 嵌套滚动和平台差异可能随版本变化；避免 `List` 套 `Scroller` 后只凭编译判断体验。 | [Kuikly Scroller API](https://github.com/Tencent-TDS/KuiklyUI/blob/main/docs/API/components/scroller.md) |
| K6 | 详情参数生命周期 | T1/T2 | 从 `PagerData.params` 读取稳定 entity code，并在页面生命周期允许的时点 resolve；unknown code 进入显式恢复状态。 | 过早读取会触发生命周期错误；禁止全局默认实体掩盖路由缺陷。 | [Kuikly Page Data](https://github.com/Tencent-TDS/KuiklyUI/blob/main/docs/DevGuide/page-data.md) |
| K7 | 条件与列表指令 | T1/T2 | `vif` 表达状态分支，`vfor`/`vforLazy` 表达行情/消息/blocks；模型保持 observable 与稳定 id。 | 指令和 observable 的准确组合按 2.4.0 实际 API 验证，不能照抄最新 `main`。 | [Kuikly 指令系统](https://github.com/Tencent-TDS/KuiklyUI/blob/main/docs/DevGuide/directive.md) |
| K8 | 上游版本漂移审计 | T1/T2 | Releases 用于核对功能/修复落在哪个版本；截至本次调研页面已显示 2.19.1，含 Canvas batching 与 Web Canvas fix。 | 当前工程固定 2.4.0，与 `main`/最新 release 差距显著；任何新 API 都要过 SDK tag gate，禁止顺手升级。 | [KuiklyUI Releases](https://github.com/Tencent-TDS/KuiklyUI/releases) |
| K9 | 上游贡献规范 | T1/T2 | 上游要求 feature/bugfix 分支、Angular commit、提交前测试/校验，API 改动同步文档；与本仓库聚焦 commit 和四门一致。 | 上游规范不是 Shape 评分细则；本仓库更严格的题面/任务卡/证据规则优先。 | [KuiklyUI CONTRIBUTING](https://github.com/Tencent-TDS/KuiklyUI/blob/main/CONTRIBUTING.md) |
| K10 | AI 辅助开发的官方检索纪律 | T1/T2 | Tencent-TDS 官方 AI skill 把 List、Input、Canvas、Router、响应式、指令和 demo 路径作为索引，并明确 API 必须回到文档/源码核对。 | skill 是导航，不是版本证据；最后仍要对 2.4.0 source/artifact 编译与运行。 | [KuiklyUI-AI framework skill](https://github.com/Tencent-TDS/KuiklyUI-AI/blob/main/skills/kuikly-ui-framework/SKILL.md) |

### B. KuiklyMarkdown、Markdown 与安全边界

| # | 用途 | 适用 Task | 可直接借鉴的启发 | 采用成本 / 风险 | 官方链接 |
|---:|---|---|---|---|---|
| 16 | KuiklyMarkdown 真实能力审计 | T2 | README 声明标题、强调、代码、表格、列表、引用、图片、链接，默认 GFM；传统 DSL 可替换 21 类 block；另有面向 AI 的块级增量渲染。 | **关键边界：**只声明 Android/iOS/HarmonyOS，没有 H5；标准 artifact 示例 `1.0.6-2.1.21` 与本项目 Kotlin 2.0.21 不匹配；Compose 自定义 renderer 参数不执行且缺传统 DSL 的精确点击坐标/长按代理。必须先做 Android/JS/H5 探针。 | [KuiklyMarkdown README](https://github.com/Kuikly-contrib/KuiklyMarkdown) |
| 17 | Markdown 基线语义 | T2 | 以 CommonMark 示例集冻结首版支持子集：段落、标题、强调、列表、引用、代码、链接；未知语法安全降级为纯文本。 | CommonMark 允许 raw HTML；未受信模型输出不能直接按完整规范执行 HTML。 | [CommonMark 0.31.2 规范](https://spec.commonmark.org/0.31.2/) |
| 18 | GFM 扩展能力 | T2 | 如果要展示表格、删除线、autolink、任务列表，应逐项建立 fixture 和跨端 golden/snapshot，而不是笼统写“支持 Markdown”。 | 不同 renderer 对 GFM 子集支持不同；KuiklyMarkdown/降级 renderer 的合同要分别列出。 | [GitHub Flavored Markdown 规范](https://github.github.com/gfm/) |
| 19 | Markdown/XSS 安全 | T2 | 输出编码、URL allowlist、拒绝 raw HTML/script、限制远程图片；业务 block 不从 HTML/Markdown 反解析。 | H5 中 `innerHTML`、`javascript:`/`data:` URL、未修补 sanitizer 都是风险；CSP 不能替代正确编码/清洗。 | [OWASP XSS Prevention Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Cross_Site_Scripting_Prevention_Cheat_Sheet.html) |

### C. 页面状态、结构化 UI 与通用组件设计

| # | 用途 | 适用 Task | 可直接借鉴的启发 | 采用成本 / 风险 | 官方链接 |
|---:|---|---|---|---|---|
| 20 | 页面状态与 UDF | T1/T2 | UI 是 state 的呈现；事件向上、state 向下。`Ready/Empty/Failed`、`Sending/Complete/Failed` 应是显式状态而非零散布尔值。 | 不需要把 Android ViewModel 搬进 KMP；借鉴状态职责即可，用 commonMain 小型 state holder。 | [Android UI layer](https://developer.android.com/topic/architecture/ui-layer) |
| 21 | 事件与状态区分 | T1/T2 | “状态一直存在，事件发生后改变状态”；发送、重试、点击实体都是 event，渲染只消费可恢复 state。 | 一次性 event 若与持久 state 混用，会产生重复导航/重复消息。 | [Android UI state production](https://developer.android.com/topic/architecture/ui-layer/state-production) |
| 22 | 长列表性能与稳定 key | T1/T2 | 大量或未知数量内容用 lazy/虚拟化列表；为 item 提供稳定 id 和 content type，避免顺序变化导致错误复用。 | Compose API 不能直接套到 Kuikly DSL，但“稳定 key、按类型复用、release 测量”的原则通用。 | [Android Lazy lists](https://developer.android.com/develop/ui/compose/lists) |
| 23 | 可测的性能原则 | T1/T2 | 计算/排序/格式化移出 item 渲染，稳定 key，减少无关重绘；用关键旅程测滚动而非凭感觉优化。 | Debug 滚动感受不能当 release 性能结论。 | [Compose performance best practices](https://developer.android.com/develop/ui/compose/performance/bestpractices) |
| 24 | Android/H5 响应式布局 | T1/T2 | 为窄屏、宽屏、横屏定义 reflow/最大内容宽度；输入和按钮不盲目拉伸；详情可从单列演进为双 pane。 | 当前 Must 不要求平板专用布局；先保证现有 viewport 不遮挡、不溢出。 | [Android Adapt layouts](https://developer.android.com/design/ui/mobile/guides/layout-and-content/adapt-layout) |
| 25 | 组件语义与测试钩子 | T1/T2 | 为列表行、涨跌、AI 状态、发送、重试和业务卡提供可读语义；同一语义也能作为自动化定位依据。 | 自绘 Canvas/自定义卡不会自动获得足够语义，需要额外设计与设备读屏检查。 | [Android Accessibility Semantics](https://developer.android.com/develop/ui/compose/accessibility/semantics) |
| 26 | 声明式生成 UI 的安全模型 | T2 Inspiration | A2UI 使用声明式 JSON 和客户端预批准组件 catalog，强调“数据而非可执行代码”；启发 `ChatBlock` 白名单 renderer。 | A2UI 仍处 public preview/快速演进，且没有 Kuikly renderer；本项目只借鉴边界，不引入协议。 | [Google A2UI](https://github.com/google/A2UI) |
| 27 | 跨宿主卡片与 fallback | T2 Inspiration | Adaptive Cards 把 JSON 转成宿主原生 UI，并内置 schema version、fallback text、动作、label/无障碍概念；启发业务卡 version/fallback。 | 直接集成不符合当前技术栈且超范围；只借鉴 typed card + fallback 设计。 | [Adaptive Cards Schema Explorer](https://adaptivecards.io/explorer/) |

### D. 金融信息可视化、数字与无障碍

| # | 用途 | 适用 Task | 可直接借鉴的启发 | 采用成本 / 风险 | 官方链接 |
|---:|---|---|---|---|---|
| 28 | 通用可访问设计 | T1/T2 | 颜色之外提供文字/符号；输入有可见 label；交互状态有明确反馈；不同 viewport 仍可读。 | 不能只用自动颜色对比工具代替真实键盘/读屏/缩放检查。 | [W3C Designing for Web Accessibility](https://www.w3.org/WAI/tips/designing/) |
| 29 | 图表/图形文本等价 | T1/T2 | 为走势区提供简短摘要、关键数值和必要上下文；即使 Canvas 不可读，用户仍能理解趋势与时间范围。 | “上涨很快”是主观解释；无障碍描述优先实际值、日期、区间和来源。 | [W3C Accessibility Principles](https://www.w3.org/WAI/fundamentals/accessibility-principles/) |
| 30 | 图形与文本对比度 | T1/T2 | 正文目标至少 4.5:1；关键非文本图形边界目标 3:1；图后提供数据/摘要可减少对图形的唯一依赖。 | 视觉稿看起来清晰不等于实际配色过线，深浅色需分别测。 | [WCAG Text Contrast](https://www.w3.org/WAI/WCAG21/Understanding/contrast-minimum) / [WCAG Non-text Contrast](https://www.w3.org/WAI/WCAG22/Understanding/non-text-contrast) |
| 31 | 金融走势表达 | T1/T2 Optional | 图表聚焦少数关键信息，数据最突出，标题/单位/轴提供上下文；紧凑场景缩短标签；描述日期、数值与范围。 | 图表不是装饰；没有清晰问题就不要增加图形复杂度。 | [Apple HIG Charts](https://developer.apple.com/design/human-interface-guidelines/charts) |
| 32 | 涨跌颜色的地区语义 | T1/T2 | Apple 官方示例明确：英文股票场景绿色可表示上涨，而中文股票场景红色可表示上涨。应把涨跌配色视作 locale/market policy，并同时使用 `+/-` 与文字。 | 硬编码“绿涨红跌”会在中文金融场景制造认知错误；仅换色仍不满足色觉无障碍。 | [Apple HIG Color](https://developer.apple.com/design/human-interface-guidelines/color) |
| 33 | 数值、百分比和成交量格式 | T1/T2 | 用一致的 decimal、grouping、percent、plus/minus 和 compact number 规则；保留原始精度与显示精度边界。 | KMP 各端 locale API 不完全一致；首版可用经过测试的 common formatter，不用 `Double.toString()` 直接展示。 | [Unicode LDML Numbers](https://www.unicode.org/reports/tr35/tr35-numbers.html) |

### E. Mock-first、真实行情 API 与投资风险

| # | 用途 | 适用 Task | 可直接借鉴的启发 | 采用成本 / 风险 | 官方链接 |
|---:|---|---|---|---|---|
| 34 | 真实 API 候选 A | Optional | Alpha Vantage 提供 time series/quote 等官方接口；可作为后置 adapter 研究对象。 | 免费额度有限，实时/延迟行情权限与展示许可需核对；密钥不能进入客户端或 Git。 | [Alpha Vantage Documentation](https://www.alphavantage.co/documentation/) / [Support & limits](https://www.alphavantage.co/support/) |
| 35 | 真实 API 候选 B | Optional | Finnhub quote 返回 current/change/percent/high/low/open/previous close，字段与 Task 1 很接近；429 和 token 机制清楚。 | 国际市场实时能力与 candle 可能受套餐限制；官方明确不建议常量轮询，真实接入需缓存/退避/WebSocket 决策。 | [Finnhub API Documentation](https://finnhub.io/docs/api) |
| 36 | 真实 API 候选 C | Optional | Twelve Data 的 quote/time_series 包含 OHLCV、时区、exchange、market-open、null/error/429 处理建议，适合验证 provider contract 是否足够。 | API key、credits、计划权限、空值、限流与缓存都是新增完成条件；不能让网络决定 Demo 成败。 | [Twelve Data API Documentation](https://twelvedata.com/docs) |
| 37 | 市场时段与时间戳 | Optional | Polygon 股票文档强调盘前/常规/盘后和 UTC timestamp；启发详情明确“数据时间、市场状态、时区”。 | 产品/套餐、地区覆盖和授权复杂；不能用美国市场规则解释所有股票。 | [Polygon Stocks Overview](https://polygon.io/docs/rest/stocks/overview) |
| 38 | 行情许可与再分发 | Optional | 数据提供方条款提醒市场数据可能仅限个人/非商业用途，且不保证准确、及时、完整，也不构成投资建议。 | 活动公开 Demo 是否属于可再分发/商业展示必须逐服务确认；这正是默认离线 fixture 更稳的原因。 | [Polygon Market Data Terms](https://polygon.io/terms/market_data_terms.pdf) |
| 39 | AI 金融信息风险 | T1/T2 | AI 可能基于不准确、不完整、误导或过时资料，甚至生成虚构信息；产品应展示来源/时间/不确定性并提醒复核。 | 一句“非投资建议”不足以抵消强荐股、保证收益或伪造事实；内容策略要从源头克制。 | [FINRA/SEC/NASAA: AI and Investment Fraud](https://www.finra.org/investors/insights/artificial-intelligence-and-investment-fraud) |
| 40 | 风险/收益基础表达 | T1/T2 | 所有投资都有风险，风险与潜在收益相关；AI 卡更适合“风险因素/条件”而非确定结论。 | 不把通用教育内容包装成个性化投资建议；Mock 仍明确标记。 | [Investor.gov: Risk](https://www.investor.gov/introduction-investing/investing-basics/glossary/risk) |

### F. README、Demo 视频与可运行交付

| # | 用途 | 适用 Task | 可直接借鉴的启发 | 采用成本 / 风险 | 官方链接 |
|---:|---|---|---|---|---|
| 41 | README 信息架构 | T1/T2 | README 首屏回答“为什么有用、能做什么、如何运行”，再给架构、Mock/免责声明、验证矩阵、已知限制和 Demo 链接。 | 不把长过程日志塞进 README；数字与平台状态必须指向实际 evidence。 | [GitHub: About READMEs](https://docs.github.com/en/repositories/managing-your-repositorys-settings-and-features/customizing-your-repository/about-readmes) |
| 42 | Gradle 可复现 CI | T1/T2 | CI 使用 Gradle Wrapper 和与本地相同的构建/测试命令；上传日志/产物并展示状态。 | CI 通过仍不等于浏览器可视或设备运行；第三方 Action 应固定 commit SHA。 | [GitHub Actions: Java with Gradle](https://docs.github.com/en/actions/tutorials/build-and-test-code/java-with-gradle) |
| 43 | 可下载交付物 | T1/T2 Optional | 最终可用 draft release 组织 release notes、APK/演示材料和版本化资产。 | 发布、tag、release 是外部状态变化，只能由用户决定；未签名/未设备验证的 APK 要明确标注。 | [GitHub: Managing releases](https://docs.github.com/en/repositories/releasing-projects-on-github/managing-releases-in-a-repository) |
| 44 | Demo 视频 + 描述性文字稿 | T1/T2 | 视频应同时让声音和画面传达关键信息；配字幕，并提供含 UI 状态、点击、结果和画面文字的 descriptive transcript。 | 只有 ASR 文字稿会漏掉画面证据；视频需避免泄露本机路径、账号、Token、通知和真实用户数据。 | [W3C Making Audio and Video Accessible](https://www.w3.org/WAI/media/av/) |
| 45 | “构建”与“运行”分级 | T1/T2 | 发布前配置、构建、测试 release；Android 官方也建议在目标手机/平板上实际测试。项目应分开记录 shared tests、H5 bundle、浏览器、APK 和设备。 | Debug APK 成功或 HTTP 200 不能冒充设备/视觉完成；未执行平台保留 UNVERIFIED。 | [Android: Prepare for release](https://developer.android.com/studio/publish/preparing) / [Publish your app](https://developer.android.com/studio/publish) |

## 3. Task 1：可执行启发

### 3.1 Must：先形成无争议闭环

以下仍以 `docs/REQUIREMENTS.md` 为准，资料不能扩写它：

1. 首页有真实可滚动行情列表；每行包含名称、代码、最新价、涨跌额、涨跌幅。
2. 点击至少两只不同股票，进入对应详情而非默认第一只。
3. 详情包含名称、代码、最新价、涨跌幅、最高价、最低价、成交量。
4. 详情存在清楚可见的 AI 分析/解读区域。
5. 默认确定性 Mock；显式 Mock 标签和“仅作技术演示，不构成投资建议”。
6. Android/H5 按实际构建、浏览器、APK、设备分级留证；四门齐全才 VERIFIED。

### 3.2 评分高杠杆：少做但做得像完整产品

#### A. 行情行做到“3 秒扫懂”

- 第一视觉层：名称 + 最新价；第二层：代码 + 涨跌额/幅。
- 数字右对齐并固定精度，涨跌同时显示 `+/-`、百分比和颜色；零涨跌有独立 neutral 状态。
- 整行都是点击目标，同时有明确按压反馈；不能只有小箭头可点。
- 准备 12～20 条 fixture，确保真的可滚动；包含上涨、下跌、平盘、长名称、大成交量和小数边界。

#### B. 详情建立“同一实体、同一时间”的可信感

- 详情顶部再次展示名称/代码，列表和详情从同一 `MarketSnapshot` resolve，禁止未知 code 回退第一只。
- 增加低成本但高价值的上下文：`Mock 数据`、`截至 HH:mm`、`币种/市场`、`交易状态（如 fixture 需要）`。
- 高/低/成交量按两列信息组展示；采用统一 formatter，避免页面各自拼接字符串。
- 返回后保留列表滚动位置是高体验加分，但不应阻塞核心字段与正确路由。

#### C. AI 分析卡避免“AI 味很浓但不可信”

推荐四段式，而不是一大段漂亮废话：

1. **观察：**只复述 fixture 可验证事实，如“最新价接近日内高位，日内区间为 X–Y”。
2. **解释：**使用条件句，如“若价格持续位于……，短线强势可能延续”。
3. **风险：**指出样本窗口、成交量、突发消息或 Mock 数据局限。
4. **边界：**`Mock AI · 仅供技术演示 · 不构成投资建议`。

可用标签：`趋势偏强`、`波动中等`、`关注量能`、`数据不足`。标签来自确定性规则并可单测；不要出现“稳赚”“强烈买入”“明日必涨”。

#### D. 把工程质量变成可见体验

- loading 用 skeleton/明确状态；empty 说明“暂无行情”；error 有“重试”；unknown code 有返回入口。
- 颜色之外有文字/符号；重要卡片和走势区有文本摘要。
- Android/H5 使用相同 fixture 跑同一脚本化 Demo，截图对齐同一股票与同一状态。

### 3.3 Optional：仅在核心 VERIFIED 后选一项

- 详情内 20～30 点 **sparkline**，附“近 N 日/分钟”文本摘要；不抽象通用 Chart API。
- 排序/筛选（涨幅、代码）或简短搜索；只选一项且要保持滚动/详情闭环。
- AI 卡“为什么这样判断”展开区，列出使用字段，增强可解释性。
- 关注列表的纯本地 UI 状态（不承诺账户同步）。
- 真实 API adapter：保留 fixture fallback，服务端/本地安全注入密钥，缓存、429、超时、时间戳、许可和错误证据齐全。

### 3.4 避免事项

- 为了“创新”先造 K 线、手势、通用 Chart DSL，导致核心路由/AI/证据未完成。
- 首页和详情各自维护一份股票数据，出现价格、代码或时间不一致。
- 用 `Double` 随意拼字符串、遗漏正号、把百分数 `0.023` 显示成 `0.023%`。
- 只有红绿，无正负号/文字；或不考虑中文市场红涨绿跌的约定。
- AI 内容看似来自实时新闻，但没有来源、时间或真实联网能力。
- 用 README、截图或旧 archive 代替当前分支运行证据。

## 4. Task 2：可执行启发

### 4.1 Must：冻结一个最小但完整的会话合同

1. 用户可输入、发送，消息按时间顺序展示。
2. 至少一条助手消息包含可验证 Markdown。
3. 同一条或后一条消息包含至少一种非 Markdown 业务内容；首选可点击 `MarketSummaryCard`。
4. 卡片携带 typed `MarketEntityRef`，点击进入正确详情。
5. 详情展示基础行情、走势区域，并包含摘要或 AI 解读至少一种。
6. Mock、失败、空输入、重复发送、unknown entity、返回后会话保持等按任务卡冻结并测试。

### 4.2 评分高杠杆：让“AI 对话”真的比普通文本有价值

#### A. 消息模型用 block，而不是字符串协议

推荐最小语义：

```kotlin
sealed interface ChatBlock {
    data class Markdown(val text: String) : ChatBlock
    data class MarketSummaryCard(
        val entity: MarketEntityRef,
        val quote: MarketQuote,
        val summary: String,
        val asOf: String,
    ) : ChatBlock
    data class Notice(val text: String) : ChatBlock
}
```

- Provider 返回 `List<ChatBlock>`；renderer 按白名单分派。
- 未知 block 显示安全 fallback/Notice，不执行代码，也不从 Markdown 解析股票 JSON。
- 这借鉴 A2UI/Adaptive Cards 的“声明式数据 + 可信组件目录”，但不引入它们的协议成本。

#### B. 会话状态要让用户知道系统正在做什么

- submit 后立即 append USER，再 append/replace 固定 id 的 ASSISTANT `SENDING → COMPLETE/FAILED`。
- 首版采用 single in-flight：发送中禁用或合并重复请求，规则清楚可测。
- retry 原位恢复同一 assistant message，避免重复整段历史。
- 长会话用稳定 message id；返回详情后保留输入草稿、消息和滚动位置。

#### C. 首版业务卡应同时完成三件事

一张 `MarketSummaryCard` 即可同时证明：

1. AI 不只返回 Markdown；
2. 结构化价格/涨跌不是从文本猜出来；
3. 点击正确承接到详情页。

建议卡片字段：名称/代码、最新价、涨跌额/幅、`截至`、一句条件化摘要、`查看详情`。不要首版同时做 QuoteCard、IndexCard、TrendChart、新闻卡和工具调用。

#### D. Markdown 兼容是“明确子集”，不是一句口号

建议验收 fixture：

- 标题 + 粗体/斜体；
- 有序/无序列表；
- 引用；
- inline code / fenced code（若选择支持）；
- 链接（受限协议与宿主确认）；
- GFM 表格（仅在选定 renderer 真实支持时）。

安全 fixture 还应包含 raw HTML、`javascript:` URL、远程图片、未闭合 fence 和超长文本，验证纯文本/拒绝/安全 fallback。

#### E. `KuiklyMarkdown` 的建议决策树

```text
独立分支依赖探针
  ├─ Android compile/test 通过？
  ├─ shared Kotlin/JS compile 通过？
  ├─ H5 production bundle + browser 通过？
  ├─ 选定 Markdown fixtures 两端一致？
  └─ 安全 URL/raw HTML 策略可控制？
       ├─ 全部是 → 可采用，并固定 artifact/version
       └─ 任一否 → commonMain 受限 renderer；不阻塞 Task 2
```

上游的流式块级更新值得学习，但它仍会在每次文本变化时全量解析 AST，只在 UI 层复用已完成 blocks。首版确定性 Mock 没有必要为了“像真 AI”立即引入 token streaming。

### 4.3 Optional：核心闭环后再选

- 推荐问题 chips：例如“概览 A 股示例”“比较两只 Mock 股票”“解释今日波动”；用于稳定演示，不等于真实检索。
- `CompareCard`：两只股票同口径并排，仍用 typed data；成本低于通用图表。
- 详情 sparkline + 时间范围切换（1D/5D），每个范围有文本摘要和空数据状态。
- 流式输出：先冻结取消、离页、retry 和 partial Markdown 行为，再使用 KuiklyMarkdown streaming 或自研增量策略。
- answer provenance：显示“使用了哪些 Mock fixture / 截至何时”，或提供“数据依据”展开区。
- 真实模型 adapter：结构化输出 schema、超时/取消、成本/配额、内容安全、日志脱敏和离线 fallback 独立建卡。

### 4.4 避免事项

- 把 `{"type":"quote"}` 藏进 Markdown 再用字符串/正则反解析。
- 直接渲染模型生成 HTML、任意 URL、iframe、script 或远程图片。
- 让模型生成可执行 Kuikly/Kotlin 组件代码；只允许预定义 block catalog。
- 为了流式效果牺牲确定性、retry、离页取消和长会话正确性。
- 把 KuiklyMarkdown README 的 Android/iOS/HarmonyOS 支持写成 H5 已支持。
- 聊天卡跳详情后丢失历史，或 unknown entity 静默打开默认股票。

## 5. 两题联动的组件与数据 seam

推荐复用的是“业务事实与可靠接口”，不是为了复用而建立庞大平台：

```text
FixtureMarketProvider
  → MarketSnapshot / MarketEntityRef / Quote / TrendPoints
      ├─ Task 1 Home state → MarketRow
      ├─ Shared Detail resolver → Detail state → AI Analysis
      └─ Task 2 MockChatProvider → Markdown + MarketSummaryCard
                                      └─ typed detail destination
```

高价值通用组件（出现真实第二 caller 时再提炼）：

- `PriceChangeText`：正负号、百分比、颜色/文字策略、accessibility text。
- `MarketIdentityHeader`：名称、代码、market/Mock/as-of。
- `MarketFactGrid`：高/低/量等 label/value 对。
- `MarketSummaryCard`：Task 2 结构化业务块，不强行替代 Task 1 row。
- `StatePanel`：empty/error/retry，但文案和 action 由调用者提供。
- `Disclaimer`：统一边界文字，不能用它掩盖强荐股。

不要过早通用化：`UniversalCard`、任意 JSON UI renderer、通用 Chart DSL、全局 Conversation Engine、所有平台导航抽象。

## 6. 将研究结果纳入现有管线的建议

本节是对现有 `PLAN GATE → READY → IN_PROGRESS → HANDOFF → REVIEW → VERIFIED` 和 `code/tests/evidence/learning` 四门的补强，不改变 Owner 与 Task 顺序。

### 6.1 PLAN GATE / READY 增加 6 个可检查项

1. **Source mapping + SDK tag gate：**每个关键 Kuikly API/第三方依赖记录 `doc URL + 当前工程版本/tag/source path + compile probe`；不能用领先于 Kuikly 2.4.0 的 `main` 文档直接授权 API。
2. **体验主线：**用一句话和 30～60 秒 demo script 描述该切片要证明什么。
3. **状态表：**每个页面列出 Ready/Loading/Empty/Failed/Unknown/Sending 等状态及可恢复动作。
4. **数据真实性：**字段、单位、时间、币种、Mock/真实、来源与免责声明如何显示。
5. **可访问性：**颜色以外的语义、文本对比、点击目标、图形文本摘要、输入 label。
6. **依赖探针：**版本/platform/许可不确定的依赖先进入独立 probe，不进入业务切片的“已完成”。

### 6.2 tests 门增加代表性 fixture 合同

- T1：up/down/flat、长名称、大成交量、unknown code、empty、provider unavailable、AI rule 边界。
- T2：空白/重复发送、成功/失败/retry、Markdown 子集、安全恶意输入、unknown block/entity、详情返回、长会话。
- Formatter：正负零、百分比换算、舍入、分组、compact volume、非有限/缺失数据。
- Cross-platform：同 fixture 在 Android/H5 产生相同业务文本与路由结果；视觉差异单独记录。

### 6.3 evidence 门使用“声明—证据”矩阵

| 声明 | 最低证据 |
|---|---|
| common 逻辑正确 | 实际单测命令 + 结果 |
| Kotlin/JS 可编译 | `:shared:compileKotlinJs` 日志 |
| H5 可交付 | production webpack/bundle 日志 |
| H5 可交互 | 浏览器截图/录屏 + 滚动/点击/返回脚本结果 |
| Android 可构建 | Android JVM test + Debug APK 路径/hash（如需要） |
| Android 可运行 | 真机/模拟器型号、系统、截图/录屏、指定交互 |
| Markdown 可用 | artifact/version + Android/JS/H5 probe + fixture 结果 |
| AI/行情真实 | 数据来源、时间、许可、provider 日志；否则显式 Mock |

### 6.4 learning 门从“用了什么 API”升级为“为什么这样取舍”

每份 learning 至少回答：

1. 用户动作如何变成 state，再如何渲染；
2. 哪些逻辑在 commonMain，哪些留在宿主，为什么；
3. 选定的 Kuikly API 及其平台/版本证据；
4. Mock、AI、Markdown、行情数据的安全和真实性边界；
5. 放弃了哪些更炫的 Optional，换来了什么确定性；
6. 下一步只有一个高杠杆切片，而不是“继续完善”。

## 7. 建议的最终 Demo 与 README 叙事

### 7.1 Task 1 60～90 秒 Demo

1. 5 秒：标题、Kuikly Android/H5、Mock/免责声明。
2. 15 秒：列表字段与实际滚动，指出涨跌不只靠颜色。
3. 20 秒：点击股票 A，核对详情字段与实体一致。
4. 15 秒：AI 卡按“观察—解释—风险—边界”阅读。
5. 10 秒：返回并打开股票 B，证明不是硬编码单实体。
6. 10 秒：展示 empty/error/retry 中一个最有价值的状态。
7. 10 秒：并列展示 Android/H5 与测试/证据摘要。

### 7.2 Task 2 90～120 秒 Demo

1. 输入一个预设问题并发送，展示 sending 状态。
2. 助手返回一段 Markdown 和一张 `MarketSummaryCard`。
3. 指出卡片为 typed business block，不是 Markdown 截图/HTML。
4. 点击卡片进入正确详情，展示基础行情、走势区域与摘要/AI。
5. 返回后会话仍在；展示一次失败重试或 unknown query fallback。
6. 结尾说明 Markdown renderer、Mock、Android/H5 与未验证项。

### 7.3 README 推荐目录

1. 项目一句话与 1 张主图/GIF；
2. Task 1 / Task 2 已实现能力（FACT/MOCK/VERIFIED 标签）；
3. 30 秒体验路径；
4. 架构图与两个核心调用链；
5. 环境、JDK/Kuikly/Kotlin 固定版本；
6. 一键 verify、H5 启动、Android 构建命令；
7. 证据矩阵（构建/浏览器/APK/设备分开）；
8. Mock 数据、AI/投资免责声明、数据来源；
9. 已知限制与未验证平台；
10. Demo 视频 + **包含画面描述**的文字稿；
11. License / 上游致谢。

## 8. 最终优先级清单

### Must（现在就进入主计划）

- Task 1 题面字段、列表滚动、正确详情、AI 可见、Mock/免责声明。
- Task 2 输入/发送/会话、明确 Markdown 子集、一个 typed business block、正确详情承接。
- commonMain domain/state/formatter、unknown/error/retry、Android/H5 分级证据、四门。
- 颜色以外的涨跌语义、图形文本摘要、数据时间/Mock 标记。

### 评分高杠杆（优先于更多功能）

- Task 1 的四段式 AI 卡、两只实体正确性、同一 snapshot、稳定数字格式。
- Task 2 的 `MarketSummaryCard` 一卡三证、状态机、返回保持、Markdown 安全子集。
- README 首屏、短 Demo script、descriptive transcript、声明—证据矩阵。
- 依赖/平台 probe 结果和诚实的 UNVERIFIED 边界。

### Optional（核心 VERIFIED 后只选 1～2 项）

- 最小 sparkline + 文本摘要；排序/筛选；解释依据展开。
- CompareCard；推荐问题 chips；流式块级更新；真实 API/LLM adapter。
- Release 资产、额外平台、通用图表或高级手势均需独立任务卡。

### 避免事项（明确不做）

- 用 Issue #1477、旧 archive 或上游全平台声明扩张题面。
- 在核心闭环前升级框架、重构工程、造通用 Chart/生成 UI 协议。
- raw HTML/任意代码执行、客户端密钥、未核许可的行情再分发。
- 无来源/时间的“实时 AI 荐股”、保证收益、只用红绿表达涨跌。
- 把编译、HTTP 200、截图、浏览器、APK、设备运行相互冒充。

## 9. 研究边界与下一步

- 本文件没有验证任一新依赖在当前工程中的可编译性，也没有修改业务代码。
- 上游 `main` 文档可能领先于项目固定的 Kuikly 2.4.0；实现者应优先核对对应 artifact/source version。
- 真实行情 API 的价格、额度、许可和覆盖会变化；只有创建真实服务任务卡时才做当期复核。
- `KuiklyMarkdown` 的公开能力已记录，但 H5 和 Kotlin 2.0.21 兼容性仍为 **UNVERIFIED**。

建议下一步：把本文件中的 Must、评分高杠杆、依赖探针与证据矩阵映射到 `docs/plans/TASK1-PLAN.md`；用户确认核心组件、AI 载体和删减线后，由 OpenCode 按计划进入 T1-CODE，不默认引入图表、真实 API 或 Task 2。T1-LEARNING 达到 `VERIFIED` 后，再在 `docs/plans/TASK2-PLAN.md` 中选择 Markdown 探针与 typed business block，并冻结进 OpenCode handoff。
