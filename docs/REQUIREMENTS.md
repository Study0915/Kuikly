# Kuikly Finance 需求与完成定义

## 范围权威

- [FACT] 2026-08-19，用户提供了 Shape with AI Task 1 / Task 2 原始题面；本文件据此记录唯一的任务范围与完成定义。
- [FACT] 2026-08-21，已逐页核对本地活动 PDF（未提交），并按完整视频时间轴同步核对导师讲解画面与用户提供的转写稿；材料理解见[人读版报告](research/2026-08-21-shape-brief-teacher-video-human.md)和[AI 版合同](research/2026-08-21-shape-brief-teacher-video-ai.md)。
- [DECISION] 信息优先级为：本文件中的原始题面 → 用户确认的 Task 总计划与当前活动源码/TESTS 证据 → Kuikly 官方参考资料 → 独立 Issue、历史归档与活动背景。
- [DECISION] Tencent-TDS/KuiklyUI Issue #1477 不是 Shape Task 1 / Task 2 题面，不能据此增加 line/bar、坐标轴、手势、DSL 或多平台矩阵等 Task Must。
- [FACT] archive/task1-v1 只保存旧版 Shape 实现、成果和证据；活动 Task 1 从 finance_home 空壳重新开发，不继承旧 API、完成状态或验收结果。

## 原始题面

本次活动基于腾讯 Kuikly 跨端框架进行业务 Demo 设计与开发。股票类应用通常围绕行情浏览、个股查看、走势分析与信息解读展开；两道题均要求在股票业务场景中结合 AI 能力完成产品原型。

| 任务 | 题目 | 难度 |
|---|---|---|
| Task 1 | 基于 Kuikly 开发 AI 股票行情原型 Demo | 三星 |
| Task 2 | 基于 Kuikly 开发 AI 股票问答应用 Demo | 四星 |

共同参考：[Kuikly 快速开始](https://kuikly.tds.qq.com/QuickStart/env-setup.html)。

## 正式评分、交付与导师校准

### 正式评分

| 维度 | 权重 | 书面考察点 |
|---|---:|---|
| 功能实现完整性 | 40% | 页面闭环、需求覆盖度、状态处理完整性 |
| 代码质量与工程设计 | 25% | 分层、可维护、扩展、规范，以及可孵化的通用组件 |
| AI 场景设计能力 | 25% | 与股票业务自然结合、能力可演示、创新场景 |
| 加分项 | 10% | 平台覆盖、真实 API、体验优化 |

### 正式交付物

1. 独立仓库中的完整、可编译运行 Kuikly 项目代码；
2. 随代码提交的说明文档，至少说明项目、技术栈、代码结构/目录和核心亮点；
3. 原型演示视频，覆盖完整功能链路、交互流程、关键操作和 AI 能力效果。

### 导师口头校准

- [FACT] 基础列表、详情、输入和发送是必要地板，但区分度有限；正式权重仍以 40/25/25/10 为准。
- [FACT] 导师反复强调“可孵化的通用组件”和“承载 AI 信息的 UI/交互载体”；AI 模型强弱、分析文本本身和数据来源不是本课题的首要评价对象。
- [FACT] 确定性 Mock 数据/Mock AI 可以用于证明交互；真实 API、更多平台和体验优化属于加分，不应阻塞前三个主维度。
- [FACT] 代码可运行、说明文档主动讲亮点、视频真实演示完整链路是关键交付要求。
- [FACT] 2026-09-13用户转发的老师群通知写明“9月14日实战结束”。另核对[指定分支README](https://github.com/Kuikly-contrib/Kuikly-awesome/tree/Tencent/OpenSourceTalent)：9月14日当天项目代码仓库必须公开可访问，否则视同未提交；该指南未列出具体截止时刻。课程仓库只新增自己的 `OpenSourceTalent/<GitHub ID>/README.md`，填写ID、Task、公开仓库链接和简短说明；项目自己的README和文档详细展开。
- [UNVERIFIED] 两题是否严格分赛道、是否必须同时交付两题、组队/个人奖项和奖项分配尚未确认。PDF交付页把两题核心功能写在同一源码完整性条目，而导师Q&A表示两题大概率分开评分、两个都做是有余力的选择；不把这些推测写成最终规则。
- [DECISION] 评分对齐开发与交付门见 `docs/evaluation-pipeline.md`；它只改变优先级和证据要求，不扩张 Task Must。

## Task 1：AI 股票行情原型 Demo

### 题面明确要求

1. 首页行情列表页：
   - 展示股票名称、股票代码、最新价、涨跌额、涨跌幅；
   - 支持列表滚动浏览；
   - 点击某只股票进入个股详情页。
2. 个股详情页：
   - 展示名称、代码、最新价、涨跌幅、最高价、最低价、成交量等基础信息。
3. 在个股详情页中提供 AI 分析与解读模块（发散性）：
   - 具体方式不限定；
   - 可从买入/卖出建议点位、操作提示、趋势判断、风险提醒、信号解读、行情总结等方向选择一种或多种；
   - 可使用卡片、标签、提示区、文本分析区等合理形态展示。

### 活动实现完成定义

- [FACT] 核心路径是：行情列表 → 点击股票 → 个股详情 → 可见的 AI 分析。
- [DECISION] 默认使用仓库内离线、确定性 Mock 行情与 Mock AI，保证可复现且不读取密钥。
- [DECISION] 当前工程按已配置的 Android/H5 target 分别构建和交互验证；题面没有要求把 Kuikly 支持的全部平台加入本仓库。
- [DECISION] loading、empty、error、retry、未知 code、免责声明、测试和证据属于工程质量合同，不冒充题面原文。
- [DECISION] 通用图表、line/bar DSL、K 线、成交量图、缩放、平移和高级手势均不是 Task 1 核心完成门；只有被用户选入 Task 1 总计划时才进入活动范围。最小趋势区域与 AI 标注可作为高杠杆候选，但不是默认 Must。

## Task 2：AI 股票问答应用 Demo

### 题面明确要求

1. AI 聊天主页面：
   - 支持用户输入问题、发送消息和展示会话记录。
2. AI 返回内容渲染（发散性）：
   - 除 Markdown 文本外，至少支持一种股票、指数或行情相关的业务内容形态；
   - 可选择结构化卡片、图表或其他适合业务场景的内容，具体实现不限定。
3. 股票/指数详情承接页：
   - 至少提供一个能由聊天结果跳转到达的详情页面；
   - 页面用于展示基础行情信息、走势区域、摘要信息或 AI 解读内容。

相关参考：[Kuikly 快速开始](https://kuikly.tds.qq.com/QuickStart/env-setup.html)、[KuiklyMarkdown](https://github.com/Kuikly-contrib/KuiklyMarkdown)。

### 活动实现完成定义

- [FACT] 核心路径是：输入问题 → 发送 → 显示会话 → 返回 Markdown 与至少一种非 Markdown 业务内容 → 点击结果进入详情承接页。
- [DECISION] 详情承接页的完成门解释为：展示基础行情与走势区域，并提供摘要信息或 AI 解读中的至少一种；“走势区域”不等于必须实现通用图表、K 线或高级手势。
- [DECISION] 首版继续使用确定性 Mock Chat；真实模型、实时行情、联网检索、语音、登录和云同步不在当前范围。
- [DECISION] 使用可扩展的显式内容模型隔离 Markdown 与结构化业务内容；首版不要求同时实现 QuoteCard、TrendChart 和所有股票/指数形态。
- [DECISION] 复用 Task 1 已验证的行情模型或详情能力时按实际消费建立接口；Task 2 不以通用 Chart module 存在为启动或完成前提。
- [DECISION] 空输入、重复发送、失败重试、未知实体、长会话、Markdown 安全和会话返回保持属于工程质量合同，不冒充题面原文。

## 顺序、真实性与共同质量门

- [DECISION] 活动固定按 `Task 1 PLAN → CODE → TESTS → LEARNING → Task 2 PLAN → CODE → TESTS → LEARNING → SUBMIT` 推进；该顺序是项目实施策略，不是题面原文。
- [DECISION] 每个 Task 只维护一份总计划、一次 CODE handoff、一份 TESTS 报告和一份 LEARNING 报告；评分证据由 TESTS 统一记录，不再创建活动 Feature 任务卡。
- [DECISION] PLAN 必须正式映射功能 40%、工程 25%、AI 场景 25%、加分 10%；核心创新、通用组件、AI 载体和删减线未获用户确认前不得进入 CODE。
- [DECISION] 两题 LEARNING 均 `VERIFIED` 后才进入 SUBMIT；本地 `SUBMIT_READY` 与外部上传、发布或获奖是不同事实。
- [DECISION] Mock、构建成功、浏览器运行、设备运行和未验证平台分别记录；未执行的能力不得写成已完成。
- [DECISION] 股票内容始终标明“仅作技术演示，不构成投资建议”。
- 文档标签统一使用 [FACT]、[MOCK]、[VERIFIED]、[UNVERIFIED]、[LIMITATION]、[DECISION]。
