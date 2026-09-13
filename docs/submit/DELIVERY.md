# 两题本地交付说明

2026-09-13。视觉实施基线 485964c，计划提交 602bc15，业务版本 **c25eeed**，分支 feature/task2-evidence-chat。本轮完成浅色行情首页、首屏双图与折叠计算、紧凑问答卡、容器适配和连续状态恢复；题面及创新范围不变。新候选名为 submission-20260913-r4，封存成功以 CANDIDATE_ARCHIVE_VERIFIED 和包外 SHA 为准。没有推送、PR、合并、发布、报名提交或外发。

## 先看新版成果

1. [项目 README](../../README.md)：定位、三页截图、两题路径、具体优势与运行方法。
2. [Task 1 视频](../demo/task1.webm)：88.44 秒，结论→区间→单日→计算→缺量→B 复用→失败恢复。
3. [Task 2 视频](../demo/task2.webm)：92.64 秒，输入→Markdown+卡片→同快照详情→比较/追问→保留草稿→缺量→原位重试→共享图形。
4. [评分证据审计](SCORING-AUDIT.md)：按 40/25/25/10 展示证据，已撤下旧版 90/91 数字自评，老师实际评分未知。
5. [课程登记候选](course-entry/OpenSourceTalent/Study0915/README.md)：仅包含自己的 GitHub ID、Task 1 & Task 2、仓库链接与简短说明，按老师模板独立准备。

两段录像来自实际 H5 操作，520×1020 外框内的应用为 390×844；字幕在应用画面外，无音频。已全片解码并各检查三个关键帧。docs/demo 的公开视频与验证录像逐字节一致，README 不依赖本机路径或忽略缓存。

## 公开入口仍待同步

2026-09-13 用不带 Authorization 的匿名请求核对：[仓库](https://github.com/Study0915/Kuikly)已 public，但默认 main 仍为 **24cedfe6b6f8be84bbed4115438c3c136241be32**，公开 README 仍写旧空壳；feature/task2-evidence-chat 的公开分支接口返回 404。本地改版完成不等于老师已经能看到这些功能，当前登记链接尚未指向本轮成果。

[官方指南](https://github.com/Kuikly-contrib/Kuikly-awesome/tree/Tencent/OpenSourceTalent)要求 9 月 14 日当天项目仓库公开可访问，未列具体时刻；用户转发的群通知另写“9 月 14 日实战结束”。不推定深夜截止，也不把公开开关已打开当成交付完成。

最终对外提交前必须逐项匿名检查：

- 登记链接打开的默认入口包含本轮完整代码，至少涵盖业务 c25eeed 及后续展示文档提交。
- README 三页截图、两段视频、运行说明和学习材料均能访问。
- 课程目录为 OpenSourceTalent/Study0915/README.md，Task 1 & Task 2 填写准确，未提交整个工程到课程登记仓库。
- 核对老师最新通知。本轮只做本地候选；同步、合并、PR 和实际提交由用户另行决定。

## 验收与复现边界

53 个共同逻辑测试通过（0 failures/errors）；H5 production 和 Android Debug 构建通过。Task 1：66 H5、7 触摸、33 深化、4 鼠标，共 110 项。Task 2：39 H5、11 会话/触摸、39 桌面，17 优化检查在触摸/桌面各通过，共 123 项。两题共 233 项浏览器检查，包含不同输入模式的重复场景，不等同于 233 项独立功能。

320/390/430/1024、短视口、三页、缺量、失败、长会话经过回归与视觉核对。完整图表在 390×844 首屏，按钮文字/48 触控区、计算展开、按文档恢复、演示面板返回通过检查。对比度、截图与产物哈希见[验证摘要](../evidence/task2/verification.json)和[视觉审查](../evidence/task2/visual-review.json)。未做全量无障碍认证。

[12 项交付门禁](../evidence/task2/receipt-gates.json)在隔离副本通过；源码、运行 JS、UI 脚本/结果、录像与产物收据绑定。打包对公开视频另做哈希匹配，导出所有构建/测试/录制输入，检查 Markdown 相对链接，再逐文件核对 ZIP 长度与 SHA。

候选源码来自明确的 tracked 白名单，附验证过的 Kotlin/JS yarn.lock，不带 Git 历史、SDK、缓存、个人工具配置或归档。导出的源码指纹、H5 JS 和 APK 必须匹配收据。本轮没有重复宣称独立目录构建；2026-09-12 r3 的独立源码目录复验使用共享工作区依赖/Gradle 缓存，属于历史证据，不能当成本轮独立机器初装或 APK 逐字节可复现证明。

快速看候选：在已有 Node 环境执行 node preview/serve.cjs，可选端口参数，例如 18771。源码运行按项目 README：JDK17、Node、Android command-line tools 全部放在 source/.cache，再运行 bootstrap-cli、doctor、verify。原始带本机路径的日志只留在忽略目录；本轮没有安装新依赖，也没有修改系统 PATH 或 base 环境。

## 保留与未验证项目

旧 submission-20260912-r3.zip 保留，13,425,449 字节、174 个清单文件，SHA256 D08B38C7D219EEE2A10CDA9F318F2EC100CA587380D68334C138474C43159E31；旧 r2 也未删除。新候选使用独立名称，不覆盖旧包。

初始 14 份 tracked 内容留有工作区备份；README 按本轮明确授权重写，其他 13 份原差异保持，未整仓纳入提交。未跟踪兼容入口未读取或提交。必要文档在候选里采用当前内容，以 MANIFEST 逐文件哈希为准。

全部行情和回答是确定性历史 Mock。Android 设备/模拟器与原生键盘、iOS、HarmonyOS、真实行情/API/模型、性能基准、真实投资效果与个人讲述能力未验证。20 轮/500 字、刷新清空会话、受限 Markdown 均保留；工具链仍有上游警告。

下一步：预览新版候选，确认正确成果同步到公开入口，再按老师格式登记。外部操作尚未执行。

## r4 封存后核验

2026-09-13：submission-20260913-r4.zip 已返回 CANDIDATE_ARCHIVE_VERIFIED，包含 195 个清单文件，19,325,649 字节，SHA256 **3025B4BACA636A37F075F90B425F54DF55BC28C5BAA559672D2930ACD79A1037**。包内源码快照基于 29f6408 及保留的必要文档工作树内容，业务提交为 c25eeed；构建输入指纹为 594DBB539D7CCDC8E9D399B20C7BCAB66931A7C1AEDF99E6BA40F74BFB96D5EF。

导出预览在独立端口 18771 实测首屏图表、展开公式、B 的 +5.49% 精确承接、返回会话和 C/Z 不生成伪造卡，运行错误为 0。包内 Markdown 链接、逐文件长度/SHA 与公开/验证视频一致性通过；禁止路径、个人绝对路径和凭据模式扫描 0 命中。旧 r3 哈希复核不变，初始 13 份非 README 工作树内容哈希不变，doctor 再次返回 CLI_ENV_OK。

本段为封存后的验收记录，不回写已封存 ZIP；候选内部以 MANIFEST 和 validation 收据为准。下一步仍是由用户决定正确版本的公开同步与实际登记，当前未推送、PR 或提交。
