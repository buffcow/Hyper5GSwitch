package cn.buffcow.hyper5g.hooker

import android.content.Context
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.IntType
import miui.telephony.TelephonyManager

/**
 * Hooker for control center in system ui plugin.
 *
 * @author qingyu
 * <p>Create on 2023/11/30 17:58</p>
 */
class ControlCenterHooker private constructor(pluginCtx: Context) : YukiBaseHooker() {

    private val pluginClsLoader by lazy { pluginCtx.classLoader }

    private val telephonyManager by lazy { TelephonyManager.getDefault() }

    override fun onHook() {
        if (telephonyManager.isFiveGCapable) {
            DetailPanelHooker.newInstance(pluginClsLoader) { panelHooker ->
                "com.android.systemui.qs.tiles.MiuiCellularTile\$CellSignalCallback".toClass().method {
                    param(IntType)
                    name = "setDefaultSim"
                }.hook().after {
                    panelHooker.setDefaultSim(args(0).int())
                }
            }.also(::loadHooker)
        }
    }

    companion object {
        private var sInstance: ControlCenterHooker? = null

        fun newInstance(pluginCtx: Context): ControlCenterHooker {
            return sInstance ?: synchronized(this) {
                sInstance ?: ControlCenterHooker(pluginCtx).also {
                    sInstance = it
                }
            }
        }
    }
}
