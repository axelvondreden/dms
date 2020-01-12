package com.dude.dms.ui.converters

import com.dude.dms.backend.brain.OptionKey
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun LocalDate.convert() = format(DateTimeFormatter.ofPattern(OptionKey.DATE_FORMAT.string))