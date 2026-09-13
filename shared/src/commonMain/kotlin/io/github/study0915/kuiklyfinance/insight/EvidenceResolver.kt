package io.github.study0915.kuiklyfinance.insight

import io.github.study0915.kuiklyfinance.market.*

object EvidenceResolver {
    fun resolve(document: EvidenceDocument, now: Long): ResolvedDocument {
        val snapshot = document.snapshot
        val freshness = MarketTime.freshness(MarketTime.epoch(snapshot.asOf) ?: now, now)
        val invalid = snapshot.validationError()
        if (invalid != null || snapshot.bars.isEmpty()) return ResolvedDocument(document, emptyList(), "暂无解读", freshness, invalid)
        val duplicates = document.evidence.map { it.id }.distinct().size != document.evidence.size
        val resolved = document.evidence.map { evidence ->
            if (duplicates) ResolvedEvidence(evidence, null, "证据 ID 重复，该证据集合不可用")
            else resolveOne(snapshot.bars, evidence)
        }
        return ResolvedDocument(document, resolved, summary(resolved), freshness)
    }

    private fun resolveOne(bars: List<MarketBar>, e: Evidence): ResolvedEvidence {
        fun fail(reason: String) = ResolvedEvidence(e, null, reason)
        if (e.id.isBlank()) return fail("证据 ID 缺失")
        return when (val ref = e.ref) {
            is EvidenceRef.CloseReturn -> {
                val start = bars.indexOfFirst { it.date == ref.start }; val end = bars.indexOfFirst { it.date == ref.end }
                if (start < 0 || end < start || (ref.wholeWindow && (start != 0 || end != bars.lastIndex))) return fail("区间引用无效")
                val a = bars[start]; val b = bars[end]
                ResolvedEvidence(e, EvidenceFact.Return(a, b, (b.closeMinor.toDouble() - a.closeMinor) / a.closeMinor * 100, ref.wholeWindow))
            }
            is EvidenceRef.VolumeRatio -> {
                val index = bars.indexOfFirst { it.date == ref.target }
                if (index < 5) return fail("目标日前不足 5 个窗口交易日")
                val samples = bars.subList(index - 5, index)
                if (samples.map { it.date } != ref.sampleDates) return fail("比较样本必须为目标日前连续 5 个交易日")
                val target = bars[index]
                val missing = (samples + target).filter { it.volumeShares == null }
                if (missing.isNotEmpty()) return fail("成交量缺失：${missing.joinToString { it.date }}；不跳过缺量日补样本")
                val average = samples.sumOf { it.volumeShares!!.toDouble() } / 5
                if (average == 0.0) return fail("比较样本平均成交量为 0，倍数不可计算")
                ResolvedEvidence(e, EvidenceFact.Volume(target, samples.toList(), average, target.volumeShares!!.toDouble() / average))
            }
        }
    }

    private fun summary(items: List<ResolvedEvidence>): String {
        if (items.isEmpty()) return "该股票暂无 AI 解读；仍可检视原始行情。"
        val parts = items.map { e ->
            when (val f = e.fact) {
                is EvidenceFact.Return -> "${if (f.wholeWindow) "窗口收盘价" else "局部区间"}${MarketFormatter.direction(f.percent)} ${MarketFormatter.percent(f.percent)}。"
                is EvidenceFact.Volume -> "目标日成交量为此前 5 日均量的 ${MarketFormatter.decimal(f.ratio)} 倍。"
                null -> "${e.evidence.label}暂不可用。"
            }
        }
        return parts.joinToString(" ") + " 这些是历史数值观察，不解释涨跌原因，也不预测后市。"
    }

    fun related(resolved: ResolvedDocument, date: String): List<RelatedEvidence> = resolved.evidence.mapNotNull { e ->
        when (val fact = e.fact) {
            is EvidenceFact.Return -> if (date in fact.start.date..fact.end.date) {
                if (fact.wholeWindow) RelatedEvidence(e, "整体观察", 3) else RelatedEvidence(e, "区间内", 1)
            } else null
            is EvidenceFact.Volume -> when {
                date == fact.target.date -> RelatedEvidence(e, "目标日", 0)
                fact.samples.any { it.date == date } -> RelatedEvidence(e, "比较样本", 2)
                else -> null
            }
            null -> null
        }
    }.sortedBy { it.priority }.distinctBy { it.evidence.evidence.id }

    fun mark(resolved: ResolvedDocument, focus: LensFocus): PlotMark = when (focus) {
        is LensFocus.DayInspect -> PlotMark(inspected = resolved.bars.indexOfFirst { it.date == focus.date }.takeIf { it >= 0 })
        is LensFocus.EvidenceFocus -> when (val fact = resolved.evidence.firstOrNull { it.evidence.id == focus.evidenceId }?.fact) {
            is EvidenceFact.Return -> PlotMark(range = resolved.bars.indexOf(fact.start)..resolved.bars.indexOf(fact.end))
            is EvidenceFact.Volume -> PlotMark(range = resolved.bars.indexOf(fact.samples.first())..resolved.bars.indexOf(fact.samples.last()), target = resolved.bars.indexOf(fact.target))
            null -> PlotMark()
        }
        LensFocus.Overview -> PlotMark()
    }
}
