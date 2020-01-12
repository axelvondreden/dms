package com.dude.dms.ui.converters

import com.dude.dms.backend.brain.OptionKey
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun LocalDateTime.convert() = format(DateTimeFormatter.ofPattern("${OptionKey.DATE_FORMAT.string} HH:mm:ss"))