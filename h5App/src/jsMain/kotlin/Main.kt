package io.github.study0915.kuiklyfinance.web

import com.tencent.kuikly.core.render.web.expand.KuiklyRenderViewDelegatorDelegate
import com.tencent.kuikly.core.render.web.ktx.SizeI
import com.tencent.kuikly.core.render.web.runtime.web.expand.KuiklyRenderViewDelegator
import io.github.study0915.kuiklyfinance.ui.Task1Routes
import com.tencent.kuikly.core.pager.Pager
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget
import org.w3c.dom.HTMLElement
import com.tencent.kuikly.core.render.web.processor.IRichTextProcessor
import com.tencent.kuikly.core.render.web.processor.KuiklyProcessor
import com.tencent.kuikly.core.render.web.expand.components.KRRichTextView
import com.tencent.kuikly.core.render.web.ktx.SizeF
import com.tencent.kuikly.core.render.web.ktx.width
import com.tencent.kuikly.core.render.web.runtime.web.expand.processor.RichTextProcessor

/** 2.4.0 DOM measurement reinserts its plain-text scratch node into the live tree.
 * Keep measurement detached from rendered nodes; rich text retains the upstream path.
 */
private object StandaloneTextProcessor : IRichTextProcessor by RichTextProcessor {
    override fun measureTextSize(constraintSize: SizeF, view: KRRichTextView, renderText: String): SizeF {
        if (view.isRichTextValues()) return RichTextProcessor.measureTextSize(constraintSize, view, renderText)
        val probe = view.ele.cloneNode(false) as HTMLElement
        probe.removeAttribute("id")
        probe.textContent = renderText
        probe.style.apply {
            width = ""; height = ""; position = "fixed"; left = "-10000px"; top = "0"
            visibility = "hidden"; whiteSpace = "pre-wrap"
            maxWidth = if (constraintSize.width > 0) "${constraintSize.width}px" else "none"
        }
        document.body!!.appendChild(probe)
        return try {
            val width = probe.offsetWidth.toFloat() + 0.5f
            SizeF(if (constraintSize.width > 0) minOf(width, constraintSize.width) else width, probe.offsetHeight.toFloat())
        } finally {
            probe.remove()
        }
    }
}

private fun installStandaloneHostCompatibility() {
    js(
        """
        (function (w) {
          var com = w.com || (w.com = {});
          var tencent = com.tencent || (com.tencent = {});
          var kuikly = tencent.kuikly || (tencent.kuikly = {});
          var core = kuikly.core || (kuikly.core = {});
          if (typeof core.setIsIgnoreRenderViewForFlatLayer !== 'function') {
            core.setIsIgnoreRenderViewForFlatLayer = function () {};
          }
        })(window);
        """,
    )
}

private class Task1WebDelegator : KuiklyRenderViewDelegatorDelegate {
    private val delegate = KuiklyRenderViewDelegator(this)

    fun attach() {
        delegate.onAttach(
            "root",
            Task1Routes.FINANCE_HOME,
            mapOf("host" to "h5", "mock" to 1),
            SizeI(window.innerWidth, window.innerHeight),
        )
        KuiklyProcessor.richTextProcessor = StandaloneTextProcessor
    }

    fun resume() = delegate.onResume()
    fun pause() = delegate.onPause()
    fun detach() = delegate.onDetach()
    fun resize() = delegate.sendEvent(
        Pager.PAGER_EVENT_ROOT_VIEW_SIZE_CHANGED,
        mapOf("width" to window.innerWidth, "height" to window.innerHeight),
    )
}

fun main() {
    installStandaloneHostCompatibility()
    val delegator = Task1WebDelegator()
    delegator.attach()
    delegator.resume()
    document.getElementById("boot")?.remove()
    window.addEventListener("resize", { delegator.resize() })
    // The pinned web renderer reports one synthesized pointer even for multitouch.
    // Cancel that stream at the host before it can be mistaken for a single tap.
    var firstTouchTarget: EventTarget? = null
    document.addEventListener("touchstart", { event ->
        val touches = event.asDynamic().touches.length as Int
        if (touches == 1) firstTouchTarget = event.target
        if (touches > 1) {
            firstTouchTarget?.dispatchEvent(Event("touchcancel"))
            firstTouchTarget = null
            event.stopImmediatePropagation()
        }
    }, true)

    document.addEventListener("visibilitychange", {
        if (document.asDynamic().hidden as Boolean) delegator.pause() else delegator.resume()
    })
    window.onbeforeunload = {
        delegator.detach()
        null
    }
}
