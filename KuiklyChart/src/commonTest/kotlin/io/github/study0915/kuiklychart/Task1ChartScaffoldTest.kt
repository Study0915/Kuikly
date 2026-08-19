package io.github.study0915.kuiklychart

import kotlin.test.Test
import kotlin.test.assertEquals

class Task1ChartScaffoldTest {
    @Test
    fun resetScaffoldIsVisibleToCommonTests() {
        assertEquals("RESET_READY", Task1ChartScaffold.STATUS)
    }
}
