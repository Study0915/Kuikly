# 行情观察 · Kuikly Finance

**从一句解读，回到可以核对的行情依据。**

2026 OpenSourceTalent / Shape with AI · **Task 1 & Task 2** · GitHub ID：**Study0915**

基于 Kuikly 的股票行情与证据问答原型。点击结论定位区间，选择交易日反查依据，展开公式核对数值；问答里的行情卡沿用同一份股票与快照，进入详情后继续核对。全部行情和回答使用确定性历史 **Mock**，不接入真实模型或实时行情。

| 行情列表 | 证据联动详情 | 问答与行情卡 |
|---|---|---|
| ![行情列表](docs/demo/home.png) | ![证据联动详情](docs/demo/detail.png) | ![证据问答](docs/demo/chat.png) |

**先看演示：** [Task 1：行情与证据核对](docs/demo/task1.webm) · [Task 2：问答到同一份详情](docs/demo/task2.webm) · [视频说明与文字稿](docs/demo/README.md)

录屏来自实际 H5 操作，说明字幕在应用画面外。手机尺寸的浏览器画面不代表原生真机部署。

## 两题完成了什么

| 任务 | 实际路径 | 操作与验收 |
|---|---|---|
| Task 1 | 12 只股票列表 → 完整报价字段 → 详情与解读 → 区间/单日核对 → 缺量及失败恢复 | [运行说明](docs/TASK1-RUN.md) · [测试报告](docs/REVIEWS/TASK1-TESTS.md) |
| Task 2 | 输入问题 → Markdown + 行情业务卡 → 精确详情 → 返回追问/比较 → 取消、重试与新会话 | [运行说明](docs/TASK2-RUN.md) · [测试报告](docs/REVIEWS/TASK2-TESTS.md) |

## 核心设计：解释可以核对

**从结论到行情，也能从交易日回到依据。** A 的“中途回落”定位到同一段 K 线与成交量，展示 `11.50 → 10.80，−6.09%`；展开计算可检查分母。点区间内的一天，则切换为该日的开高低收、成交量和相对前收涨跌，并列出已有相关依据。区间变化、单日涨跌和最大回撤不会混为一谈。

**缺失数据会改变能得出的结论。** 在缺量情景中，09-02 的成交量缺失，五日均量与量能倍数不再可用；价格依据仍可核对。组件不会跳过缺失日补凑样本，也不会继续显示此前的完整量能结论。

**两题实际共用组件。** 行情详情和问答卡共用 `EvidenceResolver`、`LensPresenter` 与 `MarketPlot`；问答卡携带股票、快照和证据身份，进入同一个 `FinanceDetail / QuoteEvidenceLens`。单股、A/B 比较和缺量回答消费同一套能力。[证据组件合同](docs/interfaces/QuoteEvidenceLens.md) · [问答接口合同](docs/interfaces/EvidenceChat.md)

**连续操作保留上下文。** 详情返回后保留消息、草稿、阅读位置和卡片展开状态；快捷追问携带原股票上下文，不覆盖未发送的草稿。取消、原位重试和新会话分别处理旧响应。会话仅保存在本次页面运行中，刷新会清空。

## 快速运行

工具全部放在项目 `.cache` 内；无需全局 Gradle，也不修改系统 PATH 或 base 环境。先准备：

- JDK 17 解压到 `.cache/jdk17`，其中包含 `bin/java.exe`。
- Node.js 解压到 `.cache/node`，其中包含 `node.exe` 和 npm。
- Android command-line tools 放到 `.cache/android-sdk/cmdline-tools/latest`，其中包含 `bin/sdkmanager.bat`。

Windows PowerShell：

```powershell
.\scripts\bootstrap-cli.ps1
.\scripts\doctor.ps1
.\scripts\verify.ps1
.\scripts\run-h5.ps1 -Production
```

预览地址由脚本输出；这是本机运行入口。首次构建需要下载固定依赖。Kuikly 2.4.0、Kotlin 2.0.21、JDK 17、Gradle Wrapper 8.0 已固定。[脚本说明](scripts/README.md)

## 课程要求与验证证据

| 课程维度 | 对应成果 |
|---|---|
| 功能完整性 40% | 两题完整路径、字段、异常恢复及明确的未知对象提示 |
| 工程设计 25% | 事实计算/状态/绘制分离，显式 Markdown 与业务卡类型，跨题复用与请求隔离 |
| AI 场景 25% | 结论、依据、风险、时效和双向核对；明确区分可计算与不可计算 |
| 体验优化 10% | 浅色移动布局、窄屏与桌面适配、触摸交互和连续阅读体验；实际效果以证据为准 |

[课程要求与验证证据](docs/submit/SCORING-AUDIT.md) · [版本与验证记录](docs/submit/DELIVERY.md)

53 项共同逻辑测试与 233 项浏览器检查通过；H5 浏览器交互、Android APK 构建和设备运行分别记录。构建输入、实际运行产物、测试与录像通过收据绑定，打包拒绝混用陈旧证据。

`shared` 包含行情、证据、会话、Markdown、页面和路由；`h5App` 与 `androidApp` 承接宿主。`finance_home` 是唯一 Pager。历史归档及空 `KuiklyChart` module 不代表活动实现能力。[当前架构](docs/ARCHITECTURE.md)

## 范围与进一步阅读

仅作技术演示，不构成投资建议。当前支持有限 Mock 意图、受限 Markdown、500 字输入和 20 轮会话；没有真实模型、行情服务、交易功能或持久化会话。Android 原生设备及键盘、iOS、HarmonyOS 未运行验证，APK 构建不等于设备运行。

- [原始题面与官方提交格式](docs/REQUIREMENTS.md) · [课程登记](docs/submit/course-entry/OpenSourceTalent/Study0915/README.md)
- [Task 1 学习报告](docs/learning/TASK1-LEARNING.md) · [Task 2 学习报告](docs/learning/TASK2-LEARNING.md)
- 开发方式：采用 AI 辅助开发，Codex 参与方案、代码实现、测试与文档整理。
- [许可证](LICENSE) · [第三方声明](THIRD_PARTY_NOTICES.md)