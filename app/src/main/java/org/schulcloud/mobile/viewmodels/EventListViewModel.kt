package org.schulcloud.mobile.viewmodels

import androidx.lifecycle.LiveData
import org.schulcloud.mobile.models.event.Event
import org.schulcloud.mobile.models.event.EventRepository
import org.schulcloud.mobile.viewmodels.base.BaseViewModel


class EventListViewModel : BaseViewModel() {
    val events: LiveData<List<Event>> = EventRepository.eventsForToday(realm)
}
