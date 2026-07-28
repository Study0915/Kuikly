package io.github.study0915.kuiklyfinance.web

import com.tencent.kuikly.core.render.web.expand.KuiklyRenderViewDelegatorDelegate
import com.tencent.kuikly.core.render.web.IKuiklyRenderExport
import com.tencent.kuikly.core.render.web.export.KuiklyRenderBaseModule
import com.tencent.kuikly.core.render.web.ktx.SizeI
import com.tencent.kuikly.core.render.web.ktx.KuiklyRenderCallback
import com.tencent.kuikly.core.render.web.ktx.toJSONObjectSafely
import com.tencent.kuikly.core.render.web.runtime.web.expand.KuiklyRenderViewDelegator
import com.tencent.kuikly.core.module.RouterModule
import kotlinx.browser.document
import kotlinx.browser.window

private external fun decodeURIComponent(encoded: String): String
private external fun encodeURIComponent(component: String): String

/**
 * Kuikly 2.4.0 的 Web renderer 会在初始化时调用由动态宿主提供的
 * `setIsIgnoreRenderViewForFlatLayer`。独立静态 H5 包没有该宿主时，
 * 仅安装一个兼容的空实现；若真实宿主已提供实现则完全保留它。
 */
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

/** Maps the shared RouterModule contract to H5 navigation for the standalone host. */
private class FinanceWebRouterModule : KuiklyRenderBaseModule() {
    override fun call(method: String, params: String?, callback: KuiklyRenderCallback?): Any? =
        when (method) {
            "openPage" -> openPage(params)
            else -> super.call(method, params, callback)
        }

    private fun openPage(param: String?) {
        if (param == null) return
        val route = param.toJSONObjectSafely()
        val pageName = route.optString("pageName")
        if (pageName.isEmpty()) return

        val code = route.optJSONObject("pageData")?.optString("code")
        val query = buildList {
            add("page_name=${encodeURIComponent(pageName)}")
            if (!code.isNullOrEmpty()) add("code=${encodeURIComponent(code)}")
        }.joinToString("&")
        val urlPrefix: String = js("window.location.origin + window.location.pathname")
        window.location.href = "$urlPrefix?$query"
    }
}

private class FinanceWebDelegator : KuiklyRenderViewDelegatorDelegate {
    private val delegate = KuiklyRenderViewDelegator(this)

    override fun registerExternalModule(kuiklyRenderExport: IKuiklyRenderExport) {
        super.registerExternalModule(kuiklyRenderExport)
        kuiklyRenderExport.moduleExport(RouterModule.MODULE_NAME) { FinanceWebRouterModule() }
    }

    fun attach(containerId: String, pageName: String, pageData: Map<String, Any>, size: SizeI) {
        delegate.onAttach(containerId, pageName, pageData, size)
    }

    fun resume() = delegate.onResume()

    fun pause() = delegate.onPause()

    fun detach() = delegate.onDetach()
}

fun main() {
    installStandaloneHostCompatibility()
    val query: Map<String, String> = window.location.search.removePrefix("?")
        .split("&")
        .mapNotNull { pair: String ->
            val parts = pair.split("=", limit = 2)
            if (parts.isEmpty() || parts.first().isEmpty()) null
            else decodeURIComponent(parts.first()) to decodeURIComponent(parts.getOrElse(1) { "" })
        }
        .toMap()
    val pageName = query["page_name"] ?: "finance_home"
    val pageData = mutableMapOf<String, Any>(
        "statusBarHeight" to 0f,
        "activityWidth" to window.innerWidth,
        "activityHeight" to window.innerHeight,
        "host" to "h5",
        "mock" to 1,
        "param" to query,
    )
    query["demoState"]?.let { pageData["demoState"] = it }
    query["code"]?.let { pageData["code"] = it }

    val delegator = FinanceWebDelegator()
    delegator.attach(
        containerId = "root",
        pageName = pageName,
        pageData = pageData,
        size = SizeI(window.innerWidth, window.innerHeight),
    )
    delegator.resume()
    document.getElementById("boot")?.remove()

    document.addEventListener("visibilitychange", {
        if (document.asDynamic().hidden as Boolean) delegator.pause() else delegator.resume()
    })
    window.onbeforeunload = {
        delegator.detach()
        null
    }
}
