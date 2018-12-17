package org.schulcloud.mobile.controllers.homework.detailed

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.course.CourseFragmentArgs
import org.schulcloud.mobile.controllers.main.MainFragmentConfig
import org.schulcloud.mobile.controllers.main.Tab
import org.schulcloud.mobile.controllers.main.TabbedMainFragment
import org.schulcloud.mobile.controllers.main.toPagerAdapter
import org.schulcloud.mobile.databinding.FragmentHomeworkBinding
import org.schulcloud.mobile.models.homework.HomeworkRepository
import org.schulcloud.mobile.models.homework.submission.SubmissionRepository
import org.schulcloud.mobile.utils.map
import org.schulcloud.mobile.utils.visibilityBool
import org.schulcloud.mobile.viewmodels.HomeworkViewModel
import org.schulcloud.mobile.viewmodels.IdViewModelFactory


class HomeworkFragment : TabbedMainFragment<HomeworkFragment, HomeworkViewModel>() {
    companion object {
        val TAG: String = HomeworkFragment::class.java.simpleName
    }

    override val pagerAdapter by lazy {
        viewModel.homework
                .map { homework ->
                    listOfNotNull(
                            Tab(getString(R.string.homework_overview)) { OverviewFragment() },
                            if (homework?.canSeeSubmissions() == true)
                                Tab(getString(R.string.homework_submissions)) { SubmissionsFragment() }
                            else null
                    )
                }
                .toPagerAdapter(this)
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
                            menuBottomRes = listOf(R.menu.fragment_homework_bottom)
                    )
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val args = HomeworkFragmentArgs.fromBundle(arguments)
        viewModel = ViewModelProviders.of(this, IdViewModelFactory(args.id))
                .get(HomeworkViewModel::class.java)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentHomeworkBinding.inflate(layoutInflater).also {
            it.setLifecycleOwner(this)
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.homework.observe(this, Observer {
            // Hide tabs if only overview is visible
            tabLayout?.visibilityBool = it?.canSeeSubmissions() == true
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.homework_action_gotoCourse -> viewModel.homework.value?.course?.id?.also { id ->
                navController.navigate(R.id.action_global_fragment_course,
                        CourseFragmentArgs.Builder(id).build().toBundle())
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override suspend fun refresh() {
        HomeworkRepository.syncHomework(viewModel.id)
        SubmissionRepository.syncSubmissionsForHomework(viewModel.id)
    }
}
