package org.schulcloud.mobile.controllers.learnstore

import android.view.LayoutInflater
import android.view.ViewGroup
import org.schulcloud.mobile.controllers.base.BaseAdapter
import org.schulcloud.mobile.controllers.base.BaseViewHolder
import org.schulcloud.mobile.databinding.ItemTagBinding

class TagAdapter
    : BaseAdapter<String, TagAdapter.TagViewHolder, ItemTagBinding>() {

    fun update(tags: List<String>) {
        items = tags
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val binding = ItemTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TagViewHolder(binding)
    }

    class TagViewHolder(binding: ItemTagBinding) : BaseViewHolder<String, ItemTagBinding>(binding) {
        override fun onItemSet() {
            binding.tagText = item
        }
    }
}
