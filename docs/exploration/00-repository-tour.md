# RECON-001 · 当前仓库只读考古入口

> 本文是 Codex 的只读分析产物，不代表 Task 2 已实现，也不授权结构重构。

## 分析目标

在任何 Task 2 编码前，核对官方 Kuikly Demo、当前模块、页面注册、路由、状态管理、List/Text/Input、Provider、网络边界和现有验收证据。

## 当前模块快照

- `KuiklyChart/`：commonMain 中的图表模型、坐标/窗口计算、DSL 和 Canvas 渲染；计算层有 commonTest。
- `shared/`：股票模型、Mock Provider、行情首页和个股详情页。
- `androidApp/`：Android Kuikly 宿主和 Debug APK 构建入口。
- `h5App/`：H5 宿主、RouterModule 映射和 production bundle 入口。

## 已知事实与待补证据

- [FACT] 当前页面注册包含 `finance_home` 和 `stock_detail`。
- [FACT] Task 1 默认使用固定 Mock 行情和 Mock AI 分析。
- [FACT] 当前共享 UI 已使用 `List`、`Text`、`View` 和 `observable`。
- [FACT] 当前没有 Task 2 聊天页面、Input 状态模型或真实 NetworkModule 接入。
- [UNVERIFIED] Android 真机/模拟器运行仍需对应设备证据。
- [UNVERIFIED] 某些图表拖动、缩放和平移行为需要专门运行时覆盖。

## 后续补充

完成官方 Demo 对照后，在本文件追加具体文档/源码路径、调用链图和 Task 2 实施建议；不要在此文件中记录未经复现的 API 猜测。
