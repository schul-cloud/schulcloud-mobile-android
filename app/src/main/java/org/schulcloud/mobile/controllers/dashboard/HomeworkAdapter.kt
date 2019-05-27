package org.schulcloud.mobile.controllers.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import org.schulcloud.mobile.controllers.base.BaseAdapter
import org.schulcloud.mobile.controllers.base.BaseViewHolder
import org.schulcloud.mobile.databinding.ItemHomeworkGroupedBinding
import org.schulcloud.mobile.models.homework.Homework
import org.schulcloud.mobile.models.homework.HomeworkCourse

class HomeworkAdapter(private val onSelected: (String?) -> Unit)
    : BaseAdapter<Pair<HomeworkCourse?, Int>, HomeworkAdapter.HomeworkViewHolder, ItemHomeworkGroupedBinding>() {

    fun update(homework: List<Homework>) {
        items = homework.groupBy { it.course }
                .mapValues { it.value.size }
                .toList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeworkAdapter.HomeworkViewHolder {
        val binding = ItemHomeworkGroupedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.onSelected = onSelected
        return HomeworkViewHolder(binding)
    }

    class HomeworkViewHolder(binding: ItemHomeworkGroupedBinding)
        : BaseViewHolder<Pair<HomeworkCourse?, Int>, ItemHomeworkGroupedBinding>(binding) {
        companion object {
            @JvmStatic
            fun courseIdOrNull(course: HomeworkCourse?): String? = course?.id
        }

        override fun onItemSet() {
            binding.course = item.first
            binding.homeworkCount = item.second
        }
    }
}
