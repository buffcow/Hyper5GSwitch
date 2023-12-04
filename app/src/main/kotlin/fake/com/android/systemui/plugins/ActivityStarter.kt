package fake.com.android.systemui.plugins

import android.content.Intent
import fake.BaseFaker

/**
 * @author qingyu
 * <p>Create on 2023/12/04 10:50</p>
 */
internal class ActivityStarter(starter: Any) : BaseFaker(starter) {

    fun postStartActivityDismissingKeyguard(intent: Intent, user: Int) {
        invoke("postStartActivityDismissingKeyguard", intent, user)
    }

    companion object {
        const val CLASS_NAME = "com.android.systemui.plugins.ActivityStarter"
    }
}
