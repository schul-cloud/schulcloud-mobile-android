package org.schulcloud.mobile.controllers.homework.attachment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.schulcloud.mobile.controllers.base.BaseSheet
import org.schulcloud.mobile.databinding.SheetHomeworkAttachmentAddBinding
import org.schulcloud.mobile.models.file.File
import org.schulcloud.mobile.utils.*
import org.schulcloud.mobile.viewmodels.AddAttachmentViewModel
import org.schulcloud.mobile.viewmodels.IdViewModelFactory
import java.text.SimpleDateFormat
import java.util.*


class AddAttachmentSheet : BaseSheet() {
    companion object {
        private const val ARGUMENT_SUBMISSION_ID = "ARGUMENT_SUBMISSION_ID"

        fun forSubmission(id: String): AddAttachmentSheet {
            return AddAttachmentSheet().apply {
                arguments = bundleOf(ARGUMENT_SUBMISSION_ID to id)
            }
        }
    }

    private lateinit var viewModel: AddAttachmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val submissionId = arguments?.getString(ARGUMENT_SUBMISSION_ID)!!
        viewModel = ViewModelProviders.of(this, IdViewModelFactory(submissionId))
                .get(AddAttachmentViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = SheetHomeworkAttachmentAddBinding.inflate(layoutInflater).also {
            it.setLifecycleOwner(this)
            val context = context!!

            it.onAttachFile = {
                launch(UI) {
                    val res = startActivityForResult(createFilePickerIntent() ?: return@launch)
                    if (!res.success) return@launch

                    val file = context.uploadFile(res.data?.data)
                    if (file != null)
                        addToSubmission(file)
                    dismiss()
                }
            }
            it.onAttachTakePhoto = {
                launch(UI) {
                    val info = context.createTakePhotoIntent() ?: return@launch
                    val res = startActivityForResult(info.intent)
                    if (!res.success) return@launch

                    val name = SimpleDateFormat.getDateTimeInstance().format(Date())
                    val file = context.uploadFile(info.tempFileUri, name = name, addEnding = true)
                    if (file != null)
                        addToSubmission(file)

                    info.tempFile.saveDelete()
                    dismiss()
                }
            }
        }
        return binding.root
    }

    private suspend fun addToSubmission(file: File) {
        viewModel.submission.first()
                .observe(this, Observer {
                    it ?: return@Observer

                    launch {
                        viewModel.addFileToSubmission(it, file)
                    }
                })
    }
}
