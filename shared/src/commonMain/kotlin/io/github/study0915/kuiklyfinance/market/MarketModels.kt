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
