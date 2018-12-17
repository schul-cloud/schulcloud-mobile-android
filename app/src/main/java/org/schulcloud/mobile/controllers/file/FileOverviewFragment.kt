package org.schulcloud.mobile.controllers.file

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_file_overview.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.main.FragmentType
import org.schulcloud.mobile.controllers.main.MainFragment
import org.schulcloud.mobile.controllers.main.MainFragmentConfig
import org.schulcloud.mobile.models.course.CourseRepository
import org.schulcloud.mobile.models.file.FileRepository
import org.schulcloud.mobile.utils.asLiveData
import org.schulcloud.mobile.viewmodels.FileOverviewViewModel

class FileOverviewFragment : MainFragment<FileOverviewFragment, FileOverviewViewModel>() {
    companion object {
        val TAG: String = FileOverviewFragment::class.java.simpleName
    }

    private val coursesAdapter: FileOverviewCourseAdapter by lazy {
        FileOverviewCourseAdapter {
            navController.navigate(R.id.action_global_fragment_file,
                    FileFragmentArgs.Builder(FileRepository.pathCourse(it)).build().toBundle())
        }
    }


    override var url: String? = "/files"
    override fun provideSelfConfig() = MainFragmentConfig(
            fragmentType = FragmentType.PRIMARY,
            title = getString(R.string.file_title)
    ).asLiveData()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(FileOverviewViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_file_overview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        personal_card.setOnClickListener(Navigation.createNavigateOnClickListener(
                R.id.action_global_fragment_file,
                FileFragmentArgs.Builder(FileRepository.pathPersonal()).build().toBundle()))

        coursesAdapter.emptyIndicator = empty
        viewModel.courses.observe(this, Observer { courses ->
            coursesAdapter.update(courses ?: emptyList())
        })

        courses_recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = coursesAdapter
        }
    }

    override suspend fun refresh() {
        CourseRepository.syncCourses()
    }
}
