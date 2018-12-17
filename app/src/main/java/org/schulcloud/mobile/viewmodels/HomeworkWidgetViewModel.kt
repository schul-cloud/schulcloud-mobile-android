package org.schulcloud.mobile.viewmodels

import androidx.lifecycle.LiveData
import org.schulcloud.mobile.models.homework.Homework
import org.schulcloud.mobile.models.homework.HomeworkRepository
import org.schulcloud.mobile.viewmodels.base.BaseViewModel


class HomeworkWidgetViewModel : BaseViewModel() {
    val homework: LiveData<List<Homework>> = HomeworkRepository.openHomeworkForNextWeek(realm)
}
