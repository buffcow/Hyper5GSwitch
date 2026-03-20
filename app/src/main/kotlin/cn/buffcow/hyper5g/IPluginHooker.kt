package cn.buffcow.hyper5g

import android.content.ComponentName
import android.content.Context

/**
 * @author qingyu
 * <p>Create on 2025/10/09 17:44</p>
 */
interface IPluginHooker {
    fun onPluginCreated(
        classLoader: ClassLoader,
        pluginContext: Context,
        component: ComponentName
    )
}
