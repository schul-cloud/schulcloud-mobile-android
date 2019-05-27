package org.schulcloud.mobile.controllers.homework.detailed

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_homework.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.course.CourseFragmentArgs
import org.schulcloud.mobile.controllers.main.MainFragment
import org.schulcloud.mobile.controllers.main.MainFragmentConfig
import org.schulcloud.mobile.controllers.main.ParentFragment
import org.schulcloud.mobile.controllers.main.TabFragment
import org.schulcloud.mobile.databinding.FragmentHomeworkBinding
import org.schulcloud.mobile.models.homework.HomeworkRepository
import org.schulcloud.mobile.models.homework.submission.SubmissionRepository
import org.schulcloud.mobile.utils.map
import org.schulcloud.mobile.utils.visibilityBool
import org.schulcloud.mobile.viewmodels.HomeworkViewModel
import org.schulcloud.mobile.viewmodels.IdViewModelFactory

class HomeworkFragment : MainFragment<HomeworkViewModel>(), ParentFragment {
    companion object {
        val TAG: String = HomeworkFragment::class.java.simpleName
    }

    private val pagerAdapter by lazy { HomeworkPagerAdapter(context!!, childFragmentManager) }

    override var url: String? = null
        get() = "homework/${viewModel.homework.value?.id}"

    override fun provideConfig() = viewModel.homework
            .map { homework ->
                MainFragmentConfig(
                        title = homework?.title ?: getString(R.string.general_error_notFound),
                        subtitle = homework?.course?.name,
                        toolbarColor = homework?.course?.color?.let { Color.parseColor(it) },
                        menuBottomRes = R.menu.fragment_homework_bottom
                )
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        val args: HomeworkFragmentArgs by navArgs()
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
        viewPager.adapter = pagerAdapter
        tabLayout.setupWithViewPager(viewPager)
        mainViewModel.toolbarColors.observe(this, Observer {
            tabLayout.setTabTextColors(it.textColorSecondary, it.textColor)
            tabLayout.setSelectedTabIndicatorColor(it.textColor)
        })

        viewModel.homework.observe(this, Observer {
            pagerAdapter.homework = it
            // Hide tabs if only overview is visible
            tabLayout.visibilityBool = it?.canSeeSubmissions() == true
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.homework_action_gotoCourse -> viewModel.homework.value?.course?.id?.also { id ->
                navController.navigate(R.id.action_global_fragment_course,
                        CourseFragmentArgs.Builder(id).build().toBundle())
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override suspend fun refresh() {
        refreshWithChild(false)
    }

    override suspend fun refreshWithChild(fromChild: Boolean) {
        if (fromChild) {
            HomeworkRepository.syncHomework(viewModel.id)
            SubmissionRepository.syncSubmissionsForHomework(viewModel.id)
        } else if (viewPager != null)
            (pagerAdapter.getItem(viewPager.currentItem) as? TabFragment<*, *>)?.performRefresh()
    }
}
