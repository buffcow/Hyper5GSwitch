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
import android.view.ViewParent

/** Performs the given action on each view in this view group. */
inline fun ViewGroup.forEach(action: (view: View) -> Unit) {
    for (index in 0 until childCount) {
        action(getChildAt(index))
    }
}

/** Returns a [MutableIterator] over the views in this view group. */
operator fun ViewGroup.iterator(): MutableIterator<View> = object : MutableIterator<View> {
    private var index = 0
    override fun hasNext() = index < childCount
    override fun next() = getChildAt(index++) ?: throw IndexOutOfBoundsException()
    override fun remove() = removeViewAt(--index)
}

/**
 * Returns a [Sequence] over the immediate child views in this view group.
 *
 * @see View.allViews
 * @see ViewGroup.descendants
 */
val ViewGroup.children: Sequence<View>
    get() = object : Sequence<View> {
        override fun iterator() = this@children.iterator()
    }

/**
 * Returns a [Sequence] over the child views in this view group recursively.
 * This performs a depth-first traversal.
 * A view with no children will return a zero-element sequence.
 *
 * @see View.allViews
 * @see ViewGroup.children
 * @see View.ancestors
 */
val ViewGroup.descendants: Sequence<View>
    get() = sequence {
        forEach { child ->
            yield(child)
            if (child is ViewGroup) {
                yieldAll(child.descendants)
            }
        }
    }

/**
 * Returns a [Sequence] of the parent chain of this view by repeatedly calling [View.getParent].
 * An unattached view will return a zero-element sequence.
 *
 * @see ViewGroup.descendants
 */
val View.ancestors: Sequence<ViewParent>
    get() = generateSequence(parent, ViewParent::getParent)

/**
 * Returns a [Sequence] over this view and its descendants recursively.
 * This is a depth-first traversal similar to [View.findViewById].
 * A view with no children will return a single-element sequence of itself.
 *
 * @see ViewGroup.descendants
 */
val View.allViews: Sequence<View>
    get() = sequence {
        yield(this@allViews)
        if (this@allViews is ViewGroup) {
            yieldAll(this@allViews.descendants)
        }
    }
