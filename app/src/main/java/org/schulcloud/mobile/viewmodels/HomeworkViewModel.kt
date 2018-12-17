package org.schulcloud.mobile.viewmodels

import androidx.lifecycle.LiveData
import org.schulcloud.mobile.models.course.Course
import org.schulcloud.mobile.models.course.CourseRepository
import org.schulcloud.mobile.models.homework.Homework
import org.schulcloud.mobile.models.homework.HomeworkRepository
import org.schulcloud.mobile.models.homework.submission.Submission
import org.schulcloud.mobile.models.homework.submission.SubmissionRepository
import org.schulcloud.mobile.models.user.User
import org.schulcloud.mobile.models.user.UserRepository
import org.schulcloud.mobile.utils.combineLatestNullable
import org.schulcloud.mobile.utils.map
import org.schulcloud.mobile.utils.switchMapNullable
import org.schulcloud.mobile.viewmodels.base.BaseViewModel


class HomeworkViewModel(val id: String) : BaseViewModel() {
    val homework: LiveData<Homework?> = HomeworkRepository.homework(realm, id)
    val course: LiveData<Course?> = homework.switchMapNullable { homework ->
        homework?.course?.id?.let { CourseRepository.course(realm, it) }
    }
    val submissions: LiveData<List<Pair<User, Submission?>>> = SubmissionRepository.submissionsForHomework(realm, id)
            .combineLatestNullable(course)
            .map { (submissions, course) ->
                if (course == null)
                    return@map emptyList<Pair<User, Submission>>()

                (course.users ?: emptyList<User>()).map { user ->
                    user to submissions.firstOrNull { it.studentId == user.id }
                }
            }

    val mySubmission: LiveData<Submission?> = SubmissionRepository.submission(realm, id, UserRepository.userId!!)
}
