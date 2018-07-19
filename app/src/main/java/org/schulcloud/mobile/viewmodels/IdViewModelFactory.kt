package org.schulcloud.mobile.viewmodels

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

/**
 * Date: 6/9/2018
 */
class IdViewModelFactory(private val id: String) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when (modelClass) {
            CourseViewModel::class.java -> CourseViewModel(id) as T
            TopicViewModel::class.java -> TopicViewModel(id) as T
            FileViewModel::class.java -> FileViewModel(id) as T
            else -> throw IllegalArgumentException("Can't instantiate view model of type $modelClass")
        }
    }
}
