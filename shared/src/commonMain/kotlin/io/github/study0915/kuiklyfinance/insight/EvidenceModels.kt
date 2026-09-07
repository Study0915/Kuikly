package io.github.study0915.kuiklyfinance.insight

import io.github.study0915.kuiklyfinance.market.*

sealed class EvidenceRef {
    data class CloseReturn(val start: String, val end: String, val wholeWindow: Boolean = false) : EvidenceRef()
    class VolumeRatio(val target: String, sampleDates: List<String>) : EvidenceRef() {
        val sampleDates = sampleDates.toList()
    }
}
data class Evidence(val id: String, val label: String, val ref: EvidenceRef)
class EvidenceDocument(val snapshot: MarketSnapshot, evidence: List<Evidence>) {
    val evidence = evidence.toList()
    val key = DocumentKey(snapshot.entityId, snapshot.snapshotId)
}

sealed class EvidenceFact {
    data class Return(val start: MarketBar, val end: MarketBar, val percent: Double, val wholeWindow: Boolean) : EvidenceFact()
    data class Volume(val target: MarketBar, val samples: List<MarketBar>, val average: Double, val ratio: Double) : EvidenceFact()
}
data class ResolvedEvidence(val evidence: Evidence, val fact: EvidenceFact?, val reason: String? = null) {
    val available get() = fact != null
}
data class RelatedEvidence(val evidence: ResolvedEvidence, val role: String, val priority: Int)
data class ResolvedDocument(
    val document: EvidenceDocument,
    val evidence: List<ResolvedEvidence>,
    val summary: String,
    val freshness: Freshness,
    val error: String? = null,
) {
    val key get() = document.key
    val bars get() = document.snapshot.bars
    val canPlot get() = error == null && bars.isNotEmpty()
}
