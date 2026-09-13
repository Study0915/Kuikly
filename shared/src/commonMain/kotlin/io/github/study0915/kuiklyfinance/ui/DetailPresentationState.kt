package io.github.study0915.kuiklyfinance.ui

import com.tencent.kuikly.core.reactive.handler.observable

/** Pager stores one instance per DocumentKey; never stores a second LensFocus. */
internal class DetailPresentationState {
    var calculationExpanded by observable(false)
    var offset = 0f
}
