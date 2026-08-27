package cn.buffcow.hyper5g

import android.content.ComponentName
import android.content.Context
import io.github.libxposed.api.XposedModule
import miui.telephony.TelephonyManager

/**
 * Hooker for control center in system ui plugin.
 *
 * @author qingyu
 * <p>Create on 2023/11/30 17:58</p>
 */
internal class ControlCenterHooker(
    private val xposedModule: XposedModule,
    private val onDebug: (msg: String) -> Unit,
    private val onError: (msg: String, throwable: Throwable?) -> Unit,
) {

    private val panelHooker by lazy {
        DetailPanelHooker(xposedModule, onDebug)
    }

    fun install(
        classLoader: ClassLoader,
        pluginContext: Context,
        component: ComponentName
    ) {
        if (component.packageName == "miui.systemui.plugin"
            && component.className == "miui.systemui.controlcenter.MiuiControlCenter"
        ) {
            onDebug("Plugin for systemui control center created.")
            if (TelephonyManager.getDefault().isFiveGCapable) {
                panelHooker.install(pluginContext)
                loadClass(
                    $$"com.android.systemui.qs.tiles.MiuiCellularTile$CellSignalCallback",
                    classLoader
                ).findMethod("setDefaultSim", Integer.TYPE).also { method ->
                    xposedModule.hook(method).intercept { chain ->
                        chain.proceed().also {
                            panelHooker.notifyDefaultSimSlotChanged(chain.args[0] as Int)
                        }
                    }
                }
            } else {
                onError("FiveG is not capable.", null)
            }
        }
    }
}
