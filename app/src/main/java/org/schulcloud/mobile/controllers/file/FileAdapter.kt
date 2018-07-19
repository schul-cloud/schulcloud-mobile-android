package org.schulcloud.mobile.controllers.file

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseAdapter
import org.schulcloud.mobile.controllers.base.BaseViewHolder
import org.schulcloud.mobile.databinding.ItemFileBinding
import org.schulcloud.mobile.models.file.File
import org.schulcloud.mobile.utils.fileExtension


class FileAdapter(private val onSelected: (File) -> Unit,
                  private val onDownload: (File) -> Unit)
    : BaseAdapter<File, FileAdapter.FileViewHolder, ItemFileBinding>() {

    fun update(fileList: List<File>) {
        items = fileList
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        val context = recyclerView.context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val binding = ItemFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.onSelected = onSelected
        binding.onDownload = onDownload
        return FileViewHolder(binding)
    }

    class FileViewHolder(binding: ItemFileBinding) : BaseViewHolder<File, ItemFileBinding>(binding) {
        companion object {
            val ICON_RES = mapOf(
                    "txt" to R.drawable.thumb_txt, "doc" to R.drawable.thumb_doc, "docx" to R.drawable.thumb_doc, "pdf" to R.drawable.thumb_pdf,
                    "xls" to R.drawable.thumb_xls, "xlsx" to R.drawable.thumb_xls,
                    "jpg" to R.drawable.thumb_jpg, "jpeg" to R.drawable.thumb_jpg, "png" to R.drawable.thumb_png, "gif" to R.drawable.thumb_gif, "psd" to R.drawable.thumb_psd, "tiff" to R.drawable.thumb_tiff,
                    "ai" to R.drawable.thumb_ai,
                    "mp3" to R.drawable.thumb_mp3, "flac" to R.drawable.thumb_flac, "aac" to R.drawable.thumb_aac,
                    "mp4" to R.drawable.thumb_mp4, "avi" to R.drawable.thumb_avi, "mov" to R.drawable.thumb_mov,
                    "html" to R.drawable.thumb_html, "js" to R.drawable.thumb_js)
        }

        override fun onItemSet() {
            binding.file = item

            val extension = item.name?.fileExtension
            val iconRes = ICON_RES[extension] ?: R.drawable.thumb_default
            binding.iconDrawable = ContextCompat.getDrawable(context, iconRes)
        }
    }
}
