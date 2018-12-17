package org.schulcloud.mobile.viewmodels

import androidx.lifecycle.LiveData
import org.schulcloud.mobile.models.homework.Homework
import org.schulcloud.mobile.models.homework.HomeworkRepository
import org.schulcloud.mobile.viewmodels.base.BaseViewModel


class HomeworkListViewModel : BaseViewModel() {
    val homework: LiveData<List<Homework>> = HomeworkRepository.homeworkList(realm)
}
