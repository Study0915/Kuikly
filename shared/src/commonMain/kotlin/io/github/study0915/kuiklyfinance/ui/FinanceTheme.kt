package io.github.study0915.kuiklyfinance.ui

import com.tencent.kuikly.core.base.*
import com.tencent.kuikly.core.views.*

/** Shared presentation only: financial meaning and selected evidence stay with their owners. */
internal object FinanceTheme {
    val background = Color(0xFFF5F7FAL)
    val line = Color(0xFFE6EAF0L)
    val tint = Color(0xFFEDF3FFL)
    val up = Color(0xFFC93838L)
    val down = Color(0xFF087D62L)
    const val webFont = "-apple-system, BlinkMacSystemFont, \"Segoe UI\", \"PingFang SC\", \"Microsoft YaHei\", sans-serif"
    fun font(web: Boolean) = if (web) webFont else "sans-serif"
}

internal val financeInk = Color(0xFF17212FL)
internal val financeMuted = Color(0xFF626D80L)
internal val financeBlue = Color(0xFF2563EBL)
internal enum class FinanceActionStyle { PRIMARY, SECONDARY, QUIET }
internal enum class FinanceIcon { BACK, MARKET, CHAT, MORE }

internal fun ViewContainer<*, *>.FinanceText(value: () -> String, size: Float = 16f,
    ink: Color = financeInk, strong: Boolean = false) {
    Text { attr {
        text(value()); fontFamily(FinanceTheme.font(pagerData.isWeb)); fontSize(size)
        lineHeight(size * 1.45f); lines(0); color(ink)
        if (strong) fontWeightSemiBold()
    } }
}

internal fun ViewContainer<*, *>.FinanceAction(label: String,
    style: FinanceActionStyle = FinanceActionStyle.SECONDARY, action: () -> Unit) {
    View {
        attr {
            height(48f); paddingLeft(12f); paddingRight(12f); marginTop(4f); justifyContentCenter(); borderRadius(10f)
            backgroundColor(when (style) { FinanceActionStyle.PRIMARY -> financeBlue; FinanceActionStyle.SECONDARY -> FinanceTheme.tint; FinanceActionStyle.QUIET -> Color.TRANSPARENT })
            accessibility(label)
        }
        event { click { action() } }
        FinanceText({ label }, 14f, if (style == FinanceActionStyle.PRIMARY) Color.WHITE else financeBlue, true)
    }
}

internal fun ViewContainer<*, *>.FinancePrice(value: String, size: Float) {
    View {
        attr { flexDirectionRow(); alignItemsCenter(); accessibility("最新价 $value 元") }
        FinanceText({ value }, size, strong = true)
        View { attr { marginLeft(4f); marginTop(size * 0.3f) }; FinanceText({ "元" }, 13f, financeMuted) }
    }
}

internal fun ViewContainer<*, *>.FinanceGlyph(icon: FinanceIcon, ink: Color = financeBlue) {
    Canvas({ attr { width(22f); height(22f); touchEnable(false) } }) { c, _, _ ->
        c.beginPath(); c.strokeStyle(ink); c.lineWidth(1.8f); c.lineCapRound()
        when (icon) {
            FinanceIcon.BACK -> { c.moveTo(14f, 4f); c.lineTo(7f, 11f); c.lineTo(14f, 18f) }
            FinanceIcon.MARKET -> {
                c.moveTo(3f, 4f); c.lineTo(3f, 19f); c.lineTo(20f, 19f)
                c.moveTo(6f, 14f); c.lineTo(10f, 9f); c.lineTo(14f, 12f); c.lineTo(20f, 4f)
            }
            FinanceIcon.CHAT -> {
                c.moveTo(5f, 3f); c.lineTo(18f, 3f); c.quadraticCurveTo(20f, 3f, 20f, 5f)
                c.lineTo(20f, 14f); c.quadraticCurveTo(20f, 16f, 18f, 16f)
                c.lineTo(9f, 16f); c.lineTo(4f, 20f); c.lineTo(4f, 16f)
                c.quadraticCurveTo(2f, 16f, 2f, 14f); c.lineTo(2f, 5f); c.quadraticCurveTo(2f, 3f, 5f, 3f)
                c.moveTo(7f, 8f); c.lineTo(15f, 8f); c.moveTo(7f, 12f); c.lineTo(12f, 12f)
            }
            FinanceIcon.MORE -> { for (y in listOf(5f, 11f, 17f)) { c.moveTo(5f, y); c.lineTo(17f, y) } }
        }
        c.stroke()
    }
}

internal fun ViewContainer<*, *>.FinanceIconAction(label: String, icon: FinanceIcon, action: () -> Unit) {
    View {
        attr { width(48f); height(48f); allCenter(); accessibility(label) }
        event { click { action() } }
        FinanceGlyph(icon, financeInk)
    }
}

internal fun ViewContainer<*, *>.FinanceTabs(inChat: Boolean, onHome: () -> Unit, onChat: () -> Unit) {
    View {
        attr { height(60f); backgroundColor(Color.WHITE); flexDirectionRow(); accessibility("主导航") }
        listOf(false, true).forEach { chat ->
            View {
                val active = chat == inChat
                attr { flex(1f); height(60f); allCenter(); accessibility(if (chat) { if (active) "当前问答" else "打开证据问答" } else { if (active) "当前行情" else "返回行情" }) }
                event { click { if (!active) { if (chat) onChat() else onHome() } } }
                FinanceGlyph(if (chat) FinanceIcon.CHAT else FinanceIcon.MARKET, if (active) financeBlue else financeMuted)
                FinanceText({ if (chat) "问答" else "行情" }, 12f, if (active) financeBlue else financeMuted, active)
            }
        }
    }
}
