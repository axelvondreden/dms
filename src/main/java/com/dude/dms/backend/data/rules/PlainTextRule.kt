package com.dude.dms.backend.data.rules

import com.dude.dms.backend.data.Tag
import org.springframework.context.i18n.LocaleContextHolder
import javax.persistence.Entity
import javax.persistence.ManyToMany

@Entity
class PlainTextRule(
        var text: String,
        var caseSensitive: Boolean,
        @ManyToMany override var tags: Set<Tag> = HashSet()
) : Rule() {

    override fun validate(line: String?): Boolean {
        return if (line != null && line.isNotEmpty()) {
            if (caseSensitive) text in line
            else {
                val locale = LocaleContextHolder.getLocale()
                text.toLowerCase(locale) in line.toLowerCase(locale)
            }
        } else false
    }

    override fun toString() = "PlainTextRule{text='$text'}"
}