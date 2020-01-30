package com.dude.dms.ui.extensions

import com.dude.dms.brain.options.Options
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun LocalDate.convert() = format(DateTimeFormatter.ofPattern(Options.get().view.dateFormat))

fun LocalDateTime.convert() = format(DateTimeFormatter.ofPattern("${Options.get().view.dateFormat} HH:mm:ss"))