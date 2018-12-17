package org.schulcloud.mobile.controllers.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_news_list.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.main.FragmentType
import org.schulcloud.mobile.controllers.main.MainFragment
import org.schulcloud.mobile.controllers.main.MainFragmentConfig
import org.schulcloud.mobile.models.news.NewsRepository
import org.schulcloud.mobile.utils.asLiveData
import org.schulcloud.mobile.viewmodels.NewsListViewModel


class NewsListFragment : MainFragment<NewsListFragment, NewsListViewModel>() {
    companion object {
        val TAG: String = NewsListFragment::class.java.simpleName
    }

    private val newsAdapter: NewsAdapter by lazy {
        NewsAdapter {
            navController.navigate(R.id.action_global_fragment_news,
                    NewsFragmentArgs.Builder(it).build().toBundle())
        }
    }


    override var url: String? = "/news"
    override fun provideSelfConfig() = MainFragmentConfig(
            fragmentType = FragmentType.PRIMARY,
            title = getString(R.string.news_title)
    ).asLiveData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(NewsListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_news_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newsAdapter.emptyIndicator = empty
        viewModel.news.observe(this, Observer {
            newsAdapter.update(it ?: emptyList())
        })

        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = newsAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    override suspend fun refresh() {
        NewsRepository.syncNews()
    }
}
