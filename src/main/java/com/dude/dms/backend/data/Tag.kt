package com.dude.dms.backend.data

import com.dude.dms.backend.data.docs.Attribute
import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.data.mails.Mail
import com.dude.dms.backend.data.mails.MailFilter
import com.dude.dms.backend.data.rules.PlainTextRule
import com.dude.dms.backend.data.rules.RegexRule
import com.dude.dms.brain.t
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator
import java.util.*
import javax.persistence.Entity
import javax.persistence.ManyToMany
import kotlin.collections.HashSet

@JsonIdentityInfo(generator = PropertyGenerator::class, property = "id")
@Entity
class Tag(
        var name: String,
        var color: String,
        @ManyToMany(mappedBy = "tags") var docs: Set<Doc> = HashSet(),
        @ManyToMany(mappedBy = "tags") var mails: Set<Mail> = HashSet(),
        @ManyToMany var attributes: Set<Attribute> = HashSet(),
        @ManyToMany(mappedBy = "tags") var plainTextRules: Set<PlainTextRule> = HashSet(),
        @ManyToMany(mappedBy = "tags") var regexRules: Set<RegexRule> = HashSet(),
        @ManyToMany(mappedBy = "tags") var mailFilters: Set<MailFilter> = HashSet()
) : DataEntity(), Diffable<Tag>, LogsEvents {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        if (!super.equals(other)) return false
        return name == (other as Tag).name
    }

    override fun hashCode() = Objects.hash(super.hashCode(), name)

    override fun toString(): String = t("tag")

}