# 版本、演示与验证记录

2026-09-13。活动业务版本 **c25eeed**，课程成果位于 [Study0915/Kuikly · feature/task2-evidence-chat](https://github.com/Study0915/Kuikly/tree/feature/task2-evidence-chat)。该入口包含 Task 1 和 Task 2 的完整实现、运行说明、截图、演示与测试证据。

## 内容索引

- [项目说明](../../README.md)：两题功能、技术栈、架构、运行方法和核心设计。
- [Task 1 演示](../demo/task1.webm)：88.44 秒，区间→单日→公式→缺量→B 复用→失败恢复。
- [Task 2 演示](../demo/task2.webm)：92.64 秒，问答卡→同快照详情→比较/追问→草稿→缺量→原位重试→共享图形。
- [课程要求与验证证据](SCORING-AUDIT.md)、[课程登记](course-entry/OpenSourceTalent/Study0915/README.md)。

两段视频均为实际 H5 操作，无音频；390×844 应用画面外有说明字幕，视频尺寸为 520×1020。视频经过全片解码和关键帧检查，公开素材与录制收据的文件哈希一致。

## 验证范围

53 项共同逻辑测试通过，0 failures/errors；H5 production 与 Android Debug APK 构建通过。Task 1：66 H5、7 触摸、33 深化、4 鼠标，共 110 项。Task 2：39 H5、11 会话/触摸、39 桌面，17 优化检查在触摸与桌面各通过，共 123 项。浏览器合计 233 项，含不同输入模式的重复场景。

320/390/430/1024 与短视口、缺量、失败和长会话经过回归与视觉核对。三类依据、展开计算、按文档恢复和演示面板返回均有检查。详见[验证摘要](../evidence/task2/verification.json)、[视觉记录](../evidence/task2/visual-review.json)及两题 TESTS。

12 项[交付门禁](../evidence/task2/receipt-gates.json)在隔离副本通过，覆盖源码、运行 JS、UI 脚本/结果和录像的版本匹配。原始构建日志含本机路径，只保存在忽略缓存中；仓库提供可复跑脚本、JSON 结果和截图。

## 本地制品存档

r4 为 c25eeed 业务和 29f6408 文档快照的本地归档，含 195 个清单文件、19,325,649 字节。ZIP SHA256：3025B4BACA636A37F075F90B425F54DF55BC28C5BAA559672D2930ACD79A1037。归档包含源码、H5 离线预览、构建 APK、视频与验证收据；本文档之后的正式说明调整不改变业务构建和该历史制品。

源码输入指纹：594DBB539D7CCDC8E9D399B20C7BCAB66931A7C1AEDF99E6BA40F74BFB96D5EF。导出源码指纹、JS/APK 与收据相符，Markdown 链接和 ZIP 文件哈希检查通过；导出预览验证首屏图表、公式、B 的 +5.49% 承接、返回会话及未知对象提示，运行错误为 0。

## 复现与边界

按项目 README 将 JDK17、Node 与 Android command-line tools 放在项目 .cache，再运行 bootstrap-cli、doctor 和 verify；不要求全局 Gradle 或系统 PATH 修改。首次构建需要下载固定依赖。

全部行情和回答为确定性历史 Mock。Android 设备/原生键盘、iOS、HarmonyOS、真实模型/行情接口、性能基准与独立机器初装未验证。H5 和构建记录分别说明执行范围；锁定工具链有上游提示，未声明零警告。2026-09-12 独立源码目录构建使用共享缓存，属于先前版本记录，不代替当前独立机器验证。
