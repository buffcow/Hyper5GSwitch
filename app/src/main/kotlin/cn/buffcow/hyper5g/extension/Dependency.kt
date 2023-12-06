/**
 * Additional extensions of Dependency.
 *
 * @author qingyu
 * <p>Create on 2023/12/06 09:12</p>
 */

package cn.buffcow.hyper5g.extension

import fake.com.android.systemui.Dependency
import fake.com.android.systemui.plugins.ActivityStarter

internal fun Dependency.getActivityStarter() = ActivityStarter(get(ActivityStarter.CLASS_NAME.toClassByAppClsLoader()))
