# 两题集成验收证据 · 2026-09-13

当前业务 c25eeed；[测试裁决](../../REVIEWS/TASK2-TESTS.md)、[产物与检查汇总](verification.json)、[视觉核对](visual-review.json)。全部历史 Mock；本目录为当前产物重跑结果，r3 候选与 Git 历史保留旧版。

| 证据 | 核对点 |
|---|---|
| [三页与新视频](../../demo/README.md) | 完整首屏双图、紧凑列表与问答卡、计算和缺量 |
| [A/B 比较](comparison-a.png)、[进入详情](chat-detail-evidence.png) | 相同窗口、各自数值，精确股票/快照/依据 |
| [缺量](missing-volume.png)、[失败重试](failure-retry.png) | 不能计算的量能与原位恢复 |
| [320](chat-320.png)、[390](chat-390.png)、[430](chat-430.png)、[1024](chat-1024.png) | 页面容器及输入区 |
| [20 轮](long-session.png)、[短视口](long-session-short.png)、[短面板](short-settings.png) | 长记录与输入仍可用，设置最后选项可到达 |
| [Task 1 H5](task1-h5-final.json)、[触摸](task1-touch-final.json)、[深化](task1-deepening-final.json)、[鼠标](task1-mouse-final.json) | 66 / 7 / 33 / 4 项，共 110 项 |
| [Task 2 H5](h5-final.json)、[会话/触摸](session-final.json)、[桌面](task2-desktop-final.json) | 39 / 11 / 39 项 |
| [优化触摸](refinement-touch.json)、[优化桌面](refinement-desktop.json) | 各 17 项，对象、草稿、可见位置、离页恢复 |
| [范围提示](ambiguous-comparison.png)、[草稿保留](draft-preserved.png) | 不替换未知股票，不覆盖未发送草稿 |
| [交付门禁](receipt-gates.json) | 12 项接纳/拒绝案例，旧产物、陈旧或修改证据会被拒绝 |

共同逻辑 53 项在 JVM 执行；浏览器 233 项含独立触摸/桌面重复场景，不宣称 233 个独立功能。构建日志包含本机路径，仅保留在工作区忽略目录。构建、UI 和录制收据绑定源码输入、实际服务 JS、测试和录像。

本轮导出检查核对源码指纹与 JS/APK 哈希，未重新宣称独立目录构建；r3 的 2026-09-12 共用缓存复验属于历史证据。Android 只有构建证据，真机/原生键盘/iOS/HarmonyOS 未验证。[版本与验证记录](../../submit/DELIVERY.md)。
