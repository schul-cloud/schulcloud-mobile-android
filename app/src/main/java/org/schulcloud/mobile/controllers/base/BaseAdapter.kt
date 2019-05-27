package org.schulcloud.mobile.controllers.base

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.schulcloud.mobile.utils.asVisibility
import kotlin.coroutines.CoroutineContext
import kotlin.properties.Delegates

abstract class BaseAdapter<T : Any, VH : BaseViewHolder<T, B>, out B : ViewDataBinding>(
    var emptyIndicator: View? = null
) : RecyclerView.Adapter<VH>(), CoroutineScope {
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    var items: List<T> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
        emptyIndicator?.visibility = items.isEmpty().asVisibility()
        recyclerView?.visibility = items.isNotEmpty().asVisibility()
    }

    var recyclerView: RecyclerView? = null
        private set

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
        job = Job()
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        job.cancel()
        this.recyclerView = null
        super.onDetachedFromRecyclerView(recyclerView)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.item = items[position]
    }
}
