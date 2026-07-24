# Kuikly Finance Charts 项目规则

## 项目目标与交付边界

- 本项目服务于 Tencent-TDS/KuiklyUI Issue #1477 与 2026 犀牛鸟 Shape with AI Task 1。
- Issue #1477 的目标是交付可复用的跨端图表组件：折线图、柱状图、DSL、坐标轴、网格线、基础交互、文档和平台验证证据。
- Task 1 的目标是复用图表组件完成 AI 股票行情原型 Demo：行情列表、个股详情、图表和 AI 解读模块。
- 未实际运行、测试或验证的能力不得写成已完成；未验证的平台不得声称支持；AI 和行情数据能力必须如实标记为 Mock、演示或真实接入。

## 语言与沟通

- 默认使用中文，结论优先，说明简洁且可执行。
- 代码片段必须标注目标文件路径。
- 需求、Kuikly API、平台行为或依赖版本不确定时，先检查当前工程、官方文档或可复现实验，不得猜测式编码。
- 遇到错误先说明根因、影响和修复方案；除非修复显然低风险且在当前任务范围内，否则先征求用户确认。

## 环境与依赖隔离

- 项目统一使用 JDK 21。使用项目 Gradle Wrapper：`.\gradlew.bat`；不得依赖或要求系统全局 Gradle。
- Gradle 缓存固定在项目 `.cache\gradle`。PowerShell 命令在仓库根目录设置：`$env:GRADLE_USER_HOME = "$PWD\.cache\gradle"`。
- Node/npm 缓存固定在 `.cache\npm`，例如：`npm ci --cache .cache\npm`。H5 依赖必须由 `package.json` 与锁文件声明，禁止全局安装项目依赖。
- Android SDK 的本机路径仅写入未提交的 `local.properties`；不得提交本机 SDK 路径、签名文件或 IDE 私有配置。
- 如后续新增 Python 工具，Python 固定使用工作区根目录 `.venv`，依赖缓存固定在 `.cache\pip`；运行依赖更新 `requirements.txt`，测试依赖更新 `requirements-dev.txt`。
- Android SDK 等其他所需的环境如果本机没有，安装的话不允许安装到C盘，安装到这个工作区文件夹
- `.gradle`、`.cache`、`build`、`node_modules`、`.venv`、本机配置、构建产物和缓存不得提交。非 Python 依赖也必须通过工程清单和项目内缓存管理。

## 安全与数据

- 不得提交、输出或记录密钥、Token、密码、真实用户数据、行情 API Key 或签名材料。
- 使用 `.env.example` 只提供变量名，不提供真实值；`.env`、`.env.*`、`local.properties`、`*.jks`、`*.keystore` 必须保持忽略。
- 默认使用可复现的 Mock 行情数据和 Mock AI Provider。接入真实行情、模型服务、付费服务或外部发布前必须先确认范围、鉴权和成本。
- 股票内容必须标注“仅作技术演示，不构成投资建议”。

## 工程结构与设计

- `KuiklyChart/`：可复用图表组件、公共数据模型、坐标计算和 `commonTest`。
- `shared/`：股票 Demo 页面、路由、Mock 数据和 Provider 接口。
- `androidApp/`、`h5App/`：Android 与 H5 运行宿主。
- `docs/`：设计、API、验收记录、运行说明和演示材料。
- `scripts/`：一键构建、测试和验收脚本；每个新增脚本必须提供运行说明。
- 图表的坐标换算、刻度、命中测试和数据处理必须与 Canvas/UI 渲染分离，确保可独立测试。
- 每个新增功能先在 `docs/` 说明目标、边界、输入输出、主要方案和验证方式。
- README 必须包含环境版本、运行命令、DSL 示例、平台验证状态、数据来源和免责声明。
- 不得复制其他参赛者实现；公开仓库只能用于理解 API、工程结构和设计思路。

## 测试与平台验证

- 图表核心逻辑必须覆盖空数据、单点、全等值、正负混合值、非有限值、边界坐标和选点等单元测试。
- 每次功能变更后，至少运行组件单测、Kotlin/JS 编译和 Android Debug 构建；无法运行时记录执行命令、失败原因和未覆盖风险。
- Windows 环境只允许声明已实际验证的 Android/H5 支持。iOS 或鸿蒙仅在对应环境构建或运行成功后才可标记支持。
- 不得以“理论可行”替代实际运行、截图、视频、测试日志或构建产物证据。

## Git、变更与发布

- 修改已有文件前先检查用途和现有改动，采取最小改动；与当前任务无关的问题只记录，不顺手重构。
- 分支使用 `feature/<topic>` 或 `bugfix/<topic>`；提交信息使用 Angular Commit Convention，例如 `feat(chart): add line chart DSL`。
- 删除或覆盖大量文件、依赖大版本升级、数据迁移、真实金融/模型服务接入、外部发布和持久化外部连接前，必须先确认目标与影响范围。
- Issue 交付应提供仓库地址、验收对照、测试结果和实际平台证据；不得提交未经验证的结论。
