package org.schulcloud.mobile.controllers.homework

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_homework_list.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.homework.detailed.HomeworkFragmentArgs
import org.schulcloud.mobile.controllers.main.FragmentType
import org.schulcloud.mobile.controllers.main.MainFragment
import org.schulcloud.mobile.controllers.main.MainFragmentConfig
import org.schulcloud.mobile.models.homework.HomeworkRepository
import org.schulcloud.mobile.utils.asLiveData
import org.schulcloud.mobile.viewmodels.HomeworkListViewModel


class HomeworkListFragment : MainFragment<HomeworkListFragment, HomeworkListViewModel>() {
    companion object {
        val TAG: String = HomeworkListFragment::class.java.simpleName
    }

    private val homeworkAdapter: HomeworkAdapter by lazy {
        HomeworkAdapter({
            navController.navigate(
                    R.id.action_global_fragment_homework,
                    HomeworkFragmentArgs.Builder(it).build().toBundle())
        }, {
            navController.navigate(
                    R.id.action_global_fragment_course,
                    HomeworkFragmentArgs.Builder(it).build().toBundle())
        })
    }


    override var url: String? = "/homework"
    override fun provideSelfConfig() = MainFragmentConfig(
            fragmentType = FragmentType.PRIMARY,
            title = getString(R.string.homework_title)
    ).asLiveData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(HomeworkListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_homework_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeworkAdapter.emptyIndicator = empty
        viewModel.homework.observe(this, Observer {
            homeworkAdapter.update(it ?: emptyList())
        })

        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = homeworkAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    override suspend fun refresh() {
        HomeworkRepository.syncHomeworkList()
    }
}
