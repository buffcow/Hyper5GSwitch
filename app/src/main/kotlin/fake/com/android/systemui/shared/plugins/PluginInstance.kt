package fake.com.android.systemui.shared.plugins

import android.content.ComponentName
import fake.BaseFaker
import java.util.function.Supplier

/**
 * @author qingyu
 * <p>Create on 2023/12/01 14:58</p>
 */
internal class PluginInstance(plugin: Any) : BaseFaker(plugin) {

    val componentName: ComponentName get() = fieldAny("mComponentName")

    val pluginFactory: PluginFactory get() = PluginFactory(fieldAny("mPluginFactory"))

    class PluginFactory(factory: Any) : BaseFaker(factory) {
        val classLoaderFactory: Supplier<ClassLoader> get() = fieldAny("mClassLoaderFactory")
    }

    companion object {
        const val CLASS_NAME = "com.android.systemui.shared.plugins.PluginInstance"
    }
}
