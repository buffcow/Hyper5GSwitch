package cn.buffcow.hyper5g.hooker

import android.content.ComponentName
import android.content.Context
import android.content.ContextWrapper
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog

/**
 * Hooker for system ui while plugin is loading.
 *
 * @author qingyu
 * <p>Create on 2023/12/01 16:05</p>
 */
object PluginLoader : YukiBaseHooker() {
    override fun onHook() {
        "com.android.systemui.shared.plugins.PluginInstance\$PluginFactory".toClass().method {
            name = "createPluginContext"
        }.hook().after {
            val wrapper = result<ContextWrapper>() ?: kotlin.run {
                YLog.error("Failed to create plugin context.")
                return@after
            }
            val cmp = instance
                .current()
                .field { name = "mComponentName" }
                .cast<ComponentName>()
            onPluginLoaded(wrapper, cmp)
        }
    }

    private fun onPluginLoaded(pluginCtx: Context, cmp: ComponentName?) {
        if (cmp?.packageName == "miui.systemui.plugin"
            && cmp.className == "miui.systemui.controlcenter.MiuiControlCenter"
        ) {
            YLog.info("Plugin for systemui control center loaded.")
            loadHooker(ControlCenterHooker.newInstance(pluginCtx))
        }
    }
}
