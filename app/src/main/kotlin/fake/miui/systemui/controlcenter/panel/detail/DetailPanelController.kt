package fake.miui.systemui.controlcenter.panel.detail

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import cn.buffcow.hyper5g.extension.SystemUI
import cn.buffcow.hyper5g.extension.children
import cn.buffcow.hyper5g.extension.updateLayoutParams
import fake.BaseFaker
import fake.com.android.systemui.plugins.qs.DetailAdapter

/**
 * @author qingyu
 * <p>Create on 2023/12/01 17:02</p>
 */
internal class DetailPanelController(ctrl: Any) : BaseFaker(ctrl) {

    val inflatedHeaderView: ViewGroup
        get() = getView().findViewById(android.R.id.content)

    val headerTiltleTv: TextView
        get() = inflatedHeaderView.findViewById(android.R.id.title)

    val header5GToggle: CheckBox
        get() = inflatedHeaderView.findViewById(android.R.id.toggle)

    val detailAdapter: DetailAdapter get() = DetailAdapter(fieldAny("detailAdapter"))

    fun getView(): ViewGroup = invokeAny("getView")

    private fun getContext(): Context = invokeAny("getContext")

    private fun getContent(): LinearLayout = invokeAny("getContent")

    fun addDetailHeaderLayout() {
        val ctx = getContext()
        val res = ctx.resources

        fun inflateDetailHeaderLayout(root: ViewGroup): View {
            @SuppressLint("DiscouragedApi")
            val headerLayoutId = res.getIdentifier(
                "detail_header",
                "layout",
                SystemUI.Plugin.PACKAGE_NAME
            )
            return LayoutInflater.from(ctx).inflate(headerLayoutId, root, false).apply {
                (this as ViewGroup)
                id = android.R.id.content
                children.first().id = android.R.id.title
                updateLayoutParams<LinearLayout.LayoutParams> { topMargin = 0 }
            }
        }

        getContent().also { parent ->
            inflateDetailHeaderLayout(parent).also {
                parent.addView(it, 1, it.layoutParams)
            }
        }
    }

    companion object {
        const val M_onCreate = "onCreate"
        const val M_updateTexts = "updateTexts"
        const val M_setupDetailHeader = "setupDetailHeader"
        const val M_updateBackgroundColor = "updateBackgroundColor"
        const val CLASS_NAME = "miui.systemui.controlcenter.panel.detail.DetailPanelController"
    }
}
