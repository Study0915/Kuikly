package io.github.study0915.kuiklyfinance.chat

import kotlin.test.*

class SafeMarkdownTest {
    @Test fun preservesHeadingBulletQuoteBoldAndCode() {
        val lines = SafeMarkdown.parse("## 结论\n**上涨** 与 `Mock`\n- 依据\n> 风险")
        assertEquals(listOf(MarkdownKind.HEADING, MarkdownKind.PARAGRAPH, MarkdownKind.BULLET, MarkdownKind.QUOTE), lines.map { it.kind })
        assertTrue(lines[1].spans.any { it.bold && it.text == "上涨" })
        assertTrue(lines[1].spans.any { it.code && it.text == "Mock" })
    }
    @Test fun linksAndImagesArePlainLabelsAndHtmlIsInertText() {
        val parsed = SafeMarkdown.parse("[来源](javascript:alert) ![图](https://invalid.test/a.png)\n<script>alert(1)</script>")
        val text = parsed.flatMap { it.spans }.joinToString("") { it.text }
        assertFalse(text.contains("javascript:")); assertFalse(text.contains("https://"))
        assertTrue(text.contains("来源")); assertTrue(text.contains("<script>"))
    }
    @Test fun fencedContentStaysLiteralAndUnclosedMarkersDoNotCrash() {
        val parsed = SafeMarkdown.parse("```\n**literal**\n```\n**unclosed")
        assertEquals(MarkdownKind.CODE, parsed.first().kind)
        assertEquals("**literal**", parsed.first().spans.single().text)
        assertEquals("**unclosed", parsed.last().spans.single().text)
    }
    @Test fun adversarialInputIsBounded() {
        assertTrue(SafeMarkdown.parse("x\n".repeat(10000)).size <= 160)
        assertTrue(SafeMarkdown.parse("x".repeat(20000)).flatMap { it.spans }.sumOf { it.text.length } <= 12000)
    }
}
