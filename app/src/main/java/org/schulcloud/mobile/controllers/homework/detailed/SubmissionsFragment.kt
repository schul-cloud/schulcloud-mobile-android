package org.schulcloud.mobile.controllers.homework.detailed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_homework_submissions.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.homework.submission.SubmissionFragmentArgs
import org.schulcloud.mobile.controllers.main.InnerMainFragment
import org.schulcloud.mobile.databinding.FragmentHomeworkSubmissionsBinding
import org.schulcloud.mobile.models.course.CourseRepository
import org.schulcloud.mobile.utils.showGenericNeutral
import org.schulcloud.mobile.viewmodels.HomeworkViewModel
import org.schulcloud.mobile.views.DividerItemDecoration


class SubmissionsFragment : InnerMainFragment<SubmissionsFragment, HomeworkFragment, HomeworkViewModel>() {
    private val submissionsAdapter by lazy {
        SubmissionsAdapter {
            if (it.isEmpty())
                context!!.showGenericNeutral(R.string.homework_submissions_error_selectedEmpty)
            else
                findNavController(this).navigate(
                        R.id.action_global_fragment_submission,
                        SubmissionFragmentArgs.Builder(it).build().toBundle())
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentHomeworkSubmissionsBinding.inflate(layoutInflater).also {
            it.viewModel = viewModel
            it.setLifecycleOwner(this)
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        submissionsAdapter.emptyIndicator = empty
        viewModel.submissions.observe(this, Observer {
            submissionsAdapter.update(it)
        })

        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = submissionsAdapter
            addItemDecoration(DividerItemDecoration(context))
        }
    }

    override suspend fun refresh() {
        viewModel.homework.value?.course?.id?.also { CourseRepository.syncCourse(it) }
    }
}
