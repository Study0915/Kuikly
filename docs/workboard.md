# Kuikly 任务板

当前状态只在本文件登记。状态：`BACKLOG`、`READY`、`IN_PROGRESS`、`HANDOFF`、`REVIEW`、`VERIFIED`、`INTEGRATED`、`BLOCKED`。

## 活动任务

| 任务 ID | 目标 | Owner | 基线 SHA | 分支 | 状态 | 验收与证据 |
|---|---|---|---|---|---|---|
| WF-003 | Codex 主开发管线、旧 Task 1 隔离归档和新空壳部署 | Codex | `ab8675d` | `feature/codex-primary-reset` | REVIEW | [任务卡](handoffs/WF-003-TASK.md)；归档/doctor/verify/learning 通过；H5 可视受 Browser 插件阻塞 |
| T1-000 | 新版 Task 1 跨端最小可运行空壳 | Codex | `ab8675d` | `feature/codex-primary-reset` | REVIEW | [任务卡](handoffs/T1-000-TASK.md)；common/JS/H5 bundle/JVM/APK 通过；可视渲染未验证 |
| T1-VERTICAL | 行情列表到详情的首个离线垂直切片 | Codex | 待 T1-000 VERIFIED | `feature/task1-vertical` | BACKLOG | 新模型/Provider/路由/基础图表、Android/H5 证据和学习报告 |
| T1-EXPERIENCE | 图表交互、Mock AI 解读、边界状态和跨端体验 | Codex | 待 T1-VERTICAL VERIFIED | `feature/task1-experience` | BACKLOG | 四门完成门、浏览器交互和 APK 构建证据 |
| T2-000 | AI 股票问答最小可运行页面 | Codex | 待 T1-EXPERIENCE VERIFIED | `feature/task2-min-shell` | BACKLOG | Android/H5 空壳、测试、证据和学习报告 |
| T2-VERTICAL | 消息模型、Mock Chat、状态和结构化内容首个切片 | Codex | 待 T2-000 VERIFIED | `feature/task2-chat-demo` | BACKLOG | common/Android 测试、H5 编译和内容块证据 |
| T2-EXPERIENCE | Markdown、长会话、失败重试、卡片/图表承接 | Codex | 待 T2-VERTICAL VERIFIED | `feature/task2-experience` | BACKLOG | 跨端交互、回归、证据和学习报告 |
| WB-01 | 产品定位与两题联动评审 | WorkBuddy | 用户选择时 | 本地评审目录 | BACKLOG | 不写主仓库；接受项登记任务板 |
| WB-02 | 新版 Task 1 体验与创新表达评审 | WorkBuddy | 待 T1-EXPERIENCE | 本地评审目录 | BACKLOG | 评审清单与采纳项 |
| WB-03 | Task 2 聊天与结构化行情评审 | WorkBuddy | 待 T2-EXPERIENCE | 本地评审目录 | BACKLOG | 评审清单与采纳项 |
| WB-04 | 最终 Demo、README 和答辩叙事评审 | WorkBuddy | 待最终候选版本 | 本地评审目录 | BACKLOG | 最终材料与边界声明 |

## 历史记录

- `WF-001`、`WF-002` 已在 `feature/learning-workflow@ab8675d` 完成旧版协作和学习基础设施。
- 旧 Task 1 源码、历史任务语义和验收证据已迁入 `archive/task1-v1/`，不再作为活动任务或回归基线。

## 更新规则

- `IN_PROGRESS` 前必须有完整任务卡和唯一写入者。
- `HANDOFF` 必须登记 commit、测试结果、未执行项、证据和未 merge/release 声明。
- `VERIFIED` 只能由 Codex 根据实际命令和平台证据标记。
- 用户确认后才推进到 `INTEGRATED`。
