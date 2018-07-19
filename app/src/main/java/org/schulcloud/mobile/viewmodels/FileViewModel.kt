package org.schulcloud.mobile.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import io.realm.Realm
import org.schulcloud.mobile.models.file.Directory
import org.schulcloud.mobile.models.file.File
import org.schulcloud.mobile.models.file.FileRepository

/**
 * Date: 7/6/2018
 */
class FileViewModel(path_: String) : ViewModel() {
    val path = FileRepository.fixPath(path_)

    val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    val directories: LiveData<List<Directory>> = FileRepository.directories(realm, path)
    val files: LiveData<List<File>> = FileRepository.files(realm, path)
}
