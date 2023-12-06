package cn.buffcow.hyper5g.hooker

import android.content.Intent
import android.widget.CheckBox
import android.widget.TextView
import cn.buffcow.hyper5g.R
import cn.buffcow.hyper5g.extension.CellSignalCallback
import cn.buffcow.hyper5g.extension.Phone
import cn.buffcow.hyper5g.extension.addDetailHeaderLayout
import cn.buffcow.hyper5g.extension.getActivityStarter
import cn.buffcow.hyper5g.extension.header5GToggle
import cn.buffcow.hyper5g.extension.headerTiltleTv
import cn.buffcow.hyper5g.extension.inflatedHeaderView
import cn.buffcow.hyper5g.extension.isCellularDetailPanel
import cn.buffcow.hyper5g.extension.isVisible
import cn.buffcow.hyper5g.extension.pluginClassLoader
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.java.IntType
import fake.com.android.systemui.Dependency
import fake.com.android.systemui.shared.plugins.PluginInstance
import fake.miui.systemui.controlcenter.panel.detail.DetailPanelController
import fake.miui.systemui.controlcenter.utils.DetailAdapterCompat
import miui.telephony.TelephonyManager

/**
 * Hooker for control center in systemui plugin.
 *
 * @author qingyu
 * <p>Create on 2023/11/30 17:58</p>
 */
internal class ControlCenter(plugin: PluginInstance) : YukiBaseHooker() {

    private val pluginClassLoader = plugin.pluginClassLoader

    private val telephonyManager by lazy { TelephonyManager.getDefault() }

    private val adapterCompat by lazy { DetailAdapterCompat(pluginClassLoader) }
    private val tv5GTitle by lazy { moduleAppResources.getString(R.string.hyper_5g_switch_title) }

    private val activityStarter by lazy { Dependency.getActivityStarter() }
    private val fivegSettingIntent by lazy { Intent().setComponent(Phone.CMP_FIVEG_SETTING) }

    private var panelController: DetailPanelController? = null

    override fun onHook() {
        if (telephonyManager.isFiveGCapable) {
            hookDetailPanelController()
            hookCellSignalCallback()
        }
    }

    private fun hookDetailPanelController() {
        val ctrlCls = DetailPanelController.CLASS_NAME.toClass(pluginClassLoader)

        ctrlCls.method { name = DetailPanelController.M_onCreate }.hook {
            after {
                DetailPanelController(instance).also { ctrl ->
                    panelController = ctrl
                    ctrl.addDetailHeaderLayout()
                }
            }
        }

        ctrlCls.method {
            paramCount(1)
            name = DetailPanelController.M_setupDetailHeader
        }.hook { after { setupDetailHeader() } }

        ctrlCls.method { name = DetailPanelController.M_updateBackgroundColor }.hook {
            after { panelController?.headerTiltleTv?.updateBackgroundColor() }
        }
    }

    private fun setupDetailHeader() {
        panelController?.apply {
            inflatedHeaderView.isVisible = detailAdapter.isCellularDetailPanel
            headerTiltleTv.apply {
                text = tv5GTitle
                updateBackgroundColor()
                setOnLongClickListener {
                    activityStarter.postStartActivityDismissingKeyguard(fivegSettingIntent, 0)
                    true
                }
            }
            header5GToggle.apply {
                updateToggleStatus()
                setOnCheckedChangeListener { _, isChecked ->
                    telephonyManager.isUserFiveGEnabled = isChecked
                }
            }
        }
    }

    private fun CheckBox.updateToggleStatus(slot: Int? = null) {
        isChecked = slot?.let {
            telephonyManager.isUserFiveGEnabled(it)
        } ?: telephonyManager.isUserFiveGEnabled
    }

    private fun TextView.updateBackgroundColor() {
        val adapter = panelController?.detailAdapter ?: return
        val color = adapterCompat.getTitleTextColorCompat(adapter, context)
        setTextColor(color)
    }

    private fun hookCellSignalCallback() {
        CellSignalCallback.CLASS_NAME.toClass().method {
            param(IntType)
            name = CellSignalCallback.M_setDefaultSim
        }.hook {
            after {
                panelController?.apply {
                    getView().postDelayed({
                        header5GToggle.updateToggleStatus(args(0).int())
                    }, 100)
                }
            }
        }
    }
}
