package com.dude.dms.utils

import com.dude.dms.brain.options.Options
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

private val userDateFormat: DateTimeFormatter
    get() = DateTimeFormatter.ofPattern(Options.get().view.dateFormat)

private val userDateTimeFormat: DateTimeFormatter
    get() = DateTimeFormatter.ofPattern("${Options.get().view.dateFormat} HH:mm:ss")

private val dateScanFormats: Map<String, DateTimeFormatter>
    get() = Options.get().view.dateScanFormats.associateWith { DateTimeFormatter.ofPattern(it) }


fun Double.convert(): String = NumberFormat.getNumberInstance(Locale.forLanguageTag(Options.get().view.locale)).format(this)

fun String.findDecimal() = filterNot { it.isLetter() || it.isWhitespace() || it == '€' }.let { s ->
    var txt = s.replace(',', '.')
    while (txt.count { it == '.' } > 1) {
        txt = txt.replaceFirst(".", "")
    }
    txt.toDoubleOrNull()
}

fun String.findInt() = filterNot { it.isLetter() || it.isWhitespace() || it == '€' || it == ',' || it == '.' }.trim().toIntOrNull()

fun LocalDate.convert(): String = format(userDateFormat)

fun LocalDateTime.convert(): String = format(userDateTimeFormat)

fun String.findDate() = filterNot { it.isLetter() || it.isWhitespace() }.replace(',', '.').let { txt ->
    dateScanFormats.flatMap { entry ->
        (0..txt.length - entry.key.length).mapNotNull {
            try {
                LocalDate.parse(txt.substring(it, it + entry.key.length), entry.value)
            } catch (e: DateTimeParseException) {
                null
            }
        }
    }.firstOrNull()
}

fun Float.round(decimals: Int): Float {
    var multiplier = 1.0F
    repeat(decimals) { multiplier *= 10 }
    return kotlin.math.round(this * multiplier) / multiplier
}

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return kotlin.math.round(this * multiplier) / multiplier
}