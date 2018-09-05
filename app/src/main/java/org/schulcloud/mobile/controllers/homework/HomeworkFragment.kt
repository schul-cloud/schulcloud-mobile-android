package org.schulcloud.mobile.controllers.homework

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.course.CourseFragmentArgs
import org.schulcloud.mobile.controllers.main.MainFragment
import org.schulcloud.mobile.controllers.main.MainFragmentConfig
import org.schulcloud.mobile.databinding.FragmentHomeworkBinding
import org.schulcloud.mobile.models.homework.HomeworkRepository
import org.schulcloud.mobile.utils.map
import org.schulcloud.mobile.viewmodels.HomeworkViewModel
import org.schulcloud.mobile.viewmodels.IdViewModelFactory

class HomeworkFragment : MainFragment() {
    companion object {
        val TAG: String = HomeworkFragment::class.java.simpleName
    }

    private lateinit var viewModel: HomeworkViewModel


    override var url: String? = null
        get() = "/homework/${viewModel.homework.value?.id}"

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
        val args = HomeworkFragmentArgs.fromBundle(arguments)
        viewModel = ViewModelProviders.of(this, IdViewModelFactory(args.id))
                .get(HomeworkViewModel::class.java)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentHomeworkBinding.inflate(layoutInflater).also {
            it.viewModel = viewModel
            it.setLifecycleOwner(this)
        }.root
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
    }
}
