package cn.buffcow.hyper5g.hooker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import cn.buffcow.hyper5g.R
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XposedHelpers.callMethod
import miui.telephony.TelephonyManager
import java.lang.ref.WeakReference

/**
 * Hooker for detail panel controller of system ui plugin.
 *
 * @author qingyu
 * <p>Create on 2024/12/31 17:22</p>
 */
class DetailPanelHooker private constructor(
    private val pluginClassLoader: ClassLoader,
    private val afterHooked: ((DetailPanelHooker) -> Unit)?,
) : YukiBaseHooker() {

    private val telephonyManager by lazy { TelephonyManager.getDefault() }

    private var _headerLayout: WeakReference<ViewGroup>? = null
    private val headerLayout get() = _headerLayout?.get()

    private inline val headerTitleTv: TextView?
        get() = headerLayout?.findViewById(android.R.id.title)

    private inline val header5GToggle: CheckBox?
        get() = headerLayout?.findViewById(android.R.id.toggle)

    override fun onHook() {
        "miui.systemui.controlcenter.panel.detail.DetailPanelController".toClass(pluginClassLoader).apply {
            method { name = "onCreate" }.hook().after {
                add5GDetailHeaderLayout(instance)
            }

            method {
                paramCount(1)
                name = "setupDetailHeader"
            }.hook().after {
                headerLayout?.let { setup5GDetailHeader(instance, it) }
            }

            method { name = "updateTexts" }.hook().after {
                headerTitleTv?.update5GHeaderText()
            }

            method { name = "updateBackgroundColor" }.hook().after {
                headerTitleTv?.update5GHeaderBgColor(getDetailAdapter(instance))
            }

            method { name = "onDestroy" }.hook().after { onDestroy() }

            afterHooked?.invoke(this@DetailPanelHooker)
        }
    }

    fun setDefaultSim(slot: Int?) {
        header5GToggle?.apply {
            postDelayed({ update5GHeaderToggleStatus(slot) }, 100)
        }
    }

    private fun add5GDetailHeaderLayout(ctrl: Any) {
        YLog.debug("add5GDetailHeaderLayout, ctrl=$ctrl")
        val ctx = callMethod(ctrl, "getContext") as Context
        val res = ctx.resources

        fun inflateDetailHeaderLayout(root: ViewGroup): View {
            @SuppressLint("DiscouragedApi")
            val headerLayoutId = res.getIdentifier(
                "detail_header",
                "layout",
                "miui.systemui.plugin"
            )
            return LayoutInflater.from(ctx).inflate(headerLayoutId, root, false).apply {
                (this as ViewGroup)
                id = android.R.id.content
                getChildAt(0).id = android.R.id.title
                (layoutParams as? LinearLayout.LayoutParams)?.also { params ->
                    params.topMargin = 0
                    layoutParams = params
                }
                _headerLayout = WeakReference(this)
            }
        }

        (callMethod(ctrl, "getContent") as LinearLayout).also { parent ->
            inflateDetailHeaderLayout(parent).also {
                parent.addView(it, 1, it.layoutParams)
            }
        }
    }

    private fun setup5GDetailHeader(ctrl: Any, header: ViewGroup) {
        header.visibility = if (isCellularDetailPanel(ctrl)) View.VISIBLE else View.GONE
        headerTitleTv?.apply {
            update5GHeaderText()
            update5GHeaderBgColor(getDetailAdapter(ctrl))
            setOnLongClickListener {
                Intent().apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK xor Intent.FLAG_ACTIVITY_CLEAR_TASK
                    setClassName(
                        PKG_NAME_PHONE,
                        "$PKG_NAME_PHONE.settings.MiuiFiveGNetworkSetting"
                    )
                    postStartActivityDismissingKeyguard(ctrl, this)
                }
                true
            }
            setOnClickListener(object : View.OnClickListener {
                private var lastClickTime: Long = 0

                override fun onClick(v: View) {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastClickTime < 300) {
                        Intent().apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK xor Intent.FLAG_ACTIVITY_CLEAR_TASK
                            setClassName(
                                PKG_NAME_PHONE,
                                "$PKG_NAME_PHONE.settings.PreferredNetworkTypeListPreference"
                            )
                            postStartActivityDismissingKeyguard(ctrl, this)
                        }
                    }
                    lastClickTime = currentTime
                }
            })
        }
        header5GToggle?.apply {
            update5GHeaderToggleStatus()
            setOnCheckedChangeListener { _, isChecked ->
                telephonyManager.isUserFiveGEnabled = isChecked
            }
        }
    }

    private fun TextView.update5GHeaderText() {
        refreshModuleAppResources()
        text = moduleAppResources.getString(R.string.hyper_5g_switch_title)
    }

    private fun TextView.update5GHeaderBgColor(adapter: Any?) {
        adapter ?: return
        val compatCls = "miui.systemui.controlcenter.utils.DetailAdapterCompat".toClass(pluginClassLoader)
        (callMethod(
            XposedHelpers.getStaticObjectField(compatCls, "INSTANCE"),
            "getTitleTextColorCompat",
            adapter, context
        ) as? Int)?.also(::setTextColor)
    }

    private fun CheckBox.update5GHeaderToggleStatus(slot: Int? = null) {
        isChecked = slot?.let {
            telephonyManager.isUserFiveGEnabled(it)
        } ?: telephonyManager.isUserFiveGEnabled
    }

    private fun onDestroy() {
        YLog.debug("onDestroy")
        headerTitleTv?.apply {
            setOnClickListener(null)
            setOnLongClickListener(null)
        }
        _headerLayout?.clear()
        _headerLayout = null
    }

    private fun postStartActivityDismissingKeyguard(ctrl: Any, intent: Intent, delay: Int = 200) {
        XposedHelpers.getObjectField(ctrl, "activityStarter").apply {
            callMethod(this, "postStartActivityDismissingKeyguard", intent, delay)
        }
    }

    private fun getDetailAdapter(ctrl: Any): Any? {
        return XposedHelpers.getObjectField(ctrl, "detailAdapter")
    }

    private fun isCellularDetailPanel(ctrl: Any): Boolean {
        val adapter = getDetailAdapter(ctrl) ?: return false
        return (callMethod(adapter, "getSettingsIntent") as? Intent)?.let {
            it.component?.let { cmp ->
                cmp.className == "$PKG_NAME_PHONE.settings.MobileNetworkSettings"
                        && cmp.packageName == PKG_NAME_PHONE
            }
        } == true
    }

    companion object {
        private const val PKG_NAME_PHONE = "com.android.phone"

        private var sInstance: DetailPanelHooker? = null

        fun newInstance(
            pluginClassLoader: ClassLoader,
            afterHooked: ((DetailPanelHooker) -> Unit)? = null,
        ): DetailPanelHooker {
            return sInstance ?: synchronized(this) {
                sInstance ?: DetailPanelHooker(pluginClassLoader, afterHooked).also {
                    sInstance = it
                }
            }
        }
    }
}
