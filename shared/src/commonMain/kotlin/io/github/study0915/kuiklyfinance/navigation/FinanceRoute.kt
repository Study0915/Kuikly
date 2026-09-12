package io.github.study0915.kuiklyfinance.navigation

import io.github.study0915.kuiklyfinance.insight.LensFocus

sealed class FinanceRoute {
    data object Home : FinanceRoute()
    data object Chat : FinanceRoute()
    data class Detail(val entityId: String, val snapshotId: String? = null, val focus: LensFocus? = null,
        val fromChat: Boolean = false) : FinanceRoute()
}
