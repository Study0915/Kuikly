# Kuikly 高分交付工作台

当前活动只使用 `BACKLOG`、`IN_PROGRESS`、`WAITING_USER`、`BLOCKED`、`VERIFIED` 五种状态。阶段顺序固定为：

`Task 1 PLAN → CODE → TESTS → LEARNING → Task 2 PLAN → CODE → TESTS → LEARNING → SUBMIT`

## 当前阶段

2026-09-13：用户批准联合视觉与提交改版，基线 485964c，计划 602bc15，业务 c25eeed。当前本地实现与验收见下表；最终候选封存以 DELIVERY 和包外 SHA 为准，公开入口仍待同步。

| 阶段 | Owner | 状态 | 当前产物 / 边界 |
|---|---|---|---|
| T1-PLAN | Codex / 用户确认 | VERIFIED | [TASK1-PLAN](plans/TASK1-PLAN.md) 含联合视觉范围，ADR-015 记录结构增量 |
| T1-CODE | Codex | VERIFIED | [CODE](handoffs/TASK1-CODE.md)：共享主题、紧凑行情、首屏图表、折叠计算与文档状态 |
| T1-TESTS | Codex | VERIFIED | [TESTS](REVIEWS/TASK1-TESTS.md)：53 共同逻辑、66 H5 / 7 触摸 / 33 深化 / 4 鼠标；设备未验证 |
| T1-LEARNING | Codex / 用户自测 | VERIFIED | [复盘](learning/TASK1-LEARNING.md) 已更新；个人掌握程度未代签 |
| T2-PLAN | Codex / 用户确认 | VERIFIED | [TASK2-PLAN](plans/TASK2-PLAN.md) 含联合视觉与正式登记要求 |
| T2-CODE | Codex | VERIFIED | [CODE](handoffs/TASK2-CODE.md)：紧凑证据卡、统一输入和导航、旧状态保护 |
| T2-TESTS | Codex | VERIFIED | [TESTS](REVIEWS/TASK2-TESTS.md)：39 H5 / 11 会话 / 39 桌面，17 优化检查触摸/桌面各通过 |
| T2-LEARNING | Codex / 用户自测 | VERIFIED | [复盘](learning/TASK2-LEARNING.md) 已核对；无真机和真实模型声明 |
| SUBMIT | Codex | VERIFIED | 新版 README、截图、88.44 / 92.64 秒录像、登记候选及本地 r4；封存记录见 [DELIVERY](submit/DELIVERY.md)；外部提交未执行 |

## 当前下一步

1. 本地成果、233 项浏览器检查、53 JVM 和新录像已绑定；旧 90/91 数字自评撤下，按[评分证据](submit/SCORING-AUDIT.md)讲述具体功能与边界。
2. 公开仓库已 public，但 main 仍为 24cedfe 旧空壳；feature/task2-evidence-chat 尚未公开。最终提交前必须匿名核对正确代码、README、截图、视频全部可访问。
3. 用户决定推送、合并、PR、发布和实际登记。本轮没有执行这些外部动作。9 月 14 日要求不按深夜预留时间。
4. Android 设备/键盘、iOS/HarmonyOS、真实接口与个人讲述另行验证。依赖与临时文件全部限制在工作区，本轮无新增安装。

## 2026-09-07 PLAN 记录

- 已将固定 20 日 K 线、成交量、十字光标和双向证据联动纳入正式计划；拟议包组织见 [ADR-013](decisions/ADR-013-task1-evidence-lens.md)。
- 已核验两组设计 fixture 的 40 条行情和 6 条证据计算；该结果仅为算例核验，未进行业务构建、浏览器或设备运行。
- 已创建 Task 短期分支，计划使用聚焦 commit；本轮不安装依赖、不改 base 环境，不将本机路径/配置或缓存加入 Git。
- 开始时有既存流程文档差异。本轮 workboard 同步保留其 Codex 角色修改；其他非本轮文件继续保留原状，不自动整仓提交。

## 历史审计

- `WF-001` 至 `WF-005`、`T1-000` 及旧 `T1-VERTICAL` / `T1-EXPERIENCE` / `T2-*` 卡片只保存过去的流程、空壳与研究事实，不是当前活动入口，也不授权代码修改。
- 旧 Task 1 源码与证据位于 `archive/task1-v1/`，不计入当前实现完成度。
- Issue #1477、通用图表、K 线和高级手势不定义题面 Must；最小趋势区域与 AI 标注可以作为 PLAN 中的评分候选，由用户决定是否采用。

## 更新规则

- 每个 Task 只有一份总计划、一份 CODE 实施记录、一份 TESTS 报告和一份 LEARNING 报告，不再拆活动 Feature 任务卡。
- PLAN 未获用户确认时必须停在 `WAITING_USER`；任何 Agent 不得进入 CODE。
- CODE 只有一个写入者：Codex。在用户明确重设角色并更新总计划与 ADR 前，Claude Code 与 OpenCode 不参与活动实施。
- TESTS 的 `VERIFIED` 只能由 Codex 根据实际命令、交互和平台证据标记；构建成功不能冒充运行成功。
- LEARNING 必须在 TESTS 后撰写，且不补写未经验证的成果。
- SUBMIT 只有在两题 LEARNING 均 `VERIFIED` 后启动；本地 `SUBMIT_READY` 不等于已经上传、发布或获奖。
