package org.schulcloud.mobile.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.realm.Realm
import org.schulcloud.mobile.models.material.Material
import org.schulcloud.mobile.models.material.MaterialRepository

class MaterialListViewModel : ViewModel() {
    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    val currentMaterials: LiveData<List<Material>> = MaterialRepository.currentMaterials(realm)
    val popularMaterials: LiveData<List<Material>> = MaterialRepository.popularMaterials(realm)
}
