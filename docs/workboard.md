# Kuikly 多 Agent 任务板

本表是当前协作状态的共享登记。状态变更必须伴随任务卡、commit 或证据路径；原始 WorkBuddy 评审内容不直接写入本表。

状态：`BACKLOG`、`READY`、`IN_PROGRESS`、`HANDOFF`、`REVIEW`、`VERIFIED`、`INTEGRATED`、`BLOCKED`。

| 任务 ID | 目标 | Owner | 基线 SHA | 分支 | 允许路径 | 状态 | 验收与证据 |
|---|---|---|---|---|---|---|---|
| WF-001 | 建立多 Agent 协作规则、任务卡、任务板和权限边界 | Codex | `7848aa3d6d52626e010d0b080287da39db525c08` | `feature/agent-workflow` | `AGENTS.md`、`PLAN.md`、`docs/`、`.codebuddy/rules/`、`README.md`、`CONTRIBUTING.md`、`.gitignore` | VERIFIED | `52ee3ac`、`9d2bfca`、`fb076d2`；Windows `git diff --check`、`doctor.ps1`=`CLI_ENV_OK`、`verify.ps1` Android/H5/JS 全通过；WSL `~/code/Kuikly` 原生克隆与 OpenCode 1.18.18 已验证 |
| T1-DEPTH | 在现有 Task 1 回归基线之上补充技术深挖、创新表达和跨端证据 | Codex + OpenCode | 待 WF-001 VERIFIED | `feature/task1-depth` | 按任务卡分配，禁止重写既有图表/行情/详情边界 | BACKLOG | WorkBuddy 里程碑 2；Android/H5 证据和演示材料 |
| T2-VERTICAL | 实现 Task 2 首个离线垂直切片：消息模型、Mock Provider、聊天状态、结构化内容承接 | OpenCode | 待 WF-001 VERIFIED | `feature/task2-chat-demo` | 以任务卡为准；优先 `shared/commonMain` 与对应测试 | BACKLOG | common/Android 测试、H5 编译和卡片跳转证据 |
| T2-EXPERIENCE | 完善 Markdown、长会话、失败重试、结构化图表和跨端交互 | Codex + OpenCode | 待 T2-VERTICAL VERIFIED | `feature/task2-experience` | 以任务卡为准；复用 `KuiklyChart` 与 `stock_detail` | BACKLOG | WorkBuddy 里程碑 3；浏览器交互、APK 构建证据 |
| WB-01 | 产品定位与两题联动评审 | WorkBuddy | 待 W0 | 本地评审目录 | 不写主仓库 | BACKLOG | 原始输出本地保存；接受项登记任务板 |
| WB-02 | Task 1 技术深挖与创新表达评审 | WorkBuddy | 待 T1-DEPTH | 本地评审目录 | 不写主仓库 | BACKLOG | 评审清单、采纳项和演示修改 |
| WB-03 | Task 2 聊天、结构化行情和交互体验评审 | WorkBuddy | 待 T2-EXPERIENCE | 本地评审目录 | 不写主仓库 | BACKLOG | 评审清单、采纳项和回归证据 |
| WB-04 | 最终 Demo、README、演示脚本和答辩叙事评审 | WorkBuddy | 待最终候选版本 | 本地评审目录 | 不写主仓库 | BACKLOG | 最终截图/视频、演示脚本和边界声明 |

## 更新规则

- 任务进入 `IN_PROGRESS` 前，必须有完整任务卡和唯一写入者。
- `HANDOFF` 至少登记 commit、测试结果、未执行项、证据路径和未 merge/release 声明。
- `VERIFIED` 只能由 Codex 根据实际命令、日志和平台证据标记。
- 用户确认后才把 `VERIFIED` 推进到 `INTEGRATED`。
