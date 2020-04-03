package com.dude.dms.backend.data.rules

import com.dude.dms.backend.data.Tag
import com.dude.dms.brain.t
import java.util.regex.Pattern
import javax.persistence.Entity
import javax.persistence.ManyToMany
import javax.persistence.Transient

@Entity
class RegexRule(
        var regex: String,
        @ManyToMany override var tags: Set<Tag> = HashSet()
) : Rule() {

    @Suppress("SuspiciousVarProperty")
    @Transient
    private var pattern: Pattern = Pattern.compile(regex)
        get() = Pattern.compile(regex)

    override fun validate(line: String?): Boolean {
        return if (line == null || line.isEmpty()) false else pattern.matcher(line).matches()
    }

    override fun toString() = t("rule.regex")
}