package com.dude.dms.ui.extensions

import com.dude.dms.brain.OptionKey
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun LocalDate.convert() = format(DateTimeFormatter.ofPattern(OptionKey.DATE_FORMAT.string))

fun LocalDateTime.convert() = format(DateTimeFormatter.ofPattern("${OptionKey.DATE_FORMAT.string} HH:mm:ss"))