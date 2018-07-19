package org.schulcloud.mobile.controllers.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.*
import kotlinx.android.synthetic.main.fragment_course_list.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseFragment
import org.schulcloud.mobile.controllers.course.CourseActivity
import org.schulcloud.mobile.models.course.CourseRepository
import org.schulcloud.mobile.utils.HOST
import org.schulcloud.mobile.viewmodels.CourseListViewModel
import org.schulcloud.mobile.views.ItemOffsetDecoration

class CourseListFragment : BaseFragment() {
    companion object {
        val TAG: String = CourseListFragment::class.java.simpleName
    }

    override var url: String? = "$HOST/courses"

    private lateinit var viewModel: CourseListViewModel
    private val coursesAdapter: CourseListAdapter by lazy {
        CourseListAdapter {
            startActivity(CourseActivity.newIntent(context!!, it))
        }.apply {
            emptyIndicator = empty
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = ViewModelProviders.of(this).get(CourseListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.title = getString(R.string.course_title)
        return inflater.inflate(R.layout.fragment_course_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout = swipeRefresh

        viewModel.courses.observe(this, Observer { courses ->
            coursesAdapter.update(courses!!)
        })

        recyclerView.apply {
            layoutManager = GridLayoutManager(activity, 2)
            adapter = coursesAdapter
            addItemDecoration(ItemOffsetDecoration(context, R.dimen.grid_spacing))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.fragment_default, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override suspend fun refresh() {
        CourseRepository.syncCourses()
    }
}
