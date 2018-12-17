package org.schulcloud.mobile.controllers.homework.submission

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_homework_submission.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.course.CourseFragmentArgs
import org.schulcloud.mobile.controllers.homework.detailed.HomeworkFragmentArgs
import org.schulcloud.mobile.controllers.main.MainFragmentConfig
import org.schulcloud.mobile.controllers.main.Tab
import org.schulcloud.mobile.controllers.main.TabbedMainFragment
import org.schulcloud.mobile.controllers.main.toPagerAdapter
import org.schulcloud.mobile.databinding.FragmentHomeworkSubmissionBinding
import org.schulcloud.mobile.models.homework.HomeworkRepository
import org.schulcloud.mobile.models.homework.submission.SubmissionRepository
import org.schulcloud.mobile.utils.map
import org.schulcloud.mobile.viewmodels.IdViewModelFactory
import org.schulcloud.mobile.viewmodels.SubmissionViewModel


class SubmissionFragment : TabbedMainFragment<SubmissionFragment, SubmissionViewModel>() {
    override val pagerAdapter by lazy {
        listOf(
                Tab(getString(R.string.homework_submission_overview)) { OverviewFragment() },
                Tab(getString(R.string.homework_submission_feedback)) { FeedbackFragment() }
        ).toPagerAdapter(this)
    }

    override var url: String? = null
        get() = "/homework/${viewModel.homework.value?.id}"

    override fun provideSelfConfig(): LiveData<MainFragmentConfig> {
        return viewModel.homework
                .map { homework ->
                    MainFragmentConfig(
                            title = homework?.title ?: getString(R.string.general_error_notFound),
                            subtitle = homework?.course?.name,
                            toolbarColor = homework?.course?.color?.let { Color.parseColor(it) },
                            menuBottomRes = listOf(R.menu.fragment_homework_submission_bottom)
                    )
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val args = HomeworkFragmentArgs.fromBundle(arguments)
        viewModel = ViewModelProviders.of(this, IdViewModelFactory(args.id))
                .get(SubmissionViewModel::class.java)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentHomeworkSubmissionBinding.inflate(layoutInflater).also {
            it.viewModel = viewModel
            it.setLifecycleOwner(this)
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel.toolbarColors.observe(this, Observer {
            selectedStudent.setTextColor(it.textColor)
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.submission_action_gotoHomework -> viewModel.homework.value?.id?.also { id ->
                navController.navigate(R.id.action_global_fragment_homework,
                        HomeworkFragmentArgs.Builder(id).build().toBundle())
            }
            R.id.submission_action_gotoCourse -> viewModel.homework.value?.course?.id?.also { id ->
                navController.navigate(R.id.action_global_fragment_course,
                        CourseFragmentArgs.Builder(id).build().toBundle())
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override suspend fun refresh() {
        SubmissionRepository.syncSubmission(viewModel.id)
        viewModel.homework.value?.id?.also { HomeworkRepository.syncHomework(it) }
    }
}
