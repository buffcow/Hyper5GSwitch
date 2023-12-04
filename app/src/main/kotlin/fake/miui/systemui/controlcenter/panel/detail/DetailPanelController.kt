package fake.miui.systemui.controlcenter.panel.detail

import android.content.Context
import android.view.ViewGroup
import fake.BaseFaker
import fake.com.android.systemui.plugins.qs.DetailAdapter

/**
 * @author qingyu
 * <p>Create on 2023/12/01 17:02</p>
 */
internal class DetailPanelController(ctrl: Any) : BaseFaker(ctrl) {

    val detailAdapter: DetailAdapter get() = DetailAdapter(fieldAny("detailAdapter"))

    fun getView(): ViewGroup = invokeAny("getView")

    fun getContext(): Context = invokeAny("getContext")

    companion object {
        const val M_onCreate = "onCreate"
        const val M_setupDetailHeader = "setupDetailHeader"
        const val M_updateBackgroundColor = "updateBackgroundColor"
        const val CLASS_NAME = "miui.systemui.controlcenter.panel.detail.DetailPanelController"
    }
}
