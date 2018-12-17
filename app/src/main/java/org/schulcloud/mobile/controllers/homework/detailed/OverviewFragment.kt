package org.schulcloud.mobile.controllers.homework.detailed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.fragment_homework_overview.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.homework.submission.SubmissionFragmentArgs
import org.schulcloud.mobile.controllers.main.InnerMainFragment
import org.schulcloud.mobile.databinding.FragmentHomeworkOverviewBinding
import org.schulcloud.mobile.viewmodels.HomeworkViewModel


class OverviewFragment : InnerMainFragment<OverviewFragment, HomeworkFragment, HomeworkViewModel>() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentHomeworkOverviewBinding.inflate(layoutInflater).also {
            it.viewModel = viewModel
            it.setLifecycleOwner(this)
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gotoMySubmission.setOnClickListener {
            viewModel.mySubmission.value?.id?.also {
                NavHostFragment.findNavController(this).navigate(
                        R.id.action_global_fragment_submission,
                        SubmissionFragmentArgs.Builder(it).build().toBundle())
            }
        }
    }

    override suspend fun refresh() {}
}
