package org.schulcloud.mobile.controllers.course

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import kotlinx.android.synthetic.main.activity_course.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseActivity
import org.schulcloud.mobile.controllers.topic.TopicActivity
import org.schulcloud.mobile.databinding.ActivityCourseBinding
import org.schulcloud.mobile.models.course.CourseRepository
import org.schulcloud.mobile.models.topic.TopicRepository
import org.schulcloud.mobile.viewmodels.CourseViewModel
import org.schulcloud.mobile.viewmodels.IdViewModelFactory

/**
 * Date: 6/9/2018
 */
class CourseActivity : BaseActivity() {
    companion object {
        val TAG: String = CourseActivity::class.java.simpleName
        const val EXTRA_ID = "org.schulcloud.extras.EXTRA_ID"

        fun newIntent(context: Context, id: String): Intent {
            return Intent(context, CourseActivity::class.java)
                    .apply { putExtra(EXTRA_ID, id) }
        }
    }

    override var url: String? = null
        get() = viewModel.course.value?.url

    private lateinit var viewModel: CourseViewModel
    private val topicsAdapter: TopicListAdapter by lazy {
        TopicListAdapter { id ->
            startActivity(TopicActivity.newIntent(this@CourseActivity, id))
        }.apply {
            emptyIndicator = empty
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, IdViewModelFactory(intent.getStringExtra(EXTRA_ID)))
                .get(CourseViewModel::class.java)
        val binding = ActivityCourseBinding.inflate(layoutInflater)
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)
        setContentView(binding.root)
        setupActionBar()
        swipeRefreshLayout = swipeRefresh

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@CourseActivity)
            adapter = topicsAdapter
            addItemDecoration(DividerItemDecoration(this@CourseActivity, DividerItemDecoration.VERTICAL))
        }

        viewModel.topics.observe(this, Observer { topicsAdapter.update(it ?: emptyList()) })

        performRefresh()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_course, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override suspend fun refresh() {
        viewModel.course.value?.also {
            CourseRepository.syncCourse(it.id)
            TopicRepository.syncTopics(it.id)
        }
    }
}
