package com.dude.dms.extensions

import com.dude.dms.brain.options.Options
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private val userDateFormat: DateTimeFormatter
    get() = DateTimeFormatter.ofPattern(Options.get().view.dateFormat)

private val userDateTimeFormat: DateTimeFormatter
    get() = DateTimeFormatter.ofPattern("${Options.get().view.dateFormat} HH:mm:ss")

private val scanFormats: Map<String, DateTimeFormatter>
    get() = Options.get().view.dateScanFormats.map { it to DateTimeFormatter.ofPattern(it) }.toMap()

fun LocalDate.convert() = format(userDateFormat)

fun LocalDateTime.convert() = format(userDateTimeFormat)

fun String.findDate() = scanFormats.flatMap { entry ->
    (0..length - entry.key.length).mapNotNull {
        try {
            LocalDate.parse(substring(it, it + entry.key.length), entry.value)
        } catch (e: DateTimeParseException) {
            null
        }
    }
}.firstOrNull()