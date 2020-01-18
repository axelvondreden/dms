package com.dude.dms.ui.components.dialogs

import com.dude.dms.brain.CreateEvent
import com.dude.dms.brain.DeleteEvent
import com.dude.dms.brain.EditEvent
import com.dude.dms.backend.data.DataEntity
import com.vaadin.flow.component.dialog.Dialog

abstract class EventDialog<T : DataEntity> : Dialog() {

    var createEvent: CreateEvent<T>? = null
    var editEvent: EditEvent<T>? = null
    var deleteEvent: DeleteEvent<T>? = null

    protected fun triggerCreateEvent(entity: T) {
        createEvent?.invoke(entity)
    }

    protected fun triggerDeleteEvent(entity: T) {
        deleteEvent?.invoke(entity)
    }

    protected fun triggerEditEvent(entity: T) {
        editEvent?.invoke(entity)
    }
}