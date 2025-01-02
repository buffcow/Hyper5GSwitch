package cn.buffcow.hyper5g

import cn.buffcow.hyper5g.hooker.PluginLoader
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit

/**
 * Entry for hooker.
 *
 * @author qingyu
 * <p>Create on 2023/11/30 17:52</p>
 */
@InjectYukiHookWithXposed
class HookEntry : IYukiHookXposedInit {
    override fun onInit() = configs {
        debugLog { isDebug = BuildConfig.DEBUG; tag = LOG_TAG }
    }

    override fun onHook() = encase {
        loadApp("com.android.systemui") {
            loadHooker(PluginLoader)
        }
    }

    companion object {
        private const val LOG_TAG = "Hyper5GSwitch"
    }
}
