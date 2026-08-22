# Kuikly 高分交付工作流

## 1. 唯一活动管线

```text
Task 1 PLAN → CODE → TESTS → LEARNING
    → Task 2 PLAN → CODE → TESTS → LEARNING
    → SUBMIT
```

这条顺序是活动唯一入口。Task 1 未完成 LEARNING 时不启动 Task 2 PLAN；两题未完成 LEARNING 时不进入 SUBMIT。

不再为活动实现创建 `T1-VERTICAL`、`T1-EXPERIENCE`、`T2-000` 等 Feature 任务卡。旧卡片保留为历史审计，不授权当前代码修改，也不代表当前完成度。

## 2. 角色

| 阶段 | 默认 Owner | 完成标准 |
|---|---|---|
| PLAN | Windows Codex | 详细 Task 总计划完成 40/25/25/10 映射并获用户确认 |
| CODE | WSL OpenCode | 严格按确认计划实现并给出聚焦 commit/handoff |
| TESTS | Windows Codex | Windows diff、自动测试、H5/Android、交互与评分证据验收通过 |
| LEARNING | Windows Codex | 形成通俗、简历可写、面试可答的项目报告 |
| SUBMIT | Windows Codex 准备，用户决定外部操作 | 两题代码、文档、视频和评分证据组成可提交候选包 |

OpenCode 只写 WSL Linux 原生克隆 `~/code/Kuikly`，不写 Windows 挂载工作树。Codex 代码备选接管必须先停止 OpenCode 写入并获得用户确认。同一 Task 同一时间只有一个代码写入者。

## 3. 阶段状态

每个阶段只使用以下状态：

- `BACKLOG`：前置阶段未完成；
- `IN_PROGRESS`：Owner 正在执行；
- `WAITING_USER`：等待用户选择创新点、确认计划或批准外部操作；
- `BLOCKED`：存在带证据的环境、合同或实现阻塞；
- `VERIFIED`：该阶段的退出条件全部满足。

`VERIFIED` 必须有直接证据。构建、浏览器交互、APK、设备运行和外部提交互不替代。

## 4. PLAN：Codex 详细规划

每题只维护一份总计划：

- Task 1：`docs/plans/TASK1-PLAN.md`
- Task 2：`docs/plans/TASK2-PLAN.md`

PLAN 必须包含：

1. 题面 Must、明确非目标和归档/Issue 隔离边界；
2. 40/25/25/10 评分矩阵，每项对应可见结果和直接证据；
3. 创新候选表：候选方案、预期评分收益、实现成本、风险、删减线和 Codex 推荐；
4. 用户最终选择的核心组件与 AI 信息载体；
5. 文件落点、模块职责、接口、数据、状态、路由、错误恢复和跨端策略；
6. CODE 的有序实现清单、每项退出条件和回滚点；
7. TESTS 矩阵：命令、平台、交互、期望结果和失败判据；
8. LEARNING 的简历亮点和面试主题；
9. SUBMIT 所需 README、演示视频与评分证据落点；
10. 风险、依赖、版本探针、许可、安全、Mock/真实边界。

### 创新未确定时的约束

- PLAN 可以列出 2-4 个候选与推荐，不能把 Codex 的推荐冒充用户决定。
- 核心组件与 AI 载体未获用户确认时，PLAN 状态为 `WAITING_USER`，CODE 不得开始。
- 用户确认后，把选择、拒绝理由和删减线写回同一总计划；不另建 Feature 任务卡。

### PLAN 退出条件

- 所有题面 Must 有实现和测试落点；
- 40/25/25/10 四项均有目标和证据合同；
- 核心组件、AI 载体与删减线已由用户确认；
- OpenCode handoff 所需的基线、分支、允许路径、首条提示词和验收命令齐全。

## 5. 评分映射约束

每份 Task 总计划必须包含：

| 维度 | 权重 | 计划必须回答 |
|---|---:|---|
| R-FUNC | 40% | 哪些页面、链路、状态和题面字段直接拿分？如何证明完整？ |
| R-ENG | 25% | 核心组件是什么？接口、状态和至少两个复用场景如何证明？ |
| R-AI | 25% | AI 由什么载体承载？比静态文本多提供什么理解或交互价值？ |
| R-BONUS | 10% | 核心三项稳定后选择哪项加分？若时间不足首先删除什么？ |

硬约束：

- 评分映射是计划合同，不是事后包装；
- “通用组件”必须有聚焦接口和至少两个 caller、场景或数据变体；
- AI 载体至少呈现结论、理由、风险/不确定性、数据时效和一种有意义交互；
- Bonus 不能阻塞 R-FUNC/R-ENG/R-AI；
- 没有运行、测试或视频证据时不得标记为得分已验证。

详细评分定义只在 `docs/evaluation-pipeline.md` 维护。

## 6. CODE：OpenCode 实现

CODE 只消费用户确认后的 Task 总计划，不创建新的活动任务卡。

Codex handoff 必须提供：

- Task、计划版本、基线 SHA、`feature/<topic>` 分支；
- WSL 目录 `~/code/Kuikly`、启动命令和首条提示词；
- 允许/禁止路径、实现清单、公开接口和明确非目标；
- 验收命令、评分证据要求、失败升级条件；
- 未 push、未 merge、未 release、未外发声明。

OpenCode 必须：

- 按计划顺序实现，范围变化先回到 PLAN；
- 使用聚焦 commit，记录实际文件、命令结果、未执行项和风险；
- 不把编译成功写成浏览器或设备通过；
- 不读取或提交密钥、签名、本机配置或真实用户数据。

CODE 完成后只进入 TESTS，不自行裁决 `VERIFIED`。

分支命名、交接运输和清理规则见 `docs/GIT-WORKFLOW.md`。

## 7. TESTS：Codex 验收

TESTS 同时承担原 `tests + evidence` 职责。Codex 在 Windows 主仓库完成：

1. diff 与计划合同审查；
2. 相关单元、状态、路由和安全测试；
3. Kotlin/JS、H5 production bundle、Android JVM 和 Debug APK；
4. H5 真实浏览器主链路；
5. Android 设备/模拟器状态单独记录；
6. 40/25/25/10 声明—证据矩阵；
7. Mock、真实 API、平台与投资风险边界检查。

失败时记录复现命令、日志、影响和解除条件，阶段回到 CODE。只有全部必需项通过或被明确标为未验证且不阻塞题面时，TESTS 才能 `VERIFIED`。

## 8. LEARNING：简历与面试级项目报告

LEARNING 不再要求学习逐个 Kuikly/Kotlin API。报告服务两个目标：

1. 用户能把项目写进简历；
2. 面试官追问时，用户能用自己的话解释目标、架构、难点、取舍和结果。

每题报告控制在通俗、可复述的深度，至少包含：

- 一句话项目介绍和用户价值；
- 用户本人可声明的职责；
- 完整体验链路；
- 高层架构与核心组件；
- 3-5 个关键难点、选择和结果；
- 40/25/25/10 对应亮点；
- 真实验证结果与未验证边界；
- 2-3 条可直接改写进简历的 bullet；
- 8-12 个高概率面试问题及简洁答案；
- 已知限制和下一步。

报告避免逐文件流水账、逐 API 教程和无法由用户解释的术语堆砌。模板见 `docs/learning/_TEMPLATE.md`。

## 9. SUBMIT：两题统一提交准备

SUBMIT 只有在 Task 1、Task 2 的 LEARNING 都 `VERIFIED` 后启动。Codex 按 `docs/submit/_SUBMIT-CHECKLIST.md` 准备：

- 可从干净环境启动的源代码；
- 面向评审的 README 与架构/亮点说明；
- Task 1、Task 2 完整演示视频和包含画面信息的文字稿；
- 40/25/25/10 最终评分证据矩阵；
- 构建、浏览器、APK、设备和未验证平台声明；
- Mock/真实数据、AI、投资风险、许可和隐私边界；
- 提交文件清单、版本、hash 和回滚备份；
- 内部路径、账号、Token、通知和真实用户数据清理结果。

Codex 可以生成本地候选包和报告，但实际 push、PR、tag、release、报名提交和外部沟通由用户确认。

## 10. 当前下一步

当前只进入 `T1-PLAN`。Codex 先给出创新候选、评分收益、成本与推荐；用户确认核心组件和 AI 载体后，Codex 完成 Task 1 总计划并生成 OpenCode handoff。此前不启动业务 CODE。
