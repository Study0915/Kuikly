package io.github.study0915.kuiklyfinance.ui

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Task1ShellContractTest {
    @Test
    fun shellKeepsStableEntryAndDisclaimer() {
        assertEquals("finance_home", Task1Routes.FINANCE_HOME)
        assertTrue(Task1ShellContract.DISCLAIMER.contains("不构成投资建议"))
    }
}
