# Kuikly 高分交付工作台

当前活动只使用 `BACKLOG`、`IN_PROGRESS`、`WAITING_USER`、`BLOCKED`、`VERIFIED` 五种状态。阶段顺序固定为：

`Task 1 PLAN → CODE → TESTS → LEARNING → Task 2 PLAN → CODE → TESTS → LEARNING → SUBMIT`

## 当前阶段

2026-09-13：用户已批准联合视觉与提交改版，基线485964c，见两题总计划与ADR-015。本轮CODE/TESTS/LEARNING/SUBMIT为IN_PROGRESS；下表VERIFIED记录仅代表此前r3基线，不代表新视觉已通过。当前下一步：共享主题→首页→详情→问答→宿主→全部回归/视觉→新录像/README/候选。

| 阶段 | Owner | 前置条件 | 状态 | 唯一产物 / 完成条件 |
|---|---|---|---|---|
| T1-PLAN | Codex | 用户审阅计划提交后明确“开始实施” | VERIFIED | [TASK1-PLAN v1.0](plans/TASK1-PLAN.md) 已于 2026-09-07 获确认；确认时提交 `0060711` |
| T1-CODE | Codex | T1-PLAN VERIFIED | VERIFIED | 2026-09-08 D1–D4 深化完成；会话隔离、上下文恢复、逐日导航、计算明细；最终业务 e6bb182 |
| T1-TESTS | Codex | T1-CODE 实施记录完整 | VERIFIED | [TESTS](REVIEWS/TASK1-TESTS.md)：32 单测、59 原有 H5、23 深化、7 触摸、4 桌面检查通过；设备 UNVERIFIED |
| T1-LEARNING | Codex / 用户自测 | T1-TESTS VERIFIED | VERIFIED | 2026-09-12核对[复盘材料](learning/TASK1-LEARNING.md)，个人讲述能力仍未验证；用户授权自主推进，见ADR-014 |
| T2-PLAN | Codex | T1-LEARNING VERIFIED | VERIFIED | [TASK2-PLAN](plans/TASK2-PLAN.md) v1.1，延续自主决策授权；初版计划25514f5，优化计划随972167b保存 |
| T2-CODE | Codex | T2-PLAN VERIFIED | VERIFIED | [CODE记录](handoffs/TASK2-CODE.md)，优化业务972167b；比较对象、草稿和阅读位置已修复 |
| T2-TESTS | Codex | T2-CODE 实施记录完整 | VERIFIED | [TESTS](REVIEWS/TASK2-TESTS.md)：53共同逻辑、32H5、11会话/触摸、32桌面；新增17项触摸/桌面各通过；Task1全93项通过 |
| T2-LEARNING | Codex | T2-TESTS VERIFIED | VERIFIED | [复盘](learning/TASK2-LEARNING.md)材料已核对；个人掌握程度未代签 |
| SUBMIT | Codex / 用户 | T1、T2 LEARNING 均 VERIFIED | VERIFIED | r3本地候选174项文件/ZIP哈希、文档链接、隐私扫描与导出预览通过；构建/UI/录像收据一致；外部提交未执行 |

## 当前下一步

1. 两题持续优化、联合回归、学习材料与最新视频已完成；12项版本门禁反例通过，源码/运行产物/测试/录像绑定。[评分审计](submit/SCORING-AUDIT.md)保守自评约90/91，实际老师评分未知。
2. 本地候选已核验；用户预览视频与Demo，练习讲述并决定外部提交。个人讲述能力另记未验证。
3. Android 设备运行单独待验证：先证明 ADB/设备配置落点满足工作区约束，不把 APK 构建申报为设备运行。
4. 分支 `feature/task2-evidence-chat`，基于既有 Task 1 HEAD 创建；聚焦本地提交，初始差异保留，没有 push、PR、merge、tag 或外发。

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
