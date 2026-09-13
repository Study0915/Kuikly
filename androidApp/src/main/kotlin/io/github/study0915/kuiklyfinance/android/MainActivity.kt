package io.github.study0915.kuiklyfinance.android

import android.graphics.Color
import android.os.Bundle
import android.os.Build
import android.view.KeyEvent
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.tencent.kuikly.core.render.android.adapter.IKRLogAdapter
import com.tencent.kuikly.core.render.android.adapter.KuiklyRenderAdapterManager
import com.tencent.kuikly.core.render.android.expand.KuiklyRenderViewBaseDelegator
import com.tencent.kuikly.core.render.android.expand.KuiklyRenderViewBaseDelegatorDelegate
import io.github.study0915.kuiklyfinance.ui.Task1Routes

class MainActivity : AppCompatActivity(), KuiklyRenderViewBaseDelegatorDelegate {
    private lateinit var delegator: KuiklyRenderViewBaseDelegator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.WHITE
        window.navigationBarColor = if (Build.VERSION.SDK_INT >= 26) Color.WHITE else Color.rgb(23, 33, 47)
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }
        KuiklyRenderAdapterManager.krLogAdapter = AndroidLogAdapter

        val container = FrameLayout(this).apply {
            setBackgroundColor(Color.rgb(245, 247, 250))
        }
        setContentView(container)

        delegator = KuiklyRenderViewBaseDelegator(this)
        delegator.onAttach(container, "", Task1Routes.FINANCE_HOME, mapOf("host" to "android", "mock" to 1))
    }

    override fun onResume() {
        super.onResume()
        delegator.onResume()
    }

    override fun onPause() {
        delegator.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        delegator.onDetach()
        super.onDestroy()
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP && delegator.onBackPressed()) {
            return true
        }
        return super.dispatchKeyEvent(event)
    }

    private object AndroidLogAdapter : IKRLogAdapter {
        override val asyncLogEnable: Boolean = true
        override fun i(tag: String, msg: String) = android.util.Log.i(tag, msg).let { }
        override fun d(tag: String, msg: String) = android.util.Log.d(tag, msg).let { }
        override fun e(tag: String, msg: String) = android.util.Log.e(tag, msg).let { }
    }
}
