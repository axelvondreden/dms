package com.dude.dms.backend.data.filter

import com.dude.dms.backend.data.Tag
import com.dude.dms.brain.t
import org.springframework.context.i18n.LocaleContextHolder
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.ManyToMany

@Entity
class PlainTextRule(
        var text: String,
        var caseSensitive: Boolean,
        @ManyToMany(fetch = FetchType.EAGER) override var tags: Set<Tag> = HashSet()
) : Rule() {

    override fun validate(line: String?): Boolean {
        return if (line != null && line.isNotEmpty()) {
            if (caseSensitive) text in line
            else {
                val locale = LocaleContextHolder.getLocale()
                text.lowercase(locale) in line.lowercase(locale)
            }
        } else false
    }

    override fun toString(): String = t("rule.plain")
}