# Task WF-005 · 题面视频理解与评分对齐管线

## 目标与价值

- 目标：同步理解 PDF、导师视频画面与转写稿，把正式评分、导师强调、交付要求和未确认事项写入仓库，并在不扩张题面 Must 的前提下完善当前工作流。
- 用户价值：后续 Task 1/2 规划、实现、Reviewer 和演示都能直接按同一评分证据合同执行。
- 完成定义：人读版、AI 版、评分管线、资料手册齐全；REQUIREMENTS、workflow 和两个 readiness 对齐；临时抽帧删除；文档检查通过。

## 执行边界

- Owner：`Codex`
- 基线 commit：`0deeecd`
- 功能分支：`feature/task2-opencode-primary`
- 允许修改：`docs/` 下需求、工作流、readiness、research、handoff、evidence、learning 和 workboard 文档。
- 禁止修改：Kotlin/Gradle/Android/H5 业务代码、`archive/task1-v1/`、外部发布状态。
- 必须复用：现有 `code/tests/evidence/learning` 四门、题面优先级与 Mock/平台真实性边界。
- 明确非目标：实现 Task 1/2、接入真实 API/LLM、增加图表 Must、启动 OpenCode 编码、推送或发布。

## 验收

- 自动命令：`git diff --check`、Markdown link/路径检查、临时目录清理检查、业务代码 diff 为空。
- 人工检查：PDF 10 页；视频完整时长覆盖 + 关键帧同步；正式权重与口头校准分开；未确认项不冒充事实。
- 证据：`docs/evidence/2026-08-21-shape-material-workflow.md`。
- 风险、依赖和失败升级条件：外部资料可能变化，使用访问日期；组委会规则未确认时保持 UNCONFIRMED。

## 评分映射

- 主评分维度：`R-ENG`
- 次评分维度：`R-FUNC`、`R-AI`
- 评审可见结果：后续每个 Feature 都有评分映射、AI 载体、组件复用与演示合同。
- 直接证据：本任务新增/修改的 Markdown 与检查结果。

## 创新、组件与演示合同

- AI 信息载体：不适用，本任务定义后续填写合同。
- 通用组件与输入/输出/状态：不适用，本任务定义通用组件完成条件。
- 复用证明：同一评分层覆盖 Task 1、Task 2，且不改变四门。
- Mock / 真实边界：报告明确 Mock 可用但不得冒充真实预测。
- 演示起点、操作与可见结果：不适用，本任务定义最终视频模板。
