package cn.buffcow.hyper5g

import android.content.ComponentName
import android.content.Context
import android.util.Log
import de.robv.android.xposed.XposedHelpers
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface
import java.util.concurrent.CopyOnWriteArrayList

/**
 * @author qingyu
 * <p>Create on 2025/10/09 15:15</p>
 */
class MainModule : XposedModule() {

    private val hookers = CopyOnWriteArrayList<IPluginHooker>()

    override fun onModuleLoaded(param: XposedModuleInterface.ModuleLoadedParam) {
        xposedModule = this
    }

    override fun onPackageReady(param: XposedModuleInterface.PackageReadyParam) {
        super.onPackageReady(param)

        val packageName = param.packageName
        log("onPackageLoaded: $packageName")

        if (!param.isFirstPackage || packageName != "com.android.systemui") return

        addHookerIfAbsent(ControlCenterHooker)

        hookPluginFactory(param.classLoader)
    }

    private fun hookPluginFactory(classLoader: ClassLoader) {
        XposedHelpers.findMethodExact(
            $$"com.android.systemui.shared.plugins.PluginInstance$PluginFactory",
            classLoader,
            "createPluginContext"
        ).also { method ->
            xposedModule.hook(method).intercept { chain ->
                chain.proceed().also { result ->
                    if (hookers.isNotEmpty()) {
                        val pluginContext = result as Context
                        val cmp = XposedHelpers.getObjectField(chain.thisObject, "mComponentName") as ComponentName
                        hookers.forEach {
                            it.onPluginCreated(classLoader, pluginContext, cmp)
                        }
                    }
                }
            }
        }
    }

    private fun addHookerIfAbsent(hooker: IPluginHooker) {
        hookers.addIfAbsent(hooker)
    }
}

private const val TAG = "Hyper5GSwitch"

lateinit var xposedModule: XposedModule
    private set

fun log(msg: String) = xposedModule.log(Log.DEBUG, TAG, msg)
// fun loge(msg: String) = xposedModule.log(Log.ERROR, TAG, msg)
// fun loge(msg: String, tr: Throwable) = xposedModule.log(Log.ERROR, TAG, msg, tr)
