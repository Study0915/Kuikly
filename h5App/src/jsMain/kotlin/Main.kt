package io.github.study0915.kuiklyfinance.web

import com.tencent.kuikly.core.render.web.expand.KuiklyRenderViewDelegatorDelegate
import com.tencent.kuikly.core.render.web.ktx.SizeI
import com.tencent.kuikly.core.render.web.runtime.web.expand.KuiklyRenderViewDelegator
import kotlinx.browser.document
import kotlinx.browser.window

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
            "finance_home",
            mapOf("host" to "h5", "mock" to 1),
            SizeI(window.innerWidth, window.innerHeight),
        )
    }

    fun resume() = delegate.onResume()
    fun pause() = delegate.onPause()
    fun detach() = delegate.onDetach()
}

fun main() {
    installStandaloneHostCompatibility()
    val delegator = Task1WebDelegator()
    delegator.attach()
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
