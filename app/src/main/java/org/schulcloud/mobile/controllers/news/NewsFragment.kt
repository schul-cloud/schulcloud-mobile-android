package org.schulcloud.mobile.controllers.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.main.MainFragment
import org.schulcloud.mobile.controllers.main.MainFragmentConfig
import org.schulcloud.mobile.databinding.FragmentNewsBinding
import org.schulcloud.mobile.models.news.NewsRepository
import org.schulcloud.mobile.utils.map
import org.schulcloud.mobile.viewmodels.IdViewModelFactory
import org.schulcloud.mobile.viewmodels.NewsViewModel

class NewsFragment : MainFragment<NewsViewModel>() {
    companion object {
        val TAG: String = NewsFragment::class.java.simpleName
    }


    override var url: String? = null
        get() = "news/${viewModel.news.value?.id}"

    override fun provideConfig() = viewModel.news
            .map { news ->
                MainFragmentConfig(
                        title = news?.title ?: getString(R.string.general_error_notFound)
                )
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        val args: NewsFragmentArgs by navArgs()
        viewModel = ViewModelProviders.of(this, IdViewModelFactory(args.id))
                .get(NewsViewModel::class.java)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentNewsBinding.inflate(layoutInflater).also {
            it.viewModel = viewModel
            it.setLifecycleOwner(this)
        }.root
    }

    override suspend fun refresh() {
        NewsRepository.syncNews(viewModel.id)
    }
}
