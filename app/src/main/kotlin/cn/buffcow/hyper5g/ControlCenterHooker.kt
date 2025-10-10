package cn.buffcow.hyper5g

import android.content.ComponentName
import android.content.Context
import cn.buffcow.xp.helper.HookerClassHelper
import cn.buffcow.xp.helper.ModuleHelper
import cn.buffcow.xp.helper.XposedHelpers
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModuleInterface
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
        param: XposedModuleInterface.PackageLoadedParam,
        pluginContext: Context,
        component: ComponentName
    ) {
        if (component.packageName == "miui.systemui.plugin"
            && component.className == "miui.systemui.controlcenter.MiuiControlCenter"
        ) {
            XposedHelpers.log("Plugin for systemui control center created.")
            if (TelephonyManager.getDefault().isFiveGCapable) {
                panelModifier.mod(pluginContext)
                ModuleHelper.findAndHookMethod(
                    "com.android.systemui.qs.tiles.MiuiCellularTile\$CellSignalCallback",
                    param.classLoader,
                    "setDefaultSim",
                    Int::class.javaPrimitiveType, object : HookerClassHelper.MethodHook() {
                        override fun after(callback: XposedInterface.AfterHookCallback) {
                            super.after(callback)
                            panelModifier.notifyDefaultSimSlotChanged(callback.args[0] as Int)
                        }
                    }
                )
            }
        }
    }
}
