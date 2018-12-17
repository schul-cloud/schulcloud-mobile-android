package org.schulcloud.mobile.controllers.topic

import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.fragment_topic.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.main.MainFragment
import org.schulcloud.mobile.controllers.main.MainFragmentConfig
import org.schulcloud.mobile.databinding.FragmentTopicBinding
import org.schulcloud.mobile.models.topic.TopicRepository
import org.schulcloud.mobile.utils.combineLatestBothNullable
import org.schulcloud.mobile.utils.dpToPx
import org.schulcloud.mobile.utils.map
import org.schulcloud.mobile.utils.switchMapNullable
import org.schulcloud.mobile.viewmodels.IdViewModelFactory
import org.schulcloud.mobile.viewmodels.TopicViewModel
import org.schulcloud.mobile.views.DividerItemDecoration
import org.schulcloud.mobile.views.ItemOffsetDecoration


class TopicFragment : MainFragment<TopicFragment, TopicViewModel>() {
    companion object {
        val TAG: String = TopicFragment::class.java.simpleName

        private const val COLUMN_WIDTH_MIN = 440
    }

    override var url: String? = null
        get() = viewModel.topic.value?.url

    override fun provideSelfConfig() = viewModel.topic
            .combineLatestBothNullable(
                    viewModel.topic.switchMapNullable { topic ->
                        topic?.courseId?.let { viewModel.course(it) }
                    })
            .map { (topic, course) ->
                MainFragmentConfig(
                        title = topic?.name ?: getString(R.string.general_error_notFound),
                        subtitle = course?.name,
                        toolbarColor = course?.color?.let { Color.parseColor(it) },
                        menuBottomRes = listOf(R.menu.fragment_topic_bottom)
                )
            }

    private val contentsAdapter: ContentListAdapter by lazy {
        org.schulcloud.mobile.controllers.topic.ContentListAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val args = TopicFragmentArgs.fromBundle(arguments)
        viewModel = ViewModelProviders.of(this, IdViewModelFactory(args.id))
                .get(TopicViewModel::class.java)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentTopicBinding.inflate(inflater, container, false).also {
            it.viewModel = viewModel
            it.setLifecycleOwner(this)
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.topic.observe(this, Observer {
            contentsAdapter.update(it)
        })

        // calculate amount of columns
        val metrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(metrics)
        val spans = Math.max(1, metrics.widthPixels / COLUMN_WIDTH_MIN.dpToPx())

        contentsAdapter.emptyIndicator = empty
        recyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(spans, StaggeredGridLayoutManager.VERTICAL)
            adapter = contentsAdapter
            addItemDecoration(ItemOffsetDecoration(context, R.dimen.content_spacing_half))
            addItemDecoration(DividerItemDecoration.middle(context, LinearLayoutCompat.SHOW_DIVIDER_MIDDLE))
            addItemDecoration(ItemOffsetDecoration(context, R.dimen.content_spacing_half))
        }
    }

    override suspend fun refresh() {
        TopicRepository.syncTopic(viewModel.id)
    }
}
