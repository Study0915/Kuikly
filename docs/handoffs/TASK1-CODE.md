# Task 1 CODE 实施记录

## 2026-09-13 联合视觉实施（当前）

用户明确要求实施完整视觉与提交计划。启动基线 485964c，计划记录 602bc15，业务提交 c25eeed；分支 feature/task2-evidence-chat。允许路径为 shared 活动 UI/insight、h5App/Android 宿主、既有回归/录制脚本和当前计划、架构、证据、学习与提交文档；详细清单与 40/25/25/10 映射见两题计划。结构增量按 ADR-015 记录，未新增 module 或依赖。

实施：FinanceTheme 统一排版/按钮/Canvas 图标，首页分隔行与两个有效入口；详情先看双图、证据与事实，计算折叠，演示选项收进面板。DetailPresentationState 按文档保存展开/偏移，挂载代次防止旧布局回调改变新页面；证据仍由原 LensState 决定。聊天卡复用主题与数值层级，composer 固定在页面底部。H5 读取实际最大 480 容器，Android 同步浅色宿主。

验收命令：scripts/verify.ps1、scripts/test-ui.ps1 -Task Both；53 JVM 与两题 233 项浏览器检查通过，Android 仅构建。随后运行 record-demos、test-receipts 和 prepare-submission；精确产物以 DELIVERY 和 validation 收据为准。

保护：初始 14 份 tracked 内容已在工作区缓存留存；README 按本轮明确授权重写，其余 13 份原差异保持，不整仓提交。未读取未跟踪兼容入口，未安装全局环境，未推送、PR、合并或外发。下一步：本地候选和公开入口检查。


- 计划：[TASK1-PLAN v1.0](../plans/TASK1-PLAN.md)；2026-09-07 用户明确“开始实施”，已确认并授权 CODE。
- 实施者：Windows Codex；工作树为当前 Windows 仓库根，路径不写入公开材料。
- 启动基线：`00607119173ce75682fad46bd6d6e4e514d4e5ca`；原空壳源码基线 `24cedfe6b6f8be84bbed4115438c3c136241be32`。
- 分支：`feature/task1-quote-evidence-lens`。
- 允许路径、实现清单、Interface、验收命令、F/P/I/Q 场景与评分证据：以总计划第 4–9 节为合同，不另建 Feature 卡。
- 既存差异：`.gitignore`、AGENTS、CHANGELOG、CONTRIBUTING、README、DECISIONS、GIT-WORKFLOW、agent-workflow、handoff/learning/plan 模板、skills、两题 readiness 共 14 个 tracked 修改，另有未跟踪兼容入口。仅按实际需要编辑直接相关文件，其余保留；不读取兼容入口或启用外部实施者。
- 外部操作：未 push、merge、PR、tag、release、实际提交或外发。

## 步骤记录

2026-09-08 深化：用户选择“交互与工程一起深化”，基线 `3309d2f`。范围及允许路径先提交 `0d9d6ce`，随后 `ea72928` 修复会话和历史恢复，`bf8824f` 完成日期导航、证据计算明细和共享展示投影。对应总计划 D1–D4，未扩大周期、手势、平台或 Task 2 范围；最终验证和截图见 TESTS 的 2026-09-08 记录。

页面现在由 FinanceSession 持有当前 FinanceContent；旧挂载事件、旧请求及不匹配/空文档不能形成有效详情。H5 区分 push/replace/back，恢复实际 snapshot 和 focus，并以实际快照反查演示场景。LensPresenter 从已验证事实产生计算式、日期邻居、样本入口与关联顺序，QuoteEvidenceLens 只渲染投影并发出既有动作。

本轮没有安装任何依赖，继续使用既有工作区运行时。开始时 14 个既存 tracked 差异的 SHA-256 已保存并再次逐项核对无变化；未读取或提交既存 untracked 兼容入口。新原始日志放在 `.cache/task1-improvements-20260908`。

| 步骤 | 实际实施 | 证据与状态 |
|---|---|---|
| C0 | 补充进程级 TEMP/TMP、Java home/temp、npm prefix/config 与浏览器缓存/daemon 路径；doctor 检查工作区隔离 | VERIFIED；bootstrap/doctor 输出 CLI_ENV_OK；空壳 verify 全部构建成功，不代表新业务通过 |
| C1 | 锁定 2.4.0 的源码与双图点选/滚动探针 | VERIFIED；328a1bf，见技术探针；夹具已从活动入口移除 |
| C2 | Mock 与证据事实、焦点状态 | VERIFIED；db14587，A/B 算例与数据/引用/时效/状态验证 |
| C3 | 行情列表、详情与恢复 | VERIFIED；f6669fa，12 个实体、返回位置、失败重试和旧请求隔离 |
| C4 | 联动卡完整接入 | VERIFIED；与 C3 同批 f6669fa，完整 H5 行为验收通过 |
| C5 | TESTS 后编写 LEARNING | TESTS 受限 VERIFIED；验收/录像脚本与证据提交 6f23a53；复盘已交付，用户掌握程度待自测 |

## 环境与真实性

只使用仓库 `.cache` 中 JDK/Node/SDK 与依赖，不激活或安装到 conda base，不写系统 PATH 或用户持久配置。Android 设备运行仍独立验收；未经确认配置目录落点不启动 ADB。

实际环境：JDK 17.0.12、Node 24.12.0、npm 11.6.2、Gradle Wrapper 8.0；Android build-tools 30.0.3、platform-tools、platforms 33/34 已有，无新增 SDK 安装。原始日志保存在 `.cache/task1-evidence/raw/c0-bootstrap.log` 与 `c0-baseline-build.log`。CLI 复用工作区已有 Playwright 0.1.18；未使用全局安装。日志可含本机路径，不直接加入公开仓库。

源码来源：KuiklyUI tag `2.4.0` 对应 `63cdb10b07065fac9692899dcd8f15bd1f4bc61b`。旧缓存 HEAD 不作为锁定源码证据；按 tag 提取到工作区缓存。公共源码缺失对象通过 Git 单次 OpenSSL 参数读取，未修改全局 Git 配置。

后续补充：C3/C4 在同一完整页面构建后合并为聚焦业务提交；C1 的返回桥接由 C3 实际路由验收补全。增加 scripts 中生产服务、浏览器验收和录像入口，公开文件清单已经写回总计划，没有扩大产品范围。

实际修复包括 H5 普通文本测量副本、混合设备鼠标/多指处理、反查入口顺序、浮点日槽边界和 KSP 增量丢失注册。最终一次非 Page 文件修改后的构建/浏览器回归证明注册保持；详见 [TESTS](../REVIEWS/TASK1-TESTS.md)。

录像新增工具仅为工作区 FFmpeg build 1011（LGPLv2.1）及安装器附带的 Winldd v1007，均在 `.cache/browsers`。源码运行不需要这些工具；其用途和实际安装来源见证据索引。

下一步：按运行说明体验 Demo，并完成学习自测；Android 设备与 Task 2 各自按原计划推进。
