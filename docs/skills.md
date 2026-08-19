# Kuikly Agent Skill 清单

四个 Skill 固定在仓库 `.agents/skills/`，由 Windows Codex 作为主开发上下文读取。内容来自腾讯官方 `KuiklyUI-AI` 仓库并保留本地审计记录。

官方入口：[Tencent-TDS/KuiklyUI-AI](https://github.com/Tencent-TDS/KuiklyUI-AI)

| Skill | 用途 | Codex 项目路径 | OpenCode 备选路径 | SHA-256 |
|---|---|---|---|---|
| `kuikly-ui-framework` | 组件、Module、布局、页面和路由 | `.agents/skills/kuikly-ui-framework/` | `/home/weila/.config/opencode/skills/kuikly-ui-framework/` | `957a37c76c602eee71a152b771656803d09104391bc5fb7fbc3633c45c441771` |
| `kuikly-reactive-observer` | `observable`、列表和状态更新 | `.agents/skills/kuikly-reactive-observer/` | `/home/weila/.config/opencode/skills/kuikly-reactive-observer/` | `42a38cd7c00331be35b8117a6648df51789ebeb643ae4e8805348bf8f8ce960e` |
| `kuikly-coroutines-threading` | 异步任务、线程和 UI 回切 | `.agents/skills/kuikly-coroutines-threading/` | `/home/weila/.config/opencode/skills/kuikly-coroutines-threading/` | `53c0e979bf073f9c87dc84fe4fcc3a4e759227148c1b60df4269efe258e86357` |
| `kuikly-network-and-json` | NetworkModule、JSON 和错误处理 | `.agents/skills/kuikly-network-and-json/` | `/home/weila/.config/opencode/skills/kuikly-network-and-json/` | `032a80849f0840e9f4b4002bde41316e586c1a95c46da852ec2996d11cca6dc4` |

## 策略

- Codex 使用项目内固定快照，不自动 clone/update。
- OpenCode 仅在备选 handoff 被确认后使用 `/home/weila/.config/opencode/skills/` 的同源副本，并重新核对 hash。
- 来源 URL、抓取日期和本地 SHA 必须可追溯；各 Skill 目录的 `AUDIT.md` 是局部审计记录。
- Skill 不读取 `.env`、Token、签名或真实行情凭据。
- 新增或更新 Skill 前检查来源、版本、网络行为和缓存位置。
- `animation`、`assets`、`expand-*`、`multi-module` 和 Compose 相关 Skill 只有任务卡明确需要时才单独审计。
