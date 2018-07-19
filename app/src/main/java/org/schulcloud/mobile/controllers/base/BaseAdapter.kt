package org.schulcloud.mobile.controllers.base

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.View
import org.schulcloud.mobile.utils.asVisibility
import kotlin.properties.Delegates

/**
 * Date: 6/21/2018
 */
abstract class BaseAdapter<T : Any, VH : BaseViewHolder<T, B>, out B : ViewDataBinding>(var emptyIndicator: View? = null)
    : RecyclerView.Adapter<VH>() {
    var items: List<T> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
        emptyIndicator?.visibility = items.isEmpty().asVisibility()
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.item = items[position]
    }
}
