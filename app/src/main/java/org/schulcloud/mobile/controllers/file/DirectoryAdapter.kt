package org.schulcloud.mobile.controllers.file

import android.view.LayoutInflater
import android.view.ViewGroup
import org.schulcloud.mobile.controllers.base.BaseAdapter
import org.schulcloud.mobile.controllers.base.BaseViewHolder
import org.schulcloud.mobile.databinding.ItemDirectoryBinding
import org.schulcloud.mobile.models.file.Directory

class DirectoryAdapter(private val onSelected: (String) -> Unit)
    : BaseAdapter<Directory, DirectoryAdapter.DirectoryViewHolder, ItemDirectoryBinding>() {

    fun update(directoryList: List<Directory>) {
        items = directoryList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DirectoryAdapter.DirectoryViewHolder {
        val binding = ItemDirectoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.onSelected = onSelected
        return DirectoryAdapter.DirectoryViewHolder(binding)
    }

    class DirectoryViewHolder(binding: ItemDirectoryBinding) : BaseViewHolder<Directory, ItemDirectoryBinding>(binding) {
        override fun onItemSet() {
            binding.directory = item
        }
    }
}
