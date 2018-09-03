package org.schulcloud.mobile.models.homework

import androidx.lifecycle.LiveData
import io.realm.Realm
import io.realm.Sort
import org.schulcloud.mobile.utils.WEEK_IN_DAYS
import org.schulcloud.mobile.utils.asLiveData
import org.schulcloud.mobile.utils.firstAsLiveData
import org.schulcloud.mobile.utils.map

class HomeworkDao(private val realm: Realm) {

    fun homeworkList(): LiveData<List<Homework>> {
        return realm.where(Homework::class.java)
                .findAllAsync()
                .asLiveData()
                .map { homeworkList ->
                    homeworkList.let {
                        val outdatedHomework = homeworkList.filter { it.dueTimespanDays ?: -1 < 0 }
                                .sortedByDescending { it.dueDate }
                                .toList()

                        val displayedHomework = homeworkList.filter { it.dueTimespanDays ?: -1 >= 0 }
                                .sortedBy { it.dueDate }
                                .toMutableList()

                        displayedHomework.addAll(outdatedHomework)

                        displayedHomework
                    }
                }
    }

    fun openHomeworkForNextWeek(): LiveData<List<Homework>> {
        return realm.where(Homework::class.java)
                .findAllAsync()
                .asLiveData()
                .map {
                    it.filter { it.dueTimespanDays in 0..WEEK_IN_DAYS }
                }
    }

    fun homework(id: String): LiveData<Homework?> {
        return realm.where(Homework::class.java)
                .sort("dueDate", Sort.ASCENDING)
                .equalTo("id", id)
                .firstAsLiveData()
    }
}
