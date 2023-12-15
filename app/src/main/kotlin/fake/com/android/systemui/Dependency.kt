package fake.com.android.systemui

import cn.buffcow.hyper5g.extension.toClassByAppClsLoader
import fake.BaseStaticFacker
import fake.com.android.systemui.plugins.ActivityStarter

/**
 * @author qingyu
 * <p>Create on 2023/12/04 10:45</p>
 */
internal object Dependency : BaseStaticFacker("com.android.systemui.Dependency") {

    private fun get(clazz: Class<Any>): Any = invokeAny("get", clazz)

    fun getActivityStarter() = ActivityStarter(get(ActivityStarter.CLASS_NAME.toClassByAppClsLoader()))
}
