package io.github.study0915.kuiklyfinance.market

import kotlin.math.abs
import kotlin.math.floor

object MarketFormatter {
    fun decimal(value: Double, signed: Boolean = false): String {
        require(value.isFinite() && abs(value) < 9e16)
        val units = floor(abs(value) * 100.0 + 0.5 + 1e-9).toLong()
        val prefix = if (units == 0L) "" else if (value < 0) "-" else if (signed) "+" else ""
        return "$prefix${units / 100}.${(units % 100).toString().padStart(2, '0')}"
    }
    fun price(minor: Int) = decimal(minor / 100.0)
    fun percent(value: Double) = decimal(value, signed = true) + "%"
    fun volume(shares: Long?) = shares?.let { decimal(it / 10_000.0) + " 万股" } ?: "缺失"
    fun change(previous: MarketBar?, current: MarketBar): Double? =
        previous?.takeIf { it.closeMinor > 0 }?.let { (current.closeMinor.toDouble() - it.closeMinor) / it.closeMinor * 100 }
    fun direction(value: Double) = when { value > 0 -> "上涨"; value < 0 -> "下跌"; else -> "持平" }
}

enum class Freshness(val text: String) {
    RECENT("历史 Mock · 截止后 72 小时内"),
    STALE("历史 Mock · 已超过演示时效 72 小时"),
    FUTURE("历史 Mock · 截止时间晚于当前时间，请核对"),
}

object MarketTime {
    const val STALE_AFTER_MS = 72L * 60 * 60 * 1000
    // Freshness is a demonstration policy; every enum label still identifies historical Mock.
    fun freshness(asOf: Long, now: Long) = when {
        now < asOf -> Freshness.FUTURE
        now - asOf > STALE_AFTER_MS -> Freshness.STALE
        else -> Freshness.RECENT
    }
    private fun leap(y: Int) = y % 4 == 0 && (y % 100 != 0 || y % 400 == 0)
    private fun monthDays(y: Int, m: Int) = when (m) { 2 -> if (leap(y)) 29 else 28; 4, 6, 9, 11 -> 30; else -> 31 }
    fun validDate(date: String): Boolean {
        if (!Regex("[0-9]{4}-[0-9]{2}-[0-9]{2}").matches(date)) return false
        val y = date.take(4).toInt(); val m = date.substring(5, 7).toInt(); val d = date.takeLast(2).toInt()
        return y in 1970..2100 && m in 1..12 && d in 1..monthDays(y, m)
    }
    /** The local snapshot format has an explicit +08:00 offset. No system locale/timezone. */
    fun epoch(asOf: String): Long? {
        if (!Regex("[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\+08:00").matches(asOf) || !validDate(asOf.take(10))) return null
        val y = asOf.take(4).toInt(); val m = asOf.substring(5, 7).toInt(); val d = asOf.substring(8, 10).toInt()
        val h = asOf.substring(11, 13).toInt(); val n = asOf.substring(14, 16).toInt(); val s = asOf.substring(17, 19).toInt()
        if (h > 23 || n > 59 || s > 59) return null
        val days = (1970 until y).fold(0) { total, year -> total + if (leap(year)) 366 else 365 } + (1 until m).sumOf { monthDays(y, it) } + d - 1
        return ((days * 24L + h - 8) * 3600 + n * 60 + s) * 1000
    }
}
