# Kuikly Finance 需求与完成定义

## 产品范围

项目承载 KuiklyUI Issue #1477、Shape with AI Task 1 和 Task 2。旧实现只保留在 `archive/task1-v1/`；活动版本不继承旧业务 API 或完成状态。

默认使用离线、确定性的 Mock。真实行情、真实模型、登录、交易、联网检索和付费服务不属于默认范围。

## Task 1：AI 股票行情原型

按最小空壳、行情模型与 Provider、行情列表、详情路由、基础图表、Mock AI 解读、边界与跨端体验依次交付。

完成定义：

- 行情首页、详情页、图表和 Mock AI 解读形成可操作路径。
- 图表计算与 Canvas/UI 分离，空数据、单点、等值、正负值、非有限值和命中边界可测试。
- Android Debug、Kotlin/JS、H5 production bundle、H5 交互和设备运行分别记录。
- Mock 与免责声明清晰可见；旧归档证据不作为新版验收。

## Task 2：AI 股票问答原型

Task 1 进入 `VERIFIED` 后，按最小页面、消息模型、Mock Chat、Markdown、结构化行情卡片/图表、详情承接、失败状态和跨端体验依次交付。

完成定义：

- 用户可输入问题、发送并查看多轮会话。
- Markdown、行情卡片、图表和错误使用显式内容块。
- 结构化内容只复用新版 Task 1 已验证的模型、图表和路由。
- 空输入、重复发送、失败重试、未知代码、长会话和免责声明有证据。
- Markdown 不执行原始 HTML、脚本或未确认外链。

## 共同质量门

每个 Feature 同时具备 `code`、`tests`、`evidence`、`learning` 才能进入 `VERIFIED`。文档标签统一使用 `[FACT]`、`[MOCK]`、`[VERIFIED]`、`[UNVERIFIED]`、`[LIMITATION]`、`[DECISION]`。
