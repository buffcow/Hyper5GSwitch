package cn.buffcow.hyper5g

import cn.buffcow.hyper5g.ext.LOG_TAG
import cn.buffcow.hyper5g.ext.SystemUI
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
        appClassLoader?.also { Companion.appClassLoader = it }
        loadApp(SystemUI.PACKAGE_NAME) {
            loadHooker(PluginLoader)
        }
    }

    companion object {
        lateinit var appClassLoader: ClassLoader
    }
}
