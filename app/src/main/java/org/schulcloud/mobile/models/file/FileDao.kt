package org.schulcloud.mobile.models.file

import androidx.lifecycle.LiveData
import io.realm.Realm
import org.schulcloud.mobile.utils.allAsLiveData

class FileDao(private val realm: Realm) {
    fun files(path: String): LiveData<List<File>> {
        return realm.where(File::class.java)
                .equalTo("path", path)
                .allAsLiveData()
    }

    fun files(ids: Array<String>): LiveData<List<File>> {
        return realm.where(File::class.java)
                .`in`("id", ids)
                .allAsLiveData()
    }

    fun directories(path: String): LiveData<List<Directory>> {
        return realm.where(Directory::class.java)
                .equalTo("path", path)
                .allAsLiveData()
    }


    fun updateFile(value: File) {
        realm.executeTransaction {
            it.copyToRealmOrUpdate(value)
        }
    }
}
