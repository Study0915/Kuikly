package io.github.study0915.kuiklyfinance.insight

data class DocumentKey(val entityId: String, val snapshotId: String)

sealed class LensFocus {
    data object Overview : LensFocus()
    data class EvidenceFocus(val evidenceId: String) : LensFocus()
    data class DayInspect(val date: String) : LensFocus()
}

data class LensUiState(val documentKey: DocumentKey, val focus: LensFocus)
