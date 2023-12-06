/**
 * Additional extensions of DetailPanelController.
 *
 * @author qingyu
 * <p>Create on 2023/12/06 09:04</p>
 */

package cn.buffcow.hyper5g.extension

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import fake.miui.systemui.controlcenter.panel.detail.DetailPanelController

internal val DetailPanelController.inflatedHeaderView
    get() = getView().findViewById<ViewGroup>(android.R.id.content)

internal val DetailPanelController.headerTiltleTv
    get() = inflatedHeaderView.findViewById<TextView>(android.R.id.title)

internal val DetailPanelController.header5GToggle
    get() = inflatedHeaderView.findViewById<CheckBox>(android.R.id.toggle)

internal fun DetailPanelController.addDetailHeaderLayout() {
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
            (children.first() as TextView).id = android.R.id.title

            updateLayoutParams<LinearLayout.LayoutParams> { topMargin = 0 }
        }
    }

    getView().descendants.filterIsInstance<LinearLayout>().firstOrNull { v ->
        v.id != View.NO_ID && res.getResourceEntryName(v.id) == "scale_content"
    }?.also { parent ->
        inflateDetailHeaderLayout(parent).also {
            parent.addView(it, 1, it.layoutParams)
        }
    }
}
