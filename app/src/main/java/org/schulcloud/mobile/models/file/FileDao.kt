package org.schulcloud.mobile.models.file

import android.arch.lifecycle.LiveData
import io.realm.Realm
import org.schulcloud.mobile.utils.asLiveData

class FileDao(private val realm: Realm) {
    fun files(path: String): LiveData<List<File>> {
        return realm.where(File::class.java)
                .equalTo("path", path)
                .findAllAsync()
                .asLiveData()
    }

    fun directories(path: String): LiveData<List<Directory>> {
        return realm.where(Directory::class.java)
                .equalTo("path", path)
                .findAllAsync()
                .asLiveData()
    }
}
