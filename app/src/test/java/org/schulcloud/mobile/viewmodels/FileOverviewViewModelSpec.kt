package org.schulcloud.mobile.viewmodels

import io.mockk.*
import io.realm.Realm
import org.schulcloud.mobile.commonTest.courseList
import org.schulcloud.mobile.models.course.CourseRepository
import org.schulcloud.mobile.commonTest.prepareTaskExecutor
import org.schulcloud.mobile.commonTest.resetTaskExecutor
import org.schulcloud.mobile.utils.asLiveData
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

object FileOverviewViewModelSpec : Spek({
    val courses = courseList(5)

    describe("A fileOverviewViewModel") {
        val fileOverviewViewModel by memoized {
            FileOverviewViewModel()
        }
        val mockRealm = mockk<Realm>()
        mockkObject(CourseRepository)
        mockkStatic(Realm::class)

        beforeEach {
            prepareTaskExecutor()
            every { CourseRepository.courses(mockRealm) } returns courses.asLiveData()
            every { Realm.getDefaultInstance() } returns mockRealm
        }

        afterEach {
            resetTaskExecutor()
            clearAllMocks()
        }

        describe("data access") {
            it("should return the correct data") {
                assertEquals(courses, fileOverviewViewModel.courses.value)
            }
        }
    }
})
