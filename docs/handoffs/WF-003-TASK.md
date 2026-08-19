# Task WF-003 · Codex 主开发管线与旧 Task 1 隔离归档

## 目标与价值

- 目标：把 Codex 设为 Task 1/2 默认规划与代码 Owner，将 OpenCode 改为经确认才启用的备选，并隔离旧 Task 1。
- 用户价值：减少跨工作树交接，使规划、实现和 Windows Android/H5 验收形成单一闭环。
- 完成定义：规则单一事实来源更新；旧源码/证据受版本控制且退出活动构建；新空壳和四门材料就绪。

## 执行边界

- Owner：Codex
- 基线 commit：`ab8675d3fa377486d492c39f320745314ec71467`
- 功能分支：`feature/codex-primary-reset`
- 允许修改：工作区/仓库 `AGENTS.md`、README/CONTRIBUTING/CHANGELOG、`docs/`、`scripts/README.md`、`archive/task1-v1/`、活动四模块。
- 禁止修改：Gradle Wrapper/版本清单、密钥/本机配置、远程分支、main、PR、release。
- 必须复用：项目内 JDK/Node/Android SDK/Gradle 缓存、四门状态机、固定 Skill 快照。
- 明确非目标：实现新版行情/图表/AI 业务、真实服务、ADB/设备运行、Task 2 代码。

## 验收

- 自动命令：`git diff --check`、`scripts\doctor.ps1`、`scripts\verify.ps1`、归档依赖/敏感文件审计。
- 人工交互：H5 浏览器确认 `finance_home` 空壳可见。
- 证据：`docs/evidence/2026-08-19-codex-primary-reset.md`、`docs/learning/T1-000.md`。
- 风险和依赖：Browser 插件受信任路径错误会阻断可视验收，但不能被 HTTP 200 替代。
- 失败升级条件：同一仓库内失败连续复现两次，或 Browser/平台环境缺失时进入 `BLOCKED`/`REVIEW` 并记录解除条件。
