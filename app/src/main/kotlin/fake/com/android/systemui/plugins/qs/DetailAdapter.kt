package fake.com.android.systemui.plugins.qs

import android.content.Intent
import cn.buffcow.hyper5g.extension.Phone
import fake.BaseFaker

/**
 * @author qingyu
 * Email: yangyiyu@dofun.cc
 * <p>Create on 2023/12/01 15:30</p>
 */
internal class DetailAdapter(adapter: Any) : BaseFaker(adapter) {

    val isCellularDetailPanel
        get() = getSettingsIntent().component == Phone.CMP_NET_SETTINGS

    private fun getSettingsIntent(): Intent = invokeAny("getSettingsIntent")
}
