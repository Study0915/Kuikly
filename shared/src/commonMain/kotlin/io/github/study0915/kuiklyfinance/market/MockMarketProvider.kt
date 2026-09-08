package io.github.study0915.kuiklyfinance.market

import io.github.study0915.kuiklyfinance.insight.*

enum class DemoScenario(val label: String) {
    COMPLETE("完整行情"), MISSING_VOLUME("量能缺失"), EMPTY("空行情"),
    FAIL_ONCE("首次失败"), NO_INSIGHT("暂无解读"), BAD_REFERENCE("无效引用"), LONG_TEXT("长文说明"),
}

sealed class MarketLoad {
    data object Loading : MarketLoad()
    data class Ready(val document: EvidenceDocument) : MarketLoad()
    data object Empty : MarketLoad()
    data class Failed(val reason: String) : MarketLoad()
    data class UnknownEntity(val entityId: String) : MarketLoad()
    data class SnapshotUnavailable(val entityId: String, val snapshotId: String) : MarketLoad()
}

class MockMarketProvider {
    val entityIds = ('A'..'L').map { "MOCK_$it" }
    fun snapshotId(entityId: String, scenario: DemoScenario = DemoScenario.COMPLETE): String =
        "${entityId}_20260904_${scenario.name.lowercase()}_v1"

    fun scenarioForSnapshot(entityId: String, snapshotId: String): DemoScenario? =
        DemoScenario.entries.firstOrNull { snapshotId(entityId, it) == snapshotId }

    fun load(entityId: String, explicitSnapshot: String? = null, scenario: DemoScenario = DemoScenario.COMPLETE, attempt: Int = 0): MarketLoad {
        if (entityId !in entityIds) return MarketLoad.UnknownEntity(entityId)
        val selected = if (explicitSnapshot == null) scenario else scenarioForSnapshot(entityId, explicitSnapshot)
            ?: return MarketLoad.SnapshotUnavailable(entityId, explicitSnapshot)
        if (selected == DemoScenario.FAIL_ONCE && attempt == 0) return MarketLoad.Failed("演示：首次请求失败，重试将加载同一股票。")
        if (selected == DemoScenario.EMPTY) return MarketLoad.Empty
        val number = entityIds.indexOf(entityId)
        val template = if (number % 2 == 0) MockMarketFixtures.barsA else MockMarketFixtures.barsB
        val offset = if (number < 2) 0 else number * 25
        val bars = template.map { bar ->
            bar.copy(openMinor = bar.openMinor + offset, highMinor = bar.highMinor + offset,
                lowMinor = bar.lowMinor + offset, closeMinor = bar.closeMinor + offset,
                volumeShares = if (selected == DemoScenario.MISSING_VOLUME && bar.date == "2026-09-02") null else bar.volumeShares)
        }
        val snapshot = MarketSnapshot(entityId, snapshotId(entityId, selected), "示例股票 ${entityId.last()}", "2026-09-04T15:00:00+08:00", bars)
        val localStart = if (number % 2 == 0) "2026-08-24" else "2026-08-27"
        val localEnd = if (number % 2 == 0) "2026-08-27" else "2026-09-04"
        val items = if (selected == DemoScenario.NO_INSIGHT) emptyList() else listOf(
            Evidence("E1", "区间表现", EvidenceRef.CloseReturn(bars.first().date, bars.last().date, true)),
            Evidence("E2", if (number % 2 == 0) "中途回落" else "局部反弹", EvidenceRef.CloseReturn(if (selected == DemoScenario.BAD_REFERENCE) "2026-08-23" else localStart, localEnd)),
            Evidence("E3", "量能观察", EvidenceRef.VolumeRatio(bars.last().date, bars.takeLast(6).dropLast(1).map { it.date })),
        )
        return MarketLoad.Ready(EvidenceDocument(snapshot, items))
    }
}
