# 两题本地交付说明

日期：2026-09-12。业务版本`66c9ab0`，分支`feature/task2-evidence-chat`。两题实现、TESTS、学习材料与最新演示已完成；本地候选位于`.cache/submission-20260912-r1/`及同名ZIP。包外SHA文件与包内MANIFEST记录确切文件版本。未推送、PR、合并、发布、报名提交或外发。

## 先看什么

1. `videos/task1.webm`：87.64秒，行情→依据→单日/样本→缺量→B复用→失败重试。
2. `videos/task2.webm`：99.12秒，输入→Markdown+卡片→精确详情→返回→A/B→追问→缺量→未知→重试→共享图形。
3. `source/README.md`与[评分审计](SCORING-AUDIT.md)：内部自评T1约90、T2约91；实际老师评分未知。
4. `preview/serve.cjs`：已有Node环境运行`node preview/serve.cjs`，打开`http://127.0.0.1:18770/`，无需先构建。

两段视频均为真实H5操作，无音频，说明字幕在应用画面外。已抽帧目检核心场景，并用工作区FFmpeg解码完整视频；没有将字幕中的功能描述当作额外应用功能。

## 源码与复现

源码由明确的tracked文件白名单导出，包含Gradle Wrapper、各活动module、测试、运行/验收脚本、当前说明与证据；另附当前Kotlin/JS yarn.lock快照。排除归档源码、Git历史、个人配置、SDK、依赖缓存、密钥和原始私有材料。

在Windows PowerShell中，将完整JDK17、Node.js、Android command-line tools分别解压到`source/.cache/jdk17`、`.cache/node`、`.cache/android-sdk/cmdline-tools/latest`。然后运行`bootstrap-cli.ps1`、`doctor.ps1`、`verify.ps1`。bootstrap将所需Yarn1.22.17和缺失SDK包安装在工作区；首次构建需要固定依赖下载，不要求全局Gradle或改系统PATH。

2026-09-12已在独立源码目录、无原项目构建产物的条件下复验：共用工作区依赖缓存，JS/H5与Android构建分别成功（1m25s、29s），49项JVM测试通过；H5产物SHA与活动目录完全一致。这证明本地源码完整性，不冒充第二台机器全新联网安装验证。

## 验收范围

49个共同逻辑测试；T1 59/7/23/4项H5/触摸/深化/鼠标；T2 32项H5、11项会话/触摸和独立32项桌面通过。详情见[Task2测试报告](../REVIEWS/TASK2-TESTS.md)。APK解包检查包含ChatSession、FinanceChatView和FinanceHomePage。

H5与APK版本、截图及JSON结果位于源码的`docs/evidence/task2`。原始包含本机路径的构建日志只留在忽略目录，不放入候选。

## 保留的边界

全部行情和AI回答为确定性历史Mock。Android设备/原生键盘、iOS、鸿蒙、真实模型/API、性能基准与个人讲述能力未验证。20轮/500字上限、刷新清空会话、受限Markdown有明确说明。锁定工具链存在上游警告，不宣称零警告。

原仓库初始14个tracked修改及未跟踪CLAUDE.md保留；本轮聚焦提交没有整仓纳入旧差异。候选会携带必要文档的当前内容，MANIFEST逐文件哈希为准。

下一步：用户预览视频与Demo，练习讲述，并决定是否对外提交；当前无需额外环境安装。
