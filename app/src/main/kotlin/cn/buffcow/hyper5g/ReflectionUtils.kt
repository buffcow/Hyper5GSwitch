/**
 * Native reflection helpers for resolving System UI classes and members across HyperOS versions.
 *
 * @author qingyu
 * Email: 42611305+buffcow@users.noreply.github.com
 * <p>Create on 2026/08/27 15:57</p>
 */
package cn.buffcow.hyper5g

import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

internal fun loadClass(name: String, classLoader: ClassLoader): Class<*> {
    return Class.forName(name, false, classLoader)
}

internal fun loadClassOrNull(name: String, classLoader: ClassLoader): Class<*>? {
    return try {
        loadClass(name, classLoader)
    } catch (_: ClassNotFoundException) {
        null
    }
}

internal fun Class<*>.findMethod(name: String, vararg parameterTypes: Class<*>): Method {
    try {
        return getMethod(name, *parameterTypes).apply { isAccessible = true }
    } catch (_: NoSuchMethodException) {
        // Search non-public methods below.
    }

    var type: Class<*>? = this
    while (type != null) {
        try {
            return type.getDeclaredMethod(name, *parameterTypes).apply { isAccessible = true }
        } catch (_: NoSuchMethodException) {
            type = type.superclass
        }
    }
    throw NoSuchMethodException("${this.name}#$name(${parameterTypes.joinToString { it.name }})")
}

internal fun Any.readField(name: String): Any? {
    var type: Class<*>? = javaClass
    while (type != null) {
        try {
            return type.getDeclaredField(name).run {
                isAccessible = true
                get(this@readField)
            }
        } catch (_: NoSuchFieldException) {
            type = type.superclass
        }
    }
    throw NoSuchFieldException("${javaClass.name}#$name")
}

internal fun Class<*>.readStaticField(name: String): Any? {
    var type: Class<*>? = this
    while (type != null) {
        try {
            return type.getDeclaredField(name).run {
                isAccessible = true
                get(null)
            }
        } catch (_: NoSuchFieldException) {
            type = type.superclass
        }
    }
    throw NoSuchFieldException("${this.name}#$name")
}

internal fun Method.invokeNative(receiver: Any?, vararg args: Any?): Any? {
    return try {
        isAccessible = true
        invoke(receiver, *args)
    } catch (e: InvocationTargetException) {
        throw e.targetException
    }
}
