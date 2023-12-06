/**
 * Additional extensions of PluginInstance.
 *
 * @author qingyu
 * <p>Create on 2023/12/06 09:02</p>
 */

package cn.buffcow.hyper5g.extension

import fake.com.android.systemui.shared.plugins.PluginInstance

internal val PluginInstance.pluginClassLoader
    get() = pluginFactory.classLoaderFactory.get()
