# Kuikly Agent Skill 使用边界

Agent Skill 是本地开发工具，不是活动源代码或公开仓库的运行依赖。仓库只记录用途、来源和安全边界，不提交 Codex、OpenCode 或其他 Agent 的本机安装目录。

官方来源：[Tencent-TDS/KuiklyUI-AI](https://github.com/Tencent-TDS/KuiklyUI-AI)

| Skill | 用途 |
|---|---|
| `kuikly-ui-framework` | 组件、Module、布局、页面和路由 |
| `kuikly-reactive-observer` | `observable`、列表和状态更新 |
| `kuikly-coroutines-threading` | 异步任务、线程和 UI 回切 |
| `kuikly-network-and-json` | NetworkModule、JSON 和错误处理 |

## 使用策略

- Agent 在需要时从官方来源安装到各自本机的工具目录；这些目录由 `.gitignore` 排除。
- 每次 CODE handoff 应记录实际使用的来源版本或 commit，并在开始实现前核对内容。
- Skill 只提供开发指导，不能替代 `docs/REQUIREMENTS.md`、Task 总计划、代码审查或运行证据。
- Skill 不得读取、输出或提交 `.env`、Token、签名、真实行情凭据或真实用户数据。
- 新增或更新 Skill 前检查来源、许可、版本、网络行为和缓存位置。
- `animation`、`assets`、`expand-*`、`multi-module` 和 Compose 相关 Skill 仅在用户确认的 Task 总计划明确需要时审计和使用。
