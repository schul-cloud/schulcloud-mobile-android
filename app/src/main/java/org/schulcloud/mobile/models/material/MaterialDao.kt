package org.schulcloud.mobile.models.material

import androidx.lifecycle.LiveData
import io.realm.Realm
import io.realm.Sort
import org.joda.time.LocalDate
import org.schulcloud.mobile.utils.allAsLiveData
import org.schulcloud.mobile.utils.map

class MaterialDao(private val realm: Realm) {

    fun currentMaterials(): LiveData<List<Material>> {
        val currentDate = LocalDate.now().toDate()
        return realm.where(Material::class.java)
                .greaterThanOrEqualTo("featuredUntil", currentDate)
                .sort("title", Sort.ASCENDING)
                .allAsLiveData()
    }

    fun popularMaterials(limitPopular: Int): LiveData<List<Material>> {
        return realm.where(Material::class.java)
                .sort("clickCount", Sort.DESCENDING)
                .allAsLiveData()
                .map { materialList ->
                    materialList.take(limitPopular)
                }
    }
}
