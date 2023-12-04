package fake.com.android.systemui.plugins.qs

import android.content.Intent
import fake.BaseFaker

/**
 * @author YiyuYang
 * Email: yangyiyu@dofun.cc
 * <p>Create on 2023/12/01 15:30</p>
 */
internal class DetailAdapter(adapter: Any) : BaseFaker(adapter) {

    fun hasHeader(): Boolean = invokeAny("hasHeader")

    fun getSettingsIntent(): Intent = invokeAny("getSettingsIntent")
}
