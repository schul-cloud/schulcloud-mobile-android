package org.schulcloud.mobile.viewmodels

import androidx.lifecycle.LiveData
import org.schulcloud.mobile.models.news.News
import org.schulcloud.mobile.models.news.NewsRepository
import org.schulcloud.mobile.viewmodels.base.BaseViewModel


class NewsViewModel(val id: String) : BaseViewModel() {
    val news: LiveData<News?> = NewsRepository.news(realm, id)
}
