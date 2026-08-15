# Kuikly Agent Skill 清单

本清单是 Codex 项目作用域与 WSL OpenCode 用户级 Skill 的共同版本记录。Skill 内容来自腾讯官方 `KuiklyUI-AI` 仓库；本项目只固定与当前 Task 1/Task 2 直接相关的四个能力，不整包安装。

官方入口：[Tencent-TDS/KuiklyUI-AI](https://github.com/Tencent-TDS/KuiklyUI-AI)

| Skill | 用途 | 项目路径 | WSL OpenCode 路径 | SHA-256 |
|---|---|---|---|---|
| `kuikly-ui-framework` | 组件、Module、布局、页面和路由 | `.agents/skills/kuikly-ui-framework/` | `/home/weila/.config/opencode/skills/kuikly-ui-framework/` | `db8c5ed31dffa4fa2a04c2216ddaa816a7391081f1ca136a6f488d8f061f1a19` |
| `kuikly-reactive-observer` | `observable`、`observableList`、`vfor`、`vif` 和更新排查 | `.agents/skills/kuikly-reactive-observer/` | `/home/weila/.config/opencode/skills/kuikly-reactive-observer/` | `42a38cd7c00331be35b8117a6648df51789ebeb643ae4e8805348bf8f8ce960e` |
| `kuikly-coroutines-threading` | Kuikly 线程、异步任务和 UI 回切 | `.agents/skills/kuikly-coroutines-threading/` | `/home/weila/.config/opencode/skills/kuikly-coroutines-threading/` | `53c0e979bf073f9c87dc84fe4fcc3a4e759227148c1b60df4269efe258e86357` |
| `kuikly-network-and-json` | `NetworkModule`、`JSONObject`、`JSONArray` 和错误处理 | `.agents/skills/kuikly-network-and-json/` | `/home/weila/.config/opencode/skills/kuikly-network-and-json/` | `032a80849f0840e9f4b4002bde41316e586c1a95c46da852ec2996d11cca6dc4` |

## 固定策略

- 来源 URL、抓取日期和本地 SHA 必须可追溯；各 Skill 目录中的 `AUDIT.md` 是局部审计记录。
- Skill 不读取 `.env`、Token、签名文件或真实行情凭据。
- Skill 不自动 clone/update KuiklyUI；更新由 Codex 重新审计后提交新版本。
- `animation`、`assets`、`expand-*`、`multi-module` 和 Compose 相关 Skill 暂不安装，只有任务卡明确需要时才单独审计。
