# Task T1-VERTICAL · 行情列表到个股详情的首个离线垂直切片

- 状态：READY
- 前置结论：`T1-000` 已在 `90e00661e3db486b16996e074b0270900af8ab5a` 完成四门与 H5 真实浏览器验收。
- 本卡只授权 T1-VERTICAL；AI、图表和 Task 2 仍不得开始。

## 目标与价值

- 目标：使用仓库内确定性 Mock 行情，建立“可滚动行情列表 → 点击股票 → 正确个股详情”的 Android/H5 首个业务垂直切片。
- 用户价值：用户可以浏览多只股票的基础行情，并从任意列表项到达对应实体的详情，而不是只看到 reset 空壳。
- 完成定义：
  1. `finance_home` 展示足以产生真实滚动的确定性 Mock 股票列表；
  2. 每行显示名称、代码、最新价、涨跌额和涨跌幅；
  3. 至少两只不同股票的点击均进入与所点 code 一致的 `stock_detail`；
  4. 详情显示名称、代码、最新价、涨跌幅、最高价、最低价和成交量；
  5. 缺失、空白或未知 code 不得回退到第一只股票，必须进入明确、可返回的未找到状态；
  6. 列表与详情显示 `[MOCK]` 和“仅作技术演示，不构成投资建议”；
  7. `code/tests/evidence/learning` 四门齐全。

## 执行边界

- Owner：Codex；同一时间唯一代码写入者仍为 Windows Codex。
- 基线 commit：`90e00661e3db486b16996e074b0270900af8ab5a`。
- 功能分支：`feature/task1-vertical`。
- 允许修改：
  - `shared/src/commonMain/kotlin/io/github/study0915/kuiklyfinance/market/**`
  - `shared/src/commonMain/kotlin/io/github/study0915/kuiklyfinance/navigation/**`
  - `shared/src/commonMain/kotlin/io/github/study0915/kuiklyfinance/ui/**`
  - `shared/src/commonTest/kotlin/io/github/study0915/kuiklyfinance/**`
  - `androidApp/src/main/kotlin/io/github/study0915/kuiklyfinance/android/MainActivity.kt`
  - 必要时新增同包内唯一的 Android 路由 adapter
  - `h5App/src/jsMain/kotlin/Main.kt`
  - `h5App/src/jsMain/resources/index.html`
  - 本任务的 `CHANGELOG`、workboard、acceptance、task/handoff、review、evidence、learning 与截图
- Android/H5 宿主只允许承接白名单页面名和 `stock_code` 参数；不得包含行情 fixture、查找或格式化逻辑。
- 禁止修改：
  - `archive/task1-v1/**`、`KuiklyChart/**`
  - 根构建、版本目录、Gradle Wrapper、`scripts/**`、`.github/**`
  - Task 2 源码或任务材料
  - 本机路径、SDK 配置、Token、密钥、签名和真实用户数据
  - 远程分支、PR、tag、release 或外部消息
- 必须复用：
  - `Task1Routes.FINANCE_HOME == "finance_home"`
  - Kuikly `@Page`、`Pager`、`List`、响应式列表和 `RouterModule` 模式
  - 当前 Android/H5 delegator 生命周期与项目内 CLI
  - Kuikly 2.4.0、Kotlin 2.0.21、JDK 17 和 Mock-first 决策
- 不得复制或依赖 archive 中的类型、Provider、页面、路由实现或完成证据。

## 最小 module、interface 与 seam

T1-VERTICAL 只建立一个行情 module。其 interface 同时服务列表、详情和测试，隐藏 fixture、索引、不变量与查找实现。

```kotlin
data class StockQuote(
    val name: String,
    val code: String,
    val latestPriceCent: Long,
    val changeAmountCent: Long,
    val changePercentBasisPoints: Int,
    val highPriceCent: Long,
    val lowPriceCent: Long,
    val volumeShares: Long,
)

sealed interface MarketLoadResult {
    data class Ready(val snapshot: MarketSnapshot) : MarketLoadResult
    data object Unavailable : MarketLoadResult
}

fun interface MarketProvider {
    fun load(): MarketLoadResult
}

class MarketSnapshot internal constructor(
    val quotes: List<StockQuote>,
) {
    fun findByCode(rawCode: String): StockQuote?
}
```

Interface 合同：

- `FixtureMarketProvider` 是活动 adapter，只读取 commonMain 内的确定性 fixture，并始终返回 `Ready`；commonTest 使用可返回两种结果的 test-only fake adapter。
- `load()` 不联网、不读取密钥，不依赖系统时间或随机数；重复成功加载的内容与顺序一致。
- `MarketSnapshot` 对 caller 不可变；code 在快照内唯一，`findByCode()` 统一处理 trim 与未知 code。
- 首页从 `Ready.snapshot.quotes` 取列表，详情从同一类快照按 code 查找；禁止增加 `loadList()` / `loadDetail()` 两套事实入口。
- `Unavailable` 冻结 Provider 的 typed failure mode，避免 T1-EXPERIENCE 为 error/retry 破坏 interface；本切片只要求 basic fallback 不崩溃，不实现完整 loading/error/retry 体验。
- 价格与百分比使用整数最小单位，不用 `Float` / `Double` 保存金融事实；格式化由 commonMain 纯函数统一完成。
- `name`、`code` 非空；`high >= latest >= low`；成交量非负；涨跌额和涨跌幅符号一致。
- 页面不得在 render/list item 中创建 fixture 或自行拼接小数、正负号和成交量单位。
- 首页与详情的非 UI state/resolver 必须通过构造参数接收 `MarketProvider`；页面只能从一个 shared composition root 取得默认 resolver，不得自行 `FixtureMarketProvider()`。
- commonTest 必须用 fake 经由同一 state/resolver 分别驱动首页与详情 caller；只测试 fake 自身不算 seam 已接入。
- 本切片不冻结真实网络、缓存或通用数据源 interface；出现第二个真实生产 adapter 前不增加新的外部 seam。

## 路由合同

```kotlin
object Task1Routes {
    const val FINANCE_HOME = "finance_home"
    const val STOCK_DETAIL = "stock_detail"
}

object Task1RouteParams {
    const val STOCK_CODE = "stock_code"
}
```

- commonMain 只传 code，不把整个 `StockQuote` 序列化进路由。
- H5 只解析白名单 `page_name` 与 `stock_code`，不得继续无条件 attach `finance_home`。
- Android 先核对当前 Kuikly 2.4.0 已提供的 `RouterModule` 承接方式；能复用就不新增 adapter，确实缺失时才增加最小白名单 adapter。
- 宿主不得复制行情查找、fallback 或格式化规则。
- 未知 code 显示收到的 code、未找到说明和返回入口，不得静默显示其他实体。
- 页面名和参数名只在上述常量中定义一次。

## code 门

- reset 页面被真实行情首页取代，`finance_home` 只注册一次。
- 新增 `stock_detail` 并通过 JS/Android KSP 与编译。
- fixture 足以在 390 × 844 H5 viewport 中证明列表滚动。
- 列表与详情覆盖题面全部字段；commonMain 不使用 Android、浏览器或 JVM 专属 API。
- 归档不进入活动 dependency graph；diff 中没有 AI、图表、K 线或高级手势实现。

## tests 门

commonTest 至少覆盖：

1. fixture 重复加载的内容与顺序确定，code 唯一且字段不变量成立；
2. `findByCode()` 对首条、中间条、末条正确，空白和未知 code 返回 `null`；
3. 正、负、零价格/涨跌格式，以及最高、最低和成交量格式；
4. route/page/parameter 常量和 code 编解码保持一致；
5. detail resolver 对已知 code 返回同一实体，对未知 code 返回明确错误状态；
6. fake adapter 可经同一非 UI state/resolver 驱动首页与详情，并分别覆盖 `Ready` / `Unavailable`；caller 不依赖具体实现。

必须实际执行：

```powershell
.\scripts\doctor.ps1
.\scripts\verify.ps1
git diff --check
```

空 `KuiklyChart:jsNodeTest` 即使为 `SKIPPED`，也不得写成图表能力证据。

## evidence 门

新增 `docs/evidence/<YYYY-MM-DD>-t1-vertical.md`，记录基线/实现 commit、分支、环境版本、命令/退出码/耗时、APK 大小与 SHA-256、未执行平台和外部操作边界。

H5 真实浏览器证据至少包含：

- 首屏五个列表字段；
- 滚动后出现首屏之外的股票；
- 两只不同 code 的点击与详情截图；
- 缺失或未知 code 的可恢复错误；
- 点击、返回、DOM、console 与入口/JS 网络状态。

HTTP 200 不能代替可视证据。Android JVM tests 与 Debug APK 构建必须通过；未运行真机/模拟器时继续写 `[UNVERIFIED] Android 设备交互`。

## learning 门

新增 `docs/learning/T1-VERTICAL.md`，解释：

- 首页点击到详情渲染的完整调用链；
- `MarketProvider` seam、fixture/fake adapter 与不可变快照的取舍；
- `List`、响应式列表、`@Page`、`RouterModule` 和宿主 adapter 的职责；
- 为什么用整数最小单位，以及 formatter / route / resolver 如何独立测试；
- Android/H5 路由差异、限制与 5 道带答案要点的面试题。

## 人工验收

1. 390 × 844 H5 首屏显示名称、代码、最新价、涨跌额和涨跌幅，列表可实际滚动。
2. 点击股票 A 后详情实体与 A 一致；返回点击股票 B 后实体与 B 一致且不残留 A。
3. 详情显示最新价、涨跌幅、最高价、最低价和成交量。
4. 缺失、空白和未知 code 不崩溃、不回退到第一只股票。
5. 列表和详情均显示 Mock 标识与投资免责声明。
6. Android APK 构建与设备运行状态分开记录。

## 明确非目标

- AI 分析、`AnalysisProvider`、趋势判断、风险提示、行情总结或买卖建议；
- loading、empty、Provider error 和 retry 的完整体验；
- 通用图表、折线/柱状/K 线/成交量图、`KuiklyChart` 业务实现和高级手势；
- 实时行情、真实模型、网络、缓存、数据库、搜索、自选、刷新、排序或分页；
- Task 2、iOS、HarmonyOS、依赖升级、模块重组或项目结构重构。

## 风险与失败升级

- 当前宿主把 `finance_home` 写死；共享 `RouterModule` 调用本身不能证明 Android/H5 已承接详情跳转。
- H5 external module 导出、Android 白名单路由、JS KSP 页面注册、URL 编码和响应式列表根节点是重点审查项。
- 相同编译、路由或浏览器错误连续复现两次，且 Windows 无安全替代路径时，保留日志并将任务置为 `BLOCKED`。
- 只有用户确认后才生成 OpenCode handoff；不得自行启动 OpenCode 或 WorkBuddy。

## 状态推进

- 本卡与真实基线已齐：`BACKLOG → READY`。
- Codex 开始业务代码唯一写入时：`READY → IN_PROGRESS`。
- 四门齐全并形成聚焦实现 commit 后：`IN_PROGRESS → HANDOFF → REVIEW → VERIFIED`。
- merge、push、PR、tag、release 和 `INTEGRATED` 仍由用户决定。
