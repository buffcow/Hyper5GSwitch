package cn.buffcow.hyper5g

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XposedHelpers.callMethod
import io.github.libxposed.api.XposedInterface
import miui.telephony.TelephonyManager
import java.lang.ref.WeakReference

/**
 * Detail panel modifier of system ui plugin.
 *
 * @author qingyu
 * <p>Create on 2024/12/31 17:22</p>
 */
class DetailPanelModifier : XposedInterface.Hooker {

    private val telephonyManager by lazy { TelephonyManager.getDefault() }

    private var _headerLayout: WeakReference<ViewGroup>? = null
    private val headerLayout get() = _headerLayout?.get()

    private inline val headerTitleTv: TextView?
        get() = headerLayout?.findViewById(android.R.id.title)

    private inline val header5GToggle: CheckBox?
        get() = headerLayout?.findViewById(android.R.id.toggle)

    private var moduleResources: Resources? = null

    fun mod(pluginContext: Context) {
        var isHyperOS3 = true
        val pluginClassLoader = pluginContext.classLoader

        val controllerClass = XposedHelpers.findClassIfExists(
            "miui.systemui.controlcenter.panel.secondary.detail.DetailPanelDelegate",
            pluginClassLoader
        ) ?: kotlin.run {
            isHyperOS3 = false
            XposedHelpers.findClass(
                "miui.systemui.controlcenter.panel.detail.DetailPanelController",
                pluginClassLoader
            )
        }

        // onCreate
        xposedModule
            .hook(controllerClass.getDeclaredMethod("onCreate"))
            .intercept(this)

        // setupDetailHeader
        XposedHelpers.findMethodExact(
            controllerClass,
            "setupDetailHeader",
            "com.android.systemui.plugins.qs.DetailAdapter"
        ).also { m -> xposedModule.hook(m).intercept(this) }

        // updateTexts
        xposedModule
            .hook(controllerClass.getDeclaredMethod("updateTexts"))
            .intercept(this)

        // updateResources
        controllerClass.getDeclaredMethod(
            if (isHyperOS3) "updateResources" else "updateBackgroundColor",
        ).also { m -> xposedModule.hook(m).intercept(this) }

        // onDestroy
        xposedModule
            .hook(controllerClass.getDeclaredMethod("onDestroy"))
            .intercept(this)
    }

    override fun intercept(chain: XposedInterface.Chain): Any? {
        return chain.proceed().apply {
            val controller = chain.thisObject ?: return null
            when (chain.executable.name) {
                "onCreate" -> onCreate(controller)

                "setupDetailHeader" -> setup5GDetailHeader(controller)

                "updateTexts" -> headerTitleTv?.update5GHeaderText()

                "updateResources",
                "updateBackgroundColor" -> {
                    headerTitleTv?.update5GHeaderBgColor(getDetailAdapter(controller))
                }

                "onDestroy" -> onDestroy()
            }
        }
    }

    fun notifyDefaultSimSlotChanged(slot: Int?) {
        header5GToggle?.apply {
            postDelayed({ update5GHeaderToggleStatus(slot) }, 100)
        }
    }

    private fun onCreate(ctrl: Any) {
        log("onCreate, ctrl=$ctrl")
        val ctx = callMethod(ctrl, "getContext") as Context
        initModuleResource(ctx)
        inflate5GDetailHeader(ctx, ctrl)
    }

    private fun inflate5GDetailHeader(ctx: Context, ctrl: Any) {
        fun inflateDetailHeaderLayout(root: ViewGroup): View? {
            @SuppressLint("DiscouragedApi")
            val headerLayoutId = ctx.getIdentifier("detail_header", "layout")
            return if (headerLayoutId != 0) {
                LayoutInflater.from(ctx).inflate(headerLayoutId, root, false).apply {
                    (this as ViewGroup)
                    id = android.R.id.content
                    getChildAt(0).id = android.R.id.title
                    (layoutParams as? LinearLayout.LayoutParams)?.also { params ->
                        params.topMargin = 0
                        layoutParams = params
                    }
                    _headerLayout = WeakReference(this)
                }
            } else {
                null
            }
        }

        getContent(ctx, ctrl).also { parent ->
            inflateDetailHeaderLayout(parent)?.let {
                parent.addView(it, 1, it.layoutParams)
            }
        }
    }

    private fun setup5GDetailHeader(ctrl: Any) {
        headerLayout?.let {
            it.visibility = if (isCellularDetailPanel(ctrl)) View.VISIBLE else View.GONE
        }
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
        moduleResources?.let {
            text = it.getString(R.string.hyper_5g_switch_title)
        }
    }

    private fun TextView.update5GHeaderBgColor(adapter: Any?) {
        adapter ?: return
        val compatCls = XposedHelpers.findClass(
            "miui.systemui.controlcenter.utils.DetailAdapterCompat",
            context.classLoader
        )
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

    private fun initModuleResource(ctx: Context) {
        if (moduleResources == null) {
            moduleResources = createModuleContext(ctx).resources
            log("created moduleResources: $moduleResources")
        }
    }

    private fun onDestroy() {
        log("onDestroy")
        headerTitleTv?.apply {
            setOnClickListener(null)
            setOnLongClickListener(null)
        }
        _headerLayout?.clear()
        _headerLayout = null
        moduleResources = null
    }

    private fun getContent(ctx: Context, ctrl: Any): ViewGroup {
        return (callMethod(ctrl, "getView") as ViewGroup).run {
            val id = ctx.getIdentifier("scale_content", "id")
            findViewById(id)
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun Context.getIdentifier(name: String, type: String, pkg: String = packageName): Int {
        return resources.getIdentifier(name, type, pkg)
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

    private fun createModuleContext(
        context: Context,
        modulePkg: String? = BuildConfig.APPLICATION_ID,
        config: Configuration? = null
    ): Context {
        return with(context.createPackageContext(modulePkg, Context.CONTEXT_IGNORE_SECURITY)) {
            config?.let { createConfigurationContext(config) } ?: this
        }
    }

    companion object {
        private const val PKG_NAME_PHONE = "com.android.phone"
    }
}
