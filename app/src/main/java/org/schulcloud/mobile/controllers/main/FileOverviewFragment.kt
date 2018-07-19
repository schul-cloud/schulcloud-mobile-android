package org.schulcloud.mobile.controllers.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import kotlinx.android.synthetic.main.fragment_file_overview.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseFragment
import org.schulcloud.mobile.controllers.file.FileActivity
import org.schulcloud.mobile.models.course.CourseRepository
import org.schulcloud.mobile.models.file.FileRepository
import org.schulcloud.mobile.utils.HOST
import org.schulcloud.mobile.viewmodels.FileOverviewViewModel

class FileOverviewFragment : BaseFragment() {
    companion object {
        val TAG: String = FileOverviewFragment::class.java.simpleName
    }

    override var url: String? = "$HOST/files"

    private lateinit var viewModel: FileOverviewViewModel
    private val coursesAdapter: FileOverviewCourseAdapter by lazy {
        FileOverviewCourseAdapter {
            startActivity(FileActivity.newIntent(context!!, FileRepository.pathCourse(it)))
        }.apply {
            emptyIndicator = empty
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = ViewModelProviders.of(this).get(FileOverviewViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.title = getString(R.string.file_title)
        return inflater.inflate(R.layout.fragment_file_overview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout = swipeRefresh
        personal_card.setOnClickListener {
            startActivity(FileActivity.newIntent(context!!, FileRepository.pathPersonal()))
        }

        viewModel.courses.observe(this, Observer { courses ->
            coursesAdapter.update(courses!!)
        })

        courses_recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = coursesAdapter
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
