package org.schulcloud.mobile.controllers.learnstore

import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.android.flexbox.*
import org.schulcloud.mobile.controllers.base.BaseAdapter
import org.schulcloud.mobile.controllers.base.BaseViewHolder
import org.schulcloud.mobile.databinding.ItemMaterialBinding
import org.schulcloud.mobile.models.material.Material
import org.schulcloud.mobile.utils.asUri
import org.schulcloud.mobile.utils.openUrl

class MaterialListAdapter
    : BaseAdapter<Material, MaterialListAdapter.MaterialViewHolder, ItemMaterialBinding>() {

    fun update(materials: List<Material>) {
        items = materials
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialViewHolder {
        val binding = ItemMaterialBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MaterialViewHolder(binding)
    }

    class MaterialViewHolder(binding: ItemMaterialBinding) : BaseViewHolder<Material, ItemMaterialBinding>(binding) {
        private val tagAdapter: TagAdapter = TagAdapter()

        init {
            binding.recyclerViewTags.apply {
                layoutManager = FlexboxLayoutManager(this.context).apply {
                    flexWrap = FlexWrap.WRAP
                    flexDirection = FlexDirection.ROW
                    alignItems = AlignItems.STRETCH
                }
                adapter = tagAdapter
            }
        }

        override fun onItemSet() {
            binding.material = item
            binding.viewHolder = this

            tagAdapter.update(item.tags ?: emptyList())
        }

        fun openExternal() {
            context.openUrl(item.url.asUri())
        }
    }
}
