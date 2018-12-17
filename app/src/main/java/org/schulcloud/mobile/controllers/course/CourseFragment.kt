package org.schulcloud.mobile.controllers.course

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_course.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.file.FileFragmentArgs
import org.schulcloud.mobile.controllers.main.MainFragment
import org.schulcloud.mobile.controllers.main.MainFragmentConfig
import org.schulcloud.mobile.controllers.topic.TopicFragmentArgs
import org.schulcloud.mobile.databinding.FragmentCourseBinding
import org.schulcloud.mobile.models.course.CourseRepository
import org.schulcloud.mobile.models.file.FileRepository
import org.schulcloud.mobile.models.topic.TopicRepository
import org.schulcloud.mobile.utils.map
import org.schulcloud.mobile.viewmodels.CourseViewModel
import org.schulcloud.mobile.viewmodels.IdViewModelFactory
import org.schulcloud.mobile.views.DividerItemDecoration

class CourseFragment : MainFragment<CourseFragment, CourseViewModel>() {
    companion object {
        val TAG: String = CourseFragment::class.java.simpleName
    }

    private val topicsAdapter: TopicListAdapter by lazy {
        org.schulcloud.mobile.controllers.course.TopicListAdapter {
            navController.navigate(R.id.action_global_fragment_topic,
                    TopicFragmentArgs.Builder(it).build().toBundle())
        }
    }


    override var url: String? = null
        get() = "/courses/${viewModel.course.value?.id}"

    override fun provideSelfConfig() = viewModel.course.map { course ->
        MainFragmentConfig(
                title = course?.name ?: getString(R.string.general_error_notFound),
                toolbarColor = if (course != null) Color.parseColor(course.color) else null,
                menuBottomRes = listOf(R.menu.fragment_course_bottom)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val args = CourseFragmentArgs.fromBundle(arguments)
        viewModel = ViewModelProviders.of(this, IdViewModelFactory(args.id))
                .get(CourseViewModel::class.java)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentCourseBinding.inflate(inflater, container, false).also {
            it.viewModel = viewModel
            it.setLifecycleOwner(this)
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        topicsAdapter.emptyIndicator = empty
        viewModel.topics.observe(this, Observer {
            topicsAdapter.update(it ?: emptyList())
        })

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = topicsAdapter
            addItemDecoration(DividerItemDecoration(context))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.course_action_gotoFiles -> viewModel.course.value?.id?.also { id ->
                navController.navigate(R.id.action_global_fragment_file,
                        FileFragmentArgs.Builder(FileRepository.pathCourse(id))
                                .build().toBundle())
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override suspend fun refresh() {
        CourseRepository.syncCourse(viewModel.id)
        TopicRepository.syncTopics(viewModel.id)
    }
}
