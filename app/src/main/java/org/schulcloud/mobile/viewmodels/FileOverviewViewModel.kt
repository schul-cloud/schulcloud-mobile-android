package org.schulcloud.mobile.viewmodels

import androidx.lifecycle.LiveData
import org.schulcloud.mobile.models.course.Course
import org.schulcloud.mobile.models.course.CourseRepository
import org.schulcloud.mobile.viewmodels.base.BaseViewModel


class FileOverviewViewModel : BaseViewModel() {
    val courses: LiveData<List<Course>> = CourseRepository.courses(realm)
}
