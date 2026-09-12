# Task 2 TESTS · 2026-09-12

Owner：Windows Codex。分支`feature/task2-evidence-chat`；业务提交`66c9ab0`。CODE/TESTS VERIFIED，在明确的平台限制下通过。原始日志在`.cache/task2-evidence/`；可随源码携带的结果与截图见[证据目录](../evidence/task2/README.md)。

| 检查 | 实际结果 | 直接证据 |
|---|---|---|
| 工作区环境 | doctor CLI_ENV_OK；复用工作区JDK/Node/SDK/Gradle/Playwright，未新增全局安装 | use-cli-env.ps1、doctor执行记录 |
| JS与H5 | compileKotlinJs、production webpack、页面注册通过 | verify-release.log，BUILD SUCCESSFUL 43s |
| JVM与Android APK | 49 tests，0 failures/errors；assembleDebug与页面注册通过 | testDebugUnitTest XML、verify-release.log，BUILD SUCCESSFUL 39s |
| Task2 H5 | 32项通过，输入/内容/详情/草稿/前进后退/风险追问/缺量/取消/重试/恶意文本/窄屏 | h5-final.json |
| Task2长会话与触摸 | 11项通过，20轮上限与早期记录、离页完成、父滚动、精确日期、展开状态返回 | session-final.json |
| Task2桌面 | hasTouch=false独立浏览器，32项通过 | task2-desktop-final.json |
| Task1回归 | 59 H5、7触摸、23深化、4鼠标通过 | task1-*-final.json |
| 视觉核对 | 320/390/1024宽度，输入固定可见，文字无横向溢出；缺量无错误倍数 | chat-*.png、missing-volume.png |
| 外部资源 | 专项测试没有外部请求和未捕获运行错误 | H5与桌面结果errors/external为空 |

本轮新增17个JVM测试：ChatSession 6、MockChatProvider 7、SafeMarkdown 4；原Task1的32项保留。JVM测试数量不冒充各平台单独运行次数。Task1鼠标4项在卡片展开保持改动前的集成构建执行，后续只改ChatController/聊天卡，不涉及该鼠标路径；最终两题主链路与Task1其余89项均在66c9ab0构建复验。

## 真实修复

1. vfor要求每项一个根节点，原两气泡并列在运行时失败；改为一个消息轮容器，再验收发送、更新、20轮长列表。
2. JVM能接受的未转义方括号正则在JS Unicode模式失败；修正转义，并通过真实Markdown响应跨端运行检查。
3. 返回详情后的聊天图表展开状态原由临时视图持有；改为按消息ID+文档键存储，返回仍展开，不串卡。
4. 首次失败注入仅影响ChatProvider；成功重试使用完整行情快照，避免进入详情重复触发无关故障。

测试脚本等待实际渲染状态与视口尺寸稳定，不把异步队列尚未完成的瞬时布局当成产品失败。触摸滚动从有可滚动空间的方向操作，并同时断言父列表真的移动、没有误跳详情。

## 不申报的能力

交付复核：独立源码目录共用工作区依赖缓存，从无项目build产物起构建通过（H5 1m25s、Android 29s），49项JVM测试通过；H5 SHA与活动构建一致。APK的DEX中确认含ChatSession、FinanceChatView和FinanceHomePage。两段最新视频分别87.64s/99.12s，全片解码与核心帧目检通过。此项不代表第二台机器全新安装或Android设备运行。

Android设备/模拟器、原生键盘、iOS、鸿蒙、真实API、真实模型、性能基准、真实投资效果均未验证。H5缩小视口只能证明布局响应，不能证明手机系统键盘。会话刷新清空、20轮/500字上限、受限Markdown均为可见合同。锁定工具链存在AGP/compileSdk与webpack大小提示，不声称零警告。

下一步：学习材料核对、最新演示和本地候选包；外部发布与老师实际评分未知。
