# Git 分支与公开仓库工作流

## 1. 最小分支模型

仓库只保留一个长期分支：`main`。PLAN、CODE、TESTS 和 LEARNING 是交付阶段，不分别创建长期分支。

每个阶段中的工作使用短期分支：

| 分支 | 用途 | 结束条件 |
|---|---|---|
| `main` | 稳定、可公开、可供评审查看的基线 | 长期保留 |
| `feature/competition-workflow` | 整理公开文档、规则和活动管线 | 用户确认后合并回 `main` |
| `feature/task1-<topic>` | Task 1 经确认计划的实现与修复 | TESTS、LEARNING 完成并由用户决定合并 |
| `feature/task2-<topic>` | Task 2 经确认计划的实现与修复 | TESTS、LEARNING 完成并由用户决定合并 |
| `bugfix/<topic>` | 已合并基线上的独立缺陷修复 | 验证后由用户决定合并 |

不要为 PLAN、TESTS、LEARNING、证据或 Agent 分别建立常驻分支。它们是同一 Task 交付链上的提交和文档。

## 2. 当前活动顺序

```text
main
  └─ feature/competition-workflow
       └─ 用户确认并合并
            └─ feature/task1-<topic>
                 PLAN → CODE → TESTS → LEARNING
                    └─ 用户确认并合并
                         └─ feature/task2-<topic>
                              PLAN → CODE → TESTS → LEARNING → SUBMIT
```

Task 1 PLAN 未获用户确认前，不创建业务 CODE 提交；Task 1 LEARNING 未完成前，不启动 Task 2。

## 3. Windows 与 WSL 交接

- Windows Codex 在 Windows 仓库完成 PLAN、TESTS、LEARNING 和 SUBMIT 准备。
- WSL OpenCode 只在 Linux 原生克隆 `~/code/Kuikly` 的同名 Task 分支完成 CODE。
- CODE handoff 必须固定基线 SHA、分支名、允许路径、验收命令和运输方式。
- 提交从 WSL 返回 Windows 时，只能采用用户批准的远端 push/fetch，或本地 `git bundle`/patch；不得让两个 Agent 同时写同一工作树。
- Agent 不自动 push、merge、创建 PR、删除远端分支、tag 或 release。

## 4. 提交与合并

使用聚焦提交和 Angular Convention，例如：

```text
docs(workflow): publish competition delivery pipeline
feat(task1): add reusable quote component
test(task1): verify h5 quote interactions
docs(task1): add learning and scoring evidence
```

合并前至少检查：

1. `git diff --check` 无格式错误；
2. `git status --short` 中没有密钥、本机配置、缓存或原始私有材料；
3. 文档不包含个人绝对路径、账号、Token 或真实用户数据；
4. 对应阶段在 `docs/workboard.md` 有真实状态和证据；
5. 由用户决定 merge、push、PR 和对外发布。

## 5. 历史分支清理

本地分支满足以下全部条件后可以删除：

- 工作已经进入 `main` 或当前可达的保留分支；
- 没有未提交文件只存在于该工作树；
- 关键决策和证据已进入仓库文档；
- 用户确认不再需要该分支名作为书签。

优先删除本地分支，远端历史分支等公开基线稳定后再由用户单独决定。删除分支不会自动删除已被保留分支引用的 commit。

## 6. 公开内容边界

应提交：源码、构建脚本、`AGENTS.md`、`README.md`、题面解释、Task 总计划、验收、证据、学习报告和提交清单。

应忽略：`.agents/`、`.codex/`、`.opencode/`、`.env*`、密钥、签名、本机 SDK/缓存、原始含身份信息的材料和临时分析文件。`docs/`、`docs/plans/`、`docs/submit/` 不得整体忽略。
