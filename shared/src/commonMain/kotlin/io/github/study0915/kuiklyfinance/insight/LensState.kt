package io.github.study0915.kuiklyfinance.insight

data class DocumentKey(val entityId: String, val snapshotId: String)

sealed class LensFocus {
    data object Overview : LensFocus()
    data class EvidenceFocus(val evidenceId: String) : LensFocus()
    data class DayInspect(val date: String) : LensFocus()
}

data class LensUiState(val documentKey: DocumentKey, val focus: LensFocus, val notice: String? = null)

sealed class LensAction {
    data class SelectEvidence(val id: String) : LensAction()
    data class InspectDay(val date: String) : LensAction()
    data object ReturnToSummary : LensAction()
}

object LensState {
    fun initial(doc: ResolvedDocument, requested: LensFocus? = null): LensUiState {
        if (requested != null) return normalize(doc, LensUiState(doc.key, requested))
        return LensUiState(doc.key, doc.evidence.firstOrNull { it.available }?.let { LensFocus.EvidenceFocus(it.evidence.id) } ?: LensFocus.Overview)
    }
    fun normalize(doc: ResolvedDocument, state: LensUiState): LensUiState {
        if (state.documentKey != doc.key) return initial(doc)
        val valid = when (val focus = state.focus) {
            LensFocus.Overview -> true
            is LensFocus.EvidenceFocus -> doc.canPlot && doc.evidence.any { it.evidence.id == focus.evidenceId && it.available }
            is LensFocus.DayInspect -> doc.canPlot && doc.bars.any { it.date == focus.date }
        }
        return if (valid) state else LensUiState(doc.key, LensFocus.Overview, "原选择已失效，请重新选择依据或日期。")
    }
    fun reduce(doc: ResolvedDocument, state: LensUiState, eventKey: DocumentKey, action: LensAction): LensUiState {
        val current = normalize(doc, state)
        if (eventKey != doc.key) return current
        return when (action) {
            is LensAction.SelectEvidence -> if (doc.evidence.any { it.evidence.id == action.id && it.available }) LensUiState(doc.key, LensFocus.EvidenceFocus(action.id)) else current
            is LensAction.InspectDay -> if (doc.canPlot && doc.bars.any { it.date == action.date }) LensUiState(doc.key, LensFocus.DayInspect(action.date)) else current
            LensAction.ReturnToSummary -> initial(doc)
        }
    }
}
