package org.schulcloud.mobile.controllers.homework.submission

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.main.InnerMainFragment
import org.schulcloud.mobile.controllers.main.MainFragmentConfig
import org.schulcloud.mobile.databinding.FragmentHomeworkSubmissionFeedbackBinding
import org.schulcloud.mobile.utils.asLiveData
import org.schulcloud.mobile.viewmodels.SubmissionViewModel


class FeedbackFragment : InnerMainFragment<FeedbackFragment, SubmissionFragment, SubmissionViewModel>() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentHomeworkSubmissionFeedbackBinding.inflate(layoutInflater).also {
            it.viewModel = viewModel
            it.setLifecycleOwner(this)
        }.root
    }

    override fun provideSelfConfig(): LiveData<MainFragmentConfig> {
        return MainFragmentConfig(
                menuBottomHiddenIds = listOf(R.id.submission_action_addAttachment)
        ).asLiveData()
    }

    override suspend fun refresh() {}
}
