package cn.buffcow.hyper5g

import android.content.ComponentName
import android.content.Context
import android.util.Log
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface

/**
 * @author qingyu
 * <p>Create on 2025/10/09 15:15</p>
 */
class MainModule : XposedModule() {

    override fun onModuleLoaded(param: XposedModuleInterface.ModuleLoadedParam) {
        xposedModule = this
    }

    override fun onPackageReady(param: XposedModuleInterface.PackageReadyParam) {
        super.onPackageReady(param)

        val packageName = param.packageName
        if (!param.isFirstPackage || packageName != "com.android.systemui") return

        logDebug("onPackageLoaded: $packageName")
        hookPluginFactory(param.classLoader)
    }

    private fun hookPluginFactory(classLoader: ClassLoader) {
        xposedModule.hook(
            loadClass(
                $$"com.android.systemui.shared.plugins.PluginInstance$PluginFactory",
                classLoader
            ).findMethod("createPluginContext")
        ).intercept { chain ->
            val pluginContext = chain.proceed() as Context
            val pluginFactory = requireNotNull(chain.thisObject)
            kotlin.runCatching {
                try {
                    // HyperOS 2 or HyperOS 3
                    pluginFactory.readField("mComponentName")
                } catch (_: NoSuchFieldException) {
                    // HyperOS 4
                    pluginFactory.readField("componentName")
                }
            }.onFailure {
                logError("failed to resolve plugin component name", it)
            }.getOrNull()?.let { cmp ->
                kotlin.runCatching {
                    ControlCenterHooker(
                        xposedModule = xposedModule,
                        onDebug = ::logDebug,
                        onError = ::logError
                    ).install(
                        classLoader = classLoader,
                        pluginContext = pluginContext,
                        component = cmp as ComponentName
                    )
                }.onFailure {
                    logError("install ControlCenterHooker failed", it)
                }
            }
            pluginContext
        }
    }
}

private fun logDebug(message: String) {
    xposedModule.log(Log.DEBUG, TAG, message)
}

private fun logError(message: String, throwable: Throwable? = null) {
    if (throwable == null) {
        xposedModule.log(Log.ERROR, TAG, message)
    } else {
        xposedModule.log(Log.ERROR, TAG, message, throwable)
    }
}

private const val TAG = "Hyper5GSwitch"

private lateinit var xposedModule: XposedModule
