/**
 * Additional extensions of String.
 *
 * @author qingyu
 * <p>Create on 2023/12/06 09:11</p>
 */

package cn.buffcow.hyper5g.extension

import cn.buffcow.hyper5g.HookEntry
import com.highcapable.yukihookapi.hook.factory.toClass

internal fun String.toClassByAppClsLoader() = this.toClass<Any>(HookEntry.appClassLoader)
