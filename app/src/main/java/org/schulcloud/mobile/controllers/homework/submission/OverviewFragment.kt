package org.schulcloud.mobile.controllers.homework.submission

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_homework_submission_overview.*
import kotlinx.coroutines.experimental.launch
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.file.FileAdapter
import org.schulcloud.mobile.controllers.homework.attachment.AddAttachmentSheet
import org.schulcloud.mobile.controllers.main.InnerMainFragment
import org.schulcloud.mobile.databinding.FragmentHomeworkSubmissionOverviewBinding
import org.schulcloud.mobile.models.file.FileRepository
import org.schulcloud.mobile.utils.downloadFile
import org.schulcloud.mobile.utils.visibilityBool
import org.schulcloud.mobile.viewmodels.SubmissionViewModel


class OverviewFragment : InnerMainFragment<OverviewFragment, SubmissionFragment, SubmissionViewModel>() {
    private val attachmentsAdapter: FileAdapter by lazy {
        FileAdapter({ launch { downloadFile(it, false) } },
                { launch { downloadFile(it, true) } })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentHomeworkSubmissionOverviewBinding.inflate(layoutInflater).also {
            it.viewModel = viewModel
            it.setLifecycleOwner(this)
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.files.observe(this, Observer {
            attachmentsAdapter.update(it ?: emptyList())
            attachments_header.visibilityBool = it.isNotEmpty()
        })
        attachments_recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = attachmentsAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.submission_action_addAttachment -> {
                val sheet = AddAttachmentSheet.forSubmission(viewModel.id)
                sheet.show(fragmentManager, sheet.tag)
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override suspend fun refresh() {
        viewModel.submission.value?.fileIds?.let {
            for (id in it)
                FileRepository.syncFile(id)
        }
    }
}
