package com.dude.dms.ui.components.standard

import com.dude.dms.backend.brain.OptionKey
import com.vaadin.flow.component.datepicker.DatePicker
import java.util.*

class DmsDatePicker(label: String = "") : DatePicker(label) {

    init {
        locale = Locale.forLanguageTag(OptionKey.LOCALE.string)
    }
}