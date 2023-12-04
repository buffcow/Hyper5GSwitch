package fake

import cn.buffcow.hyper5g.ext.toClassByAppClsLoader
import com.highcapable.yukihookapi.hook.factory.toClass
import de.robv.android.xposed.XposedHelpers.callMethod
import de.robv.android.xposed.XposedHelpers.callStaticMethod
import de.robv.android.xposed.XposedHelpers.getObjectField
import de.robv.android.xposed.XposedHelpers.getStaticObjectField

/**
 * Base faker.
 *
 * @author qingyu
 * <p>Create on 2023/12/01 14:49</p>
 */
internal abstract class BaseFaker(private val instance: Any) {

    fun getSelf() = instance

    protected inline fun <reified T> fieldAny(name: String) = getObjectField(getSelf(), name) as T

    protected fun invoke(name: String, vararg args: Any) {
        callMethod(getSelf(), name, *args)
    }

    protected inline fun <reified T> invokeAny(name: String, vararg args: Any) = callMethod(getSelf(), name, *args) as T
}

internal abstract class BaseStaticFacker {
    private lateinit var clazz: Class<Any>

    private constructor()

    constructor(clazz: Class<Any>) : this() {
        this.clazz = clazz
    }

    constructor(className: String) : this(className.toClassByAppClsLoader())

    constructor(className: String, classLoader: ClassLoader) : this(className.toClass<Any>(classLoader))

    fun getClazz() = clazz

    protected inline fun <reified T> fieldAny(name: String) = getStaticObjectField(getClazz(), name) as T

    protected fun invoke(name: String, vararg args: Any) {
        callStaticMethod(getClazz(), name, *args)
    }

    protected inline fun <reified T> invokeAny(name: String, vararg args: Any) = callStaticMethod(getClazz(), name, *args) as T
}
