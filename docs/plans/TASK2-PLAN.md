# Task 2 总计划 v1.1

## 2026-09-13 已授权视觉与提交改版

用户以“PLEASE IMPLEMENT THIS PLAN”确认完整方案。基线 `485964cbd2cc351146c1b01391268e9e373ce454`，分支 `feature/task2-evidence-chat`，Owner为Windows Codex主代理；初始14个tracked差异和未跟踪CLAUDE.md保留。允许shared/src、h5App/src、androidApp/src必要宿主、scripts、README、两题docs；独立课程登记候选和工具/临时产物仅在工作区.cache。禁止归档、无关文件、全局环境、推送和PR。

采用[ADR-015](../decisions/ADR-015-shared-finance-presentation.md)：浅灰#F5F7FA/白色表面/#17212F正文/#2563EB主操作；边距16、8步进，正文16/辅助12–13/标题22/报价36，触控区域48。统一字体与测量，H5最大480居中、Android浅色宿主。

1. R-FUNC 40：统一视觉、约72高完整行情行、Home/Chat双入口；详情报价→完整双图→三类依据→当前事实→折叠计算，390×844首屏完整核心图表。问答保留输入/Markdown/业务卡/正确详情/返回。
2. R-ENG 25：只增加共享呈现组件及Pager按文档展开/滚动状态，不复制LensFocus、resolver或图表。证明Task1、单股、比较、缺量的实际复用。
3. R-AI 25：突出可核对数值和依据；展示结论定位区间、交易日反查、缺量使量能计算不可用。公式按需展开，风险与时效仍可见。
4. R-BONUS 10：320/390/430/1024及短视口无溢出或输入遮挡，48触控、文本对比度；新增真机/模型不进入关键路径。测试不能代替视觉完成度。
5. TESTS：doctor、verify（JS/H5/JVM/APK）、test-ui -Task Both、视觉截图与人工检查；更新计算/设置入口与480尺寸断言，不削弱业务断言。再录约90秒的两题视频、收据核验、新候选打包，保留r3。
6. LEARNING/SUBMIT：README改为产品、演示、两题对照、创新、运行、架构/边界；公开素材进入docs/demo，另备OpenSourceTalent/Study0915/README.md。官方要求9月14日当天公开可访问，具体时刻未公布。当前public main仍为旧空壳；公开最新版本的核验是后续外部交付门槛，本地通过不能代替。

退出条件：以上布局实看、回归、新视频与包通过；重新审视原90/91自评，不保证老师成绩。依次实施，失败回CODE，环境与缓存只在工作区。

2026-09-12，Owner：Windows Codex。授权：用户要求自主完成并持续完善两题至约 90 分以上，常规选项不再询问。依据 REQUIREMENTS 与 ADR-014。T1-LEARNING 材料已核对；个人学习未代签。

## 目标与评分

输入问题 → 用户消息/响应状态 → Markdown 与证据卡 → 精确行情详情 → 返回原会话 → 追问/比较/异常恢复。

| 维度 | 目标区间 | 实现与可见结果 | 直接验收与提交证据 | 状态 |
|---|---:|---|---|---|
| R-FUNC 40 | 37–39 | 输入、记录、Markdown、卡片、详情、空/失败/取消/重试/未知/返回 | JVM + H5 主链路与视频 | SELECTED |
| R-ENG 25 | 23–24 | typed blocks、会话隔离、证据合同、A/B 两卡复用、Task 1 共用详情 | 接口与隔离/安全/路由测试 | SELECTED |
| R-AI 25 | 23–24 | 结论/依据/数值/风险/时效；带实体追问；同窗口比较 | UI交互、缺量/未知恢复 | SELECTED |
| R-BONUS 10 | 5–7 | 窄屏/触摸/桌面、历史恢复、最新回答导航、环境可复现 | 实际浏览器检查与构建；设备未跑不计 | SELECTED |

这是工作目标，不是老师成绩。最终自评只依据实际证据；不足约90分时优先补功能/工程/AI载体。

## 创新取舍

| 候选 | 用户价值与20秒效果 | 成本/风险 | 选择及删减线 |
|---|---|---|---|
| 可追溯证据问答 | 点理由到正确实体/快照/区间，再回原消息 | 中；路由/异步隔离 | 主线，不删 |
| A/B同窗口对比 | 同种卡片两组数值，分别打开详情 | 中；串线 | 选定；先单股后对比 |
| 真实模型/实时行情 | 更开放的输入 | 鉴权/数据时效/网络 | 放弃；Mock透明 |
| 语音与更多平台 | 额外交互 | 环境/设备成本 | 放弃，不阻塞核心 |

## 文件、接口、状态与实现顺序

1. 基线 `1515a8e2ca835e06898c121f93be33035befdfce`，分支 `feature/task2-evidence-chat`；先提交本计划、ADR-014、T1学习核对。保留初始差异。
2. `chat/ChatModels.kt`：Request、Turn、AnswerBlock.Markdown/EvidenceCard、Reply/Failure；卡片持有 ResolvedDocument。`ChatSession.kt`：trim、500字上限、单pending、20轮上限、代次/取消/原位重试/reset；草稿与记录返回保留。
3. `chat/MockChatProvider.kt`：A–L、比较A/B、风险/量能追问；未知返回支持范围；缺量/首次失败可复现。通过 EvidenceResolver 生成事实，不从Markdown解析实体。`SafeMarkdown.kt`：common受限parser，标题/列表/引用/段落、粗体/代码；不执行HTML/链接/图片，长度有界。
4. `ui/FinanceChatView.kt`：消息容器、Markdown renderer、EvidenceAnswerCard，呈现结论/依据/风险/时间/详情/追问；聊天列表与底部输入分离。A/B数据证明同一卡片复用。
5. `navigation/FinanceRoute.kt`、`ui/FinanceHomePage.kt`：内部Chat与Detail返回来源；行情请求与聊天请求分别隔离；首页增加问答入口。`h5App/.../Main.kt`：history加Chat/origin；URL不含问题；返回/前进正确，刷新清空会话并明确告知。
6. TESTS：doctor、verify、JVM、H5主链路/边界/窄屏/输入/历史/触摸、Android APK、Task1全部浏览器回归；失败返回CODE，不拿文案掩盖。
7. TESTS通过后写LEARNING、最终评分审计、两题最新视频、README与本地候选包。

允许路径：shared/src、h5App/src、必要androidApp配置、scripts、两题docs和README本轮明确段落；工具/日志仅`.cache`。禁止archive、无关文件、系统配置、全局环境、密钥/真实数据与外部操作。结构变化先更新本计划和ADR。依赖锁定Kuikly2.4.0，不引入KuiklyMarkdown，故不触发其探针。

## 验收与退出条件

| 验收 | 期望 | 失败判据 |
|---|---|---|
| doctor/verify | 工作区CLI，JS production + JVM + APK | 失败或缺页注册 |
| Session unit | 空/超长拒绝、重复/迟到隔离、取消/重试同消息、reset | 串会话/重复/错实体 |
| Provider unit | A/B数值一致、比较两卡、未知不伪造、缺量不补样本 | 文本/证据不符 |
| Markdown unit/UI | 格式可见，HTML/图片/链接不执行 | 远程请求/可执行DOM |
| H5 | 输入发送/loading/失败retry/详情/返回草稿消息与位置/清空 | 错页、消息丢失、按钮失效 |
| 窄屏/长会话 | 320/390/桌面可用，底部输入不被内容挤走 | 横溢、不能滚动/发送 |
| Task1回归 | 原H5/深化/触摸/鼠标脚本通过 | 既有链路退化 |

键盘H5 visualViewport与Android resize分别处理与验证；无设备证据不声称设备输入已验证。所有状态有恢复动作，未知实体不跳详情。取消/reset使旧响应无效，重试固定原问题和上下文。未来provider仍须返回typed blocks。

## 学习与交付

报告包含30秒介绍、职责、typed blocks、请求隔离、恢复、Markdown安全、复用、真实测试与10个面试问答。视频先写脚本：输入→证据→详情→返回→比较/缺量→失败重试；字幕同步。包包含源码、说明、两题证据/录像、版本/校验值，排除缓存、归档、个人配置；只本地准备。

## 2026-09-12 持续优化批次

用户再次要求“继续完善和优化”，沿用自主决策授权。基线 `d2203e4`，原分支与允许路径不变，Codex 主代理是唯一写入者；并行子代理只做只读规范、需求与交付审查。

1. **R-FUNC / R-AI**：修复比较对象不足时擅自补 A/B、未知单字母遗漏、日期/数量误作股票代码；明确展示需要补全、更正或缩小范围的提示。保持受限 Mock，不声称理解任意输入。补对象、上下文、日期与数量单测和真实输入回归。
2. **R-FUNC / R-BONUS**：快捷问题保留未发送草稿；“最新回答”定位最后一轮开头，读取结论不必从卡片底部倒找。按实际布局定位，回调只作用于当前挂载的会话。核对长会话、详情往返、取消/重试、窄屏与触摸。
3. **R-ENG**：代码围栏保留缩进和空行；仍仅输出文本，保留长度上限与安全边界。
4. **R-ENG / 交付**：构建验收记录输入指纹与产物哈希，打包拒绝混合旧产物、新源码或陈旧视频；补齐候选包内必要文档引用。新增脚本仅位于 scripts，收据和产物仅位于 .cache，不加依赖或 Gradle module。
5. **退出条件**：verify、Task 1 全部浏览器回归、Task 2 原有与新增行为测试通过；新视频与包绑定同一运行产物；写回 TESTS、LEARNING、评分证据和交付入口。保留 r2 候选，不覆盖历史包；没有设备运行证据就继续标未验证。
