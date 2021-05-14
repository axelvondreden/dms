package com.dude.dms.brain

import com.dude.dms.brain.options.Options
import org.springframework.context.MessageSource
import org.springframework.context.NoSuchMessageException
import java.util.*

val source = SpringContext.getBean(MessageSource::class.java)!!

fun t(msg: String, vararg args: Any): String = try {
    source.getMessage(msg, args, Locale.forLanguageTag(Options.get().view.locale))
} catch (e: NoSuchMessageException) {
    msg
}