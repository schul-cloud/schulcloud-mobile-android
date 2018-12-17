package org.schulcloud.mobile.viewmodels

import androidx.lifecycle.LiveData
import org.schulcloud.mobile.models.news.News
import org.schulcloud.mobile.models.news.NewsRepository
import org.schulcloud.mobile.viewmodels.base.BaseViewModel


class NewsListViewModel : BaseViewModel() {
    val news: LiveData<List<News>> = NewsRepository.newsList(realm)
}
