package org.schulcloud.mobile.views

import android.view.View
import android.graphics.Rect
import android.content.Context
import android.support.annotation.DimenRes
import android.support.v7.widget.RecyclerView

class ItemOffsetDecoration(private val mItemOffset: Int) : RecyclerView.ItemDecoration() {

    constructor(context: Context?, @DimenRes itemOffsetId: Int) : this(context!!.resources.getDimensionPixelSize(itemOffsetId)) {}

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                                state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset)
    }
}