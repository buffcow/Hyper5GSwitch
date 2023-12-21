/**
 * Objects that stores string constants.
 *
 * @author qingyu
 * <p>Create on 2023/12/01 15:56</p>
 */

package cn.buffcow.hyper5g.extension

import android.content.ComponentName

const val LOG_TAG = "Hyper5GSwitch"

object SystemUI {
    const val PACKAGE_NAME = "com.android.systemui"

    object Plugin {
        const val PACKAGE_NAME = "miui.systemui.plugin"
        val CMP_CONTROL_CENTER = ComponentName(PACKAGE_NAME, "miui.systemui.controlcenter.MiuiControlCenter")
    }
}

object Phone {
    private const val PACKAGE_NAME = "com.android.phone"
    val CMP_NET_SETTINGS = ComponentName(PACKAGE_NAME, "$PACKAGE_NAME.settings.MobileNetworkSettings")
    val CMP_FIVEG_SETTING = ComponentName(PACKAGE_NAME, "$PACKAGE_NAME.settings.MiuiFiveGNetworkSetting")
    val CMP_NET_TYPE_PREF = ComponentName(PACKAGE_NAME, "$PACKAGE_NAME.settings.PreferredNetworkTypeListPreference")
}

object CellSignalCallback {
    const val M_setDefaultSim = "setDefaultSim"
    const val CLASS_NAME = "com.android.systemui.qs.tiles.MiuiCellularTile\$CellSignalCallback"
}
