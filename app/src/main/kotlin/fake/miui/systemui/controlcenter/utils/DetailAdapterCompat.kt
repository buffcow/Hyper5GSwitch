package fake.miui.systemui.controlcenter.utils

import android.content.Context
import com.highcapable.yukihookapi.hook.factory.toClass
import de.robv.android.xposed.XposedHelpers.getStaticObjectField
import fake.BaseFaker
import fake.com.android.systemui.plugins.qs.DetailAdapter

/**
 * @author qingyu
 * <p>Create on 2023/12/02 17:05</p>
 */
internal class DetailAdapterCompat(pluginClassLoader: ClassLoader) : BaseFaker(
    getStaticObjectField(CLASS_NAME.toClass(pluginClassLoader), "INSTANCE")
) {

    fun getTitleTextColorCompat(adapter: DetailAdapter, context: Context): Int {
        return invokeAny("getTitleTextColorCompat", adapter.getSelf(), context)
    }

    companion object {
        private const val CLASS_NAME = "miui.systemui.controlcenter.utils.DetailAdapterCompat"
    }
}
