# Task 2 CODE 实施记录

## 2026-09-13 联合视觉实施（当前）

用户明确要求实施完整视觉与提交计划。启动基线 485964c，计划记录 602bc15，业务提交 c25eeed；分支 feature/task2-evidence-chat。允许路径为 shared 活动 UI/insight、h5App/Android 宿主、既有回归/录制脚本和当前计划、架构、证据、学习与提交文档；详细清单与 40/25/25/10 映射见两题计划。结构增量按 ADR-015 记录，未新增 module 或依赖。

实施：FinanceTheme 统一排版/按钮/Canvas 图标，首页分隔行与两个有效入口；详情先看双图、证据与事实，计算折叠，演示选项收进面板。DetailPresentationState 按文档保存展开/偏移，挂载代次防止旧布局回调改变新页面；证据仍由原 LensState 决定。聊天卡复用主题与数值层级，composer 固定在页面底部。H5 读取实际最大 480 容器，Android 同步浅色宿主。

验收命令：scripts/verify.ps1、scripts/test-ui.ps1 -Task Both；53 JVM 与两题 233 项浏览器检查通过，Android 仅构建。随后运行 record-demos、test-receipts 和 prepare-submission；精确产物以 DELIVERY 和 validation 收据为准。

保护：初始 14 份 tracked 内容已在工作区缓存留存；README 按本轮明确授权重写，其余 13 份原差异保持，不整仓提交。未读取未跟踪兼容入口，未安装全局环境，未推送、PR、合并或外发。下一步：本地候选和公开入口检查。


Owner：Windows Codex。2026-09-12。计划 TASK2-PLAN v1.0，授权与结构依据 ADR-014。基线 `1515a8e`，计划提交 `25514f5`，分支 `feature/task2-evidence-chat`。

允许路径、接口、顺序与验收命令依总计划执行。环境仅 `.cache`，doctor 已返回 CLI_ENV_OK。初始14个tracked差异及未跟踪CLAUDE.md保留；不读CLAUDE.md、不调用WSL工具、不整仓提交。

顺序：common模型/会话/Markdown/provider及测试 → UI/卡片 → 路由/宿主 → verify/两题浏览器 → 学习与本地提交。未push、未merge、未PR、未tag、未release、未外发。

当前：CODE VERIFIED，业务提交66c9ab0；新增typed blocks、请求票据、受限Markdown、A/B与缺量卡、共享图形/详情、Chat history与返回状态。49项JVM、Task2主链路/长会话/桌面、Task1回归与两端构建通过，详见TASK2-TESTS。设备运行仍未验证。下一步：LEARNING与本地SUBMIT准备。

持续优化批次：2026-09-12，基线 d2203e4，计划 v1.1，业务提交972167b，CODE/TESTS VERIFIED。实体解析、草稿/列表和Markdown保真修复通过53项JVM与两题完整浏览器回归；新增17项在触摸和桌面各通过。构建/UI/录像收据用于交付版本绑定；允许路径与初始差异保护不变。命令：scripts/verify.ps1、scripts/test-ui.ps1、新增意图/草稿/导航浏览器检查、record-demos、test-receipts与prepare-submission。评分证据要求与退出条件见总计划v1.1；最新本地交付以DELIVERY为准。
