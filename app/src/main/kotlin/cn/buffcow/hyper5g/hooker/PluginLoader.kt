package cn.buffcow.hyper5g.hooker

import android.content.ContextWrapper
import cn.buffcow.hyper5g.extension.SystemUI
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import fake.com.android.systemui.shared.plugins.PluginFactory
import java.lang.ref.WeakReference

/**
 * Hooker for systemui while plugin is loading.
 *
 * @author qingyu
 * <p>Create on 2023/12/01 16:05</p>
 */
internal object PluginLoader : YukiBaseHooker() {
    override fun onHook() {
        PluginFactory.CLASS_NAME.toClass().method {
            name = PluginFactory.M_createPluginContext
        }.hook().after {
            val wrapper = result<ContextWrapper>() ?: kotlin.run {
                YLog.error("Failed to create plugin context.")
                return@after
            }
            onPluginLoaded(PluginFactory(instance).also { it.pluginCtxRef = WeakReference(wrapper) })
        }
    }

    private fun onPluginLoaded(factory: PluginFactory) {
        when (factory.mComponentName) {
            SystemUI.Plugin.CMP_CONTROL_CENTER -> {
                YLog.info("Plugin for sysui control center loaded.")
                loadHooker(ControlCenter(factory))
            }
        }
    }
}
