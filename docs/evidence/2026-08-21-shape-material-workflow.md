# WF-005 · 题面视频理解与评分管线证据

## 1. 输入材料与完整性

| 材料 | 检查结果 |
|---|---|
| 本地活动 PDF（未提交） | 10 页；SHA-256 `13EBA084662D6C2F525B3E47E6BC1ACA1878C146BA546B09209223463674AD97` |
| 本地导师讲解视频（未提交） | 34:04.955；720×1582；H.264 + AAC；SHA-256 `B0789367CD76B3A399B6FA3C90A1757C3B5A32515FD75E93FDCF9542BB180A49` |
| 用户提供的转写稿 | 约 34:05；SHA-256 `93C7A0D2FB653F880A1F75E19038A661F4A3278B1D4248B363DCF1894043CDA9` |

## 2. 音频与画面同步覆盖

- PDF 10 页全部以 PNG 渲染后逐页检查，确认 Task 总览、两个示例、40/25/25/10 评分、交付物和节奏建议。
- 视频先按 30 秒间隔覆盖完整时长，再对 01:50、05:00、05:32、06:18、06:45、07:50、08:25、09:25、10:25、15:50、16:20、17:20、18:30、21:50、22:30、24:50 抽取原分辨率关键帧核对。
- 转写稿只辅助理解音频；正式权重来自画面，导师偏好来自对应时间点的口头说明。
- 识别并保留四类未确认项：截止日期、两题是否分赛道/是否必须同时交付、组队与个人奖项、奖项分配。
- 临时目录 `tmp/shape-material-analysis` 已按要求删除，`Test-Path` 为 `False`；原 PDF、视频和用户提供的转写稿未删除、未修改。

## 3. 产物

- 人读版：`docs/research/2026-08-21-shape-brief-teacher-video-human.md`
- AI 版：`docs/research/2026-08-21-shape-brief-teacher-video-ai.md`
- 资料与灵感手册：`docs/research/2026-08-21-task1-task2-inspiration-sources.md`
- 评分管线：`docs/evaluation-pipeline.md`
- Task 总计划模板：`docs/plans/_TASK-PLAN-TEMPLATE.md`（由早期竞赛策略模板统一而来）
- 需求、工作流、Task 1/2 readiness、任务/学习/handoff 模板已同步。

资料手册包含 55 条官方或一手来源，覆盖 Kuikly/KMP、KuiklyMarkdown、状态/结构化 UI、无障碍与金融表达、真实行情 API/风险、README/演示/发布边界。上游 `main` 只作导航，当前固定 Kuikly 2.4.0 必须另做 tag/source/compile probe。

## 4. 验证命令与结果

### 文档范围

```powershell
git diff --name-only
git ls-files --others --exclude-standard
```

结果：全部变更都在 `docs/`；没有 Kotlin、Gradle、Android/H5 业务代码或 `archive/task1-v1/` 变更。

### Markdown 与 diff

```powershell
git diff --check
```

结果：通过。PowerShell 只报告 Git 的 LF→CRLF 提示，没有 whitespace error。

本地 Markdown link 解析检查结果：`LOCAL_LINKS_OK`。

### 评分一致性与资料数量

```powershell
rg -n "功能实现完整性.*40%|代码质量与工程设计.*25%|AI 场景设计能力.*25%|加分项.*10%" docs
rg -n '^\| ([0-9]+|K[0-9]+) \|' docs/research/2026-08-21-task1-task2-inspiration-sources.md
```

结果：REQUIREMENTS、评分管线与人读报告一致为 40/25/25/10；资料表行数为 55。

### 构建边界

本轮没有修改业务代码、依赖或构建配置，因此未运行 Gradle、H5 浏览器或 Android 构建。此前运行证据不能冒充本轮视频理解/文档任务的验证；本轮以材料覆盖、语义一致性、链接、diff 与范围检查作为比例适当的证据。

## 5. 外部与真实性声明

- 没有启动 Task 1/2 业务实现，没有修改归档。
- 没有接入真实行情、LLM、密钥或账号。
- 没有 push、merge、PR、tag、release 或发送外部消息。
- 外部资料记录访问日与官方链接；版本、价格、额度、许可和平台能力在真正采用前仍需当期复核。
