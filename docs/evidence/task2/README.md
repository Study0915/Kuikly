# 两题集成验收证据 · 2026-09-12

当前业务版本`972167b`；[正式裁决](../../REVIEWS/TASK2-TESTS.md)，[产物SHA与检查汇总](verification.json)。全部为历史Mock。初版证据可在r2候选或Git历史查阅，本目录为本批次重跑结果。

| 证据 | 核对点 |
|---|---|
| [A证据卡](comparison-a.png) | +12%、-6.09%、1.50倍、时间与风险 |
| [聊天直达详情](chat-detail-evidence.png) | 同一实体/快照/E2与计算式 |
| [缺量](missing-volume.png) | B价格仍可用，09-02缺量使量能倍数不可用 |
| [失败重试](failure-retry.png) | 原问题保留和恢复入口 |
| [320](chat-320.png)、[390](chat-390.png)、[1024](chat-1024.png) | 输入区与证据/共享图形布局 |
| [Task2 H5 32项](h5-final.json) | 主链路、异步、恢复、安全、视口 |
| [Task2会话/触摸11项](session-final.json) | 20轮、滚动、日期跳转、展开保持 |
| [Task2桌面32项](task2-desktop-final.json) | 独立非触摸浏览器 |
| [优化触摸17项](refinement-touch.json)、[优化桌面17项](refinement-desktop.json) | 错误/歧义对象不出卡；日期/数量；草稿；结论在可见范围；返回与清空 |
| [范围提示](ambiguous-comparison.png)、[草稿保留](draft-preserved.png) | 比较C与Z不会变A/B；追问B仍保留未发送的L草稿 |
| [交付门禁12项](receipt-gates.json) | 新源码配旧产物、旧UI/录像、未完成或被修改的证据均被拒绝 |
| [Task1 H5](task1-h5-final.json)、[触摸](task1-touch-final.json)、[深化](task1-deepening-final.json)、[鼠标](task1-mouse-final.json) | 59/7/23/4回归 |

原始构建日志保存在忽略的`.cache/task2-evidence`，包含本机路径故不直接进Git；源码内保留可重跑命令、结构化结果与截图。Android只有构建证据；没有将历史录像当成本轮界面。

当前所有检查绑定工作区build/UI收据；最终候选的validation目录包含精确哈希与录制收据。独立源码目录的H5 SHA相同、JVM 53项通过，Android构建通过但APK SHA不同；不声称APK逐字节可复现。该复验共用工作区依赖与Gradle构建缓存。

下一步：最新视频与提交候选见docs/submit，实际上传由用户决定。
