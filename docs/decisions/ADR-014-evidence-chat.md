# ADR-014 · 证据问答与自主交付授权

日期：2026-09-12。状态：Accepted。

用户本轮要求持续完成两题至约 90 分以上，拿不准的选项由 Codex 决定；环境只能安装在工作区，不乱删改无关文件。这是两题产品取舍与实施授权，覆盖此前逐选项等待确认门，不代表外部发布授权。

LEARNING 的代理验收对象是完整且真实的学习材料；个人讲述能力另记未验证，不代签。先核对 Task 1 材料，再启动 Task 2 PLAN、CODE、TESTS、LEARNING，最后准备本地提交候选。

Task 2 选择显式 Markdown + EvidenceCard。新增 shared 内 chat 包（模型、会话、受限 Markdown、确定性 provider）与 UI renderer；不新增 Gradle module 或依赖。沿用 finance_home 唯一 Pager，新增内部 Chat 路由与 Detail 返回来源。聊天卡复用 EvidenceResolver/LensPresenter/MarketPlot，详情复用 FinanceDetail/QuoteEvidenceLens；每条回答持有独立快照。

选择理由：证据直达行情比生成更长文本更符合导师评分；A/B 比较证明相同卡片复用与隔离。放弃真实模型、在线行情、语音、云端存储和额外平台依赖，优先稳定恢复与可审计计算。

会话只保存在 Pager 生命周期，刷新明确重建空会话；问题不放 URL、磁盘或远程服务。H5 history 只保存路由/实体/快照/证据。旧异步请求用代次拒绝，取消与重试可测试。Markdown 仅支持标题、段落、列表、引用、粗体与行内代码；链接仅显示标签，不加载远程资源、不执行 HTML。

在当前 Task 1 分支上创建依赖式 feature/task2-evidence-chat 分支，不合并 main。保留初始未提交差异，不整仓 add。回滚以聚焦提交为单位，不删除历史或缓存。
