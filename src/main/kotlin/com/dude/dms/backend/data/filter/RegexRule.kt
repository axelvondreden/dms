package com.dude.dms.backend.data.filter

import com.dude.dms.backend.data.Tag
import com.dude.dms.brain.t
import java.util.regex.Pattern
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.ManyToMany
import javax.persistence.Transient

@Entity
class RegexRule(
        var regex: String,
        @ManyToMany(fetch = FetchType.EAGER) override var tags: Set<Tag> = HashSet()
) : Rule() {

    @Suppress("SuspiciousVarProperty")
    @Transient
    private var pattern: Pattern = Pattern.compile(regex)
        get() = Pattern.compile(regex)

    override fun validate(line: String?): Boolean {
        return if (line == null || line.isEmpty()) false else pattern.matcher(line).find()
    }

    override fun toString(): String = t("rule.regex")
}