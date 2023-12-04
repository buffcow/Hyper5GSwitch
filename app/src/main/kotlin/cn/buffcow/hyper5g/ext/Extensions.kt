/**
 * @author qingyu
 * <p>Create on 2023/12/02 16:31</p>
 */

package cn.buffcow.hyper5g.ext

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.core.view.descendants
import androidx.core.view.updateLayoutParams
import cn.buffcow.hyper5g.HookEntry
import com.highcapable.yukihookapi.hook.factory.toClass
import fake.com.android.systemui.Dependency
import fake.com.android.systemui.plugins.ActivityStarter
import fake.com.android.systemui.plugins.qs.DetailAdapter
import fake.com.android.systemui.shared.plugins.PluginInstance
import fake.miui.systemui.controlcenter.panel.detail.DetailPanelController

internal val PluginInstance.pluginClassLoader
    get() = pluginFactory.classLoaderFactory.get()

internal val DetailPanelController.inflatedHeaderView
    get() = getView().findViewById<ViewGroup>(android.R.id.content)

internal val DetailPanelController.headerTiltleTv
    get() = inflatedHeaderView.findViewById<TextView>(android.R.id.title)

internal val DetailPanelController.header5GToggle
    get() = inflatedHeaderView.findViewById<CheckBox>(android.R.id.toggle)

internal val DetailAdapter?.isCellularDetailPanel
    get() = this?.let {
        it.hasHeader() && it.getSettingsIntent().component == Phone.CMP_NET_SETTINGS
    } ?: false

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

internal fun String.toClassByAppClsLoader() = this.toClass<Any>(HookEntry.appClassLoader)

internal fun Dependency.getActivityStarter() = ActivityStarter(get(ActivityStarter.CLASS_NAME.toClassByAppClsLoader()))
