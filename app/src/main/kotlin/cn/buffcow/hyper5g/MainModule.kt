package cn.buffcow.hyper5g

import android.content.ComponentName
import android.content.Context
import cn.buffcow.xp.helper.HookerClassHelper
import cn.buffcow.xp.helper.ModuleHelper
import cn.buffcow.xp.helper.XposedHelpers
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface
import java.util.concurrent.CopyOnWriteArrayList

/**
 * @author qingyu
 * <p>Create on 2025/10/09 15:15</p>
 */
class MainModule(
    base: XposedInterface,
    param: XposedModuleInterface.ModuleLoadedParam
) : XposedModule(base, param) {

    private val hookers = CopyOnWriteArrayList<IPluginHooker>()

    init {
        XposedHelpers.moduleInst = this
        XposedHelpers.TAG_NAME = "Hyper5G"
    }

    override fun onPackageLoaded(param: XposedModuleInterface.PackageLoadedParam) {
        super.onPackageLoaded(param)

        val packageName = param.packageName
        log("onPackageLoaded: $packageName")

        if (!param.isFirstPackage || packageName != "com.android.systemui") return

        addHookerIfAbsent(ControlCenterHooker)

        hookPluginFactory(param)
    }

    private fun hookPluginFactory(param: XposedModuleInterface.PackageLoadedParam) {
        ModuleHelper.findAndHookMethod(
            "com.android.systemui.shared.plugins.PluginInstance\$PluginFactory",
            param.classLoader,
            "createPluginContext",
            object : HookerClassHelper.MethodHook() {
                override fun after(callback: XposedInterface.AfterHookCallback) {
                    super.after(callback)
                    if (hookers.isNotEmpty()) {
                        val pluginContext = callback.result as Context
                        val cmp = XposedHelpers.getObjectField(callback.thisObject, "mComponentName") as ComponentName
                        hookers.forEach {
                            it.onPluginCreated(param, pluginContext, cmp)
                        }
                    }
                }
            }
        )
    }

    private fun addHookerIfAbsent(hooker: IPluginHooker) {
        hookers.addIfAbsent(hooker)
    }
}
