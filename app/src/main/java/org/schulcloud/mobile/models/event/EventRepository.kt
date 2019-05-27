package org.schulcloud.mobile.models.event

import androidx.lifecycle.LiveData
import io.realm.Realm
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.models.base.Repository
import org.schulcloud.mobile.utils.eventDao
import org.schulcloud.mobile.utils.toFeatherResponse

object EventRepository : Repository() {

    fun events(realm: Realm): LiveData<List<Event>> {
        return realm.eventDao().events()
    }

    fun eventsForToday(realm: Realm): LiveData<List<Event>> {
        return realm.eventDao().eventsForToday()
    }

    fun event(realm: Realm, id: String): LiveData<Event?> {
        return realm.eventDao().event(id)
    }


    suspend fun syncEvents() {
        RequestJob.Data.with({ listEvents().toFeatherResponse() }).run()
    }
}
