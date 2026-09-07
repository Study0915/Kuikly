package io.github.study0915.kuiklyfinance.market

/** Prices are integer CNY cents. Missing volume is distinct from zero shares. */
data class MarketBar(
    val date: String,
    val openMinor: Int,
    val highMinor: Int,
    val lowMinor: Int,
    val closeMinor: Int,
    val volumeShares: Long?,
)

class MarketSnapshot(
    val entityId: String,
    val snapshotId: String,
    val name: String,
    val asOf: String,
    bars: List<MarketBar>,
    val source: String = "MOCK",
    val currency: String = "CNY",
    val priceScale: Int = 100,
) {
    val bars: List<MarketBar> = bars.toList()
    fun withBars(id: String, values: List<MarketBar>) =
        MarketSnapshot(entityId, id, name, asOf, values, source, currency, priceScale)
}

/** Never sort or repair a malformed input: dates are part of the evidence contract. */
fun MarketSnapshot.validationError(): String? {
    if (entityId.isBlank() || snapshotId.isBlank()) return "行情身份缺失"
    if (currency != "CNY" || priceScale != 100 || source != "MOCK") return "不支持的行情格式"
    if (MarketTime.epoch(asOf) == null) return "截止时间无效"
    if (bars.isNotEmpty() && bars.size != 20) return "首版需要完整的 20 个交易日"
    bars.forEachIndexed { index, bar ->
        if (!MarketTime.validDate(bar.date) || (index > 0 && bars[index - 1].date >= bar.date)) return "交易日无效、重复或乱序"
        if (bar.openMinor <= 0 || bar.closeMinor <= 0 || bar.lowMinor <= 0 ||
            bar.lowMinor > minOf(bar.openMinor, bar.closeMinor) ||
            bar.highMinor < maxOf(bar.openMinor, bar.closeMinor)) return "${bar.date} 开高低收不合法"
        if (bar.volumeShares != null && bar.volumeShares < 0) return "${bar.date} 成交量为负"
    }
    if (bars.lastOrNull()?.date?.let { it > asOf.take(10) } == true) return "交易日晚于截止时间"
    return null
}
