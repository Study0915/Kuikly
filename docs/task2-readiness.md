# Task 2 阶段准备合同

## 题面依据与启动条件

- Task 2 的 Must 与完成定义只以 `docs/REQUIREMENTS.md` 为准；Issue #1477 不是题面。
- 只有 T1-LEARNING `VERIFIED` 后才能启动 T2-PLAN；这是项目顺序，不是题面附加要求。
- Task 2 在现有 shared module 增加 `finance_chat` 能力，不新建第二套应用，不引用 `archive/task1-v1/`。
- 固定阶段为 `T2-PLAN → T2-CODE → T2-TESTS → T2-LEARNING`，不再拆分 Feature 任务卡。

## 阶段责任

| 阶段 | Owner | 入口 | 唯一产物 | 完成门 |
|---|---|---|---|---|
| PLAN | Codex | T1 已验证能力、题面、评分 | `docs/plans/TASK2-PLAN.md` | 独立完成 40/25/25/10 映射，用户选定创新、business block、AI 载体和删减线 |
| CODE | OpenCode | 已确认的 T2-PLAN | CODE handoff 与聚焦 commit | 在 Linux 原生克隆实现；未静默偏离计划 |
| TESTS | Codex | CODE handoff | T2 TESTS 报告及证据 | Windows 构建、交互、详情承接、评分证据和平台边界全部裁决 |
| LEARNING | Codex | T2-TESTS VERIFIED | 简历与面试版复盘 | 能讲清问答主线、结构化内容、复用、测试和取舍 |

OpenCode 不写 Windows 挂载工作树，不推送、合并、创建 PR 或发布。连续出现相同可复现阻塞时先置 `BLOCKED`；只有用户批准后，Codex 才能在 Windows 分支作为 CODE fallback 接管。

## PLAN 强制约束

- Task 2 必须拥有自己的 40/25/25/10 映射；Task 1 的得分证据不能自动替代。
- Codex 提供 2–4 个创新候选，比较用户价值、核心组件、AI 载体、20 秒可见效果、工作量、风险和删减代价；用户选择前不得 CODE。
- 计划必须冻结至少一种非 Markdown 业务内容、详情承接、状态模型、Markdown 方案、测试矩阵、演示主线和降级线。
- 最小趋势区域与 AI 标注可以作为高杠杆候选，但不默认扩大为通用图表、K 线或高级手势。

## 题面能力合同

### 聊天主页面

- 支持输入问题、发送消息和展示会话记录。
- 非空问题显示用户消息与助手响应状态；空输入、重复发送、失败和 retry 采用明确、可测试的策略。

### 返回内容

- 支持 Markdown，并至少实现一种显式、可测试的股票、指数或行情业务内容。
- 结构化业务内容使用 typed model，不从 Markdown 文本反解析。
- 具体 business block 由用户在 PLAN 中选择，不要求同时实现所有卡片或图表。
- 最终候选的 business block 应提供展开、比较、追问、详情承接、信号定位或其他至少一种有意义交互，并覆盖适用的 loading/failed/unknown 状态。

### 详情承接

- 至少一个聊天结果能打开正确的股票或指数详情。
- 详情展示基础行情、走势区域，并提供摘要或 AI 解读中的至少一种。
- 结构化结果携带可校验实体标识；未知实体不跳转、不伪造行情。

## 工程与高分约束

- 首版使用离线确定性 Mock Chat；真实模型、实时行情、联网检索、语音、登录和云同步默认不在范围。
- Markdown 与业务 block 采用显式内容模型隔离；Markdown 不执行 raw HTML、script 或未经确认的远程资源。
- KuiklyMarkdown 只是候选依赖。选用前必须按当前固定版本实际探测 Android、Kotlin/JS 和 H5；不兼容时使用受测的 commonMain 受限 renderer。
- 被称为通用 renderer、卡片或趋势组件的能力必须在至少两个真实 caller、block 或数据变体中复用。
- 股票内容展示“仅作技术演示，不构成投资建议”，且不把 Mock 响应描述为真实模型推理。

## TESTS 完成门

- Unit：输入、发送、会话、确定性响应、选定 business block、未知实体、失败/retry、免责声明和 Markdown 安全。
- Route：聊天结果打开正确详情；未知实体不跳转。
- Detail：基础行情、走势区域，以及摘要或 AI 解读至少一种可见。
- UI：消息滚动、键盘不遮挡输入、状态清晰，结构化内容不只依赖颜色传达语义。
- Build：执行 `scripts\doctor.ps1`、`scripts\verify.ps1` 及 Task 2 专项测试；H5 production bundle 与浏览器交互分别留证。
- Score：40/25/25/10 每个申领项均有直接证据；未验证平台或 API 不计 Bonus。

## LEARNING 与后续

按 `docs/learning/_TEMPLATE.md` 输出通俗复盘，无需学习底层 API 细节。T2-LEARNING `VERIFIED` 后进入 SUBMIT，由 `docs/submit/_SUBMIT-CHECKLIST.md` 统一构建提交候选。
