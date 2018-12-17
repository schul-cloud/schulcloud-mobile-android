package org.schulcloud.mobile.viewmodels

import androidx.lifecycle.LiveData
import org.schulcloud.mobile.models.course.Course
import org.schulcloud.mobile.models.course.CourseRepository
import org.schulcloud.mobile.models.file.Directory
import org.schulcloud.mobile.models.file.File
import org.schulcloud.mobile.models.file.FileRepository
import org.schulcloud.mobile.models.user.User
import org.schulcloud.mobile.models.user.UserRepository
import org.schulcloud.mobile.utils.getPathParts
import org.schulcloud.mobile.utils.liveDataOf
import org.schulcloud.mobile.viewmodels.base.BaseViewModel


class FileViewModel(path: String) : BaseViewModel() {
    val path = FileRepository.fixPath(path)

    val directories: LiveData<List<Directory>> = FileRepository.directories(realm, this.path)
    val files: LiveData<List<File>> = FileRepository.files(realm, this.path)

    val courseId: String? = if (path.startsWith(FileRepository.CONTEXT_COURSES))
        path.getPathParts()[1]
    else null
    val course: LiveData<Course?> = (courseId?.let {
        CourseRepository.course(realm, it)
    } ?: liveDataOf())

    val currentUser: LiveData<User?> = UserRepository.currentUser(realm)
}
