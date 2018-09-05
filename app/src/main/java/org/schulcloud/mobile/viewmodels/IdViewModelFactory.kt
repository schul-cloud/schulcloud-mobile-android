package org.schulcloud.mobile.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.schulcloud.mobile.utils.trimTrailingSlash

class IdViewModelFactory(private val id: String) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        // Deep linking also matches a trailing slash
        val id = id.trimTrailingSlash()

        return when (modelClass) {
            NewsViewModel::class.java -> NewsViewModel(id) as T
            CourseViewModel::class.java -> CourseViewModel(id) as T
            TopicViewModel::class.java -> TopicViewModel(id) as T
            HomeworkViewModel::class.java -> HomeworkViewModel(id) as T
            FileViewModel::class.java -> FileViewModel(id) as T
            else -> throw IllegalArgumentException("Can't instantiate view model of type $modelClass")
        }
    }
}
