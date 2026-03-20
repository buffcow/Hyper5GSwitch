package cn.buffcow.hyper5g

import android.content.ComponentName
import android.content.Context
import de.robv.android.xposed.XposedHelpers
import miui.telephony.TelephonyManager

/**
 * Hooker for control center in system ui plugin.
 *
 * @author qingyu
 * <p>Create on 2023/11/30 17:58</p>
 */
object ControlCenterHooker : IPluginHooker {

    private val panelModifier by lazy {
        DetailPanelModifier()
    }

    override fun onPluginCreated(
        classLoader: ClassLoader,
        pluginContext: Context,
        component: ComponentName
    ) {
        if (component.packageName == "miui.systemui.plugin"
            && component.className == "miui.systemui.controlcenter.MiuiControlCenter"
        ) {
            log("Plugin for systemui control center created.")
            if (TelephonyManager.getDefault().isFiveGCapable) {
                panelModifier.mod(pluginContext)
                XposedHelpers.findMethodExact(
                    $$"com.android.systemui.qs.tiles.MiuiCellularTile$CellSignalCallback",
                    classLoader,
                    "setDefaultSim",
                    Int::class.javaPrimitiveType,
                ).also { method ->
                    xposedModule.hook(method).intercept { chain ->
                        chain.proceed().also {
                            panelModifier.notifyDefaultSimSlotChanged(chain.args[0] as Int)
                        }
                    }
                }
            }
        }
    }
}
