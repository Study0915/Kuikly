# Reviewer 结果归档

每个 Feature 完成后，由独立 Codex session 以固定基线和当前 diff 只读审查。Reviewer 不读取原始设计意图，不直接修代码。

原始输出保存在仓库外的评审目录；Codex 复现并经用户接受后，将去重结论写入 `docs/REVIEWS/<task-id>.md`。

## 检查维度

- 任务边界与允许路径；
- Kuikly API 和官方 Skill 使用；
- 状态管理、生命周期和重复重建；
- Android/H5 跨端一致性；
- 重复代码、崩溃风险和错误状态；
- 自动测试、人工交互和证据真实性；
- 学习报告是否能解释调用链和设计原因。

## 归档模板

```markdown
# Review <Task ID>

- 基线 SHA：
- 被审查 commit：
- Reviewer session：
- 发现的问题：
- 已复现问题：
- 采纳的修改：
- 拒绝的建议及原因：
- 回归证据：
- 状态：OPEN / ACCEPTED / RESOLVED
```
