package com.dude.dms.backend.data.rules

import com.dude.dms.backend.data.DataEntity
import com.dude.dms.backend.data.Tag
import javax.persistence.MappedSuperclass

@MappedSuperclass
abstract class Rule(var active: Boolean) : DataEntity() {

    abstract var tags: Set<Tag>

    /**
     * Checks a line of text against the rule pattern.
     * @param line line of text
     * @return `true`, if the pattern found a match
     */
    abstract fun validate(line: String?): Boolean
}