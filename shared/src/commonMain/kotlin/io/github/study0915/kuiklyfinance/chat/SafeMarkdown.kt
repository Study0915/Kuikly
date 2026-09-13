package io.github.study0915.kuiklyfinance.chat

enum class MarkdownKind { HEADING, PARAGRAPH, BULLET, QUOTE, CODE }
data class MarkdownSpan(val text: String, val bold: Boolean = false, val code: Boolean = false)
data class MarkdownLine(val kind: MarkdownKind, val spans: List<MarkdownSpan>)

/** Pure bounded parser. The renderer only emits text spans, never HTML, URLs or image nodes. */
object SafeMarkdown {
    fun parse(source: String): List<MarkdownLine> {
        var fenced = false
        return source.take(12000).lines().take(160).mapNotNull { raw ->
            val line = raw.trim()
            if (line.startsWith("```")) { fenced = !fenced; return@mapNotNull null }
            if (fenced) return@mapNotNull MarkdownLine(MarkdownKind.CODE, listOf(MarkdownSpan(raw, code = true)))
            if (line.isEmpty()) return@mapNotNull null
            val kind = when {
                Regex("^#{1,3} ").containsMatchIn(line) -> MarkdownKind.HEADING
                line.startsWith("- ") || Regex("^\\d+\\. ").containsMatchIn(line) -> MarkdownKind.BULLET
                line.startsWith("> ") -> MarkdownKind.QUOTE
                else -> MarkdownKind.PARAGRAPH
            }
            val content = when (kind) {
                MarkdownKind.HEADING -> line.substringAfter(' ')
                MarkdownKind.BULLET -> "• " + line.substringAfter(' ')
                MarkdownKind.QUOTE -> line.drop(2)
                else -> line
            }
            MarkdownLine(kind, inline(content))
        }
    }

    private fun inline(text: String): List<MarkdownSpan> {
        val plain = text.replace(Regex("!?\\[([^\\]\\n]*)\\]\\([^\\n]*?\\)")) { it.groupValues[1] }
        val pattern = Regex("\\*\\*([^*]+)\\*\\*|`([^`]+)`")
        val spans = mutableListOf<MarkdownSpan>(); var offset = 0
        pattern.findAll(plain).forEach { match ->
            if (match.range.first > offset) spans += MarkdownSpan(plain.substring(offset, match.range.first))
            spans += if (match.value.startsWith("**")) MarkdownSpan(match.groupValues[1], bold = true)
                else MarkdownSpan(match.groupValues[2], code = true)
            offset = match.range.last + 1
        }
        if (offset < plain.length) spans += MarkdownSpan(plain.substring(offset))
        return spans
    }
}
