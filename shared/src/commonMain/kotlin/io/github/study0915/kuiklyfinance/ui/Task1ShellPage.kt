package io.github.study0915.kuiklyfinance.ui

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View

internal object Task1ShellContract {
    const val PAGE_NAME: String = "finance_home"
    const val DISCLAIMER: String = "仅作技术演示，不构成投资建议"
}

@Page(Task1ShellContract.PAGE_NAME, supportInLocal = true)
class Task1ShellPage : Pager() {
    override fun body(): ViewBuilder = {
        attr {
            backgroundColor(Color(0xFF07111FL))
        }
        View {
            attr {
                flex(1f)
                padding(32f)
                justifyContentCenter()
                alignItemsCenter()
            }
            Text {
                attr {
                    text("TASK 1 / RESET")
                    color(Color(0xFFFFC857L))
                    fontSize(13f)
                    fontWeight600()
                }
            }
            Text {
                attr {
                    text("新版 Task 1 正在重新开发")
                    color(Color(0xFFF0F4F8L))
                    fontSize(25f)
                    fontWeight600()
                    marginTop(14f)
                    textAlignCenter()
                }
            }
            Text {
                attr {
                    text("[MOCK] [UNVERIFIED]\n当前仅提供可编译、可验收的跨端空壳")
                    color(Color(0xFF8EA0B8L))
                    fontSize(14f)
                    lineHeight(21f)
                    marginTop(14f)
                    textAlignCenter()
                }
            }
            Text {
                attr {
                    text(Task1ShellContract.DISCLAIMER)
                    color(Color(0xFF8EA0B8L))
                    fontSize(11f)
                    marginTop(28f)
                    textAlignCenter()
                }
            }
        }
    }
}
