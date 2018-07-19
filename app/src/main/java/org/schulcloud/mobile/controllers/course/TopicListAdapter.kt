package org.schulcloud.mobile.controllers.course

import android.view.LayoutInflater
import android.view.ViewGroup
import org.schulcloud.mobile.controllers.base.BaseAdapter
import org.schulcloud.mobile.controllers.base.BaseViewHolder
import org.schulcloud.mobile.databinding.ItemTopicBinding
import org.schulcloud.mobile.models.topic.Topic

/**
 * Date: 6/10/2018
 */
class TopicListAdapter(private val onSelected: (String) -> Unit)
    : BaseAdapter<Topic, TopicListAdapter.TopicViewHolder, ItemTopicBinding>() {

    fun update(topicList: List<Topic>) {
        items = topicList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val binding = ItemTopicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.onSelected = onSelected
        return TopicViewHolder(binding)
    }

    class TopicViewHolder(binding: ItemTopicBinding) : BaseViewHolder<Topic, ItemTopicBinding>(binding) {
        override fun onItemSet() {
            binding.topic = item
        }
    }
}
