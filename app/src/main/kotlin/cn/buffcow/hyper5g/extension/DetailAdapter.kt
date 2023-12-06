/**
 * Additional extensions of DetailAdapter.
 *
 * @author qingyu
 * <p>Create on 2023/12/06 09:06</p>
 */

package cn.buffcow.hyper5g.extension

import fake.com.android.systemui.plugins.qs.DetailAdapter

internal val DetailAdapter?.isCellularDetailPanel
    get() = this?.let {
        it.hasHeader() && it.getSettingsIntent().component == Phone.CMP_NET_SETTINGS
    } ?: false
