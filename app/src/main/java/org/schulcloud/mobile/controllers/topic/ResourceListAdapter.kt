package org.schulcloud.mobile.controllers.topic

import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.schulcloud.mobile.controllers.base.BaseAdapter
import org.schulcloud.mobile.controllers.base.BaseViewHolder
import org.schulcloud.mobile.databinding.ItemResourceBinding
import org.schulcloud.mobile.models.content.Resource
import org.schulcloud.mobile.utils.openUrl
import org.schulcloud.mobile.utils.resolveRedirect
import org.schulcloud.mobile.utils.setForegroundForJellyBean

class ResourceListAdapter
    : BaseAdapter<Resource, ResourceListAdapter.ResourceViewHolder, ItemResourceBinding>() {

    fun update(resources: List<Resource>) {
        items = resources
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResourceViewHolder {
        val binding = ItemResourceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.materialCard.setForegroundForJellyBean(binding.materialCard.context)
        return ResourceListAdapter.ResourceViewHolder(binding)
    }

    class ResourceViewHolder(binding: ItemResourceBinding) : BaseViewHolder<Resource, ItemResourceBinding>(binding) {
        override fun onItemSet() {
            binding.resource = item
            binding.viewHolder = this
        }

        fun openExternal() {
            async(UI) {
                resolveRedirect(item.url!!)?.also { this@ResourceViewHolder.context.openUrl(it) }
            }
        }
    }
}
