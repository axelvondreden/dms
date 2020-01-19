package com.dude.dms.ui.components.standard

import com.dude.dms.brain.options.Options
import com.vaadin.flow.component.datepicker.DatePicker
import java.util.*

class DmsDatePicker(label: String = "") : DatePicker(label) {

    init {
        locale = Locale.forLanguageTag(Options.get().view.locale)
    }
}