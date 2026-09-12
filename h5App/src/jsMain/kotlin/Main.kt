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
import com.tencent.kuikly.core.render.web.IKuiklyRenderExport
import com.tencent.kuikly.core.render.web.expand.module.KRNotifyModule
import com.tencent.kuikly.core.render.web.ktx.KuiklyRenderCallback
import com.tencent.kuikly.core.render.web.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.render.web.context.KuiklyRenderCoreExecuteMode
import com.tencent.kuikly.core.render.web.exception.ErrorReason

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
    private val initialQuery = window.location.search

    fun prepareHistory() {
        // A reload keeps the existing detail entry; a first deep link gets a real home entry.
        if (window.history.state.asDynamic()?.financeDetail != true && window.history.state.asDynamic()?.financeChat != true) {
            window.history.replaceState(js("({financeDetail:false})"), "", routeUrl())
        }
    }

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
        mapOf("width" to window.innerWidth, "height" to viewportHeight()),
    )
    private fun viewportHeight(): Int = (window.asDynamic().visualViewport?.height as? Double)?.toInt() ?: window.innerHeight
    override fun registerExternalModule(kuiklyRenderExport: IKuiklyRenderExport) {
        kuiklyRenderExport.moduleExport(KRNotifyModule.MODULE_NAME) {
            object : KRNotifyModule() {
                override fun call(method: String, params: String?, callback: KuiklyRenderCallback?): Any? {
                    if (method == "postNotify" && params != null) {
                        val event = JSONObject(params)
                        if (event.optString("eventName") == Task1Routes.HOST_ROUTE_EVENT) {
                            val data = event.optJSONObject("data") ?: JSONObject(event.optString("data"))
                            if (data.optString("route") == "detail") {
                                // popstate has already changed the active entry. Never let a late
                                // render notification turn the home entry back into a detail entry.
                                val replace = data.optString("operation") == "replace"
                                val current = window.history.state.asDynamic()
                                if (!replace || (current?.financeDetail == true && current.entityId == data.optString("entityId"))) {
                                    writeDetail(data.optString("entityId"), data.optString("snapshotId"),
                                        data.optString("date"), data.optString("evidenceId"), data.optString("overview"), replace, data.optString("fromChat"))
                                }
                            } else if (data.optString("operation") == "back") {
                                if (window.history.state.asDynamic()?.financeDetail == true || window.history.state.asDynamic()?.financeChat == true) window.history.back()
                            } else if (data.optString("route") == "chat") writeChat()
                            return null
                        }
                    }
                    return super.call(method, params, callback)
                }
            }
        }
    }
    private fun routeUrl(entity: String = "", snapshot: String = "", date: String = "", evidence: String = "", overview: String = "", fromChat: String = "", chat: Boolean = false): String {
        val query = js("new URLSearchParams(window.location.search)")
        listOf("entity", "snapshot", "date", "evidence", "overview", "fromChat", "page").forEach { query.delete(it) }
        mapOf("entity" to entity, "snapshot" to snapshot, "date" to date, "evidence" to evidence, "overview" to overview,
            "fromChat" to fromChat, "page" to if (chat) "chat" else "")
            .filterValues { it.isNotEmpty() }.forEach { (key, value) -> query.set(key, value) }
        val encoded = query.toString() as String
        return window.location.pathname + (if (encoded.isEmpty()) "" else "?$encoded") + window.location.hash
    }
    private fun writeChat() {
        window.history.pushState(js("({financeChat:true})"), "", routeUrl(chat = true))
    }
    private fun writeDetail(entity: String, snapshot: String = "", date: String = "", evidence: String = "", overview: String = "", replace: Boolean = false, fromChat: String = "") {
        val state = js("({})")
        state.financeDetail = true; state.entityId = entity; state.snapshotId = snapshot; state.date = date; state.evidenceId = evidence
        state.overview = overview
        state.fromChat = fromChat
        val url = routeUrl(entity, snapshot, date, evidence, overview, fromChat)
        if (replace) window.history.replaceState(state, "", url) else window.history.pushState(state, "", url)
    }
    fun historyChanged() {
        val state = window.history.state.asDynamic()
        if (state?.financeDetail == true) {
            delegate.sendEvent(Task1Routes.HOST_OPEN_EVENT, mapOf(
                "entityId" to (state.entityId as? String ?: ""), "snapshotId" to (state.snapshotId as? String ?: ""),
                "date" to (state.date as? String ?: ""), "evidenceId" to (state.evidenceId as? String ?: ""),
                "overview" to (state.overview as? String ?: ""), "fromChat" to (state.fromChat as? String ?: "")))
        } else if (state?.financeChat == true) delegate.sendEvent(Task1Routes.HOST_CHAT_EVENT, emptyMap())
        else delegate.sendEvent(Task1Routes.HOST_BACK_EVENT, emptyMap())
    }
    override fun onPageLoadComplete(isSucceed: Boolean, errorReason: ErrorReason?, executeMode: KuiklyRenderCoreExecuteMode) {
        if (!isSucceed) return
        if (window.history.state.asDynamic()?.financeDetail == true || window.history.state.asDynamic()?.financeChat == true) { historyChanged(); return }
        // Pass the runtime query as data to the native constructor.
        val ctor = js("URLSearchParams")
        val params = js("Reflect").construct(ctor, arrayOf(initialQuery))
        if (params.get("page") == "chat") { writeChat(); historyChanged(); return }
        val entity = params.get("entity") as? String ?: return
        val snapshot = params.get("snapshot") as? String ?: ""
        val date = params.get("date") as? String ?: ""
        val evidence = params.get("evidence") as? String ?: ""
        val overview = params.get("overview") as? String ?: ""
        val fromChat = params.get("fromChat") as? String ?: ""
        if (fromChat == "1") writeChat()
        writeDetail(entity, snapshot, date, evidence, overview, fromChat = fromChat)
        historyChanged()
    }
}

fun main() {
    installStandaloneHostCompatibility()
    val delegator = Task1WebDelegator()
    delegator.prepareHistory()
    delegator.attach()
    delegator.resume()
    document.getElementById("boot")?.remove()
    window.addEventListener("resize", { delegator.resize() })
    window.asDynamic().visualViewport?.addEventListener("resize", { _: dynamic -> delegator.resize() })
    window.addEventListener("popstate", { delegator.historyChanged() })
    // On hybrid devices the renderer binds touch handlers but still receives mouse clicks.
    // Keep the click fallback from turning a mouse drag into a point inspection.
    var mouseOrigin: Pair<Double, Double>? = null
    var mouseCancelled = false
    fun trackMouse(x: Double, y: Double) {
        mouseOrigin?.let { origin ->
            if (kotlin.math.abs(x - origin.first) > 8 || kotlin.math.abs(y - origin.second) > 8) mouseCancelled = true
        }
    }
    document.addEventListener("mousedown", { event ->
        mouseOrigin = (event.asDynamic().clientX as Double) to (event.asDynamic().clientY as Double)
        mouseCancelled = false
    }, true)
    document.addEventListener("mousemove", { event -> trackMouse(event.asDynamic().clientX as Double, event.asDynamic().clientY as Double) }, true)
    document.addEventListener("mouseup", { event ->
        trackMouse(event.asDynamic().clientX as Double, event.asDynamic().clientY as Double); mouseOrigin = null
    }, true)
    document.addEventListener("click", { event ->
        if (mouseCancelled) { mouseCancelled = false; event.preventDefault(); event.stopImmediatePropagation() }
    }, true)
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
