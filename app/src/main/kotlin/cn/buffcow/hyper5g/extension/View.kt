/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.buffcow.hyper5g.extension

import android.view.View
import android.view.ViewGroup

/**
 * Returns true when this view's visibility is [View.VISIBLE], false otherwise.
 *
 * ```
 * if (view.isVisible) {
 *     // Behavior...
 * }
 * ```
 *
 * Setting this property to true sets the visibility to [View.VISIBLE], false to [View.GONE].
 *
 * ```
 * view.isVisible = true
 * ```
 */
inline var View.isVisible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }

/**
 * Executes [block] with a typed version of the View's layoutParams and reassigns the
 * layoutParams with the updated version.
 *
 * @see View.getLayoutParams
 * @see View.setLayoutParams
 */
@JvmName("updateLayoutParamsTyped")
inline fun <reified T : ViewGroup.LayoutParams> View.updateLayoutParams(
    block: T.() -> Unit
) {
    val params = layoutParams as T
    block(params)
    layoutParams = params
}
