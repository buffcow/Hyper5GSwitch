package cn.buffcow.hyper5g

import android.content.ComponentName
import android.content.Context
import io.github.libxposed.api.XposedModuleInterface

/**
 * @author qingyu
 * <p>Create on 2025/10/09 17:44</p>
 */
interface IPluginHooker {
    fun onPluginCreated(
        param: XposedModuleInterface.PackageLoadedParam,
        pluginContext: Context,
        component: ComponentName
    )
}
