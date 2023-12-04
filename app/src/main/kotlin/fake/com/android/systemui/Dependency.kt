package fake.com.android.systemui

import fake.BaseStaticFacker

/**
 * @author qingyu
 * <p>Create on 2023/12/04 10:45</p>
 */
internal object Dependency : BaseStaticFacker("com.android.systemui.Dependency") {
    fun get(clazz: Class<Any>): Any = invokeAny("get", clazz)
}
