# EvidenceChat · 显式内容与会话合同

入口：`finance_home` 内部 Chat。UI → ChatController → ChatSession → ChatProvider；返回 `Markdown(source)` 或 `EvidenceCard(ResolvedDocument)`。非Markdown业务块不从文本解析。业务层不依赖Kuikly。

## 会话

一次最多一个pending，输入trim后1–500字，最多20轮。取消使票据失效；重试替换原位置，保留问题/实体/场景，只增加generation与attempt。新会话清空现有记录并拒绝迟到响应。消息ID不复用。返回行情或进入详情不清空会话。刷新/重启不保存；不使用磁盘、登录、远程模型或问题URL。

## 证据

卡片直接消费Task1的EvidenceResolver和LensPresenter，计算一次得到可核对值。单股票、比较A/B和缺量样本共用EvidenceAnswerCard；展开走势复用MarketPlot；详情复用FinanceDetail/QuoteEvidenceLens。不能将重复代码称为复用。

卡片跳转带entityId、snapshotId、focus、fromChat。不可用依据不可点击；未知实体只给支持范围提示。来源时间、历史Mock、风险始终有可见字段。比较窗口一致，价格幅度与量能分别按各自基准计算。

## Markdown

支持标题、段落、列表、引用、粗体、行内代码和代码围栏；最多12000字符/160行。不实现完整CommonMark表格、嵌套列表或交互链接。链接/图片仅显示标签；HTML只作为文字，renderer只创建RichText/Span，不创建HTML、iframe、远程图片或导航动作。

## 平台与扩展

Kuikly2.4.0/Kotlin2.0.21。H5 history保存最小位置，不保存问题；Android返回依赖同一内部路由。设备键盘/触摸不能由APK构建证明。未来真实provider必须继续输出此显式合同，并另行验证数据来源、模型边界、失败与许可。
