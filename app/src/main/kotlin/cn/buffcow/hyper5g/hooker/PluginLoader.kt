package cn.buffcow.hyper5g.hooker

import cn.buffcow.hyper5g.ext.SystemUI
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.log.YLog
import fake.com.android.systemui.shared.plugins.PluginInstance

/**
 * Hooker for systemui while plugin is loading.
 *
 * @author qingyu
 * <p>Create on 2023/12/01 16:05</p>
 */
internal object PluginLoader : YukiBaseHooker() {
    override fun onHook() {
        PluginInstance.CLASS_NAME.toClass().constructor().hookAll {
            after { onPluginLoaded(PluginInstance(instance)) }
        }
    }

    private fun onPluginLoaded(plugin: PluginInstance) {
        when (plugin.componentName) {
            SystemUI.Plugin.CMP_CONTROL_CENTER -> {
                YLog.info("Plugin for sysui control center loaded.")
                loadHooker(ControlCenter(plugin))
            }
        }
    }
}
