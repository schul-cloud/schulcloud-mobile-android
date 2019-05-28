package org.schulcloud.mobile.models.material

import androidx.lifecycle.LiveData
import io.realm.Realm
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.utils.materialDao

object MaterialRepository {
    const val LIMIT_POPULAR = 3

    fun currentMaterials(realm: Realm): LiveData<List<Material>> {
        return realm.materialDao().currentMaterials()
    }

    fun popularMaterials(realm: Realm): LiveData<List<Material>> {
        return realm.materialDao().popularMaterials(LIMIT_POPULAR)
    }

    suspend fun syncMaterials() {
        val currentDate = LocalDate.now()
        val dateString = currentDate.toString(DateTimeFormat.forPattern("yyyy-MM-dd"))
        RequestJob.Data.with({ listCurrentMaterials(dateString) }, {
            greaterThanOrEqualTo("featuredUntil", currentDate.toDate())
        }).run()

        RequestJob.Data.with({ listPopularMaterials(LIMIT_POPULAR) }, {
            beginGroup()
                    .isNull("featuredUntil")
                    .or()
                    .lessThan("featuredUntil", currentDate.toDate())
                    .endGroup()
        }).run()
    }
}
