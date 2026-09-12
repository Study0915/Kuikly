# 评分审计 · 两题分别自评

2026-09-12。评分依据只使用REQUIREMENTS记录的老师40/25/25/10标准。下表是Codex结合当前可见结果的内部判断，不能替代老师评分。目标是约90分以上；不申领真实模型/API、Android设备、iOS或鸿蒙的加分。

| 维度 | Task1自评 | Task2自评 | 直接证据与扣分边界 |
|---|---:|---:|---|
| 功能完整40 | 38 | 38 | T1列表A–L/字段/详情/AI/异常与恢复；T2输入/记录/Markdown+卡片/详情/返回/重试/取消。全部H5实测；原生设备未验证 |
| 工程设计25 | 24 | 24 | resolver/presenter/state/plot分离；T2 typed blocks、请求票据、消息与文档键；A/B/缺量变体和两个业务场景实际消费。53共同逻辑测试；构建/UI/录像以哈希绑定；未做性能基准 |
| AI场景25 | 23 | 24 | T1双向证据与计算明细；T2带上下文追问、同窗口比较、缺量情景、精确证据承接。时效/风险可见；能力限于Mock意图 |
| 加分10 | 5 | 5 | 320/390/1024布局、桌面/触摸、位置/展开恢复、20轮边界、无外部资源。只申领体验改进，不重复把H5当多平台设备验证 |
| 合计100 | **90** | **91** | 合理波动约87–94；老师对创新和视觉体验的判断仍未知 |

## 评分证据索引

- T1：[测试裁决](../REVIEWS/TASK1-TESTS.md)，[59项主链路](../evidence/task2/task1-h5-final.json)、[23项深化](../evidence/task2/task1-deepening-final.json)、7触摸/4鼠标；演示展示“依据→图→日期→计算→缺量→B复用”。
- T2：[测试裁决](../REVIEWS/TASK2-TESTS.md)，[32项主链路](../evidence/task2/h5-final.json)、[11项长会话/触摸](../evidence/task2/session-final.json)、独立桌面32项；[新增17项](../evidence/task2/refinement-touch.json)在触摸与桌面各通过，覆盖对象忠实性、草稿保护和可见阅读位置。
- 工程：[QuoteEvidenceLens合同](../interfaces/QuoteEvidenceLens.md)、[EvidenceChat合同](../interfaces/EvidenceChat.md)，源码`market/insight/chat/ui/navigation`与`commonTest`。
- 产物：[哈希与验证摘要](../evidence/task2/verification.json)，两题学习报告与本地录像/候选清单。

## 本轮优化后的判断

持续审查发现并修复了错股比较、日期/数量误识别、草稿丢失和长回答定位问题，补充了源码、产物、验收与视频一致性门禁。这些修复提高了原自评的可信度；保持T1约90、T2约91的保守自评，不因测试数量增加自动抬分。题面闭环和组件复用已有实际运行证据，最终仍由老师判断创新和呈现质量。

保留的后续改善：实际Android设备与键盘、个人讲述练习、真实服务接入另行确认、独立机器全新环境复验。本轮不把这些写成完成。下一步：预览本地候选并由用户决定外部提交。
