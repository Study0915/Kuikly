# 评分审计 · 两题分别自评

2026-09-12。评分依据只使用REQUIREMENTS记录的老师40/25/25/10标准。下表是Codex结合当前可见结果的内部判断，不能替代老师评分。目标是约90分以上；不申领真实模型/API、Android设备、iOS或鸿蒙的加分。

| 维度 | Task1自评 | Task2自评 | 直接证据与扣分边界 |
|---|---:|---:|---|
| 功能完整40 | 38 | 38 | T1列表A–L/字段/详情/AI/异常与恢复；T2输入/记录/Markdown+卡片/详情/返回/重试/取消。全部H5实测；原生设备未验证 |
| 工程设计25 | 24 | 24 | resolver/presenter/state/plot分离；T2 typed blocks、请求票据、消息与文档键；A/B/缺量变体和两个业务场景实际消费。49共同逻辑测试；未做性能基准 |
| AI场景25 | 23 | 24 | T1双向证据与计算明细；T2带上下文追问、同窗口比较、缺量情景、精确证据承接。时效/风险可见；能力限于Mock意图 |
| 加分10 | 5 | 5 | 320/390/1024布局、桌面/触摸、位置/展开恢复、20轮边界、无外部资源。只申领体验改进，不重复把H5当多平台设备验证 |
| 合计100 | **90** | **91** | 合理波动约87–94；老师对创新和视觉体验的判断仍未知 |

## 评分证据索引

- T1：[测试裁决](../REVIEWS/TASK1-TESTS.md)，[59项主链路](../evidence/task2/task1-h5-final.json)、[23项深化](../evidence/task2/task1-deepening-final.json)、7触摸/4鼠标；演示展示“依据→图→日期→计算→缺量→B复用”。
- T2：[测试裁决](../REVIEWS/TASK2-TESTS.md)，[32项主链路](../evidence/task2/h5-final.json)、[11项长会话/触摸](../evidence/task2/session-final.json)、独立桌面32项；演示展示“输入→Markdown+卡片→精确详情→返回→比较→追问→缺量→失败重试”。
- 工程：[QuoteEvidenceLens合同](../interfaces/QuoteEvidenceLens.md)、[EvidenceChat合同](../interfaces/EvidenceChat.md)，源码`market/insight/chat/ui/navigation`与`commonTest`。
- 产物：[哈希与验证摘要](../evidence/task2/verification.json)，两题学习报告与本地录像/候选清单。

## 为什么目前可以结束本轮完善

题面闭环已具备运行证据；工程与AI载体两项区分度有可演示的实体、快照、证据和恢复语义。继续堆真实模型、复杂手势或更多平台会引入新依赖和无法验证的声明，当前优先级低于已经完成的核心能力与交付质量。

保留的后续改善：实际Android设备与键盘、个人讲述练习、真实服务接入另行确认、独立机器全新环境复验。本轮不把这些写成完成。下一步：预览本地候选并由用户决定外部提交。
